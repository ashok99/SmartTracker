package com.dreamlabs.smarttracker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamlabs.smarttracker.net.NetworkUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * @author ashok
 * 
 */
public class ShowTrackActivity extends ListActivity {

	GoogleMap googleMap;
	String currentLat;
	String currentLong;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String routes[] = getAllLiveTracks();

		if (routes != null && routes.length > 0) {
			// FIXME if there are no broacaster availble then it still shows
			// empty in list veiew and
			/*
			 * if(routes.length==1) System.out.println("");
			 */

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
			builder.setMessage("Aahh! No routes are currently being tracked. How about reminding your buddy to broadcast? ");
			// builder.setCancelable(true);
			builder.setPositiveButton("Cool.. Lemme just do it now",
					new OkOnClickListener());
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
					+ currentLat + "," + currentLong + ";crs=moon-2011;u=35"));
			startActivity(intent);
		}
	}

	private String[] getAllLiveTracks() {
		String routes = null;
		String url = "http://testapp.ashoksurya99.cloudbees.net/rest/getRouteInfo/";
		String liveRoutes = NetworkUtil.invokeServiceCall(url, true);
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

		String url = "http://testapp.ashoksurya99.cloudbees.net/rest/getRouteInfo/"
				+ routeName;
		String[] resluts = processResluts(NetworkUtil.invokeServiceCall(url,
				true));
		Toast.makeText(
				getApplicationContext(),
				"Retrieved latest location info. Lat=" + resluts[0]
						+ "Langitude=" + resluts[1], Toast.LENGTH_LONG).show();

		if (resluts[2] != null && resluts.length > 0) {
			long lastUpdatedTime = Long.parseLong(resluts[2]);
			long diffInMinutes = (System.currentTimeMillis() - lastUpdatedTime)
					/ (1000 * 60);

			if (diffInMinutes > 10) {
				Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Smells stale! The location info is 10 minutes older");
				//builder.setCancelable(true);
				builder.setPositiveButton("Fine. Let me see anyways",
						new OkOnClickListener());
				currentLat = resluts[0];
				currentLong = resluts[1];
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				if (resluts != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
							+ resluts[0] + "," + resluts[1] + ";crs=moon-2011;u=35"));
					startActivity(intent);
				}
			}
		}

		
	}

	/**
	 * This is very bad logic to extract the results... live with it for a while
	 * 
	 * @param inputText
	 * @return
	 */
	private String[] processResluts(String inputText) {
		String[] output = new String[3];
		if (inputText != null && !inputText.isEmpty()) {
			String[] ss = inputText.split(":");
			output[0] = ss[0];
			output[1] = ss[1];
			output[2] = ss[2];

		}
		return output;
	}
}