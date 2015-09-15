package com.redriver.modbus;


/**
 * 保持寄存器读取请求 {@see FunctionCode.READ_HOLDING_REGISTERS} Created by zwq00000 on 2014/7/10.
 */
public class ReadHoldingRegistersRequest extends ModbusFrame {

  private final RegisterHolder mHolder;

  /**
   * @param holder
   */
  public ReadHoldingRegistersRequest(RegisterHolder holder) {
    super(holder.getSlaveId(), FunctionCode.READ_HOLDING_REGISTERS, holder.getStartNum(),
          holder.getCount());
    this.mHolder = holder;
  }

  /**
   * 获取该请求的 功能码 {@link com.redriver.modbus.FunctionCode}
   */
  @Override
  public byte getFunctionCode() {
    return FunctionCode.READ_HOLDING_REGISTERS;
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

  /**
   * 读取响应数据
   */
  @Override
  boolean readResponse(byte[] responseBuffer, int length) {
    if (responseBuffer[1] == getFunctionCode()) {
      int byteCount = responseBuffer[2];
      int regCount = this.mHolder.getCount();
      if (byteCount == regCount * 2) {
        for (int i = 0; i < regCount; i++) {
          short regValue = ByteUtils.bytesToShort(responseBuffer, 3 + (i * 2));
          mHolder.setValue(i, regValue);
        }
      }
    }
    return true;
  }

  /**
   * PDU： 协议数据单元 长度 包括 功能码 和 数据 不包括 地址域 和 CRC 校验
   */
  @Override
  int getPDULen() {
    return 2 + mHolder.getCount() * 2;
  }
}
