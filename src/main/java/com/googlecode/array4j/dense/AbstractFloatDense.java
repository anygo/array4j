package com.googlecode.array4j.dense;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.googlecode.array4j.FloatBLAS;
import com.googlecode.array4j.FloatMatrix;
import com.googlecode.array4j.FloatVector;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import com.googlecode.array4j.VectorSupport;

public abstract class AbstractFloatDense<M extends FloatMatrix<M, FloatDenseVector>> extends
        AbstractDenseMatrix<M, FloatDenseVector, float[]> {
    private static final int DEFAULT_OFFSET = 0;

    private static final int DEFAULT_STRIDE = 1;

    private static final int ELEMENT_SIZE = 1;

    private static final int FLOAT_BYTES = Float.SIZE >>> 3;

    public static FloatBuffer createFloatBuffer(final int size, final Storage storage) {
        if (storage.equals(Storage.DIRECT)) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(size * ELEMENT_SIZE * FLOAT_BYTES);
            buffer.order(ByteOrder.nativeOrder());
            return buffer.asFloatBuffer();
        } else {
            return FloatBuffer.allocate(size * ELEMENT_SIZE);
        }
    }

    protected transient FloatBuffer data;

    /**
     * Constructor for matrix with existing data.
     */
    public AbstractFloatDense(final FloatBuffer data, final int rows, final int columns, final int offset,
            final int stride, final Orientation orientation) {
        super(ELEMENT_SIZE, rows, columns, offset, stride, orientation);
        this.data = data;
    }

    /**
     * Constructor for vector with existing data.
     */
    public AbstractFloatDense(final FloatBuffer data, final int size, final int offset, final int stride,
            final Orientation orientation) {
        this(data, VectorSupport.rows(size, orientation), VectorSupport.columns(size, orientation), offset, stride,
                orientation);
    }

    /**
     * Constructor for new matrix.
     */
    public AbstractFloatDense(final int rows, final int columns, final Orientation orientation, final Storage storage) {
        super(ELEMENT_SIZE, rows, columns, DEFAULT_OFFSET, DEFAULT_STRIDE, orientation);
        this.data = createFloatBuffer(size, storage);
    }

    /**
     * Constructor for new vector.
     */
    public AbstractFloatDense(final int size, final Orientation orientation, final Storage storage) {
        this(VectorSupport.rows(size, orientation), VectorSupport.columns(size, orientation), orientation, storage);
    }

    @Override
    public final FloatDenseVector column(final int column) {
        checkColumnIndex(column);
        return new FloatDenseVector(data, rows, columnOffset(column), rowStride, orientation.COLUMN);
    }

    @Override
    protected final float[] createArray(final int length) {
        return new float[length];
    }

    @Override
    protected final float[][] createArrayArray(final int length) {
        return new float[length][];
    }

    @Override
    public final FloatDenseVector createColumnVector() {
        return new FloatDenseVector(rows, Orientation.COLUMN, storage());
    }

    @Override
    public final FloatDenseVector createRowVector() {
        return new FloatDenseVector(columns, Orientation.ROW, storage());
    }

    public final FloatBuffer data() {
        return data;
    }

    public final float[] dataAsArray() {
        return data.array();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof AbstractFloatDense)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (new EqualsBuilder().appendSuper(super.equals(obj)).isEquals()) {
            return false;
        }
        // TODO optimize this
        AbstractFloatDense<?> other = (AbstractFloatDense<?>) obj;
        for (int i = 0; i < size; i++) {
            if (get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }

    public final void fill(final float value) {
        FloatBuffer xdata = createFloatBuffer(1, storage());
        FloatDenseVector x = new FloatDenseVector(xdata, size, 0, 0, Orientation.DEFAULT_FOR_VECTOR);
        xdata.put(0, value);
        FloatBLAS.copy(x, asVector());
    }

    @Override
    protected final void fillFrom(final float[] dest, final int srcPos) {
        Arrays.fill(dest, data.get(srcPos));
    }

    public final float get(final int index) {
        return data.get(elementOffset(index));
    }

    public final float get(final int row, final int column) {
        return data.get(elementOffset(row, column));
    }

    public final boolean hasArray() {
        return data.hasArray();
    }

    public final boolean isDirect() {
        return data.isDirect();
    }

    public final int offset() {
        return offset;
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Storage storage = (Storage) in.readObject();
        this.data = createFloatBuffer(size, storage);
        // TODO this stuff can fail when there are offsets and strides involved
        for (int i = 0; i < size; i++) {
            data.put(i, in.readFloat());
        }
    }

    @Override
    public final FloatDenseVector row(final int row) {
        return new FloatDenseVector(data, columns, rowOffset(row), columnStride, Orientation.ROW);
    }

    public final void set(final int index, final float value) {
        data.put(elementOffset(index), value);
    }

    public final void set(final int row, final int column, final float value) {
        data.put(elementOffset(row, column), value);
    }

    public final void setColumn(final int column, final FloatVector<?> columnVector) {
        // TODO this code is almost identical to setRow
        checkArgument(rows == columnVector.size());
        int targetOffset = columnOffset(column);
        int targetStride = rowStride;
        // TODO this could be optimized
        for (int i = 0; i < rows; i++) {
            data.put(targetOffset + i * targetStride, columnVector.get(i));
        }
    }

    @Override
    protected final void setFrom(final float[] dest, final int destPos, final int srcPos) {
        dest[destPos] = data.get(srcPos);
    }

    public final void setRow(final int row, final FloatVector<?> rowVector) {
        // TODO this code is almost identical to setColumn
        checkArgument(columns == rowVector.size());
        int targetOffset = rowOffset(row);
        int targetStride = columnStride;
        // TODO this could be optimized
        for (int i = 0; i < columns; i++) {
            data.put(targetOffset + i * targetStride, rowVector.get(i));
        }
    }

    public final Storage storage() {
        return data.isDirect() ? Storage.DIRECT : Storage.JAVA;
    }

    public final int stride() {
        return stride;
    }

    public final void timesEquals(final float value) {
        throw new UnsupportedOperationException();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(storage());
        // TODO optimize this
        for (int i = 0; i < size; i++) {
            out.writeFloat(get(i));
        }
    }
}