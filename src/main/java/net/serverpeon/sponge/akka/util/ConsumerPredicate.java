package net.serverpeon.sponge.akka.util;

import com.google.common.base.Predicate;

public class ConsumerPredicate<T> implements Predicate<T> {
    private final Consumer<T> consumer;
    private final boolean ret;

    private ConsumerPredicate(Consumer<T> consumer, boolean ret) {
        this.consumer = consumer;
        this.ret = ret;
    }

    public static <T> Predicate<T> wrap(Consumer<T> consumer) {
        return wrap(consumer, true);
    }

    public static <T> Predicate<T> wrap(Consumer<T> consumer, boolean ret) {
        return new ConsumerPredicate<>(consumer, ret);
    }

    @Override
    public boolean apply(T input) {
        this.consumer.apply(input);
        return this.ret;
    }

    public interface Consumer<T> {
        void apply(T input);
    }
}
