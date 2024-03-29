package net.lunglet.hdf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataSet extends AbstractDs implements Comparable<DataSet> {
    private static final CloseAction CLOSE_ACTION = new CloseAction() {
        @Override
        public void close(final int id) {
            int err = H5Library.INSTANCE.H5Dclose(id);
            if (err < 0) {
                throw new H5DataSetException("H5Dclose failed");
            }
        }
    };

    private final Logger logger = LoggerFactory.getLogger(DataSet.class);

    DataSet(final int id) {
        super(id, CLOSE_ACTION);
        logger.debug("Created {} [id={}]", getName(), getId());
    }

    @Override
    public int compareTo(final DataSet o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public DataSpace getSpace() {
        final int dataspaceId;
        synchronized (H5Library.INSTANCE) {
            dataspaceId = H5Library.INSTANCE.H5Dget_space(getId());
            if (dataspaceId < 0) {
                throw new H5DataSetException("H5Dget_space failed");
            }
        }
        return new DataSpace(dataspaceId, true);
    }

    @Override
    public long getStorageSize() {
        synchronized (H5Library.INSTANCE) {
            return H5Library.INSTANCE.H5Dget_storage_size(getId());
        }
    }

    @Override
    public DataType getType() {
        final int typeId;
        synchronized (H5Library.INSTANCE) {
            typeId = H5Library.INSTANCE.H5Dget_type(getId());
            if (typeId < 0) {
                throw new H5DataSetException("H5Aget_type failed");
            }
        }
        return DataType.createTypeFromId(typeId);
    }

    @Override
    public void read(final Buffer buf, final DataType memType) {
        read(buf, memType, DataSpace.ALL, DataSpace.ALL);
    }

    public void read(final Buffer buf, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace) {
        read(buf, memType, memSpace, fileSpace, DataSetMemXferPropList.DEFAULT);
    }

    public void read(final Buffer buf, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace,
            final DataSetMemXferPropList xferPlist) {
        checkBuffer(buf, memType, memSpace, fileSpace);
        final int memTypeId = memType.getId();
        final int memSpaceId = memSpace.getId();
        final int fileSpaceId = fileSpace.getId();
        final int xferPlistId = xferPlist.getId();
        synchronized (H5Library.INSTANCE) {
            int err = H5Library.INSTANCE.H5Dread(getId(), memTypeId, memSpaceId, fileSpaceId, xferPlistId, buf);
            if (err < 0) {
                throw new H5DataSetException("H5Dread failed", true);
            }
        }
    }

    public void read(final byte[] arr, final DataType memType) {
        read(arr, memType, DataSpace.ALL, DataSpace.ALL);
    }

    public void read(final byte[] arr, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace) {
        read(arr, memType, memSpace, fileSpace, DataSetMemXferPropList.DEFAULT);
    }

    public void read(final byte[] arr, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace,
            final DataSetMemXferPropList xferPlist) {
        read(ByteBuffer.wrap(arr), memType, memSpace, fileSpace, xferPlist);
    }

    @Override
    public String toString() {
        if (isOpen()) {
            return "DataSet[name=" + getName() + "]";
        } else {
            return "DataSet[invalid]";
        }
    }

    @Override
    public void write(final Buffer buf, final DataType memType) {
        write(buf, memType, DataSpace.ALL, DataSpace.ALL);
    }

    public void write(final Buffer buf, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace) {
        write(buf, memType, memSpace, fileSpace, DataSetMemXferPropList.DEFAULT);
    }

    public void write(final Buffer buf, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace,
            final DataSetMemXferPropList xferPlist) {
        checkBuffer(buf, memType, memSpace, fileSpace);
        final int memTypeId = memType.getId();
        final int memSpaceId = memSpace.getId();
        final int fileSpaceId = fileSpace.getId();
        final int xferPlistId = xferPlist.getId();
        synchronized (H5Library.INSTANCE) {
            int err = H5Library.INSTANCE.H5Dwrite(getId(), memTypeId, memSpaceId, fileSpaceId, xferPlistId, buf);
            if (err < 0) {
                throw new H5DataSetException("H5Dwrite failed");
            }
        }
    }

    public void write(final byte[] arr, final DataType memType) {
        write(arr, memType, DataSpace.ALL, DataSpace.ALL);
    }

    public void write(final byte[] arr, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace) {
        write(arr, memType, memSpace, fileSpace, DataSetMemXferPropList.DEFAULT);
    }

    public void write(final byte[] arr, final DataType memType, final DataSpace memSpace, final DataSpace fileSpace,
            final DataSetMemXferPropList xferPlist) {
        write(ByteBuffer.wrap(arr), memType, memSpace, fileSpace, xferPlist);
    }
}
