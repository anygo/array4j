package com.googlecode.array4j.dense;

import java.nio.FloatBuffer;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;

public final class FloatDenseVector extends AbstractFloatDense<FloatDenseVector> implements
        FloatVector<FloatDenseVector> {
    public FloatDenseVector(final float... values) {
        this(Orientation.DEFAULT_FOR_VECTOR, Storage.DEFAULT_FOR_DENSE, values);
    }

    /**
     * Constructor for internal use.
     */
    FloatDenseVector(final FloatBuffer data, final int size, final int offset, final int stride,
            final Orientation orientation) {
        super(data, size, offset, stride, orientation);
    }

    /**
     * Constructor.
     */
    public FloatDenseVector(final int size) {
        this(size, Orientation.DEFAULT_FOR_VECTOR, Storage.DEFAULT_FOR_DENSE);
    }

    /**
     * Construct with specified orientation and storage.
     */
    public FloatDenseVector(final int size, final Orientation orientation, final Storage storage) {
        super(size, orientation, storage);
    }

    public FloatDenseVector(final Orientation orientation, final Storage storage, final float... values) {
        this(values.length, orientation, storage);
        data.put(values);
    }

    public FloatDenseVector(final Storage storage, final float... values) {
        this(Orientation.DEFAULT_FOR_VECTOR, storage, values);
    }

    @Override
    public FloatDenseVector asVector() {
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof FloatDenseVector)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return new EqualsBuilder().appendSuper(super.equals(obj)).isEquals();
    }

    public boolean isColumnVector() {
        return orientation.equals(Orientation.COLUMN);
    }

    public boolean isRowVector() {
        return orientation.equals(Orientation.ROW);
    }

    @Override
    public FloatDenseVector minus(final FloatVector<?> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void plusEquals(final FloatVector<?> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatDenseVector subMatrixColumns(final int column0, final int column1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FloatDenseVector transpose() {
        return new FloatDenseVector(data, size, offset, stride, orientation.transpose());
    }
}