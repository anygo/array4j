package com.googlecode.array4j.dense;

import com.googlecode.array4j.Matrix;
import com.googlecode.array4j.Order;
import com.googlecode.array4j.Storage;
import java.nio.Buffer;
import java.util.List;

public interface DenseMatrix extends Matrix {
    /** {@inheritDoc} */
    DenseVector asVector();

    /** {@inheritDoc} */
    DenseVector column(int column);

    /** {@inheritDoc} */
    Iterable<? extends DenseVector> columnsIterator();

    /** {@inheritDoc} */
    List<? extends DenseVector> columnsList();

    /** Get data buffer. */
    Buffer data();

    int leadingDimension();

    /** Get the offset into the data buffer of the first element. */
    int offset();

    /** Get the storage order (row-major or column-major). */
    Order order();

    /** {@inheritDoc} */
    DenseVector row(int row);

    Iterable<? extends DenseVector> rowsIterator();

    /** Get the storage type (heap or direct). */
    Storage storage();

    /** Get the stride between elements. */
    int stride();

    /** {@inheritDoc} */
    DenseMatrix transpose();
}
