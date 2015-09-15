package com.redriver.modbus;


/**
 * Created by zwq00000 on 2014/7/10.
 */
public class RegisterHolder implements WriteableHolder<Short> {

  /**
   * 保持寄存器状态
   */
  public final short[] mStatus;
  /**
   * 起始位置
   */
  private final short startNum;
  /**
   * 从站Id
   */
  private byte slaveId;

  public RegisterHolder(byte slaveId, int regCount) {
    this(slaveId, (short) 0, (short) regCount);
  }

  public RegisterHolder(byte slaveId,short startNum , int regCount) {
    this.startNum = startNum;
    this.slaveId = slaveId;
    this.mStatus = new short[regCount];
  }

  /**
   * 设置 寄存器 数值
   * @param offset
   * @param value
   */
  @Override
  public void setValue(int offset,Short value) {
    if (offset < 0 || offset >= mStatus.length) {
      throw new IndexOutOfBoundsException(String.format("offset 超出范围 [0,%d]", mStatus.length - 1));
    }
    mStatus[offset] = value;
  }

  /**
   * 获取指定位置的数值
   */
  @Override
  public Short getValue(int offset) {
    if (offset < 0 || offset >= mStatus.length) {
      throw new IndexOutOfBoundsException(String.format("offset 超出范围 [0,%d]", mStatus.length - 1));
    }
    return mStatus[offset];
  }

  /**
   * 获取从站Id    {@value 1~255}
   */
  @Override
  public byte getSlaveId() {
    return slaveId;
  }

  /**
   * 返回全部数据
   * @return
   */
  @Override
  public byte[] toBytes() {
    byte[] bytes = new byte[2 + 2 + 1 + this.mStatus.length * 2];
    ByteUtils.setBytes(bytes, 0, startNum);
    ByteUtils.setBytes(bytes, 2, (short) mStatus.length);
    bytes[4] = (byte) (mStatus.length * 2);
    for (int i = 0; i < mStatus.length; i++) {
      ByteUtils.setBytes(bytes, (i * 2) + 5, mStatus[i]);
    }
    return bytes;
  }

  /**
   *  获取 寄存器状态数据帧 占用字节数
   * 包括 起始地址 + 数量 + 字节数 + 状态字节数组
   * @return
   */
  @Override
  public int size() {
    return 2 + 2 + 1 + this.mStatus.length * 2;
  }

  /**
   * 起始位置
   * @return
   */
  @Override
  public short getStartNum() {
    return startNum;
  }

  @Override
  public short getCount() {
    return (short) mStatus.length;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("HoldingRegister[");
    for (int i = 0; i < mStatus.length - 1; i++) {
      builder.append(Integer.toString(startNum + i));
      builder.append(":");
      builder.append(mStatus[i]);
      builder.append(", ");
    }
    builder.append(Integer.toString(startNum + mStatus.length - 1));
    builder.append(":");
    builder.append(mStatus[mStatus.length - 1]);
    builder.append(']');
    return builder.toString();
  }

  /**
   * 所有寄存器置 0
   */
  @Override
  public void reset() {
    for (int i = 0; i < mStatus.length; i++) {
      mStatus[i] = 0;
    }
  }
}
