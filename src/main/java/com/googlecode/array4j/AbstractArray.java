package com.googlecode.array4j;

public abstract class AbstractArray<A extends Array> {
    protected static final void assertPost(final boolean condition) {
        if (!condition) {
            throw new AssertionError("postcondition failed");
        }
    }

    protected static final void assertPost(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(String.format("postcondition failed: %s", message));
        }
    }

    protected static final void checkArgument(final boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    protected static final void checkArgument(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    final int size;

    public AbstractArray(final int size) {
        checkArgument(size >= 0);
        this.size = size;
    }

    public final int size() {
        return size;
    }
}