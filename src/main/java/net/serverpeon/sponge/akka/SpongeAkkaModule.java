package net.serverpeon.sponge.akka;

import akka.actor.ActorSystem;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.spongepowered.api.plugin.PluginContainer;

import javax.inject.Singleton;

class SpongeAkkaModule extends AbstractModule {
    private final ActorSystem system;
    private final AdaptorFactory factory;

    public SpongeAkkaModule(ActorSystem system, AdaptorFactory factory) {
        this.system = system;
        this.factory = factory;
    }

    @Override
    protected void configure() {
        bind(ActorSystem.class).toInstance(this.system);
    }

    @Provides
    @Singleton
    public SpongeAdaptor provideAdaptor(final PluginContainer plugin) {
        return this.factory.adaptor(this.system, plugin);
    }
}
