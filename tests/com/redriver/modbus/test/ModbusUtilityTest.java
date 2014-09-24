package com.redriver.modbus.test;

import com.redriver.modbus.ModbusFrame;
import com.redriver.modbus.ModbusUtility;
import junit.framework.TestCase;

public class ModbusUtilityTest extends TestCase {
    /**
     * 查询八路状态
     */
    byte[] testBytes1 = new byte[]{(byte) 0xFE,0x01 ,0x00 ,0x00 ,0x00 ,0x10};
    byte[] crc1 = new byte[]{0x29 , (byte) 0xC9};
    private static void traceBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(bytes[i]);
        }
    }

    public void testCRC16() throws Exception {
        //"FE 02 01 01 50 5C";
        assertCrc(new byte[]{(byte) 0xFE, 0x02, 0x01, 0x01},"505C");

        //"FE 01 00 00 00 06 A8 07";
        assertCrc(new byte[]{(byte) 0xFE ,0x01 ,0x00 ,0x00 ,0x00,0x06},"A807");

        //FE 01 01 00 61 9C
        assertCrc(new byte[]{(byte) 0xFE ,0x01,0x01 ,0x00},"619C");
    }

    private void assertCrc(byte[] values,String crcStr){
        byte[] crc = ModbusUtility.CRC16(values, values.length);
        assertNotNull(crc);
        assertTrue(crc.length == 2);
        String crc16 = toHexString(crc);
        System.out.println(toHexString(values) + crc16);
        assertEquals(crc16,crcStr);
    }

    public void testModbusFrame() throws Exception{
        ModbusFrame frame = ModbusFrame.createWriteCoilFrame((byte) 1, (short) 1, true);
        byte[] crc = frame.getCRC();
        System.out.println(String.format("0x%X%X",crc[0],crc[1]));
        byte[] values = frame.getValues();
        System.out.print(toHexString(values));
    }

    public void testHexString2Buf() throws Exception {
        String valueStr = "01050000FF";
        byte[] result = ModbusUtility.HexString2Buf(valueStr);
        traceBytes(result);
    }
    static final char[] HEXES = new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static final String toHexString(byte value) {
        char[] chars = new char[2];
        chars[0] = HEXES[(value >> 4 & 0xf)];
        chars[1] = HEXES[value & 0xf];
        return new String(chars);
    }

    public static final String toHexString(byte[] values) {
        char[] chars = new char[values.length*2];
        for (int i=0;i<values.length;i++) {
            chars[2*i] = HEXES[(values[i] >> 4 & 0xf)];
            chars[2*i + 1] = HEXES[values[i] & 0xf];
        }
        return new String(chars);
    }
}