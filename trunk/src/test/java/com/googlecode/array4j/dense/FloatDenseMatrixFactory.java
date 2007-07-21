package com.googlecode.array4j.dense;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.googlecode.array4j.FloatMatrixFactory;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;

public final class FloatDenseMatrixFactory implements FloatMatrixFactory<FloatDenseMatrix, FloatDenseVector> {
    private static FloatBuffer createFloatBuffer(final int size, final Storage storage) {
        if (storage.equals(Storage.DIRECT)) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(size * (Float.SIZE >>> 3));
            buffer.order(ByteOrder.nativeOrder());
            return buffer.asFloatBuffer();
        } else {
            return FloatBuffer.allocate(size);
        }
    }

    private final Storage storage;

    public FloatDenseMatrixFactory(final Storage storage) {
        this.storage = storage;
    }

    public FloatDenseMatrix createMatrix(final float[] values, final int rows, final int columns, final int offset,
            final int stride, final Orientation orientation) {
        FloatBuffer data = createFloatBuffer(values.length, storage);
        data.put(values);
        return new FloatDenseMatrix(data, rows, columns, offset, stride, orientation);
    }

    public FloatDenseMatrix createMatrix(final float[] values, final int rows, final int columns,
            final Orientation orientation) {
        return createMatrix(values, rows, columns, 0, 1, orientation);
    }

    public FloatDenseMatrix createMatrix(final int rows, final int columns) {
        return new FloatDenseMatrix(rows, columns, Orientation.DEFAULT, storage);
    }

    public FloatDenseMatrix createMatrix(final int rows, final int columns, final Orientation orientation) {
        return new FloatDenseMatrix(rows, columns, orientation, storage);
    }

    public FloatDenseVector createRowVector(final float... values) {
        return new FloatDenseVector(Orientation.ROW, storage, values);
    }

    public FloatDenseVector createVector(final float[] values, final int size, final int offset, final int stride,
            final Orientation orientation) {
        FloatBuffer data = createFloatBuffer(values.length, storage);
        data.put(values);
        return new FloatDenseVector(data, size, offset, stride, orientation);
    }

    public FloatDenseVector createVector(final int size, final Orientation orientation) {
        return new FloatDenseVector(size, orientation, storage);
    }
}
