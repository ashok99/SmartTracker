package com.dreamlabs.smarttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dreamlabs.smarttracker.net.LocationDataUtil;
import com.dreamlabs.smarttracker.security.DataFireWall;

/**
 * 
 * @author ashok
 * 
 */
public class MainActivity extends Activity {
	private boolean isBroadcastEnabled;
	protected LocationManager locationManager;
	private SensorManager sensorManager;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 100; // in
																	// Milliseconds
	private long lastUpdate;
	private static final int SHAKE_THRESHOLD = 800;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataFireWall.getNetworkName(getApplicationContext());

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());

		/*
		 * sensorManager.registerListener(new MySensorEventListener(),
		 * sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		 * SensorManager.SENSOR_DELAY_NORMAL);
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		addBroadcastListener();
		addTrackerListener();
		addHelpButtonListener();
		return true;
	}

	private void addBroadcastListener() {
		final View broadCastView = findViewById(R.id.broadcast);
		final Button broadCastBtn = (Button) broadCastView;

		broadCastBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasPermissions()) {
					if (!isBroadcastEnabled) {
						isBroadcastEnabled = true;
						LocationDataUtil.setCanBroadCast(true);
						Toast.makeText(getApplicationContext(),
								"Broadcasting enabled successfully..!!!",
								Toast.LENGTH_LONG).show();
					} else {
						isBroadcastEnabled = false;
						LocationDataUtil.setCanBroadCast(false);
						Toast.makeText(getApplicationContext(),
								"Broadcasting disabled successfully..!!!",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});

	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
		}
	}

	private void addTrackerListener() {

		final View trackView = findViewById(R.id.track);
		final Button tButton = (Button) trackView;
		tButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ShowTrackActivity.class);
				startActivity(intent);
			}
		});
	}

	private void addHelpButtonListener() {

		final View helpView = findViewById(R.id.help);
		final Button tButton = (Button) helpView;

		tButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						HelpActivity.class);
				startActivity(intent);
			}
		});

	}

	private boolean hasPermissions() {
		boolean broadCastStatus = true;
		try {
			if (!DataFireWall.isValidNetwork(getApplicationContext())) {
				broadCastStatus = false;
				Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Broadcasting is permitted to specified networks only.");
				// builder.setCancelable(true);
				builder.setPositiveButton("Ok Got It..!!",
						new OkOnClickListener());
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		} catch (Exception e) {
			// If could't check the network and security check fails then no go.
			broadCastStatus = false;
		}
		return broadCastStatus;
	}

	@Override
	public void onResume() {
		super.onResume();

		// cancel any notification we may have received from
		// TestBroadcastReceiver
		// ((NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);

		// This demonstrates how to dynamically create a receiver to listen to
		// the location updates.
		// You could also register a receiver in your manifest.
		// final IntentFilter lftIntentFilter = new
		// IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
		// registerReceiver(lftBroadcastReceiver, lftIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregisterReceiver(lftBroadcastReceiver);
	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			if (isBroadcastEnabled)
				new LocationDataUtil().postToServer(
						Double.toString(location.getLatitude()),
						Double.toString(location.getLongitude()),
						getApplicationContext());
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		}

		public void onProviderDisabled(String s) {
		}

		public void onProviderEnabled(String s) {
		}

	}

	private class MySensorEventListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			System.out.println("");

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float x, y, z, last_x = 0, last_y = 0, last_z = 0;
			/*
			 * if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			 * Toast.makeText(getApplicationContext(), "X=" + event.values[0] +
			 * " Y=" + event.values[1] + "Z= " + event.values[2],
			 * Toast.LENGTH_LONG).show(); long timestamp = event.timestamp;
			 * 
			 * }
			 */

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				long curTime = System.currentTimeMillis();
				// only allow one update every 100ms.
				if ((curTime - lastUpdate) > 100) {
					long diffTime = (curTime - lastUpdate);
					lastUpdate = curTime;

					x = event.values[SensorManager.DATA_X];
					y = event.values[SensorManager.DATA_Y];
					z = event.values[SensorManager.DATA_Z];
					float xx = (x + y + z);
					float yy = xx - last_x;
					float zz = yy - last_y;
					float eee = zz - last_z;
					float speed = Math.abs(eee) / diffTime * 10000;

					Toast.makeText(getApplicationContext(), "speed: " + speed,
							Toast.LENGTH_SHORT).show();

					if (speed > 2000) {
						System.out.println("");
					}

					if (speed > SHAKE_THRESHOLD) {
						Log.d("sensor", "shake detected w/ speed: " + speed);
						Toast.makeText(getApplicationContext(),
								"shake detected w/ speed: " + speed,
								Toast.LENGTH_SHORT).show();
						AudioManager audioManager = (AudioManager) getApplicationContext()
								.getSystemService(Context.AUDIO_SERVICE);
						audioManager
								.setMode(AudioManager.MODE_IN_COMMUNICATION);
						audioManager.setSpeakerphoneOn(true);

						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:0377778888"));
						startActivity(callIntent);

					}
					last_x = x;
					last_y = y;
					last_z = z;
				}
			}

		}
	}

}
