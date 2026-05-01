package com.iafenvoy.origins.util.wrapper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Mutable<T> {
    T get();

    void set(T value);

    static <T> Mutable<T> of(T value) {
        return new Constant<>(value);
    }

    static <T> Mutable<T> access(Supplier<T> reader, Consumer<T> writer) {
        return new Access<>(reader, writer);
    }

    class Constant<T> implements Mutable<T> {
        private T value;

        public Constant(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return this.value;
        }

        @Override
        public void set(T value) {
            this.value = value;
        }
    }

    class Access<T> implements Mutable<T> {
        private final Supplier<T> reader;
        private final Consumer<T> writer;

        public Access(Supplier<T> reader, Consumer<T> writer) {
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        public T get() {
            return this.reader.get();
        }

        @Override
        public void set(T value) {
            this.writer.accept(value);
        }
    }
}
