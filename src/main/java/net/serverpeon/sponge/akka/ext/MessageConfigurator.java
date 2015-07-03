package net.serverpeon.sponge.akka.ext;

import akka.dispatch.*;
import com.typesafe.config.Config;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

class MessageConfigurator extends MessageDispatcherConfigurator {
    //    private val instance = new Dispatcher(
//        this,
//        config.getString("id"),
//        config.getInt("throughput"),
//        config.getNanosDuration("throughput-deadline-time"),
//        configureExecutor(),
//        config.getMillisDuration("shutdown-timeout"))
    private final Dispatcher dispatcher;
    private final ExecutorServiceConfigurator executorConfigurator;
    private final DispatcherPrerequisites prereq;

    public MessageConfigurator(PluginContainer plugin, Game game, Config config,
                               DispatcherPrerequisites prereq) {
        super(config, prereq);
        checkNotNull(plugin);
        checkNotNull(game);
        checkNotNull(config);
        this.prereq = checkNotNull(prereq, "prereq == NULL");

        this.executorConfigurator = new ExecutorConfigurator(plugin, game, config, prereq);
        //Based on DispatcherConfigurator
        this.dispatcher = new Dispatcher(
                this,
                config.getString("id"),
                config.getInt("throughput"),
                Duration.create(
                        config.getDuration("throughput-deadline-time", TimeUnit.NANOSECONDS),
                        TimeUnit.NANOSECONDS
                ),
                configureExecutor(),
                Duration.create(
                        config.getDuration("shutdown-timeout", TimeUnit.MILLISECONDS),
                        TimeUnit.MILLISECONDS
                )
        );
    }

    @Override
    public MessageDispatcher dispatcher() {
        return this.dispatcher;
    }

    @Override
    public ExecutorServiceConfigurator configureExecutor() {
        return this.executorConfigurator;
    }

    @Override
    public DispatcherPrerequisites prerequisites() {
        return this.prereq;
    }
}
