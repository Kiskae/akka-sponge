package net.serverpeon.sponge.akka.ext;

import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.dispatch.Dispatchers;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import net.serverpeon.sponge.akka.AkkaService;
import net.serverpeon.sponge.akka.WrappedActorSystem;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class SpongeExtensionImpl implements Extension, AkkaService {
    private final Config dispatcherConfig = ConfigFactory.parseResourcesAnySyntax(
            getClass().getClassLoader(),
            "akka-sponge/base"
    ).withValue("mailbox-requirement", ConfigValueFactory.fromAnyRef(""));
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, String> definedDispatchers = Maps.newHashMap();
    private final ExtendedActorSystem system;
    private Optional<Game> game = Optional.absent();

    public SpongeExtensionImpl(final @Nonnull ExtendedActorSystem system) {
        this.system = system;
    }

    private static String uniqueId(String pluginId) {
        return "sponge-dispatcher-" + pluginId + ThreadLocalRandom.current().nextLong();
    }

    public void initGame(Game game) {
        checkNotNull(game, "game == NULL");
        this.game = Optional.of(game);
    }

    @Nonnull
    @Override
    public WrappedActorSystem forPlugin(@Nonnull PluginContainer container) {
        lock.lock();
        try {
            return new SpongeActorSystem(
                    system,
                    getDispatcherId(checkNotNull(container, "container == NULL"))
            );
        } finally {
            lock.unlock();
        }
    }

    @Nonnull
    @Override
    public ActorSystem system() {
        return this.system;
    }

    @Nonnull
    @Override
    public AkkaService adapt(@Nonnull ActorSystem system) {
        checkState(game.isPresent(), "game not initialized");
        return ExtensionAccessor.createService(checkNotNull(system, "system == NULL"), game.get());
    }

    @Nonnull
    private String getDispatcherId(final @Nonnull PluginContainer container) {
        final String pluginId = container.getId();
        if (this.definedDispatchers.containsKey(pluginId)) {
            return this.definedDispatchers.get(pluginId);
        } else {
            final String newDispatcherId = createDispatcher(
                    uniqueId(pluginId),
                    container.getInstance()
            );
            this.definedDispatchers.put(pluginId, newDispatcherId);
            return newDispatcherId;
        }
    }

    private String createDispatcher(String dispatcherId, Object plugin) {
        checkState(game.isPresent(), "game has not yet been initialized");
        checkState(plugin != null, "provided plugin is NULL");

        final Dispatchers dispatchers = system.dispatchers();

        //Register dispatcher
        dispatchers.registerConfigurator(
                dispatcherId,
                new MessageConfigurator(
                        plugin,
                        game.get(),
                        dispatcherConfig,
                        dispatchers.prerequisites()
                )
        );

        return dispatcherId;
    }
}
