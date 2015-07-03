package net.serverpeon.sponge.akka.util;

import akka.actor.ActorSystem;
import com.google.common.base.Optional;
import com.google.inject.Module;
import net.serverpeon.sponge.akka.AkkaService;
import net.serverpeon.sponge.akka.SpongeAdaptor;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility class which wraps most calls to {@link AkkaService} and uses the fact that {@link PluginContainer}
 * can be injected to automate the creation of a {@link SpongeAdaptor} for the plugin that instantiated it.
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-07-03
 */
@Singleton
public class AkkaSponge {
    private final ServiceManager serviceManager;
    private final PluginContainer pluginContainer;

    @Inject
    AkkaSponge(ServiceManager serviceManager, PluginContainer pluginContainer) {
        this.serviceManager = serviceManager;
        this.pluginContainer = pluginContainer;
    }

    /**
     * @return The instance of {@link AkkaService} that is provided by Akka-Sponge.
     * @throws IllegalStateException if called before Akka-Sponge has performed its pre-initialization.
     */
    @Nonnull
    public AkkaService service() {
        return this.internalService();
    }

    /**
     * Calls {@link AkkaService#adaptor(PluginContainer)} with the plugin that instantiated this object.
     *
     * @throws IllegalStateException if called before Akka-Sponge has performed its pre-initialization.
     */
    @Nonnull
    public SpongeAdaptor adaptor() {
        return this.internalService().adaptor(this.pluginContainer);
    }


    /**
     * Calls {@link AkkaService#adaptor(ActorSystem, PluginContainer)} with the plugin that instantiated this object
     * and the given actor system.
     *
     * @throws IllegalStateException if called before Akka-Sponge has performed its pre-initialization.
     */
    @Nonnull
    public SpongeAdaptor adaptorFor(final @Nonnull ActorSystem system) {
        return this.internalService().adaptor(system, this.pluginContainer);
    }

    /**
     * Proxies the call to {@link AkkaService#module()}
     *
     * @throws IllegalStateException if called before Akka-Sponge has performed its pre-initialization.
     */
    @Nonnull
    public Module module() {
        return this.internalService().module();
    }

    /**
     * Proxies the call to {@link AkkaService#module(ActorSystem)}
     *
     * @throws IllegalStateException if called before Akka-Sponge has performed its pre-initialization.
     */
    @Nonnull
    public Module module(final @Nonnull ActorSystem system) {
        return this.internalService().module(system);
    }

    @Nonnull
    private AkkaService internalService() {
        final Optional<AkkaService> service = this.serviceManager.provide(AkkaService.class);
        if (service.isPresent()) {
            return service.get();
        } else {
            throw new IllegalStateException("Called AkkaSponge before initialization");
        }
    }
}
