package com.dreamlabs.smarttracker.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.os.AsyncTask;

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
			Context applicationContext) {

		String url = "http://testapp.ashoksurya99.cloudbees.net/rest/updateLocation/"
				+ pLattide
				+ "/"
				+ pLongitude
				+ "/"
				+ DataFireWall.getNetworkName(applicationContext)
				+ "/ithas01/"
				+  System.currentTimeMillis();
		
		//http://testapp.ashoksurya99.cloudbees.net/rest/updateLocation/78.47239448523523/17.40783897192094/route-4/ithas01/12PM
		new LongRunningGetIO(url).execute();

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();

		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet, localContext);
			/*
			 * HttpEntity entity = response.getEntity(); text =
			 * getASCIIContentFromEntity(entity);
			 * Toast.makeText(getApplicationContext(), "Final Result..." ,
			 * Toast.LENGTH_LONG).show();
			 */
		} catch (Exception e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			StringBuffer buffer = new StringBuffer();
			for (StackTraceElement stackTraceElement : stackTrace) {
				buffer.append(stackTraceElement.toString());
			}
			System.out.println(buffer);
		}
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		String url;

		LongRunningGetIO(String uri) {
			this.url = uri;
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(url);
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
			} catch (Exception e) {
				StackTraceElement[] stackTrace = e.getStackTrace();
				StringBuffer buffer = new StringBuffer();
				for (StackTraceElement stackTraceElement : stackTrace) {
					buffer.append(stackTraceElement.toString());
				}
			}
			return text;
		}

		protected void onPostExecute(String results) {
		}
	}

}