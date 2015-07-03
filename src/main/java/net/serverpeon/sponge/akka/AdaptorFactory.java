package net.serverpeon.sponge.akka;

import akka.actor.ActorSystem;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.serverpeon.sponge.akka.ext.ExtensionAccessor;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

class AdaptorFactory implements AkkaService {
    private final Supplier<ActorSystem> defaultActorSystem = Suppliers.memoize(new Supplier<ActorSystem>() {
        @Override
        public ActorSystem get() {
            return ActorSystem.create("akka-sponge", patchConfig(ConfigFactory.load(getClass().getClassLoader())));
        }
    });
    private final Game game;

    @Inject
    AdaptorFactory(final Game game) {
        this.game = game;
    }

    @Nonnull
    @Override
    public Module module() {
        return this.module(defaultSystem());
    }

    @Nonnull
    @Override
    public Module module(@Nonnull ActorSystem backingSystem) {
        checkNotNull(backingSystem, "Provided ActorSystem is NULL");
        return new SpongeAkkaModule(backingSystem, this);
    }

    @Nonnull
    @Override
    public SpongeAdaptor adaptor(final @Nonnull PluginContainer pc) {
        return this.adaptor(defaultSystem(), pc);
    }

    @Nonnull
    @Override
    public SpongeAdaptor adaptor(@Nonnull ActorSystem backingSystem, final @Nonnull PluginContainer pc) {
        checkNotNull(backingSystem, "Provided ActorSystem is NULL");
        checkNotNull(pc, "PluginContainer is NULL!");

        return new SpongeAdaptorImpl(ExtensionAccessor.dispatcherIdFor(
                backingSystem,
                this.game,
                pc
        ), backingSystem);
    }

    @Nonnull
    @Override
    public ActorSystem defaultSystem() {
        return this.defaultActorSystem.get();
    }

    @Nonnull
    public Config patchConfig(final @Nonnull Config config) {
        checkNotNull(config, "config == NULL");
        final ClassLoader cl = AkkaSpongePlugin.class.getClassLoader();
        return config.withFallback(ConfigFactory.load(cl, "akka-sponge/reference"));
    }
}
