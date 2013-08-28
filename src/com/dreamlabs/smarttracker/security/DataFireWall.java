package com.dreamlabs.smarttracker.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class DataFireWall {
	private static String networkName =  "NOT_AVAILABLE";
	static List<String> networks = new ArrayList<String>();
	private static String networkSSIDs[]  = {"route-1" , "route-2", "route-3", "route-4", "route-5", "route-6", "route-7", "route-8", "route-9", "route-10" , "DreamLabs", "Excellence", "MOBILE_DATA_40413"};
	
	static {
		networks.addAll(Arrays.asList(networkSSIDs));
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static String getNetworkName(Context ctx) {
		if (networkName == null|| networkName.equalsIgnoreCase("NOT_AVAILABLE")) {
			ConnectivityManager connManager;
			NetworkInfo networkInfo;
			try {
				connManager = (ConnectivityManager) ctx
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				networkInfo = connManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if (networkInfo.isConnected()) {
					final WifiManager wifiManager = (WifiManager) ctx
							.getSystemService(Context.WIFI_SERVICE);
					final WifiInfo connectionInfo = wifiManager
							.getConnectionInfo();
					if (connectionInfo != null
							&& !(connectionInfo.getSSID().equals(""))) {
						networkName = connectionInfo.getSSID();
					}
				} else {
					networkInfo = connManager
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					if (networkInfo.isConnected()) {
						networkInfo.getExtraInfo();

						TelephonyManager tel = (TelephonyManager) ctx
								.getSystemService(Context.TELEPHONY_SERVICE);
						if (tel != null)
							networkName = "MOBILE_DATA_"
									+ tel.getNetworkOperator();
					}
				}
			} catch (Exception e) {

			}
		}
		return networkName;
	}
	
	
	/**
	 * Check if device connected to CA wifi or not. For now hard coding network names but have service to verify the validity on server
	 */
	public static boolean isValidNetwork(Context ctx) {
		String connectedTo = getNetworkName(ctx);
		return networks.contains(connectedTo);
	}
}
