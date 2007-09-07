package com.googlecode.array4j.dense;

import static org.junit.Assert.assertEquals;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public final class FloatDenseUtilsTest {
    @Parameters
    public static Collection<?> data() {
        return Arrays.asList(new Object[][]{{Orientation.ROW}, {Orientation.COLUMN}});
    }

    private final Orientation orientation;

    public FloatDenseUtilsTest(final Orientation orientation) {
        this.orientation = orientation;
    }

    @Test
    public void testSubMatrixColumns() {
        for (int rows = 0; rows < 5; rows++) {
            for (int columns = 0; columns < 8; columns++) {
                FloatDenseMatrix x = new FloatDenseMatrix(rows, columns, orientation, Storage.HEAP);
                for (int j = 0; j < columns; j++) {
                    FloatDenseMatrix y = FloatDenseUtils.subMatrixColumns(x, 0, j);
                    assertEquals(x.rows(), y.rows());
                    assertEquals(j, y.columns());
                    for (int k = 0; k < j; k++) {
                        for (int m = 0; m < x.rows(); m++) {
                            assertEquals(x.get(m, k), y.get(m, k), 0);
                        }
                        assertEquals(x.column(k), y.column(k));
                    }
                }
            }
        }
    }
}
