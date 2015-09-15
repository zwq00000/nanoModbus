package com.redriver.modbus.test;

import android_serialport_api.SerialPort;
import com.redriver.modbus.ByteUtils;
import com.redriver.modbus.RegisterHolder;
import com.redriver.modbus.WriteRegisterRequest;
import junit.framework.TestCase;

public class WriteRegisterRequestTest extends TestCase {

    private RegisterHolder holder;

    public void testWriteFrame() throws Exception {
        holder = new RegisterHolder((byte)1,(short)1,(short)5);
        //holder.setValue(0,(short)1);
        //holder.setValue(1,(short)120);
        //holder.setValue(2,(short)10);
        holder.setValue(3,(short)4);

        SerialPort port = ModbusFrameTest.createSerialPort();
        WriteRegisterRequest frame = new WriteRegisterRequest((byte)1,(short)3,(short)4);
        System.out.println(ByteUtils.toHexString(frame.toBytes()));
        frame.writeFrame(port.getOutputStream());
        //0106000300047809
        Thread.sleep(10);
        frame.readResponse(port.getInputStream());
    }
}