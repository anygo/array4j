package com.googlecode.array4j;

import java.nio.DoubleBuffer;

public abstract class AbstractDenseMatrix<E extends AbstractDenseMatrix>
        extends DenseArray<E> implements Matrix<E> {
    private final int fRows;

    private final int fColumns;

    protected AbstractDenseMatrix(final int rows, final int columns) {
        super(rows, columns);
        this.fRows = rows;
        this.fColumns = columns;
    }

    protected AbstractDenseMatrix(final Matrix other) {
        super(other);
        this.fRows = other.getRows();
        this.fColumns = other.getColumns();
    }

    protected AbstractDenseMatrix(final double[] values, final int rows, final int columns) {
        super(values, rows, columns);
        this.fRows = rows;
        this.fColumns = columns;
    }

    protected AbstractDenseMatrix(final DoubleBuffer buffer, final int[] shape) {
        super(buffer, shape);
        if (shape.length != 2) {
            throw new IllegalArgumentException("invalid shape for matrix");
        }
        this.fRows = shape[0];
        this.fColumns = shape[1];
    }

    public final double get(final int row, final int column) {
        return get(new int[]{row, column});
    }

    public final void set(final double value, final int row, final int column) {
        set(value, new int[]{row, column});
    }

    public final int getRows() {
        return fRows;
    }

    public final int rows() {
        return getRows();
    }

    public final int getColumns() {
        return fColumns;
    }

    public final int columns() {
        return getColumns();
    }
}
