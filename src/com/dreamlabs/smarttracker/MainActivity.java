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
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dreamlabs.smarttracker.net.LocationDataUtil;
import com.dreamlabs.smarttracker.net.NetworkUtil;
import com.dreamlabs.smarttracker.persistence.DBManager;
import com.dreamlabs.smarttracker.security.DataFireWall;

/**
 * 
 * @author ashok
 * 
 */
public class MainActivity extends Activity {
	private static boolean isBroadcastEnabled;
	public static boolean isEscortEnabled;
	protected LocationManager locationManager;
	private SensorManager sensorManager;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in
																		// Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 100; // in Milliseconds. Keep it low when we have live track. or else 1 min is good.
	private long lastUpdate;
	private static final int SHAKE_THRESHOLD = 2000;
	final MediaPlayer mMediaPlayer = new MediaPlayer();
	MySensorEventListener acceleroMeterListener =  new MySensorEventListener();
	AudioManager audioManager;
	Location currentDeviceLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DataFireWall.getNetworkName(getApplicationContext());

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
		
		  sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		  
		  audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		  audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		  audioManager.setSpeakerphoneOn(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		addBroadcastListener();
		addTrackerListener();
		addHelpButtonListener();
		addEscortListener();
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
	
	private void addEscortListener() {
		final View escortView = findViewById(R.id.escort);
		final Button escortBtn = (Button) escortView;

		escortBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
					if (!isEscortEnabled) {
						sensorManager.registerListener(acceleroMeterListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
						isEscortEnabled = true;
						escortBtn.setText("eScort - ON");
						addDeviceToCloud();
						isBroadcastEnabled = true;//explicitly broad cast when device is in escort mode
						LocationDataUtil.setCanBroadCast(true);
						Toast.makeText(getApplicationContext(),
								"Escort mode is enabled successfully..!!!",
								Toast.LENGTH_LONG).show();
					} else {
						isEscortEnabled = false;
						sensorManager.unregisterListener(acceleroMeterListener);
						escortBtn.setText("eScort - OFF");
						removeDeviceFromCloud();
						LocationDataUtil.setCanBroadCast(false);
						isBroadcastEnabled = false;
						Toast.makeText(getApplicationContext(),
								"Escort mode is disabled successfully..!!!",
								Toast.LENGTH_LONG).show();
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
				if(!isInetConnectionAvailable()) {
					showInetWarning();
				} else {
					Intent intent = new Intent(MainActivity.this,
							ShowTrackActivity.class);
					startActivity(intent);
				}
				
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
		boolean isGoodToGo = true;
		try {
			Builder builder = new AlertDialog.Builder(this);
			if (!DataFireWall.isValidNetwork(getApplicationContext())) {
				isGoodToGo = false;
				builder.setMessage("Broadcasting is permitted on specified networks only.");
				builder.setPositiveButton("Ok Got It..!!",
						new OkOnClickListener());
			}
			if(!isGPSEnabled()) {
				isGoodToGo = false;
				builder.setMessage("This requires GPS. Please turn on GPS on this device");
				builder.setPositiveButton("Let me do it now",
						new OkOnClickListener());
			}
			
			if(!isInetConnectionAvailable()) {
				isGoodToGo = false;
				builder.setMessage("This requires Internet connectivity. Please check your internet connection");
				builder.setPositiveButton("Let me do it now",
						new OkOnClickListener());
			}
			
			if(!isGoodToGo) {
				AlertDialog dialog = builder.create();
				dialog.show();	
			}
			
		} catch (Exception e) {
			// If could't check the network and security check fails then no go.
			isGoodToGo = false;
		}
		return isGoodToGo;
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
	}

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			if(isEscortEnabled) {
				currentDeviceLocation = location;
			}
			
			if (isBroadcastEnabled) {
				new LocationDataUtil().postToServer(
						Double.toString(location.getLatitude()),
						Double.toString(location.getLongitude()),
						getApplicationContext(), false);
			}
				
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

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				if(isEscortEnabled) {
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


						if (speed > SHAKE_THRESHOLD) {
							Log.d("sensor", "shake detected w/ speed: " + speed);
							Toast.makeText(getApplicationContext(),
									"shake detected at speed: " + speed,
									Toast.LENGTH_SHORT).show();
							

							//1. make call
							dialEmergencyNumber();
							
							//2. Send SMS
							//sendSMS();
							
							//3. Send hazard signal to CA security monitor
							sendEmergencySignal();
							//for Demo only
							  makeSound(audioManager);
						}
						last_x = x;
						last_y = y;
						last_z = z;
					}
				}

			}
				}

		private void sendEmergencySignal() {
			if(currentDeviceLocation == null) {
				LocationManager locMgr = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
				currentDeviceLocation  = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			new LocationDataUtil().postToServer(
					Double.toString(currentDeviceLocation.getLatitude()),
					Double.toString(currentDeviceLocation.getLongitude()),
					getApplicationContext(), true);
		}

		private void makeSound(AudioManager audioManager) {
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); 
			
			
			try {
				 	 mMediaPlayer.setDataSource(getApplicationContext(), alert);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				 mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				 mMediaPlayer.setLooping(false);
				 mMediaPlayer.setVolume(0, 1);
				 try {
					 mMediaPlayer.prepare();
				} catch (Exception e) {
					e.printStackTrace();
				}
				 
				 mMediaPlayer.start();
				 try {
						Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mMediaPlayer.stop();
			  }
		}

		/**
		 * 
		 */
		private void dialEmergencyNumber() {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:8142852025"));
			startActivity(callIntent);
		}

		/**
		 * 
		 */
		private void sendSMS() {
			SmsManager smsManger = SmsManager.getDefault();
			try
			{
				if(currentDeviceLocation == null) {
					LocationManager locMgr = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
					Location lastKnownLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					lastKnownLocation.getLatitude();
					lastKnownLocation.getLongitude();
				}
				 //currentLoc = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
				
				smsManger.sendTextMessage("9985653493", null,
						"I am in trouble please help me. I am at: Lattitue="
								+ currentDeviceLocation.getLatitude()
								+ "Longitude="
								+ currentDeviceLocation.getLongitude() + "-Sent from CA SmartTracker App",
						null, null);
			}
			catch(IllegalArgumentException e) {
			}
		}
				
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isGPSEnabled() {
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isInetConnectionAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;

	}
	
	private void showInetWarning() {
		Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("This requires a internet connection. Please check your connection");
			builder.setPositiveButton("Ok Got It..!!",
					new OkOnClickListener());
			AlertDialog dialog = builder.create();
			dialog.show();	
	}
	
	private void addDeviceToCloud() {
		
		if(currentDeviceLocation == null) {
			LocationManager locMgr = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
			currentDeviceLocation  = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		
		new LocationDataUtil().postToServer(
				Double.toString(currentDeviceLocation.getLatitude()),
				Double.toString(currentDeviceLocation.getLongitude()),
				getApplicationContext(), false);
		
	}
	
	private void removeDeviceFromCloud() {
		String url = "http://testapp.ashoksurya99.cloudbees.net/rest/escort/" + new DBManager(getApplicationContext()).getPMFKey() +  "/REMOVE";
		NetworkUtil.invokeServiceCall(url, false);
	}
	
	public String getPhoneNumber() {
		String phNumbr = "Unknown";
		try {
			TelephonyManager tMgr = (TelephonyManager) getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			phNumbr = tMgr.getLine1Number();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phNumbr;

	}
}
