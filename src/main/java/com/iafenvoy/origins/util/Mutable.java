package com.iafenvoy.origins.util;

public final class Mutable<T> {
    private T value;

    public Mutable(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }
}
