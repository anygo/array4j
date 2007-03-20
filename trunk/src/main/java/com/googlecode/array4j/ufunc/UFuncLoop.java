package com.googlecode.array4j.ufunc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

import com.googlecode.array4j.ArrayDescr;
import com.googlecode.array4j.ArrayType;
import com.googlecode.array4j.DenseArray;
import com.googlecode.array4j.Flags;
import com.googlecode.array4j.ScalarKind;

public final class UFuncLoop implements Iterable<MultiArrayIterator> {
    private enum LoopMethod {
        NO_UFUNCLOOP,
        ONE_UFUNCLOOP,
        NOBUFFER_UFUNCLOOP,
        BUFFER_UFUNCLOOP;
    }

    private int fIndex;

    private final UFunc fUfunc;

    /* Buffers for the loop */
    private final ByteBuffer[] fBuffer;

    private final MultiArrayIterator mit;

    private final Object[] fCast;

    /* The loop caused notimplemented */
    private boolean fNotImplemented;

    private boolean fFirst;

    private final int fBufSize;

    private final int fErrorMask;

    private final ErrorHandler fErrObj;

    private final boolean[] fNeedBuffer;

    private final int[] fSteps;

    private LoopMethod fMeth;

    public UFuncLoop(final UFunc ufunc, final DenseArray[] args) {
        this(ufunc, args, Error.DEFAULT_ERROR);
    }

    public UFuncLoop(final UFunc ufunc, final DenseArray[] args, final Error extobj) {
        this.fIndex = 0;
        this.fUfunc = ufunc;
        final int nargs = nargs();
        this.fBuffer = new ByteBuffer[nargs];
        fBuffer[0] = null;

        // the multi iterator contains the iterators
        this.mit = new MultiArrayIterator(nargs);
        this.fCast = new Object[nargs];
        // TODO fCast contains PyArray_VectorUnaryFuncs

        this.fNotImplemented = false;
        this.fFirst = true;

        this.fBufSize = extobj.getBufSize();
        this.fErrorMask = extobj.getErrorMask();
        this.fErrObj = extobj.getErrorHandler();

        // initialize other arrays (these have a static size in NumPy)
        this.fNeedBuffer = new boolean[nargs];
        this.fSteps = new int[nargs];

        constructArrays(args);
    }

    public void execute() {
        if (fNotImplemented) {
            throw new UnsupportedOperationException();
        }

        switch (fMeth) {
        case ONE_UFUNCLOOP:
            throw new UnsupportedOperationException();
        case NOBUFFER_UFUNCLOOP:
            for (MultiArrayIterator it : this) {
                // TODO update bufptr from iter
                // TODO call function
            }
            break;
        case BUFFER_UFUNCLOOP:
            throw new UnsupportedOperationException();
        default:
            throw new AssertionError();
        }
    }

    public Iterator<MultiArrayIterator> iterator() {
        return mit;
    }

    private int constructArrays(final DenseArray[] args) {
        // TODO if input and output arrays don't all have dtypes with the same
        // kernel (i. e. the same type of underlying buffer), use some kind of
        // heuristic to decide if we should copy the Java arrays or the C arrays
        // and call Java code or C code

        /* Check number of arguments */
        final int nargs = args.length;
        final int nin = nin();
        if (nargs < nin || nargs > nargs()) {
            throw new IllegalArgumentException("invalid number of arguments");
        }

        final DenseArray[] mps = new DenseArray[nargs()];
        final ArrayType[] argtypes = new ArrayType[mps.length];
        final ScalarKind[] scalars = new ScalarKind[mps.length];
        boolean allscalars = true;

        /* Get each input argument */
        boolean flexible = false;
        boolean object = false;
        for (int i = 0; i < nin; i++) {
            if (false) {
            } else {
                // TODO set context to null
            }
            // TODO mps[i] = PyArray_FromAny(obj, NULL, 0, 0, 0, context)

            mps[i] = args[i];
            argtypes[i] = mps[i].dtype().type();
            if (!flexible && argtypes[i].isFlexible()) {
                flexible = true;
            }
            if (!object && argtypes[i].isObject()) {
                object = true;
            }

            /* Scalars are 0-dimensional arrays at this point */
            if (mps[i].ndim() > 0) {
                scalars[i] = ScalarKind.NOSCALAR;
                allscalars = false;
            }
        }

        if (flexible && !object) {
            fNotImplemented = true;
            return nargs();
        }

        /* If everything is a scalar, then use normal coercion rules */
        if (allscalars) {
            for (int i = 0; i < nin; i++) {
                scalars[i] = ScalarKind.NOSCALAR;
            }
        }

        /* Select an appropriate function for these argument types. */
        final Object typetup = null;
        fUfunc.selectTypes(argtypes, scalars, typetup);

        if (argtypes[1].equals(ArrayType.OBJECT) && nin() == 2 && nout() == 1) {
            throw new UnsupportedOperationException();
        }

        /*
         * Create copies for some of the arrays if they are small enough and not
         * already contiguous.
         */
        createCopies(argtypes, mps);

        /* Create Iterators for the Inputs */
        for (int i = 0; i < nin; i++) {
            mit.createArrayIterator(i, mps[i]);
        }

        /* Broadcast the result over the input arguments. */
        mit.broadcast(nin);

        /* Get any return arguments */
        for (int i = nin; i < nargs; i++) {
            mps[i] = args[i];
            final int nd = mit.ndim();
            if (mps[i].ndim() != nd || !Arrays.equals(mps[i].shape(), mit.shape())) {
                throw new IllegalArgumentException("invalid return array shape");
            }
            if (!mps[i].isWriteable()) {
                throw new IllegalArgumentException("return array is not writeable");
            }
        }

        /* construct any missing return arrays and make output iterators */
        for (int i = nin; i < nargs; i++) {
            if (mps[i] == null) {
                mps[i] = null;
                // TODO call something like the following constructor:
//                PyArray_New(PyTypeObject *subtype, int nd, intp *dims, int type_num,
//                        intp *strides, void *data, int itemsize, int flags,
//                        PyObject *obj)
            } else {
                /*
                 * reset types for outputs that are equivalent -- no sense
                 * casting uselessly
                 */
                if (!mps[i].dtype().type().equals(argtypes[i])) {
                    final ArrayDescr ntype = mps[i].dtype();
                    final ArrayDescr atype = ArrayDescr.fromType(argtypes[i]);
                    if (ntype.isEquivalent(atype)) {
                        argtypes[i] = ntype.type();
                    }
                }

                if (!mps[i].dtype().type().equals(argtypes[i]) || !mps[i].isBehavedRo()) {
                    if (mit.size() < fBufSize) {
                        /*
                         * Copy the array to a temporary copy and set the
                         * UPDATEIFCOPY flag
                         */
                        final ArrayDescr ntype = ArrayDescr.fromType(argtypes[i]);
                        mps[i] = DenseArray.fromArray(mps[i], ntype,
                                Flags.or(Flags.FORCECAST, Flags.ALIGNED, Flags.UPDATEIFCOPY));
                    }
                }
            }

            mit.createArrayIterator(i, mps[i]);
        }

        /*
         * If any of different type, or misaligned or swapped then must use
         * buffers
         */
//        fBufCnt = 0;
//        obj = false

        /* Determine looping method needed */
        fMeth = LoopMethod.NO_UFUNCLOOP;

        if (mit.size() == 0) {
            return nargs;
        }

        for (int i = 0; i < nargs; i++) {
            fNeedBuffer[i] = false;
            if (!argtypes[i].equals(mps[i].dtype().type()) || !mps[i].isBehavedRo()) {
                fMeth = LoopMethod.BUFFER_UFUNCLOOP;
                fNeedBuffer[i] = true;
            }
            // TODO enable this to support object arrays
//            if (!obj && mps[i].dtype().type().equals(ArrayType.OBJECT)) {
//                obj = true;
//            }
        }

        if (fMeth.equals(LoopMethod.NO_UFUNCLOOP)) {
            fMeth = LoopMethod.ONE_UFUNCLOOP;

            /* All correct type and BEHAVED. Check for non-uniform stridedness. */
            for (int i = 0; i < nargs; i++) {
                final ArrayIterator it = mit.getIterator(i);
                if (!it.isContiguous()) {
                    // may still have uniform stride if (broadcasted result) <= 1-d
                    if (mps[i].ndim() != 0 && it.ndimMinus1() > 0) {
                        fMeth = LoopMethod.NOBUFFER_UFUNCLOOP;
                        break;
                    }
                }
            }
            if (fMeth.equals(LoopMethod.ONE_UFUNCLOOP)) {
                for (int i = 0; i < nargs; i++) {
                    fBuffer[i] = mps[i].getData();
                }
            }
        }

        /* Fill in steps */
        if (!fMeth.equals(LoopMethod.ONE_UFUNCLOOP)) {
            final int ldim = mit.optimizeAxis();
            final int maxdim = mit.shape(ldim);
            // TODO add these fields
//            fBufCnt = maxdim;
//            fLastDim = ldim;

            /* Set the steps to the strides in longest dimension */
            for (int i = 0; i < mit.numiter(); i++) {
                fSteps[i] = mit.getIterator(i).strides(ldim);
            }

            /*
             * fix up steps where we will be copying data to buffers and
             * calculate the innerloops and leftover values -- if step size is
             * already zero that is not changed...
             */
            if (fMeth.equals(LoopMethod.BUFFER_UFUNCLOOP)) {
                // TODO set these loop fields
//                leftover = maxdim % fBufSize;
//                ninnerloops = (maxdim / fBufSize) + 1;
                for (int i = 0; i < nargs; i++) {
                    if (fNeedBuffer[i] && fSteps[i] != 0) {
                        fSteps[i] = mps[i].dtype().itemSize();
                    }
                    /* These are changed later if casting is needed */
                }
            }
        } else {
            /* uniformly-strided case ONE_UFUNCLOOP */
            for (int i = 0; i < nargs; i++) {
                if (mps[i].size() == 1) {
                    fSteps[i] = 0;
                } else {
                    fSteps[i] = mps[i].strides(mps[i].ndim() - 1);
                }
            }
        }

        /* Finally, create memory for buffers if we need them */

        /*
         * buffers for scalars are specially made small -- scalars are not
         * copied multiple times
         */
        if (fMeth.equals(LoopMethod.BUFFER_UFUNCLOOP)) {
            // keeps track of bytes to allocate
            int cntcast = 0;
            int cnt = 0;
            int scntcast = 0;
            int scnt = 0;

            /* compute the element size */
            for (int i = 0; i < nargs; i++) {
                if (!fNeedBuffer[i]) {
                    continue;
                }
                if (!argtypes[i].equals(mps[i].dtype().type())) {
                    final ArrayDescr descr = ArrayDescr.fromType(argtypes[i]);
                    if (fSteps[i] != 0) {
                        cntcast += descr.itemSize();
                    } else {
                        scntcast += descr.itemSize();
                    }
                    if (i < nin) {
                        // TODO get cast function
                        fCast[i] = null;
                    } else {
                        // TODO get cast function
                        fCast[i] = null;
                    }
                    if (fCast[i] == null) {
                        // TODO signal some kind of error here
                        throw new UnsupportedOperationException();
                    }
                }
                // TODO set loop field
//                swap[i] = mps[i].isNotSwapped();
                if (fSteps[i] != 0) {
                    cnt += mps[i].dtype().itemSize();
                } else {
                    scnt += mps[i].dtype().itemSize();
                }
            }
            final int scbufsize = 4 * 8;
            final int memsize = fBufSize * (cnt + cntcast) + scbufsize * (scnt + scntcast);

//            if (fObj) {
//            }

            for (int i = 0; i < nargs; i++) {
                if (fNeedBuffer[i]) {
                    continue;
                }

                // TODO need a few lines of code here

                if (fCast[i] != null) {
                    final ArrayDescr descr = ArrayDescr.fromType(argtypes[i]);
                    if (fSteps[i] != 0) {
//                        fSteps[i] = oldsize;
                    }
                } else {
                    // TODO looks like we need bufptr and buffer
                    fBuffer[i] = fBuffer[i];
                }
                if (false) {
                    if (argtypes[i].equals(ArrayType.OBJECT)) {
                        // TODO set objfunc flag to true
                    }
                }
            }
        }

        return nargs;
    }

    private void createCopies(final ArrayType[] argtypes, final DenseArray[] mps) {
        final int nin = nin();
        for (int i = 0; i < nin; i++) {
            final int size = mps[i].size();
            /*
             * if the type of mps[i] is equivalent to argtypes[i] then set
             * arg_types[i] equal to type of mps[i] for later checking....
             */
            if (!mps[i].dtype().type().equals(argtypes[i])) {
                final ArrayDescr ntype = mps[i].dtype();
                final ArrayDescr atype = ArrayDescr.fromType(argtypes[i]);
                if (atype.isEquivalent(ntype)) {
                    argtypes[i] = ntype.type();
                }
            }
            if (size < fBufSize) {
                if (!mps[i].isBehavedRo() || mps[i].dtype().type() != argtypes[i]) {
                    final ArrayDescr ntype = mps[i].dtype();
                    mps[i] = DenseArray.fromArray(mps[i], ntype, Flags.or(Flags.FORCECAST, Flags.ALIGNED));
                }
            }
        }
    }

    public int nin() {
        return fUfunc.nin();
    }

    public int nout() {
        return fUfunc.nout();
    }

    public int nargs() {
        return fUfunc.nargs();
    }
}
