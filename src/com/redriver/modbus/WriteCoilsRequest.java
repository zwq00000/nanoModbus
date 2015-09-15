package com.redriver.modbus;

/**
 * Created by zwq00000 on 2014/7/9.
 */
public class WriteCoilsRequest extends ModbusFrame {

    private final CoilStatus coils;
    private static final int COILS_STATUS_POS = 7;


    /**
     * 从机Id 1 BYTE
     * 功能码 1 BYTE 0X0F
     * 设置起始地址 2 BYTE  0X0000 TO 0XFFFF
     * 设置长度  2 BYTE  0X0000 TO 0X7B0
     * 字节计数 1 BYTE N
     * 设置内容  N BYTE
     * @param slaveId
     */
    public WriteCoilsRequest(byte slaveId, CoilStatus coilStatus) {
        super(slaveId, FunctionCode.WRITE_COILS,coilStatus.bytesSize());
        coils = coilStatus;
        super.setValues(coilStatus.toBytes());
    }

    /**
     * 获取该请求的 功能码
     * {@link FunctionCode}
     *
     * @return
     */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.WRITE_COILS;
    }

    /**
     * 写入数据之前,数据预处理
     */
    @Override
    void beforeWriteFrame() {
       super.setValues(coils.toBytes());
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
        return 5;
    }
}
