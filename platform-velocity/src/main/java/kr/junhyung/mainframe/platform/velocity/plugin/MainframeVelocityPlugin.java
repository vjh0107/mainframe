package kr.junhyung.mainframe.platform.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import kr.junhyung.pluginjar.velocity.VelocityLibraryLoader;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * Velocity entry point. This class must not reference any Spring (or mainframe-core) type directly:
 * it lives in the bootstrap jar and is loaded before {@link VelocityLibraryLoader} has put the nested
 * libraries on the classpath. Library loading and the Spring bootstrap both happen in
 * {@link #onProxyInitialize} — the container only exposes this plugin's instance (required by
 * {@code addToClasspath}) once construction has finished. The Spring bootstrap itself lives in
 * {@link MainframeVelocityApplication}, which is only touched after the libraries are loaded.
 */
public class MainframeVelocityPlugin {

    private final ProxyServer proxyServer;
    private final PluginContainer pluginContainer;
    private final Logger logger;
    private final Path dataDirectory;
    private AutoCloseable applicationContext;

    @Inject
    public MainframeVelocityPlugin(ProxyServer proxyServer,
                                   PluginContainer pluginContainer,
                                   Logger logger,
                                   @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            VelocityLibraryLoader.load(proxyServer.getPluginManager(), pluginContainer);
            this.applicationContext = MainframeVelocityApplication.run(this, proxyServer, pluginContainer, logger, dataDirectory);
        } catch (Throwable throwable) {
            logger.error("Failed to start Spring application", throwable);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (applicationContext != null) {
            try {
                applicationContext.close();
            } catch (Exception exception) {
                logger.error("Failed to close Spring application", exception);
            }
        }
    }

}
