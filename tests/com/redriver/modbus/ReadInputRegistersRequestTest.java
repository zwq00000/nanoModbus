package com.redriver.modbus;

import com.redriver.modbus.test.ModbusFrameTest;

import junit.framework.TestCase;

import android_serialport_api.SerialPort;

/**
 * Created by zwq00000 on 2015/7/24.
 */
public class ReadInputRegistersRequestTest extends TestCase {

  private SerialPort port;

  public void setUp() throws Exception {
    super.setUp();
    port = ModbusFrameTest.createSerialPort("dev/ttySAC3");
  }

  public void tearDown() throws Exception {

  }

  public void testReadResponse() throws Exception {
    RegisterHolder holder = new RegisterHolder((byte) 1, 4);
    ReadInputRegistersRequest request = new ReadInputRegistersRequest(holder);
    request.writeFrame(port.getOutputStream());
    request.readResponse(port.getInputStream());
    System.out.println(holder);
  }

  public void testGetFunctionCode() throws Exception {

  }

  public void testGetSlaveId() throws Exception {

  }
}