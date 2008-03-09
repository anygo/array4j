package com.googlecode.array4j.dense;

import com.googlecode.array4j.ComplexFloatMatrix;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import java.nio.FloatBuffer;
import org.apache.commons.lang.builder.EqualsBuilder;

public final class CFloatDenseMatrix extends AbstractCFloatDense<CFloatDenseMatrix> implements
        ComplexFloatMatrix<CFloatDenseMatrix, CFloatDenseVector> {
    private static final long serialVersionUID = 1L;

    /**
     * Copy constructor.
     *
     * @param other
     *                matrix to copy
     */
    public CFloatDenseMatrix(final ComplexFloatMatrix<?, ?> other) {
        this(other.rows(), other.columns());
        // TODO optimize this
        for (int i = 0; i < other.rows(); i++) {
            for (int j = 0; j < other.columns(); j++) {
                set(i, j, other.get(i, j));
            }
        }
    }

    /**
     * Constructor for internal use.
     */
    CFloatDenseMatrix(final FloatBuffer data, final int rows, final int columns, final int offset, final int stride,
            final Orientation orientation) {
        super(data, rows, columns, offset, stride, orientation);
    }

    /**
     * Constructor.
     */
    public CFloatDenseMatrix(final int rows, final int columns) {
        this(rows, columns, Orientation.DEFAULT, Storage.DEFAULT_FOR_DENSE);
    }

    /**
     * Construct with specified orientation and storage.
     */
    public CFloatDenseMatrix(final int rows, final int columns, final Orientation orientation, final Storage storage) {
        super(rows, columns, orientation, storage);
    }

    @Override
    public CFloatDenseMatrix conjTranpose() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof CFloatDenseMatrix)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return new EqualsBuilder().appendSuper(super.equals(obj)).isEquals();
    }

    @Override
    public CFloatDenseMatrix transpose() {
        return new CFloatDenseMatrix(data, columns, rows, offset, stride, orientation.transpose());
    }
}