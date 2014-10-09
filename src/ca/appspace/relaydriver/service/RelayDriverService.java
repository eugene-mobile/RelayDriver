package ca.appspace.relaydriver.service;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RelayDriverService extends Service {
    public static int RELAY_PORT = 1981;
    public static String RELAY_HARDWARE_ADDR = "216.48.168.68";
	public static final String RELAY_NUMBER_PARAM = "relayNumber";
	public static final String RELAY_VALUE_PARAM = "relayValue";

	private final IBinder _binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		new Thread() {
			public void run() {
                Socket socket = null;
                DataOutputStream dataOutputStream = null;
                DataInputStream dataInputStream = null;
                try {
					socket = new Socket();
                    socket.setSoTimeout(30000);
					socket.connect(new InetSocketAddress(RELAY_HARDWARE_ADDR, RELAY_PORT));
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream.writeBytes("Hello");
                    dataOutputStream.flush();
                    StringBuilder sb = new StringBuilder();
                    while (dataInputStream.available()>0) {
                        sb.append(dataInputStream.readChar());
                    }
                    Log.i("", "Line read: "+sb.toString());
				} catch (Exception e) {
                    Log.e("", "Error opening socket", e);
                } finally {
                    safeClose(dataOutputStream);
                    safeClose(dataInputStream);
                    safeClose(socket);
                }
			}
		}.start();
		return _binder;
	}
	
	public class LocalBinder extends Binder {
		public RelayDriverService getService() {
            return RelayDriverService.this;
        }
    }

	public boolean setRelayValue(final int relayNumber, boolean isChecked) {
		new Thread() {
			public void run() {
				try {
                    Socket socket = new Socket();
					socket.connect(new InetSocketAddress(RELAY_HARDWARE_ADDR, RELAY_PORT));
					OutputStream stream = socket.getOutputStream();
					stream.write(new byte[]{(byte) (relayNumber+48)});
					stream.close();
				} catch (Exception e) {
					Log.e("", "Error writing to socket", e);
				}
			}
		}.start();
		
		
		return isChecked;
	}

    private void safeClose(Closeable cl) {
        if (cl==null) return;
        try {
            cl.close();
        } catch (Throwable e) {}
    }

}
