package ca.appspace.relaydriver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import ca.appspace.relaydriver.service.RelayDriverService;
import ca.appspace.relaydriver.service.RelayDriverService.LocalBinder;
import ca.appspace.relaydriver.service.RelayStatusCallback;

public class MainActivity extends Activity {
	
	private RelayDriverService _relayDriver;

    private Map<Integer, ToggleButton> buttons = new HashMap<Integer, ToggleButton>();

	private RelayStatusCallback _relayStatusCallback = new RelayStatusCallback() {
        @Override
        public void onRelayStatusChanged(final int relayNum, final boolean newValue) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToggleButton btn = buttons.get(relayNum);
                    if (btn!=null) {
                        btn.setChecked(newValue);
                    }
                }
            });
        }
    };

	private ServiceConnection _driverConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			_relayDriver = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			_relayDriver = binder.getService();
            _relayDriver.setRelayStatusCallback(_relayStatusCallback);
		}
	};
	
	@Override
	protected void onStart() {
		super.onStart();
		Intent startRelayDriverIntent = new Intent(this, RelayDriverService.class);
		boolean bindSuccess = bindService(startRelayDriverIntent, _driverConnection, Context.BIND_AUTO_CREATE);
		if (bindSuccess) {
			Toast.makeText(this, "Relay driver is ready", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Relay driver NOT started!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ToggleButton relayBtn1 = (ToggleButton) findViewById(R.id.relayButton1);
        relayBtn1.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRelay(relayBtn1, 1, relayBtn1.isChecked());
            }
        });
        buttons.put(1, relayBtn1);

		final ToggleButton relayBtn2 = (ToggleButton) findViewById(R.id.relayButton2);
        relayBtn2.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRelay(relayBtn2, 2, relayBtn2.isChecked());
            }
        });
        buttons.put(2, relayBtn2);

		final ToggleButton relayBtn3 = (ToggleButton) findViewById(R.id.relayButton3);
        relayBtn3.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRelay(relayBtn3, 3, relayBtn3.isChecked());
            }
        });
        buttons.put(3, relayBtn3);

		final ToggleButton relayBtn4 = (ToggleButton) findViewById(R.id.relayButton4);
        relayBtn4.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRelay(relayBtn4, 4, relayBtn4.isChecked());
            }
        });
        buttons.put(4, relayBtn4);

		final ToggleButton relayBtn5 = (ToggleButton) findViewById(R.id.relayButton5);
		relayBtn5.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRelay(relayBtn5, 5, relayBtn5.isChecked());
            }
        });
        buttons.put(5, relayBtn5);
	}

	private void setRelay(ToggleButton source, int relayNumber, boolean isChecked) {
		_relayDriver.setRelayValue(relayNumber, isChecked);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
