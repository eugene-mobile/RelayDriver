package ca.appspace.relaydriver.service;

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

	public static final String RELAY_NUMBER_PARAM = "relayNumber";
	public static final String RELAY_VALUE_PARAM = "relayValue";

	private final IBinder _binder = new LocalBinder();
	private Socket _socket = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		new Thread() {
			public void run() {
				try {
					_socket = new Socket();
					_socket.connect(new InetSocketAddress("192.168.1.177", 23));
					OutputStream stream = _socket.getOutputStream();
					stream.write(new byte[]{'h','e','l','l','o'});
					stream.close();
				} catch (Exception e) {
					Log.e("", "Error opening socket", e);
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
		if (_socket==null) {
			return false;
		}
		new Thread() {
			public void run() {
				try {
					_socket = new Socket();
					_socket.connect(new InetSocketAddress("192.168.1.177", 23));
					OutputStream stream = _socket.getOutputStream();
					stream.write(new byte[]{(byte) (relayNumber+48)});
					stream.close();
				} catch (Exception e) {
					Log.e("", "Error writing to socket", e);
				}
			}
		}.start();
		
		
		return isChecked;
	}
}
