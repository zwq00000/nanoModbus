package com.redriver.modbus.test;

import android_serialport_api.SerialPort;
import com.redriver.modbus.ModbusFrame;
import com.redriver.modbus.ReadHoldingRegistersRequest;
import com.redriver.modbus.RegisterHolder;
import junit.framework.TestCase;

public class ReadHoldingRegistersRequestTest extends TestCase {

    public void testGetFunctionCode() throws Exception {

    }

    public void testReadResponse() throws Exception {
        RegisterHolder holder = new RegisterHolder((short) 1, (short) 20);
        ReadHoldingRegistersRequest frame = new ReadHoldingRegistersRequest((byte) 1, holder);
        SerialPort port = ModbusFrameTest.createSerialPort();
        frame.writeFrame(port.getOutputStream());
        Thread.sleep(10);
        frame.readResponse(port.getInputStream());
        System.out.println(holder.toString());

    }
}