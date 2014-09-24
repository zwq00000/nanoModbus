package com.redriver.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zwq00000 on 2014/7/2.
 */
public abstract class ModbusFrame {

    /**
     * 数据帧
     */
    protected byte[] mFrame;

    protected int length;

    /**
     *
     * @param slaveId
     * @param funcCode
     */
    protected ModbusFrame(byte slaveId,byte funcCode) {
        mFrame = new byte[8];
        mFrame[0] = slaveId;
        mFrame[1] = funcCode;
        length = 2;
    }

    protected ModbusFrame(byte slaveId,byte funcCode,int valueSize) {
        mFrame = new byte[valueSize+2];
        mFrame[0] = slaveId;
        mFrame[1] = funcCode;
        length = 2;
    }

    protected void setSlaveId(byte slaveId){
        if(mFrame.length>0){
            mFrame[0] = slaveId;
        }
    }



    /**
     * 获取该请求的 功能码
     * {@link FunctionCode}
     * @return
     */
    public abstract byte getFunctionCode();

    /**
     * 获取从站设备地址
     * @return
     */
    public byte getSlaveId(){
        if(mFrame.length>0){
            return mFrame[0];
        }
        return 0;
    }

    /**
     * 写入数据之前,数据预处理
     */
    abstract void  beforeWriteFrame();

    public void writeFrame(OutputStream outputStream) throws IOException {
        if(outputStream == null){
            throw new NullPointerException("output Stream is not been null");
        }
        beforeWriteFrame();
        outputStream.write(mFrame, 0, length);
        outputStream.write(this.getCRC());
        outputStream.flush();
    }

    /**
     *
     */
    static final byte[] RESPONSE_BUFFER = new byte[128];

    /**
     * 读取响应数据流
     * @param inputStream
     */
    public synchronized void readResponse(InputStream inputStream) throws IOException, InterruptedException {
        if(inputStream == null){
            throw new NullPointerException("input stream is not been Null");
        }
        int retryCount = 0;
        while(inputStream.available()==0){
            wait(10);
            retryCount++;
            if(retryCount>3){
                return;
            }
        }
        int len = inputStream.read(RESPONSE_BUFFER);
        byte slaveId = RESPONSE_BUFFER[0];
        if(slaveId!=getSlaveId()){
            // 从站ID 不符
            return;
        }
        if(slaveId ==getFunctionCode()+(byte)0x80){
            throw new IOException("Response error "+RESPONSE_BUFFER[1]);
            //return;
        }
        readResponse(RESPONSE_BUFFER,len);
    }

    /**
     * 读取响应数据
     * @param responseBuffer
     * @param length
     */
    abstract void readResponse(byte[] responseBuffer,int length);

    /**
     * 写单个寄存器
     *
     * @param slaveId       设备地址
     * @param startCoilNum 线圈寄存器编号
     * @param coilValue    线圈寄存器的值
     * @return
     */
    public static ModbusFrame createWriteCoilFrame(byte slaveId, short startCoilNum, boolean coilValue) {
        //ModbusFrame frame = new WriteCoilRequest(slaveId,startCoilNum,coilValue);
        //byte[] regNumBytes = ByteUtils.toByteArray(startCoilNum);
        //frame.mFrame = new byte[]{slaveId, FunctionCode.WRITE_COIL, regNumBytes[0], regNumBytes[1], coilValue ? (byte) 0xff : 0x00, (byte) 0};
        //return frame;
        return new WriteCoilRequest(slaveId,startCoilNum,coilValue);
    }

    /**
     * 创建 读可读写数字量寄存器（线圈状态） 数据帧
     *
     * @param device      设备地址
     * @param startCoilNum 起始寄存器地址
     * @param coilsCount    读取寄存器数量
     * @return 用于读取寄存器状态的数据帧
     */
    public static ModbusFrame createReadCoilsFrame(byte device, short startCoilNum, short coilsCount) {
        return new ReadCoilsRequest(device,startCoilNum,coilsCount);
    }

    /**
     * 创建 读可读写数字量寄存器（线圈状态） 数据帧
     *
     * @param device
     * @param startRegNum
     * @param regCount
     * @return
     */
    public static ModbusFrame createReadCoilsFrame(int device, int startRegNum, int regCount) {
        return createReadCoilsFrame((byte) device, (short) startRegNum, (short) regCount);
    }

    /**
     * 创建 写多个线圈寄存器的 Modbus 数据帧
     * ModbusUtils.pushShort(queue, startOffset);
     * ModbusUtils.pushShort(queue, numberOfBits);
     * ModbusUtils.pushByte(queue, data.length);
     * 从机Id 1 BYTE
     * 功能码 1 BYTE 0X0F
     * 设置起始地址 2 BYTE  0X0000 TO 0XFFFF
     * 设置长度  2 BYTE  0X0000 TO 0X7B0
     * 字节计数 1 BYTE N
     * 设置内容  N BYTE
     * @param slaveId
     * @param coilStatus
     * @return
     */
    public static ModbusFrame createWriteCoilsFrame(byte slaveId,CoilStatus coilStatus) {
        WriteCoilsRequest frame = new WriteCoilsRequest(slaveId, coilStatus);
        byte[] startRegNumBytes = ByteUtils.toByteArray(coilStatus.getStartNum());
        byte[] coilsCountBytes = ByteUtils.toByteArray(coilStatus.getCount());
        byte[] statusBytes = new byte[1];
        frame.mFrame = new byte[7+statusBytes.length];
        frame.mFrame[0] = slaveId;
        frame.mFrame[1] = FunctionCode.WRITE_COILS;
        frame.mFrame[2] = startRegNumBytes[0];
        frame.mFrame[3] = startRegNumBytes[1];
        frame.mFrame[4] = coilsCountBytes[0];
        frame.mFrame[5] = coilsCountBytes[1];
        frame.mFrame[6] = (byte)statusBytes.length;
        System.arraycopy(statusBytes,0,frame.mFrame,7,statusBytes.length);
        frame.length = 7+statusBytes.length;
        return frame;
    }

    public byte[] getCRC() {
        return ModbusUtility.CRC16(mFrame, length);
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
     * @param values
     */
    public synchronized void setValues(byte[] values) {
        if(this.mFrame.length<values.length+2){
            mFrame = ByteUtils.combine(mFrame,2,values);
        }else {
            System.arraycopy(values,0,mFrame,2,values.length);
        }
        length = values.length + 2;
    }

    /**
     * 增加数值部分
     * @param value
     */
    protected synchronized void appendValue(byte value){
        if(this.mFrame.length< (length+1)){
            //扩容
            mFrame = ByteUtils.combine(mFrame,new byte[8]);
        }
        mFrame[length] = value;
        length++;
    }
    /**
     * 增加数值部分
     * @param bytes
     */
    protected synchronized void appendValue(byte[] bytes){
        if(this.mFrame.length< (length+bytes.length)){
            //扩容
            mFrame = ByteUtils.combine(mFrame,bytes);
        }else {
            System.arraycopy(bytes,0,mFrame,length,bytes.length);
        }
        length+= bytes.length;
    }
    /**
     * 增加数值部分
     * @param shortValue
     */
    protected synchronized void appendValue(short shortValue){
        byte[] bytes = ByteUtils.toByteArray(shortValue);
        appendValue(bytes);
    }

}

