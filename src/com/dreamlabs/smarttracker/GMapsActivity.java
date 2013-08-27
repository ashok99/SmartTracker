package com.dreamlabs.smarttracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapActivity;

@SuppressLint("NewApi")
public class GMapsActivity extends MapActivity {

	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(false);
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		LatLng position = null;
		if (map != null) {
			float lat = ((ShowTrackActivity.currentLat == null) ? null: Float.parseFloat(ShowTrackActivity.currentLat));
			float longg = ((ShowTrackActivity.currentLong == null) ? null: Float.parseFloat(ShowTrackActivity.currentLong));
			
			if(ShowTrackActivity.currentLat != null || ShowTrackActivity.currentLong != null) {
				position = new LatLng(lat, longg);
				map.addMarker(new MarkerOptions()
						.position(position).title("Route-4").snippet(ShowTrackActivity.timeStamp).icon(BitmapDescriptorFactory
								.fromResource(R.drawable.bus_on_map)));
			}
		}
		if(position!=null)
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 20.0f));
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}