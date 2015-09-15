package com.redriver.modbus;

/**
 * 写入寄存器数据
 * Created by zwq00000 on 2014/7/9.
 */
public class WriteCoilRequest extends ModbusFrame {

    private static final short COIL_OPEN = (short)0xff00;
    private static final short COIL_CLOSE = (short)0x0;

    /**
     * @param slaveId
     */
    public WriteCoilRequest(byte slaveId, short startCoilNum, boolean coilStatus) {
        super(slaveId, FunctionCode.WRITE_COIL,4);
        super.appendValue(startCoilNum);
        super.appendValue(coilStatus? COIL_OPEN : COIL_CLOSE);
    }

    /**
     * 获取该请求的 功能码
     * {@link FunctionCode}
     *
     * @return
     */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.WRITE_COIL;
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
     * 读取响应数据
     *
     * @param responseBuffer
     * @param length
     */
    @Override
    boolean readResponse(byte[] responseBuffer, int length) {
        return false;
    }

    /**
     * PDU： 协议数据单元 长度 包括 功能码 和 数据 不包括 地址域 和 CRC 校验
     */
    @Override
    int getPDULen() {
        return 4;
    }
}
