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

import java.io.*;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
    private String portName;

	public SerialPort(File device, int baudRate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			/*try {
				// Missing read/write permission, trying to chmod the file
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
                OutputStream outputStream = su.getOutputStream();
                outputStream.write(cmd.getBytes());
                outputStream.flush();
                outputStream.close();
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException("chmod "+device.getAbsolutePath() + " timeout or can't write");
				}
			} catch (Exception e) {
				Log.w(TAG,e.getMessage());
				//throw new SecurityException(e.getMessage());
			}*/
		}

            String devicePath = device.getAbsolutePath();
            FileUtilsProxy.setPermissions(device,777,-1,-1);
		mFd = open(devicePath, baudRate, flags);
		if (mFd == null) {
			System.out.println(TAG + "native open returns null");
			throw new IOException("native open file "+ device.getAbsolutePath() + " fail");
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
        portName = device.getAbsolutePath();
	}

    public String getPortName(){
        return portName;
    }

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudRate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
