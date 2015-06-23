package net.serverpeon.sponge.akka;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

import javax.annotation.Nonnull;

/**
 *
 */
@Singleton
public class AkkaSponge {
    private final Supplier<WrappedActorSystem> systemProvider;

    @Inject
    protected AkkaSponge(final ServiceManager sm, final PluginContainer plugin) {
        this.systemProvider = Suppliers.memoize(new SystemSupplier(sm, plugin));
    }

    /**
     * @return
     */
    @Nonnull
    public WrappedActorSystem system() {
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
            return sm.provide(AkkaService.class).get().forPlugin(plugin);
        }
    }
}
