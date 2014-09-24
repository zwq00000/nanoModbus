package com.redriver.modbus;

/**
 * Modbus 功能码
* Created by zwq00000 on 2014/7/9.
*/
public class FunctionCode {
    /**
     * 读取线圈状态   取得一组逻辑线圈的当前状态（ON/OFF)
     */
    public static final byte READ_COILS = 1;
    /**
     * 读取输入状态  取得一组开关输入的当前状态（ON/OFF)
     */
    public static final byte READ_DISCRETE_INPUTS = 2;
    /**
     * 读取保持寄存器  在一个或多个保持寄存器中取得当前的二进制值
     */
    public static final byte READ_HOLDING_REGISTERS = 3;
    /**
     * 读取输入寄存器 在一个或多个输入寄存器中取得当前的二进制值
     */
    public static final byte READ_INPUT_REGISTERS = 4;
    /**
     * 强置单线圈 强置一个逻辑线圈的通断状态
     */
    public static final byte WRITE_COIL = 5;
    /**
     * 预置单寄存器  把具体二进值装入一个保持寄存器
     */
    public static final byte WRITE_REGISTER = 6;
    /**
     * 读取异常状态 取得8个内部线圈的通断状态，这8个线圈的地址由控制器决定
     */
    public static final byte READ_EXCEPTION_STATUS = 7;
    /**
     * 强置多线圈 强置一串连续逻辑线圈的通断
     */
    public static final byte WRITE_COILS = 15;
    /**
     * 写入多个保持寄存器
     */
    public static final byte WRITE_REGISTERS = 16;
    public static final byte REPORT_SLAVE_ID = 17;
    public static final byte WRITE_MASK_REGISTER = 22;

    public static final String toString(byte code) {
        return Integer.toString(code & 0xff);
    }
}
