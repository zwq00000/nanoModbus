package com.redriver.modbus;

/**
 * Created by zwq00000 on 2014/7/2.
 */
public class CoilStatus {

    private static final int SLAVE_ID_POS = 0;
    private static final int FUNC_CODE_POS = 1;
    private static final short MAX_COILS_COUNT = 0X7D0;
    private final byte[] mStatus;
    private final short mStartNum;
    /**
     * 寄存器数量
     */
    private final short mCoilCount;

    public CoilStatus(short coilsCount) {
        this((short) 0, coilsCount);
    }

    public CoilStatus(int coilsCount) {
        this((short)coilsCount);
    }

    /**
     * 线圈寄存器状态
     *
     * @param startNum   寄存器开始编号
     * @param coilsCount 寄存器数量
     */
    public CoilStatus(short startNum, short coilsCount) {
        assert(startNum>=0);
        assert((coilsCount+startNum) < MAX_COILS_COUNT);
        this.mCoilCount = coilsCount;
        mStartNum = startNum;
        int statusSize = (coilsCount + 7) / 8;
        mStatus = new byte[statusSize];
        setAll(false);
    }

    /**
     * 获取线圈寄存器数量
     *
     * @return
     */
    public short getCount() {
        return mCoilCount;
    }

    /**
     * 寄存器组 起始编号
     *
     * @return
     */
    public short getStartNum() {
        return mStartNum;
    }

    /**
     * 获取 寄存器状态数据帧 占用字节数
     * 包括 起始地址 + 数量 + 字节数 + 状态字节数组
     *
     * @return
     */
    public int bytesSize() {
        return 2 + 2 + 1 + mStatus.length;
    }

    /**
     * Used internally for setting the value of the coil.
     *
     * @param offset 偏移量
     * @param status 寄存器状态
     */
    public void setCoil(int offset, boolean status) {
        if (offset < 0 || offset >= mCoilCount) {
            throw new IndexOutOfBoundsException("offset 超出寄存器范围");
        }
        int byteIndex = offset / 8;
        int offsetInByte = offset % 8;
        mStatus[byteIndex] = ByteUtils.setBit(this.mStatus[byteIndex], offsetInByte, status);
    }

    /**
     * 关闭指定寄存器
     *
     * @param offset
     */
    public void close(int offset) {
        this.setCoil(offset, false);
    }

    /**
     * 关闭指定寄存器
     *
     * @param offset
     */
    public void open(int offset) {
        this.setCoil(offset, true);
    }

    public void setAll(boolean status) {
        for (int i = 0; i < this.mCoilCount; i++) {
            setCoil(i, status);
        }
    }

    /**
     * Returns the current value of the coil for the given offset.
     *
     * @param offset
     * @return the value of the coil
     */
    public boolean getCoil(int offset) {
        if (offset < 0 || offset >= mCoilCount) {
            throw new IndexOutOfBoundsException("offset 超出寄存器范围");
        }
        int byteIndex = offset / 8;
        byte group = this.mStatus[byteIndex];
        int offsetInByte = offset % 8;
        byte mask = (byte) (0x1 << offsetInByte);
        return (group &= mask) == mask;
    }

    public byte[] toBytes() {
        byte[] result = new byte[2 + 2 + 1 + mStatus.length];
        ByteUtils.setBytes(result, 0, this.getStartNum());
        ByteUtils.setBytes(result, 2, this.getCount());
        result[4] = (byte) mStatus.length;
        ByteUtils.setBytes(result, 5, mStatus);
        return result;
    }

    public void readResponse(byte[] frameBuffer, int length) {
        if (frameBuffer == null) {
            throw new NullPointerException("response Frame is not been null");
        }
        byte funcCode = frameBuffer[FUNC_CODE_POS];
        switch (funcCode) {
            case  FunctionCode.READ_COILS:
                readReadCoilsResponse(frameBuffer, length);
                break;
            case  FunctionCode.WRITE_COIL:
                readWriteCoilResponse(frameBuffer, length);
                break;
            case FunctionCode.WRITE_COILS:
                readWriteCoilsResponse(frameBuffer, length);
                break;
        }
    }

    /**
     * {@link FunctionCode#WRITE_COILS}
     * 功能码 1 BYTE 0X0F
     * 设置起始地址 2 BYTE 0X0000 TO 0XFFFF
     * 设置长度  2 BYTE  0X0000 TO 0X7B0
     *
     * @param frameBuffer
     * @param length
     */
    private void readWriteCoilsResponse(byte[] frameBuffer, int length) {
        short coilStartNum = ByteUtils.bytesToShort(frameBuffer, 2);
        short byteCount = ByteUtils.bytesToShort(frameBuffer, 4);
        if (byteCount != this.getCount()) {
            //todo:容量不符
            throw new IllegalArgumentException("状态容量不符 bytes=" + byteCount + "\tstatus.length:" + mStatus.length);
        }
    }

    /**
     * 读取 01 读取线圈状态 响应
     * 功能码 1  BYTE 0X01
     * 字节计数 1  BYTE  N
     * 线圈状态  n  BYTE  n  =N or N+1
     *
     * @param frameBuffer
     * @param length
     */
    private void readReadCoilsResponse(byte[] frameBuffer, int length) {
        byte byteCount = frameBuffer[2];
        if (byteCount != mStatus.length) {
            //todo:容量不符
            throw new IllegalArgumentException("状态容量不符 bytes=" + byteCount + "\tstatus.length:" + mStatus.length);
        }
        for (int i = 0; i < byteCount; i++) {
            mStatus[i] = frameBuffer[i + 3];
        }
    }

    /**
     * {@See ModbusFrame.FunctionCode.WRITE_COIL}
     * 功能码 1 BYTE 0X05
     * 设置地址 2 BYTE 0X0000 TO 0XFFFF
     * 设置内容  2 BYTE  0x0000 OR 0XFF00
     *
     * @param frameBuffer
     * @param length
     */
    private void readWriteCoilResponse(byte[] frameBuffer, int length) {
        short coilNum = ByteUtils.bytesToShort(frameBuffer, 2);
        this.setCoil(coilNum, frameBuffer[4] == (byte) 0xff);
    }
}
