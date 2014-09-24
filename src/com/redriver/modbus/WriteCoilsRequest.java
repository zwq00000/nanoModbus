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
        super(slaveId, FunctionCode.WRITE_COILS,coilStatus.size());
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
     * 读取响应数据
     *
     * @param responseBuffer
     * @param length
     */
    @Override
    void readResponse(byte[] responseBuffer, int length) {

    }
}
