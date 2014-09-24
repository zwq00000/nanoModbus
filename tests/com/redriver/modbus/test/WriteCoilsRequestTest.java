package com.redriver.modbus.test;

import android_serialport_api.SerialPort;
import com.redriver.modbus.ByteUtils;
import com.redriver.modbus.CoilStatus;
import com.redriver.modbus.ModbusFrame;
import com.redriver.modbus.WriteCoilsRequest;
import junit.framework.TestCase;

public class WriteCoilsRequestTest extends TestCase {

    private SerialPort port;

    public void testGetFunctionCode() throws Exception {
        WriteCoilsRequest frame = new WriteCoilsRequest((byte) 1, new CoilStatus(4));
        System.out.println(ByteUtils.toHexString(frame.toBytes()));
        assertEquals(ByteUtils.toHexString(frame.toBytes()),"");
    }

    public void testWriteFrame() throws Exception {
        CoilStatus coils = new CoilStatus(4);
        coils.setCoil(1,true);
        coils.setCoil(2,true);
        WriteCoilsRequest frame = new WriteCoilsRequest((byte) 1, coils);
        System.out.println(ByteUtils.toHexString(frame.toBytes()));

        port = ModbusFrameTest.createSerialPort();

        for (int i=0;i<10;i++) {
            coils.setCoil(1, true);
            coils.setCoil(2, true);
            frame.writeFrame(port.getOutputStream());
            frame.readResponse(port.getInputStream());

            Thread.sleep(100);

            coils.setCoil(1, false);
            coils.setCoil(2, false);
            frame.writeFrame(port.getOutputStream());
            frame.readResponse(port.getInputStream());
            Thread.sleep(100);
        }

    }

    public void  testWriteCoilsResponse() throws Exception{
        CoilStatus coils = new CoilStatus(4);
        coils.setCoil(1,true);
        coils.setCoil(2,true);
        WriteCoilsRequest frame = new WriteCoilsRequest((byte) 1, coils);
        System.out.println(ByteUtils.toHexString(frame.toBytes()));

        ModbusFrame frame1 = ModbusFrame.createWriteCoilsFrame((byte) 1, coils);
        assertEquals(ByteUtils.toHexString(frame.toBytes()),ByteUtils.toHexString(frame1.toBytes()));
    }

}