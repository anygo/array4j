package com.googlecode.array4j;

import static com.googlecode.array4j.Indexing.sliceStart;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

public final class DenseArrayTest {
    @Test
    public void testZeros() {
        DenseArray arr;
        arr = DoubleArray.zeros(0);
        assertEquals(1287, arr.flags());
        // TODO numpy allocates something, but doesn't report it
//        assertEquals(0, arr.nbytes());

        DoubleArray.zeros(0, 0);
        DoubleArray.zeros(1);
        DoubleArray.zeros(1, 1);
        DoubleArray.zeros(3, 3);
        DoubleArray.zeros(0, 1);
        DoubleArray.zeros(1, 0);

        arr = DoubleArray.zeros(3, 3);
        assertEquals(1285, arr.flags());
        assertEquals(72, arr.nbytes());

        arr = DoubleArray.zeros();
        assertEquals(1285, arr.flags());
        assertEquals(8, arr.nbytes());
    }

    @Test
    public void testArange() {
        DenseArray arr;
        arr = DoubleArray.arange(12.0);
        assertEquals(9.0, arr.getDouble(9));
        assertEquals(8.0, arr.reshape(4, 3).getDouble(2, 2));
        assertEquals(10.0, arr.reshape(3, 4).getDouble(2, 2));
    }

    @Test
    public void testReshape() {
        DenseArray arr;
        arr = DoubleArray.zeros(4, 3);
        arr = arr.reshape(3, 4);
        assertNotNull(arr);
        assertTrue(Arrays.equals(new int[]{3, 4}, arr.shape()));
    }

    @Test
    public void testIndexing() {
        DenseArray arr;
        arr = DoubleArray.zeros(3, 3);
        arr = arr.get(sliceStart(1), sliceStart(1));
        assertNotNull(arr);
        assertTrue(Arrays.equals(new int[]{2, 2}, arr.shape()));
        // TODO check that we actually get the right data from the view
    }

    @Test
    public void testAddEquals() {
        final DenseArray arr1 = DoubleArray.arange(4).reshape(2, 2);
        assertTrue(arr1.isWriteable());
        assertEquals(0.0, arr1.getDouble(0, 0));
        assertEquals(1.0, arr1.getDouble(0, 1));
        assertEquals(2.0, arr1.getDouble(1, 0));
        assertEquals(3.0, arr1.getDouble(1, 1));

        final DenseArray arr2 = DoubleArray.arange(2);
        assertTrue(arr2.isWriteable());
        assertEquals(0.0, arr2.getDouble(0));
        assertEquals(1.0, arr2.getDouble(1));

        arr1.addEquals(arr2);
        assertEquals(0.0, arr1.getDouble(0, 0));
        assertEquals(2.0, arr1.getDouble(0, 1));
        assertEquals(2.0, arr1.getDouble(1, 0));
        assertEquals(4.0, arr1.getDouble(1, 1));
    }

    @Test
    public void testMultiplyEquals() {
        final DenseArray arr1 = DoubleArray.arange(4).reshape(2, 2);
        assertTrue(arr1.isWriteable());
        final DenseArray arr2 = DoubleArray.arange(2);
        arr1.multiplyEquals(arr2);
        assertEquals(0.0, arr1.getDouble(0, 0));
        assertEquals(1.0, arr1.getDouble(0, 1));
        assertEquals(0.0, arr1.getDouble(1, 0));
        assertEquals(3.0, arr1.getDouble(1, 1));
    }

    @Test
    public void testSquareEquals() {
        final DenseArray arr1 = DoubleArray.arange(4).reshape(2, 2);
        assertTrue(arr1.isWriteable());
        arr1.squareEquals();
        assertEquals(0.0, arr1.getDouble(0, 0));
        assertEquals(1.0, arr1.getDouble(0, 1));
        assertEquals(4.0, arr1.getDouble(1, 0));
        assertEquals(9.0, arr1.getDouble(1, 1));
    }

//    @Test
//    public void testAdd() {
//        // arr1 and arr2 have to be declared with <?> for add to work
//        final DoubleArray<?> arr1 = DenseDoubleArray.arange(4).reshape(2, 2);
//        final DoubleArray<?> arr2 = DenseDoubleArray.arange(2);
//        // TODO allowing this is probably unsafe, since the cast is checked
//        // against the erased type
//        final ByteArray<?> arr3 = arr1.add(arr2);
//    }
}
