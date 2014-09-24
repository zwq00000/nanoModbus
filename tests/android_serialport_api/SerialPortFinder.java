/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import com.redriver.modbus.test.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

public class SerialPortFinder {

    public static String Tag = "SerialPortFinder";

    public class Device {
		public Device(String name, String root) {
			mDriverName = name;
			mDeviceRoot = root;
		}
		private String mDriverName;
		private String mDeviceRoot;
		Vector<File> mDevices = null;
		public Vector<File> getDevices() {
			if (mDevices == null) {
				mDevices = new Vector<File>();
				File dev = new File("/dev");
				File[] files = dev.listFiles();
				int i;
                assert files != null;
                for (i=0; i<files.length; i++) {
					if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
						Log.d(TAG, "Found new device: " + files[i]);
						mDevices.add(files[i]);
					}
				}
			}
			return mDevices;
		}
		public String getName() {
			return mDriverName;
		}
	}

	private static final String TAG = "SerialPort";

	private Vector<Device> devices = null;

	Vector<Device> getDevices() throws IOException {
		if (devices == null) {
			devices = new Vector<Device>();
			LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
			String l;
			while((l = r.readLine()) != null) {
				// Issue 3:
				// Since driver name may contain spaces, we do not extract driver name with split()
				String deviceName = l.substring(0, 0x15).trim();
				String[] w = l.split(" +");
				if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
					Log.d(TAG, "Found new driver " + deviceName + " on " + w[w.length-4]);
					devices.add(new Device(deviceName, w[w.length - 4]));
				}
			}
			r.close();
		}
		return devices;
	}

	public String[] getAllDevices() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Device> driverIterator;
		try {
			driverIterator = getDevices().iterator();
			while(driverIterator.hasNext()) {
				Device driver = driverIterator.next();
				Iterator<File> deviceIterator = driver.getDevices().iterator();
				while(deviceIterator.hasNext()) {
					String device = deviceIterator.next().getName();
					String value = String.format("%s (%s)", device, driver.getName());
					devices.add(value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}

	public String[] getAllDevicesPath() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Device> iterator;
		try {
			iterator = getDevices().iterator();
			while(iterator.hasNext()) {
				Device driver = iterator.next();
				Iterator<File> deviceIterator = driver.getDevices().iterator();
				while(deviceIterator.hasNext()) {
					String device = deviceIterator.next().getAbsolutePath();
					devices.add(device);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}
}
