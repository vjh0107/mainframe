package kr.junhyung.mainframe.platform.velocity.plugin;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import kr.junhyung.mainframe.core.util.ScopedExecutions;
import org.slf4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Boots the Spring application for a Velocity plugin. Referencing Spring here is safe because this
 * class is only loaded from {@link MainframeVelocityPlugin#onProxyInitialize}, after the nested
 * libraries have been added to the plugin classpath.
 */
final class MainframeVelocityApplication {

    private static final String LOGGING_SYSTEM_PROPERTY = "org.springframework.boot.logging.LoggingSystem";
    private static final String NONE_LOGGING_SYSTEM = "none";
    private static final String MAIN_CLASS_RESOURCE = "META-INF/mainframe/application-class.properties";
    private static final String MAIN_CLASS_KEY = "mainClass";

    private MainframeVelocityApplication() {
    }

    static AutoCloseable run(Object plugin,
                             ProxyServer proxyServer,
                             PluginContainer pluginContainer,
                             Logger logger,
                             Path dataDirectory) {
        Class<?> applicationClass = resolveApplicationClass();
        ClassLoader classLoader = MainframeVelocityApplication.class.getClassLoader();
        ResourceLoader resourceLoader = new DefaultResourceLoader(classLoader);
        return ScopedExecutions.withSystemProperty(LOGGING_SYSTEM_PROPERTY, NONE_LOGGING_SYSTEM, () ->
            ScopedExecutions.withContextClassLoader(classLoader, () ->
                new SpringApplicationBuilder()
                    .sources(applicationClass)
                    .bannerMode(Banner.Mode.LOG)
                    .resourceLoader(resourceLoader)
                    .headless(true)
                    .registerShutdownHook(false)
                    .initializers(new MainframeVelocityPluginApplicationContextInitializer(plugin, proxyServer, pluginContainer, logger, dataDirectory))
                    .run()));
    }

    private static Class<?> resolveApplicationClass() {
        ClassLoader classLoader = MainframeVelocityApplication.class.getClassLoader();
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

}
