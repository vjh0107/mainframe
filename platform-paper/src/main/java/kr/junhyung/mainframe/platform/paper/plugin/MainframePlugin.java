package kr.junhyung.mainframe.platform.paper.plugin;

import kr.junhyung.mainframe.core.util.ScopedExecutions;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainframePlugin extends JavaPlugin {

    private final Class<?> applicationClass;
    private ConfigurableApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(MainframePlugin.class);
    private static final String LOGGING_SYSTEM_PROPERTY = "org.springframework.boot.logging.LoggingSystem";
    private static final String NONE_LOGGING_SYSTEM = "none";
    private static final String MAIN_CLASS_RESOURCE = "META-INF/mainframe/application-class.properties";
    private static final String MAIN_CLASS_KEY = "mainClass";

    public MainframePlugin() {
        this.applicationClass = resolveApplicationClass();
    }

    private static Class<?> resolveApplicationClass() {
        ClassLoader classLoader = MainframePlugin.class.getClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(MAIN_CLASS_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing '" + MAIN_CLASS_RESOURCE + "' on the plugin classpath");
            }
            Properties properties = new Properties();
            properties.load(in);
            String className = properties.getProperty(MAIN_CLASS_KEY);
            if (className == null) {
                throw new IllegalStateException("Missing '" + MAIN_CLASS_KEY + "' in '" + MAIN_CLASS_RESOURCE + "'");
            }
            return Class.forName(className, false, classLoader);
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Could not resolve the @SpringBootApplication class", e);
        }
    }

    @Override
    public void onEnable() {
        try {
            this.applicationContext = ScopedExecutions.withSystemProperty(LOGGING_SYSTEM_PROPERTY, NONE_LOGGING_SYSTEM, () ->
                ScopedExecutions.withContextClassLoader(getClassLoader(), this::startApplication));
        } catch (Throwable throwable) {
            logger.error("Failed to start Spring application, shutting down", throwable);
            Runtime.getRuntime().halt(exitCodeFor(throwable));
        }
    }

    private static int exitCodeFor(Throwable failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (cause instanceof ExitCodeGenerator generator) {
                int exitCode = generator.getExitCode();
                if (exitCode != 0) {
                    return exitCode;
                }
            }
        }
        return 1;
    }

    private ConfigurableApplicationContext startApplication() {
        ClassLoader pluginClassLoader = getClassLoader();
        ResourceLoader resourceLoader = new DefaultResourceLoader(pluginClassLoader);
        return new SpringApplicationBuilder()
            .sources(applicationClass)
            .bannerMode(Banner.Mode.LOG)
            .resourceLoader(resourceLoader)
            .headless(true)
            .registerShutdownHook(false)
            .initializers(new MainframePluginApplicationContextInitializer(this))
            .run();
    }

    @Override
    public void onDisable() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

}