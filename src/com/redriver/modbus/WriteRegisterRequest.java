package com.redriver.modbus;

/**
 * Created by zwq00000 on 2014/7/10.
 */
public class WriteRegisterRequest extends ModbusFrame {

    private final short mRegAddress;
    private final short mRegValue;

    /**
     *
     * @param slaveId
     * @param regAddress
     * @param regValue
     */
    public WriteRegisterRequest(byte slaveId, short regAddress, short regValue) {
        super(slaveId, FunctionCode.WRITE_REGISTER,4);
        mRegAddress = regAddress;
        mRegValue = regValue;
        super.appendValue(regAddress);
        super.appendValue(regValue);
    }

    public short getRegAddress(){
        return mRegAddress;
    }

    public short getRegValue(){
        return mRegValue;
    }

    /**
     * 获取该请求的 功能码
     * {@link FunctionCode}
     *
     * @return
     */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.WRITE_REGISTER;
    }

    /**
     * 写入数据之前,数据预处理
     */
    @Override
    void beforeWriteFrame() {

    }

    /**
     * 读取响应数据
     *
     * @param responseBuffer
     * @param length
     */
    @Override
    void readResponse(byte[] responseBuffer, int length) {

    }
}
