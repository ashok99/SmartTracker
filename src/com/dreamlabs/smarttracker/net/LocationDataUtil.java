package com.dreamlabs.smarttracker.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.os.AsyncTask;

import com.dreamlabs.smarttracker.MainActivity;
import com.dreamlabs.smarttracker.persistence.DBManager;
import com.dreamlabs.smarttracker.security.DataFireWall;

/**
 * 
 * @author ashok
 * 
 */
public class LocationDataUtil {
	private static boolean canBroadCast;

	public static boolean canBroadCast() {
		return canBroadCast;
	}

	public static void setCanBroadCast(boolean canBroadCast) {
		LocationDataUtil.canBroadCast = canBroadCast;
	}

	public void postToServer(String pLattide, String pLongitude,
			Context applicationContext, boolean isEmergency) {

		String url = "";
		String severityType = "NORMAL";
		if(isEmergency) {
			severityType = "RISK";
		}
		if(MainActivity.isEscortEnabled) {
			String pmfKey = new DBManager(applicationContext).getPMFKey();
			//http://testapp.ashoksurya99.cloudbees.net/rest/escort/17.2344/78.2323/device/madsu/1376914554772/NORMAL
			url = "http://testapp.ashoksurya99.cloudbees.net/rest/escort/" + pLattide +"/"+ pLongitude + "/" + "device" + "/" + pmfKey + "/" + System.currentTimeMillis() + "/" + severityType;
		} else {
			url = "http://testapp.ashoksurya99.cloudbees.net/rest/updateLocation/"
					+ pLattide
					+ "/"
					+ pLongitude
					+ "/"
					+ DataFireWall.getNetworkName(applicationContext)
					+ "/ithas01/"
					+ System.currentTimeMillis();
		}
		

		// http://testapp.ashoksurya99.cloudbees.net/rest/updateLocation/78.47239448523523/17.40783897192094/route-4/ithas01/12PM
		new LongRunningGetIO(url).execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		String url;
		String result;

		LongRunningGetIO(String uri) {
			this.url = uri;
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = null;
			try {
					response = httpClient.execute(httpGet, localContext);
				
			} catch (Exception e) {
				StackTraceElement[] stackTrace = e.getStackTrace();
				StringBuffer buffer = new StringBuffer();
				for (StackTraceElement stackTraceElement : stackTrace) {
					buffer.append(stackTraceElement.toString());
				}
				buffer.append("Error:");
				result = buffer.toString();
			}
			if (response != null)
				result = response.toString();
			return result;
		}

		protected void onPostExecute(String results) {
		}
	}

}
