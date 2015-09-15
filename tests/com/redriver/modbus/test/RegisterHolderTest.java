package com.redriver.modbus.test;

import com.redriver.modbus.ByteUtils;
import com.redriver.modbus.RegisterHolder;
import junit.framework.TestCase;

public class RegisterHolderTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testToBytes() throws Exception {
        RegisterHolder holder = new RegisterHolder((byte)1,(short) 40001, (short) 5);
        System.out.println(ByteUtils.toHexString(holder.toBytes()));
        assertEquals(ByteUtils.toHexString(holder.toBytes()),"9C4100050A00000000000000000000");

        holder.setValue(0,(short)2);
        holder.setValue(1,(short)500);
        holder.setValue(2,(short)200);
        holder.setValue(3,(short)4);
        holder.setValue(4,(short)5000);
        System.out.println(ByteUtils.toHexString(holder.toBytes()));
    }

    public void testSize() throws Exception {
        RegisterHolder holder = new RegisterHolder((byte)1,(short) 40001, (short) 5);
        assertEquals(holder.size(),5+10);
    }
}