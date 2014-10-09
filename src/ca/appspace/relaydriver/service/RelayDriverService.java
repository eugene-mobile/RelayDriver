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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private volatile TCPRelayClient _tcpClient = null;
    private volatile Object _initLock = new Object();
    private final IBinder _binder = new LocalBinder();
    private RelayStatusCallback _relaRelayStatusCallback;
    private final static String RELAY_STATUS_PATTERN = "^Relay #(\\d+) is turned (ON|OFF)$";
    private final static String LOG_TAG = RelayDriverService.class.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
        if (_tcpClient==null) {
            initClient();
        }
		return _binder;
	}

    private void parseResponse(String data) {
        Log.i(LOG_TAG, "ResponseReceived: "+data);
        Pattern p = Pattern.compile(RELAY_STATUS_PATTERN);
        Matcher m = p.matcher(data);
        if (m.matches() && m.groupCount()>1) {
            int relayNum = Integer.parseInt(m.group(1));
            boolean status = "ON".equalsIgnoreCase(m.group(2));
            if (_relaRelayStatusCallback!=null) {
                _relaRelayStatusCallback.onRelayStatusChanged(relayNum, status);
            }
        }
    }

    public void setRelayStatusCallback(RelayStatusCallback relayStatusCallback) {
        _relaRelayStatusCallback = relayStatusCallback;
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
                    if (_tcpClient==null) {
                        initClient();
                    }
                    _tcpClient.sendCommand(String.valueOf(relayNumber));
                    Thread.sleep(100);
                    _tcpClient.sendCommand("s");
				} catch (Exception e) {
					Log.e(LOG_TAG, "Error writing to socket", e);
				}
			}
		}.start();
		
		
		return isChecked;
	}

    private void initClient() {
        if (_tcpClient==null) {
            synchronized (_initLock) {
                new Thread() {
                    public void run() {
                        if (_tcpClient!=null) return;
                        _tcpClient = new TCPRelayClient(RELAY_HARDWARE_ADDR, RELAY_PORT);
                        _tcpClient.addDataCallback(new NewDataAvailableCallback() {
                            @Override
                            public void onNewDataAvailable(String data) {
                                parseResponse(data);
                            }
                        });
                        try {
                            _tcpClient.connect();
                            _tcpClient.sendCommand("?");
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {}
                            _tcpClient.sendCommand("s");
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Error connecting to remote relays", e);
                        }

                    }
                }.start();
            }
        }
    }

}
