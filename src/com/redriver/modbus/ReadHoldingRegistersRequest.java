package com.redriver.modbus;



/**
 * Created by zwq00000 on 2014/7/10.
 */
public class ReadHoldingRegistersRequest extends ModbusFrame {
    private final RegisterHolder mHolder;

    /**
     * @param slaveId
     */
    public ReadHoldingRegistersRequest(byte slaveId, RegisterHolder holder) {
        super(slaveId,FunctionCode.READ_HOLDING_REGISTERS,4);
        this.appendValue(holder.getStartNum());
        this.appendValue(holder.getCount());
        this.mHolder = holder;
    }

    /**
     * 获取该请求的 功能码
     * {@link com.redriver.modbus.FunctionCode}
     *
     * @return
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
     * 读取响应数据
     *
     * @param responseBuffer
     * @param length
     */
    @Override
    void readResponse(byte[] responseBuffer, int length) {
        if(responseBuffer[1] == getFunctionCode()){
            byte byteCount = responseBuffer[2];
            if(byteCount == this.mHolder.getCount()){
                for (int i=0;i<byteCount;i++){
                    short regValue = ByteUtils.bytesToShort(responseBuffer,3+(i*2));
                    mHolder.setValue(i,regValue);
                }
            }
        }
    }
}
