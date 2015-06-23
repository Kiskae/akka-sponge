package net.serverpeon.sponge.akka.ext;

import akka.dispatch.DispatcherPrerequisites;
import akka.dispatch.ExecutorServiceConfigurator;
import akka.dispatch.ExecutorServiceFactory;
import com.google.common.base.Supplier;
import com.typesafe.config.Config;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.scheduler.SynchronousScheduler;
import org.spongepowered.api.service.scheduler.Task;

import java.util.List;
import java.util.concurrent.*;

class ExecutorConfigurator extends ExecutorServiceConfigurator {
    private final ExecutorService executor;

    public ExecutorConfigurator(Object plugin, Game game, Config config,
                                DispatcherPrerequisites prereq) {
        super(config, prereq);
        this.executor = new SpongeExecutorService(
                game.getSyncScheduler(),
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
        private final SynchronousScheduler scheduler;
        private final Object plugin;

        public SpongeExecutorService(SynchronousScheduler scheduler, Object plugin) {
            this.scheduler = scheduler;
            this.plugin = plugin;
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
            scheduler.runTask(
                    plugin,
                    command
            ).or(TaskRejecter.INSTANCE);
        }
    }

    private static class TaskRejecter implements Supplier<Task> {
        public static final TaskRejecter INSTANCE = new TaskRejecter();

        @Override
        public Task get() {
            throw new RejectedExecutionException();
        }
    }
}
