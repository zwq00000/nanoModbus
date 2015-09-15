package com.redriver.modbus;

/**
 * 输入寄存器 读取请求 {@see FunctionCode.READ_INPUT_REGISTERS} Created by zwq00000 on 2015/6/3.
 */
public class ReadInputRegistersRequest extends ModbusFrame {

  private final Holder<Short> mHolder;

  /**
   * @param slaveId
   */
  public ReadInputRegistersRequest(byte slaveId, Holder<Short> holder) {
    super(slaveId, FunctionCode.READ_INPUT_REGISTERS, holder.getStartNum(), holder.getCount());
    this.mHolder = holder;
  }

  /**
   * @param holder
   */
  public ReadInputRegistersRequest(Holder<Short> holder) {
    super(holder.getSlaveId(), FunctionCode.READ_INPUT_REGISTERS, holder.getStartNum(),
          holder.getCount());
    this.mHolder = holder;
  }


  /**
   * 写入数据之前,数据预处理
   */
  @Override
  void beforeWriteFrame() {

  }

  /**
   * 发生在 读取数据之前的事件
   */
  @Override
  void beforeReadFrame() {
    mHolder.reset();
  }

/*
  @Override
  public synchronized boolean readResponse(InputStream inputStream)
      throws IOException {
    if (inputStream == null) {
      throw new NullPointerException("input stream is not been Null");
    }
    beforeReadFrame();
    byte resSlaveId = (byte) inputStream.read();
    if (resSlaveId == getSlaveId()) {
      int funcCode = inputStream.read();
      if (funcCode == this.getFunctionCode()) {
        int byteCount = inputStream.read();
        byte[] buffer = new byte[byteCount];
        readStream(inputStream, buffer);
        byte[] crc16 = new byte[2];
        readStream(inputStream, crc16);
        byte[] result = CRC16.calculate(new byte[]{resSlaveId, (byte) funcCode, (byte) byteCount},buffer);
        if(ByteUtils.compare(crc16,result)){
          throw  new IOException("CRC16 校验错误");
        }
        for (int i = 0; i < byteCount/2; i++) {
          short regValue = ByteUtils.bytesToShort(buffer, (i * 2));
          mHolder.setValue(i, regValue);
        }
      }
    }
    return false;
  }
  */


  /**
   * 读取响应数据
   */
  @Override
  boolean readResponse(byte[] responseBuffer, int length) {
    int regCount = responseBuffer[2]/2;
    for (int i = 0; i < regCount; i++) {
      short regValue = ByteUtils.bytesToShort(responseBuffer, (i * 2 + 3));
      mHolder.setValue(i, regValue);
    }
    return true;
  }

  /**
   * PDU： 协议数据单元 长度 包括 功能码 和 数据 不包括 地址域 和 CRC 校验
   * funcCode + byteCount + valueCount * 2
   */
  @Override
  int getPDULen() {
    return 2 + mHolder.getCount() * 2;
  }
}
