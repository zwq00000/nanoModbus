package com.redriver.modbus;

/**
 * 写入多个 保持寄存器
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
     * 发生在 读取数据之前的事件
     */
    @Override
    void beforeReadFrame() {
        mHolder.reset();
    }

    /**
     * 读取响应数据
     *
     * @param responseBuffer
     * @param length
     */
    @Override
    boolean readResponse(byte[] responseBuffer, int length) {
        if(responseBuffer[0]== FunctionCode.WRITE_REGISTERS+0x80){
            return  false;
            // throw new IllegalArgumentException("Modbus 错误 WRITE_REGISTERS Error Code:"+Byte.toString(responseBuffer[1]));
        }
        return true;
    }

    /**
     * PDU： 协议数据单元 长度 包括 功能码 和 数据 不包括 地址域 和 CRC 校验
     */
    @Override
    int getPDULen() {
        return this.mHolder.getCount() * 2 + 2;
    }
}
