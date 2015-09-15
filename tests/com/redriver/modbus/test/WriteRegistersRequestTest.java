package com.redriver.modbus.test;

import android_serialport_api.SerialPort;
import com.redriver.modbus.*;
import junit.framework.TestCase;

public class WriteRegistersRequestTest extends TestCase {
    private RegisterHolder holder;
    public void setUp() throws Exception {
        super.setUp();
        holder = new RegisterHolder((byte)1, (short)5,(short)5);
        //holder.setValue(0,(short)1);
        //holder.setValue(1,(short)120);
        //holder.setValue(2,(short)10);
        holder.setValue(3,(short)4);
        //holder.setValue(4,(short)20);
        //holder.setValue(5,(short)30);
        /*holder.setValue(6,(short)1);
        holder.setValue(7,(short)10);
        holder.setValue(8,(short)20);
        holder.setValue(9,(short)2);
        holder.setValue(10,(short)30);*/
        System.out.println(holder.toString());
    }

    public void testGetFunctionCode() throws Exception {
        WriteRegistersRequest request = new WriteRegistersRequest((byte) 1, holder);
        assertEquals(request.getFunctionCode(),FunctionCode.WRITE_REGISTERS);
    }

    public void testToBytes() throws Exception {
        WriteRegistersRequest request = new WriteRegistersRequest((byte) 1, holder);
        System.out.println(ByteUtils.toHexString(request.toBytes()));
    }

    public void testWriteFrame() throws Exception {
        SerialPort port = ModbusFrameTest.createSerialPort();
        ModbusFrame frame = new WriteRegistersRequest((byte)1,holder);
        frame.writeFrame(port.getOutputStream());
        Thread.sleep(10);
        frame.readResponse(port.getInputStream());

        System.out.println(holder.toString());

        holder = new RegisterHolder((byte)1, (short)1,(short)5);
        frame = new ReadHoldingRegistersRequest(holder);
        frame.writeFrame(port.getOutputStream());
        Thread.sleep(10);
        frame.readResponse(port.getInputStream());

        Thread.sleep(1000);
        frame = new WriteCoilRequest((byte)1,(byte)0,false);
        frame.writeFrame(port.getOutputStream());
        Thread.sleep(10);
        frame.readResponse(port.getInputStream());
    }

    public void testReadResponse1() throws Exception {

    }
}