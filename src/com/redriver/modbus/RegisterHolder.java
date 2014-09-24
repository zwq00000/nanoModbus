package com.redriver.modbus;


/**
 * Created by zwq00000 on 2014/7/10.
 */
public class RegisterHolder {
    /**
     * 保持寄存器状态
     */
    public final short[] mStatus;
    /**
     * 起始位置
     */
    private final short mStartNum;

    public RegisterHolder(short startNum, short regCount) {
        this.mStartNum = startNum;
        this.mStatus = new short[regCount];
    }

    public RegisterHolder(int startNum, int regCount) {
        this((short)startNum,(short)regCount);
    }

    public void setValue(int offset,short value){
        if(offset<0 || offset>=mStatus.length){
            throw new IndexOutOfBoundsException(String.format("offset 超出范围 [0,%d]",mStatus.length-1));
        }
        mStatus[offset] = value;
    }

    public byte[] toBytes(){
        byte[] bytes = new byte[2 + 2 + 1 + this.mStatus.length * 2];
        ByteUtils.setBytes(bytes,0,mStartNum);
        ByteUtils.setBytes(bytes,2,(short)mStatus.length);
        bytes[4] = (byte)(mStatus.length*2);
        for (int i=0;i<mStatus.length;i++){
            ByteUtils.setBytes(bytes,(i*2)+5,mStatus[i]);
        }
        return bytes;
    }

    public int size() {
        return 2 + 2 + 1 + this.mStatus.length * 2;
    }

    public short getStartNum() {
        return mStartNum;
    }

    public short getCount() {
        return (short) mStatus.length;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("HoldingRegister[");
        for (int i=0;i<mStatus.length-1;i++){
            builder.append(Integer.toString(mStartNum +i));
            builder.append(":");
            builder.append(mStatus[i]);
            builder.append(", ");
        }
        builder.append(Integer.toString(mStartNum + mStatus.length-1));
        builder.append(":");
        builder.append(mStatus[mStatus.length-1]);
        builder.append(']');
        return builder.toString();
    }

    public void setValue(int offset, int value) {
        setValue((short)offset,(short)value);
    }

    /**
     * 所有寄存器置 0
     */
    public void reset(){
        for (int i=0;i<mStatus.length;i++){
            mStatus[i] = 0;
        }
    }
}
