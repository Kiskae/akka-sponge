package net.serverpeon.sponge.akka;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ProvisioningException;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.scheduler.Task;

import javax.annotation.Nonnull;

/**
 * Utility class that uses the fact that each plugin has its own Guice injector.
 * By injecting the current {@link PluginContainer}, it is able to initialize a {@link WrappedActorSystem}
 * for the calling plugin.
 *
 * @author Kiskae <kiskae@serverpeon.net>
 * @since 2015-06-24
 */
@Singleton
public class AkkaSponge {
    private final Supplier<WrappedActorSystem> systemProvider;

    @Inject
    protected AkkaSponge(final ServiceManager sm, final PluginContainer plugin) {
        this.systemProvider = Suppliers.memoize(new SystemSupplier(sm, plugin));
    }

    /**
     * Creates a wrapped actor system that is able to create actors which execute on the Sponge main thread.
     * The execution of these actors will be tied to the plugin that instantiated this class.
     *
     * @return A wrapped actor system for provisioning Sponge-bound actors tied to the current plugin.
     * @throws ProvisioningException if this method is called before {@link PreInitializationEvent}.
     * @see Task#getOwner()
     */
    @Nonnull
    public WrappedActorSystem system() throws ProvisioningException {
        return this.systemProvider.get();
    }

    private static class SystemSupplier implements Supplier<WrappedActorSystem> {
        private final ServiceManager sm;
        private final PluginContainer plugin;

        private SystemSupplier(ServiceManager sm, PluginContainer plugin) {
            this.sm = sm;
            this.plugin = plugin;
        }

        @Override
        public WrappedActorSystem get() {
            return this.sm.provideUnchecked(AkkaService.class).forPlugin(this.plugin);
        }
    }
}
