package net.serverpeon.sponge.akka.ext;

import akka.dispatch.DispatcherPrerequisites;
import akka.dispatch.ExecutorServiceConfigurator;
import akka.dispatch.ExecutorServiceFactory;
import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.scheduler.SchedulerService;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class ExecutorConfigurator extends ExecutorServiceConfigurator {
    private final ExecutorService executor;

    public ExecutorConfigurator(PluginContainer plugin, Game game, Config config,
                                DispatcherPrerequisites prereq) {
        super(config, prereq);
        this.executor = new SpongeExecutorService(
                game.getScheduler(),
                plugin
        );
    }

    @Override
    public ExecutorServiceFactory createExecutorServiceFactory(String id, ThreadFactory threadFactory) {
        return new ExecutorFactory(this.executor);
    }

    private static class ExecutorFactory implements ExecutorServiceFactory {
        private final ExecutorService executor;

        public ExecutorFactory(ExecutorService executor) {
            this.executor = executor;
        }

        @Override
        public ExecutorService createExecutorService() {
            return this.executor;
        }
    }

    private static class SpongeExecutorService extends AbstractExecutorService {
        private final SchedulerService scheduler;
        private final PluginContainer plugin;
        private final String taskName;

        public SpongeExecutorService(SchedulerService scheduler, PluginContainer plugin) {
            this.scheduler = scheduler;
            this.plugin = plugin;
            this.taskName = "SpongeAkka-Executor-" + plugin.getId();
        }

        @Override
        public void shutdown() {
            //NOOP
        }

        @Override
        public List<Runnable> shutdownNow() {
            return ImmutableList.of();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            this.scheduler.getTaskBuilder()
                    .execute(command)
                    .name(this.taskName)
                    .submit(this.plugin.getInstance());
        }
    }
}
