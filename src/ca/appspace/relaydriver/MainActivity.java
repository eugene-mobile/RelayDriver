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
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import ca.appspace.relaydriver.service.RelayDriverService;
import ca.appspace.relaydriver.service.RelayDriverService.LocalBinder;

public class MainActivity extends Activity {
	
	private RelayDriverService _relayDriver;
	
	private ServiceConnection _driverConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			_relayDriver = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			_relayDriver = binder.getService();
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
		relayBtn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        setRelay(relayBtn1, 1, isChecked);
		    }
		});

		final ToggleButton relayBtn2 = (ToggleButton) findViewById(R.id.relayButton2);
		relayBtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        setRelay(relayBtn2, 2, isChecked);
		    }
		});

		final ToggleButton relayBtn3 = (ToggleButton) findViewById(R.id.relayButton3);
		relayBtn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        setRelay(relayBtn3, 3, isChecked);
		    }
		});

		final ToggleButton relayBtn4 = (ToggleButton) findViewById(R.id.relayButton4);
		relayBtn4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        setRelay(relayBtn4, 4, isChecked);
		    }
		});

		final ToggleButton relayBtn5 = (ToggleButton) findViewById(R.id.relayButton5);
		relayBtn5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        setRelay(relayBtn5, 5, isChecked);
		    }
		});

	}

	private void setRelay(ToggleButton source, int relayNumber, boolean isChecked) {
		boolean result = _relayDriver.setRelayValue(relayNumber, isChecked);
		if (result==isChecked) {
			Toast.makeText(this, "Relay #"+relayNumber+" set to "+isChecked, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Error changing relay "+relayNumber+" value", Toast.LENGTH_SHORT).show();
			source.setChecked(result);
		}
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
