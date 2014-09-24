package com.redriver.modbus.test;

import android_serialport_api.SerialPort;
import com.redriver.modbus.CoilStatus;
import com.redriver.modbus.ModbusFrame;
import com.redriver.modbus.ByteUtils;
import junit.framework.TestCase;

import java.io.*;


public class CoilStatusTest extends TestCase {

    public void testGetCount() throws Exception {
        int coilsCount = 12;
        CoilStatus coils = new CoilStatus(coilsCount);
        assertEquals(coils.getCount(),coilsCount);
    }

    public void testSetCoil() throws Exception {

    }

    public void testGetCoil() throws Exception {
        testGetSetCoils(4);
        testGetSetCoils(8);
        testGetSetCoils(14);
        testGetSetCoils(254);
    }

    private void testGetSetCoils(int coilsCount){
        CoilStatus coils = new CoilStatus(coilsCount);
        for (int i = 0; i < coilsCount; i++) {
            assertEquals(coils.getCoil(i), false);
            //System.out.println(ByteUtils.toHexString(coils.toBytes()));
        }
        for (int i = 0; i < coilsCount; i++) {
            coils.setCoil(i, true);
            assertEquals(coils.getCoil(i), true);
            //System.out.println(ByteUtils.toHexString(coils.toBytes()));
        }

        for (int i = 0; i < coilsCount; i++) {
            coils.setCoil(i, false);
            assertEquals(coils.getCoil(i), false);
            System.out.println(ByteUtils.toHexString(coils.toBytes()));
        }
    }

    public void testReadResponse1() throws Exception{
        String portName = "/dev/ttyS1";
        SerialPort port = new SerialPort(new File(portName), 115200, 0);
        InputStream input = port.getInputStream();
        OutputStream output = port.getOutputStream();
        CoilStatus coils = new CoilStatus(4);
        ModbusFrame frame = ModbusFrame.createWriteCoilsFrame((byte) 1, coils);
        output.write(frame.toBytes());
        output.flush();
        Thread.sleep(100);
        byte[] buf = new byte[255];
        int len = input.read(buf);
        assertEquals(coils.getCoil(0),false);
    }

    public void testToBytes() throws Exception{
        CoilStatus coils = new CoilStatus(4);
        System.out.println(ByteUtils.toHexString(coils.toBytes()));
        assertEquals(ByteUtils.toHexString(coils.toBytes()),"000000040100");

        coils.setCoil(0,true);
        System.out.println(ByteUtils.toHexString(coils.toBytes()));
        assertEquals(ByteUtils.toHexString(coils.toBytes()),"000000040101");
    }
}