package com.redriver.modbus;


/**
 * read Coils request
 * Created by zwq00000 on 2014/7/8.
 */
public class ReadCoilsRequest extends ModbusFrame {


    private final CoilStatus coils;

    public ReadCoilsRequest(byte slaveId, short startCoilNum, short coilsCount) {
        this((byte)slaveId,new CoilStatus(startCoilNum,coilsCount));
    }

    public ReadCoilsRequest(int slaveId, CoilStatus coilStatus) {
        super((byte) slaveId, FunctionCode.READ_COILS, 4);
        this.coils = coilStatus;
        appendValue(coilStatus.getStartNum());
        appendValue(coilStatus.getCount());
    }

    public CoilStatus getCoils(){
        return coils;
    }

    /**
     * 获取该请求的 功能码
     * {@link FunctionCode}
     *
     * @return
     */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_COILS;
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

    }


    /**
     * 读取响应数据流
     *
     * @param responseBuffer
     */
    boolean readResponse(byte[] responseBuffer,int length) {
        byte slaveId = responseBuffer[0];
        if(slaveId!=getSlaveId()){
            // 从站ID 不符
            return false;
        }
        byte funcCode = responseBuffer[1];
        if(funcCode!=this.getFunctionCode()){
            return false;
        }
        this.coils.readResponse(responseBuffer,length);
        return true;
    }

    /**
     * PDU： 协议数据单元 长度 包括 功能码 和 数据 不包括 地址域 和 CRC 校验
     */
    @Override
    int getPDULen() {
        return (coils.getCount() + 7) / 8 + 2;
    }
}
