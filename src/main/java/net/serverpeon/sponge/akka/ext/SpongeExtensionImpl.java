package net.serverpeon.sponge.akka.ext;

import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.dispatch.Dispatchers;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class SpongeExtensionImpl implements Extension {
    private final Config dispatcherConfig = ConfigFactory.parseResourcesAnySyntax(
            getClass().getClassLoader(),
            "akka-sponge/base"
    ).withValue("mailbox-requirement", ConfigValueFactory.fromAnyRef(""));
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, String> definedDispatchers = Maps.newHashMap();
    private final ExtendedActorSystem system;

    public SpongeExtensionImpl(final @Nonnull ExtendedActorSystem system) {
        this.system = system;
    }

    private static String uniqueId(String pluginId) {
        return "sponge-dispatcher-" + pluginId + ThreadLocalRandom.current().nextLong();
    }

    @Nonnull
    public String getDispatcherId(final @Nonnull PluginContainer container, final @Nonnull Game game) {
        checkNotNull(container, "container == NULL");
        checkNotNull(game, "game == NULL");
        lock.lock();
        try {
            final String pluginId = container.getId();
            if (this.definedDispatchers.containsKey(pluginId)) {
                return this.definedDispatchers.get(pluginId);
            } else {
                final String newDispatcherId = createDispatcher(
                        uniqueId(pluginId),
                        container,
                        game
                );
                this.definedDispatchers.put(pluginId, newDispatcherId);
                return newDispatcherId;
            }
        } finally {
            lock.unlock();
        }
    }

    private String createDispatcher(String dispatcherId, PluginContainer plugin, Game game) {
        checkArgument(game != null, "provided Game instance is NULL");
        checkArgument(plugin != null, "provided plugin is NULL");

        final Dispatchers dispatchers = system.dispatchers();

        //Register dispatcher
        dispatchers.registerConfigurator(
                dispatcherId,
                new MessageConfigurator(
                        plugin,
                        game,
                        dispatcherConfig,
                        dispatchers.prerequisites()
                )
        );

        return dispatcherId;
    }
}
