package com.redriver.modbus;

/**
 * Created by zwq00000 on 2014/7/10.
 */
public class WriteRegistersRequest extends  ModbusFrame{

    private final RegisterHolder mHolder;
    /**
     * @param slaveId
     */
    public WriteRegistersRequest(byte slaveId, RegisterHolder holder) {
        super(slaveId,FunctionCode.WRITE_REGISTERS,holder.size());
        super.setValues(holder.toBytes());
        mHolder = holder;
    }

    /**
     * 获取该请求的 功能码
     * {@link FunctionCode}
     *
     * @return
     */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.WRITE_REGISTERS;
    }

    /**
     * 写入数据之前,数据预处理
     */
    @Override
    void beforeWriteFrame() {
        super.setValues(mHolder.toBytes());
    }

    /**
     * 读取响应数据
     *
     * @param responseBuffer
     * @param length
     */
    @Override
    void readResponse(byte[] responseBuffer, int length) {
        if(responseBuffer[0]== FunctionCode.WRITE_REGISTERS+0x80){
            throw new IllegalArgumentException("Modbus 错误 WRITE_REGISTERS Error Code:"+Byte.toString(responseBuffer[1]));
        }
    }
}
