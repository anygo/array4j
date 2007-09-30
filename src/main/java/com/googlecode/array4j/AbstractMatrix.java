package com.googlecode.array4j;

import com.googlecode.array4j.util.AssertUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;

public abstract class AbstractMatrix<M extends Matrix<M, V>, V extends Vector<V>> extends AbstractArray<M> implements
        Matrix<M, V> {
    protected static int vectorColumns(final int size, final Orientation orientation) {
        AssertUtils.checkArgument(size >= 0);
        if (orientation.equals(Orientation.ROW)) {
            return size;
        } else {
            if (size == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    protected static int vectorRows(final int size, final Orientation orientation) {
        AssertUtils.checkArgument(size >= 0);
        if (orientation.equals(Orientation.COLUMN)) {
            return size;
        } else {
            if (size == 0) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    protected final AbstractMatrix<?, ?> base;

    protected final int columns;

    protected final int rows;

    public AbstractMatrix(final AbstractMatrix<?, ?> base, final int rows, final int columns) {
        super(new int[]{rows, columns});
        AssertUtils.checkArgument(rows >= 0);
        AssertUtils.checkArgument(columns >= 0);
        this.rows = rows;
        this.columns = columns;
        this.base = base;
    }

    protected final void checkColumnIndex(final int column) {
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException(String.format("Column index out of bounds [0,%d): %d", columns, column));
        }
    }

    protected final void checkColumnVector(final Vector<?> vector) {
        if (vector.length() != rows) {
            throw new IllegalArgumentException();
        }
    }

    protected final void checkRowIndex(final int row) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException(String.format("Row index out of bounds [0,%d): %d", rows, row));
        }
    }

    protected final void checkRowVector(final Vector<?> vector) {
        if (vector.length() != columns) {
            throw new IllegalArgumentException("vector with length " + columns + " required (length is "
                    + vector.length() + ")");
        }
    }

    public final int columns() {
        return columns;
    }

    public final Iterable<V> columnsIterator() {
        return new Iterable<V>() {
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    private int column = 0;

                    public boolean hasNext() {
                        return column < columns;
                    }

                    public V next() {
                        return column(column++);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public final List<V> columnsList() {
        List<V> columnsList = new ArrayList<V>();
        for (V column : columnsIterator()) {
            columnsList.add(column);
        }
        return columnsList;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof AbstractMatrix)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AbstractMatrix<?, ?> other = (AbstractMatrix<?, ?>) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(rows, other.rows).append(columns,
                other.columns).isEquals();
    }

    public final boolean isSquare() {
        return rows == columns;
    }

    public final int rows() {
        return rows;
    }

    public final Iterable<V> rowsIterator() {
        return new Iterable<V>() {
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    private int row = 0;

                    public boolean hasNext() {
                        return row < rows;
                    }

                    public V next() {
                        return row(row++);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
