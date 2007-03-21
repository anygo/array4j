package com.googlecode.array4j.ufunc;

import java.nio.ByteBuffer;

import com.googlecode.array4j.ArrayFunctions;
import com.googlecode.array4j.ArrayType;
import com.googlecode.array4j.kernel.Interface;

final class MultiplyUFunc extends AbstractUFunc {
    private static final int NIN = 2;

    private static final int NOUT = 1;

    private static final Signature[] SIGNATURES = {
        new Signature(new ArrayType[]{ArrayType.DOUBLE, ArrayType.DOUBLE},
            new ArrayType[]{ArrayType.DOUBLE}, Interface.defaultKernel().getDoubleFunctions())
    };

    public MultiplyUFunc() {
        super(null, SIGNATURES, NIN, NOUT, Identity.ONE);
    }

    @Override
    public void call(final ArrayFunctions functions, final ByteBuffer[] bufptr, final int[] dimensions,
            final int[] steps, final Object funcdata) {
        functions.multiply(bufptr, dimensions, steps, funcdata);
    }
}
