package com.redriver.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Modbus 数据帧 Created by zwq00000 on 2014/7/2.
 */
public abstract class ModbusFrame {

  /**
   * 数据帧
   */
  protected byte[] mFrame;

  protected int length;

  /**
   * Modbus 数据帧 构造方法
   *
   * @param slaveId  从站Id    {@value 1~255}
   * @param funcCode 功能码 {@link FunctionCode}
   */
  protected ModbusFrame(byte slaveId, byte funcCode) {
    mFrame = new byte[8];
    mFrame[0] = slaveId;
    mFrame[1] = funcCode;
    length = 2;
  }

  /**
   * 数据帧构造方法
   *
   * @param slaveId   从站Id
   * @param funcCode  功能码 {@link FunctionCode}
   * @param valueSize 帧中数值部分大小（不包括头部）
   */
  protected ModbusFrame(byte slaveId, byte funcCode, int valueSize) {
    mFrame = new byte[valueSize + 2];
    mFrame[0] = slaveId;
    mFrame[1] = funcCode;
    length = 2;
  }

  /**
   * 数据帧构造方法
   *
   * @param slaveId   从站Id
   * @param funcCode  功能码 {@link FunctionCode}
   * @param values 帧中数值部分大小（不包括头部）
   */
  protected ModbusFrame(byte slaveId, byte funcCode, byte[] values) {
    length = 2 + values.length;
    mFrame = new byte[length];
    mFrame[0] = slaveId;
    mFrame[1] = funcCode;
    System.arraycopy(values, 0, mFrame, 2, values.length);
  }

  /**
   * 数据帧构造方法
   *
   * @param slaveId   从站Id
   * @param funcCode  功能码 {@link FunctionCode}
   * @param startNum  寄存器起始位置
   * @param coilsCount 寄存器数量
   */
  protected ModbusFrame(byte slaveId, byte funcCode, short startNum,short coilsCount) {
    length = 2 + 4;
    mFrame = new byte[length];
    mFrame[0] = slaveId;
    mFrame[1] = funcCode;
    ByteUtils.setBytes(mFrame,2,startNum);
    ByteUtils.setBytes(mFrame,4,coilsCount);
  }

  /**
   * 设置从站 Id
   */
  protected void setSlaveId(byte slaveId) {
    if (mFrame.length > 0) {
      mFrame[0] = slaveId;
    }
  }


  /**
   * 获取该请求的 功能码 {@link FunctionCode}
   */
  public byte getFunctionCode() {
    return mFrame[1];
  }

  /**
   * 获取从站设备地址
   */
  public byte getSlaveId() {
    if (mFrame.length > 0) {
      return mFrame[0];
    }
    return 0;
  }

  /**
   * 写入数据之前,数据预处理
   */
  abstract void beforeWriteFrame();

  /**
   * 发生在 读取数据之前的事件
   */
  abstract void beforeReadFrame();

  /**
   * 写入 数据帧 到输出流
   */
  public void writeFrame(OutputStream outputStream) throws IOException {
    if (outputStream == null) {
      throw new NullPointerException("output Stream is not been null");
    }
    beforeWriteFrame();
    outputStream.write(mFrame, 0, length);
    outputStream.write(this.getCRC());
    outputStream.flush();
  }

  /**
   * 响应缓冲
   */
  static final byte[] RESPONSE_BUFFER = new byte[128];
  static final byte[] CRC_BUFFER = new byte[2];

  /**
   * 读取响应数据流
   */
  public synchronized boolean readResponse(InputStream inputStream)
      throws IOException {
    if (inputStream == null) {
      throw new NullPointerException("input stream is not been Null");
    }
    beforeReadFrame();
    int aduLen = getPDULen() + 1;
    int len = readStream(inputStream, RESPONSE_BUFFER,0,aduLen);
    if(len<aduLen){
      //读取失败
      return  false;
    }
    if(RESPONSE_BUFFER[0] != getSlaveId()){
      //slaveId 不正确
      return  false;
    }
    if(RESPONSE_BUFFER[1] != getFunctionCode()){
      //功能码不正确
      return  false;
    }
    len = readStream(inputStream,CRC_BUFFER);
    if(len<2){
      //CRC16 读取错误
      return false;
    }
    if(validateCRC16(RESPONSE_BUFFER,aduLen,CRC_BUFFER)){
      throw new IOException("CRC16 校验错误");
    }
    return readResponse(RESPONSE_BUFFER,aduLen);
  }

  private boolean validateCRC16(byte[] frame,int aduLen ,byte[] crc){
    byte[] result = CRC16.calculate(frame, aduLen - 2);
    return result[0] == crc[0] && result[1] == crc[1];
  }

  /**
   * 从输入流中循环读取数据 填充到 buffer 中
   * @param inputStream
   * @param buffer
   * @throws IOException
   */
   static int readStream(InputStream inputStream, byte[] buffer) throws IOException {
     return readStream(inputStream,buffer,0,buffer.length);
  }

  /**
   * 从输入流中循环读取数据 填充到 buffer 中
   * @param inputStream
   * @param buffer
   * @throws IOException
   */
  static synchronized int  readStream(InputStream inputStream, byte[] buffer,int off,int length) throws IOException {
    int retryCount = 0;
    int pos = off;
    while (pos < length) {
      int readCount = inputStream.read(buffer, pos, length - pos);
      if(readCount <0){
        retryCount++;
        if(retryCount>3){
          break;
        }
      }else{
        pos += readCount;
      }
    }
    return pos;
  }

  /**
   * 读取响应数据
   */
  abstract boolean readResponse(byte[] valueBuf, int length);

  /**
   * PDU： 协议数据单元 长度
   * 包括 功能码 和 数据
   * 不包括 地址域 和 CRC 校验
   * @return
   */
  abstract int getPDULen();

  /**
   * 写单个寄存器
   *
   * @param slaveId      设备地址
   * @param startCoilNum 线圈寄存器编号
   * @param coilValue    线圈寄存器的值
   */
  public static ModbusFrame createWriteCoilFrame(byte slaveId, short startCoilNum,
                                                 boolean coilValue) {
    //ModbusFrame frame = new WriteCoilRequest(slaveId,startCoilNum,coilValue);
    //byte[] regNumBytes = ByteUtils.toByteArray(startCoilNum);
    //frame.mFrame = new byte[]{slaveId, FunctionCode.WRITE_COIL, regNumBytes[0], regNumBytes[1], coilValue ? (byte) 0xff : 0x00, (byte) 0};
    //return frame;
    return new WriteCoilRequest(slaveId, startCoilNum, coilValue);
  }

  /**
   * 创建 读可读写数字量寄存器（线圈状态） 数据帧
   *
   * @param alaveId       设备地址
   * @param startCoilNum 起始寄存器地址
   * @param coilsCount   读取寄存器数量
   * @return 用于读取寄存器状态的数据帧
   */
  public static ModbusFrame createReadCoilsFrame(byte alaveId, short startCoilNum,
                                                 short coilsCount) {
    return new ReadCoilsRequest(alaveId, startCoilNum, coilsCount);
  }

  /**
   * 创建 读可读写数字量寄存器（线圈状态） 数据帧
   */
  public static ModbusFrame createReadCoilsFrame(int device, int startRegNum, int regCount) {
    return createReadCoilsFrame((byte) device, (short) startRegNum, (short) regCount);
  }

  /**
   * 创建 写多个线圈寄存器的 Modbus 数据帧 ModbusUtils.pushShort(queue, startOffset); ModbusUtils.pushShort(queue,
   * numberOfBits); ModbusUtils.pushByte(queue, data.length); 从机Id 1 BYTE 功能码 1 BYTE 0X0F 设置起始地址 2
   * BYTE  0X0000 TO 0XFFFF 设置长度  2 BYTE  0X0000 TO 0X7B0 字节计数 1 BYTE N 设置内容  N BYTE
   */
  public static ModbusFrame createWriteCoilsFrame(byte slaveId, CoilStatus coilStatus) {
    WriteCoilsRequest frame = new WriteCoilsRequest(slaveId, coilStatus);
    byte[] startRegNumBytes = ByteUtils.toByteArray(coilStatus.getStartNum());
    byte[] coilsCountBytes = ByteUtils.toByteArray(coilStatus.getCount());
    byte[] statusBytes = new byte[1];
    frame.mFrame = new byte[7 + statusBytes.length];
    frame.mFrame[0] = slaveId;
    frame.mFrame[1] = FunctionCode.WRITE_COILS;
    frame.mFrame[2] = startRegNumBytes[0];
    frame.mFrame[3] = startRegNumBytes[1];
    frame.mFrame[4] = coilsCountBytes[0];
    frame.mFrame[5] = coilsCountBytes[1];
    frame.mFrame[6] = (byte) statusBytes.length;
    System.arraycopy(statusBytes, 0, frame.mFrame, 7, statusBytes.length);
    frame.length = 7 + statusBytes.length;
    return frame;
  }

  public byte[] getCRC() {
    return CRC16.calculate(mFrame, length);
  }

  public byte[] getValues() {
    return mFrame;
  }

  public byte[] toBytes() {
    byte[] result = new byte[mFrame.length + 2];
    byte[] crc = getCRC();
    System.arraycopy(mFrame, 0, result, 0, mFrame.length);
    result[mFrame.length] = crc[0];
    result[mFrame.length + 1] = crc[1];
    return result;
  }

  /**
   * 设置数据部分,起始位置为 2
   */
  public synchronized void setValues(byte[] values) {
    if (this.mFrame.length < values.length + 2) {
      mFrame = ByteUtils.combine(mFrame, 2, values);
    } else {
      System.arraycopy(values, 0, mFrame, 2, values.length);
    }
    length = values.length + 2;
  }

  /**
   * 增加数值部分
   */
  protected synchronized void appendValue(byte value) {
    if (this.mFrame.length < (length + 1)) {
      //扩容
      mFrame = ByteUtils.combine(mFrame, new byte[8]);
    }
    mFrame[length] = value;
    length++;
  }

  /**
   * 增加数值部分
   */
  protected synchronized void appendValue(byte[] bytes) {
    if (this.mFrame.length < (length + bytes.length)) {
      //扩容
      mFrame = ByteUtils.combine(mFrame, bytes);
    } else {
      System.arraycopy(bytes, 0, mFrame, length, bytes.length);
    }
    length += bytes.length;
  }

  /**
   * 增加数值部分
   */
  protected synchronized void appendValue(short shortValue) {
    byte[] bytes = ByteUtils.toByteArray(shortValue);
    appendValue(bytes);
  }
}

