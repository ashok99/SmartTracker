package com.dreamlabs.smarttracker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamlabs.smarttracker.net.NetworkUtil;

/**
 * 
 * @author ashok
 * 
 */
public class ShowTrackActivity extends ListActivity {

	static String currentLat;
	static String currentLong;
	static String currentLoc;
	static String timeStamp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String routes[] = getAllLiveTracks();

		if (routes != null && routes.length > 0 && !routes[0].contains("ERROR")) {
			setListAdapter(new ArrayAdapter<String>(this,
					R.layout.available_for_track, routes));

			ListView listView = getListView();
			listView.setTextFilterEnabled(true);

			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// When clicked, show a toast with the TextView text
					handleTrackAction(((TextView) view).getText());
					// Toast.makeText(getApplicationContext(), ((TextView)
					// view).getText(), Toast.LENGTH_SHORT).show();
					// Need spinner here...
				}
			});
		} else {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Aahh! No routes are currently being tracked. How about reminding your buddy to broadcast? Colud be a network issue too...");
			// builder.setCancelable(true);
			builder.setPositiveButton("Cool.. Lemme just do it now",
					new OkOnClickListenerDoNothing());
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			showMap();
		}
	}
	
	
	private final class OkOnClickListenerDoNothing implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(ShowTrackActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	private String[] getAllLiveTracks() {
		String routes = "";
		String url = "http://testapp.ashoksurya99.cloudbees.net/rest/getRouteInfo/";
		String liveRoutes = NetworkUtil.invokeServiceCall(url, true);
		if(liveRoutes.contains("ERROR")) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Grrr! Error while retieveing bus locations. How about quick check on your network connection?");
			// builder.setCancelable(true);
			builder.setPositiveButton("Cool.. Lemme just check",
					new OkOnClickListenerDoNothing());
			AlertDialog dialog = builder.create();
			dialog.show();
		} 
		
		if (liveRoutes != null && !liveRoutes.isEmpty()) {
			routes = liveRoutes.trim();
		}

		return routes.split(",");
	}
	
	/**
	 * show the selected route in map
	 * 
	 * @param routeName
	 */
	private void handleTrackAction(CharSequence routeName) {
		currentLoc = (String) routeName;
		String url = "http://testapp.ashoksurya99.cloudbees.net/rest/getRouteInfo/"
				+ routeName;
		String[] resluts = processResluts(NetworkUtil.invokeServiceCall(url,
				true));
		

		if (resluts[2] != null && resluts.length > 0) {
			Toast.makeText(
					getApplicationContext(),
					"Retrieved latest location info. Lat=" + resluts[0]
							+ "Langitude=" + resluts[1], Toast.LENGTH_LONG).show();
			long lastUpdatedTime = Long.parseLong(resluts[2]);
			long diffInMinutes = (System.currentTimeMillis() - lastUpdatedTime)
					/ (1000 * 60);
			timeStamp = diffInMinutes + " mins before";
			currentLat = resluts[0];
			currentLong = resluts[1];
			if (diffInMinutes > 10) {
				Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Smells stale! The location info is 10 minutes older");
				builder.setPositiveButton("Fine. Let me see anyways",
						new OkOnClickListener());
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				if (resluts != null) {
					/*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
							+ resluts[0] + "," + resluts[1] + ";crs=moon-2011;u=35"));
					startActivity(intent);*/
					showMap();
				}
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Cannot display this location." , Toast.LENGTH_LONG).show();
		}
		
	}

	private void showMap() {
		if(currentLat == null || currentLat == null) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("oops! something went wrong. play again");
			//builder.setCancelable(true);
			builder.setPositiveButton("OK",
					new OkOnClickListenerDoNothing());
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			Intent intent = new Intent(ShowTrackActivity.this, GMapsActivity.class);
			startActivity(intent);
		}
	}
	

	/**
	 * 
	 * @param inputText
	 * @return
	 */
	private String[] processResluts(String inputText) {
		String[] output = new String[3];
		if (inputText != null && !inputText.isEmpty()) {
			String[] ss = inputText.split(":");
			if(ss.length > 1) {
				output[0] = ss[0];
				output[1] = ss[1];
				output[2] = ss[2];
			}
			

		}
		return output;
	}
}