package net.lunglet.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import net.lunglet.array4j.Constants;
import net.lunglet.array4j.Storage;

public final class BufferUtils {
    static interface BufferUtilsLibrary extends Library {
        BufferUtilsLibrary INSTANCE = (BufferUtilsLibrary) Native.loadLibrary("array4j", BufferUtilsLibrary.class);

        NativeLong array4j_addressof(Buffer buffer);
    }

    public static ByteBuffer createAlignedBuffer(final int size, final int alignment) {
        if (alignment < 1) {
            throw new IllegalArgumentException();
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(size + alignment - 1);
        long addr = BufferUtilsLibrary.INSTANCE.array4j_addressof(buffer).longValue();
        int mod = (int) (addr % alignment);
        int offset = mod > 0 ? alignment - mod : 0;
        ByteBuffer nativeBuffer = buffer.order(ByteOrder.nativeOrder());
        ByteBuffer slicedBuffer = ((ByteBuffer) nativeBuffer.position(offset)).slice();
        Buffer limitedBuffer = slicedBuffer.limit(size);
        return (ByteBuffer) limitedBuffer;
    }

    public static FloatBuffer createComplexFloatBuffer(final int size, final Storage storage) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        if (storage.equals(Storage.DIRECT)) {
            final ByteBuffer buffer = createAlignedBuffer(2 * size * Constants.FLOAT_BYTES, 16);
            buffer.order(ByteOrder.nativeOrder());
            return buffer.asFloatBuffer();
        }
        return FloatBuffer.allocate(2 * size);
    }

    public static DoubleBuffer createDoubleBuffer(final int size, final Storage storage) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        if (storage.equals(Storage.DIRECT)) {
            final ByteBuffer buffer = createAlignedBuffer(size * Constants.DOUBLE_BYTES, 16);
            buffer.order(ByteOrder.nativeOrder());
            return buffer.asDoubleBuffer();
        }
        return DoubleBuffer.allocate(size);
    }

    public static FloatBuffer createFloatBuffer(final int size, final Storage storage) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        if (storage.equals(Storage.DIRECT)) {
            final ByteBuffer buffer = createAlignedBuffer(size * Constants.FLOAT_BYTES, 16);
            buffer.order(ByteOrder.nativeOrder());
            return buffer.asFloatBuffer();
        }
        return FloatBuffer.allocate(size);
    }

    public static int getBytesCapacity(final Buffer buf) {
        if (buf instanceof ByteBuffer) {
            return buf.capacity();
        } else if (buf instanceof FloatBuffer) {
            return Constants.FLOAT_BYTES * buf.capacity();
        } else if (buf instanceof DoubleBuffer) {
            return Constants.DOUBLE_BYTES * buf.capacity();
        } else {
            // TODO add other buffers above
            throw new AssertionError();
        }
    }

    private BufferUtils() {
    }
}
