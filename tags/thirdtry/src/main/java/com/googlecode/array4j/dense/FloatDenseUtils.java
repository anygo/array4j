package com.googlecode.array4j.dense;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.googlecode.array4j.Constants;
import com.googlecode.array4j.Orientation;
import com.googlecode.array4j.Storage;

public final class FloatDenseUtils {
    private FloatDenseUtils() {
    }

    public static FloatDenseMatrix readHTK(final String filename) throws IOException {
        FileChannel channel = new FileInputStream(filename).getChannel();
        ByteBuffer buf = channel.map(MapMode.READ_ONLY, 0, channel.size()).order(ByteOrder.BIG_ENDIAN);
        int nsamples = buf.getInt(0);
        int period = buf.getInt(4);
        int size = buf.getShort(8) & 0xffff;
        int kind = buf.getShort(10) & 0xffff;
        FloatBuffer data = ((ByteBuffer) buf.position(12)).asFloatBuffer();
        if (data.remaining() != nsamples * size / Constants.FLOAT_BYTES) {
            throw new IOException();
        }
        int columns = data.remaining() / nsamples;
        return new FloatDenseMatrix(data, nsamples, columns, 0, 1, Orientation.ROW);
    }

    public static FloatDenseMatrix createMatrix(final float[][] values) {
        return createMatrix(values, Orientation.DEFAULT, Storage.DEFAULT_FOR_DENSE);
    }

    public static FloatDenseMatrix createMatrix(float[][] values, Orientation orientation, Storage storage) {
        int rows = values.length;
        int columns = rows > 0 ? values[0].length : 0;
        FloatDenseMatrix matrix = new FloatDenseMatrix(rows, columns, orientation, storage);
        for (int i = 0; i < rows; i++) {
            if (values[i].length != columns) {
                throw new IllegalArgumentException();
            }
            for (int j = 0; j < columns; j++) {
                matrix.set(i, j, values[i][j]);
            }
        }
        return matrix;
    }

    public static FloatDenseMatrix arange(final int rows, final int columns, final Orientation orientation,
            final Storage storage) {
        FloatDenseMatrix matrix = new FloatDenseMatrix(rows, columns, orientation, storage);
        for (int i = 0; i < matrix.size(); i++) {
            matrix.set(i, (float) i + 1);
        }
        return matrix;
    }
}