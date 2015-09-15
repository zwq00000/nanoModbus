package com.redriver.modbus.test;

import android_serialport_api.SerialPort;
import com.redriver.modbus.ByteUtils;
import com.redriver.modbus.CoilStatus;
import com.redriver.modbus.ModbusFrame;
import junit.framework.TestCase;

import java.io.*;

public class ModbusFrameTest extends TestCase {

    private static final String TAG = "ModbusFrameTest";

    public void testGetCRC() throws Exception {

    }

    public void testGetValues() throws Exception {

    }

    public void testToBytes() throws Exception {

    }

    public void testCreateReadRegFrame() throws Exception {
        //FE 01 00 00 00 06 A8 07
        ModbusFrame frame = ModbusFrame.createReadCoilsFrame((byte) 0xFE, (short) 0, (short) 6);
        String valueStr = CRC16Test.toHexString(frame.getValues());
        assertEquals(valueStr,"FE0100000006");
        assertEquals(CRC16Test.toHexString(frame.toBytes()), "FE0100000006A807");
    }

    public void testCreateReadRegFrame1() throws Exception {
        ModbusFrame frame = ModbusFrame.createReadCoilsFrame((byte) 0x1, (short) 0, (short) 4);
        SerialPort port = createSerialPort();
        OutputStream output = port.getOutputStream();
        output.write(frame.toBytes());
        output.flush();

        byte[] buf  = new byte[100];
        int len = port.getInputStream().read(buf);
        Log.d(TAG, "Read Coils Response:" + ByteUtils.toHexString(buf, len));
        CoilStatus coils = new CoilStatus(4);
        coils.readResponse(buf,len);
        assertEquals(coils.getCoil(0),false);
        assertEquals(coils.getCoil(1),true);
        assertEquals(coils.getCoil(2),false);
        assertEquals(coils.getCoil(3),false);
    }


    public void testCreateWriteRegFrame() throws Exception{
        //FE 地址 0号寄存器 开
        //FE 05 00 00 FF 00 98 35
        ModbusFrame frame = ModbusFrame.createWriteCoilFrame((byte) 0xFE, (short) 0, true);
        String valueStr = CRC16Test.toHexString(frame.getValues());
        assertEquals(valueStr,"FE050000FF00" );
        assertEquals(ByteUtils.toHexString(frame.toBytes()),"FE050000FF009835");
    }

    public void testCreateWriteCoilFrame() throws Exception{
        SerialPort port = createSerialPort();
        ModbusFrame frame = ModbusFrame.createWriteCoilFrame((byte) 0x01, (short) 1, true);
        OutputStream output = port.getOutputStream();
        output.write(frame.toBytes());
        output.flush();

        Thread.sleep(100);
        byte[] buf  = new byte[100];
        int len = port.getInputStream().read(buf);
        Log.d(TAG,"Write Coil Response:" + ByteUtils.toHexString(buf,len));
        CoilStatus coils = new CoilStatus(4);
        coils.readResponse(buf,len);
        assertEquals(coils.getCoil(0),false);
        assertEquals(coils.getCoil(1),true);

        Thread.sleep(100);

        frame = ModbusFrame.createWriteCoilFrame((byte) 0xFE, (short) 1, false);
        output = port.getOutputStream();
        output.write(frame.toBytes());
        output.flush();

        Thread.sleep(100);
        len = port.getInputStream().read(buf);
        Log.d(TAG,"Write Coil Response:" + ByteUtils.toHexString(buf,len));
        coils.readResponse(buf,len);
        assertEquals(coils.getCoil(0),false);
        assertEquals(coils.getCoil(1),false);
    }

    public void testCreateReadCoilStatusFrame() throws Exception{
        ModbusFrame readFrame = ModbusFrame.createReadCoilsFrame(1, 0, 4);

        SerialPort port = createSerialPort();
        InputStream input = port.getInputStream();
        OutputStream output = port.getOutputStream();
        output.write(readFrame.toBytes());
        output.flush();
        Thread.sleep(200);
        byte[] buf = new byte[255];
        int len = input.read(buf);
        assertTrue(len > 0);
        Log.d("ModbusFrame", ByteUtils.toHexString(buf, len));
        CoilStatus coils = new CoilStatus(4);
        coils.readResponse(buf,len);
        assertEquals(coils.getCoil(0), false);
        assertEquals(coils.getCoil(1), false);
        assertEquals(coils.getCoil(2),false);
        assertEquals(coils.getCoil(3),false);
    }

    public void testCreateWriteCoilsFrame() throws Exception{
        CoilStatus coilStatus = new CoilStatus(4);
        ModbusFrame readFrame = ModbusFrame.createWriteCoilsFrame((byte) 1,  coilStatus);
        System.out.println(ByteUtils.toHexString(readFrame.toBytes()));

        coilStatus.setAll(true);
        readFrame = ModbusFrame.createWriteCoilsFrame((byte)1, coilStatus);
        System.out.println(CRC16Test.toHexString(readFrame.toBytes()));
    }

    public void testCreateWriteCoilsFrame1() throws Exception{
        SerialPort port = createSerialPort();
        InputStream input = port.getInputStream();
        OutputStream output = port.getOutputStream();

        CoilStatus coils = new CoilStatus(4);
        coils.setCoil(1,true);
        coils.setCoil(2,true);
        coils.setCoil(3,true);
        ModbusFrame frame = ModbusFrame.createWriteCoilsFrame((byte) 1, coils);
        byte[] frameBytes = frame.toBytes();
        Log.d(TAG,"write Coils Frame:" + ByteUtils.toHexString(frameBytes));
        frame.writeFrame(output);
        Thread.sleep(200);

        frame.readResponse(input);
        Log.d(TAG,  "Read Response:" + ByteUtils.toHexString(coils.toBytes()));
        assertEquals(coils.getCoil(0),false);
        assertEquals(coils.getCoil(1), true);
        assertEquals(coils.getCoil(2),true);
        assertEquals(coils.getCoil(3), true);

        coils.setCoil(1,false);
        coils.setCoil(2,false);
        coils.setCoil(3,false);
        frame = ModbusFrame.createWriteCoilsFrame((byte) 1, coils);
        frameBytes = frame.toBytes();
        Log.d(TAG,"write Coils Frame:" + ByteUtils.toHexString(frameBytes));
        frame.writeFrame(output);
    }

    public static SerialPort createSerialPort() throws IOException {
        String portName = "/dev/ttyS1";
        return new SerialPort(new File(portName),115200,0);
    }

    public static SerialPort createSerialPort(String portName) throws IOException {
        return new SerialPort(new File(portName),9600,0);
    }
}