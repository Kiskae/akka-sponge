package net.serverpeon.sponge.akka.ext;

import akka.dispatch.DispatcherPrerequisites;
import akka.dispatch.ExecutorServiceConfigurator;
import akka.dispatch.ExecutorServiceFactory;
import com.typesafe.config.Config;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.scheduler.SchedulerService;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class ExecutorConfigurator extends ExecutorServiceConfigurator {
    private final ExecutorService executor;

    public ExecutorConfigurator(Object plugin, Game game, Config config,
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
        private final Object plugin;
        private final String taskName;

        public SpongeExecutorService(SchedulerService scheduler, Object plugin) {
            this.scheduler = scheduler;
            this.plugin = plugin;
            this.taskName = "SpongeAkka-Executor-" + plugin.getClass().getSimpleName();
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException("Unable to terminate delegate executor");
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException("Unable to terminate delegate executor");
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
            throw new UnsupportedOperationException("Unable to terminate delegate executor");
        }

        @Override
        public void execute(Runnable command) {
            this.scheduler.getTaskBuilder()
                    .execute(command)
                    .name(this.taskName)
                    .submit(this.plugin);
        }
    }
}
