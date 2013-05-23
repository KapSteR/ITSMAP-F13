package com.itsmap.kn10731.themeproject.simpledmiapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationService extends Service {

	private static final String TAG = "LocationService";

	public static final String BROADCAST_RECEIVER = "com.kn10731.themeproject.simpledmiapp.downloadIntentString";
	public static final String FORECAST_TEXT = "ForecastText";
	public static final String FORECAST_BITMAP = "ForecastBitmap";
	public static final String BY = "By";
	public static final String POST_NUMMER = "Postnr";
	public static final String REGION = "Region";
	public static final String INDEX = "Index";
	public static final int INDEX_REGION = 1;
	public static final int INDEX_BY = 2;

	protected LocationManager locationManager;
	private boolean isGPSEnabled = false;
	private boolean isNetworkEnabled = false;
	private boolean canGetLocation = false;
	private Location location;
	private String city = "Aarhus C";
	private String postalCode = "8000";
	private String region = "ostjylland";

	private Runnable downloadTask = new Runnable() {
		private static final String POSTAL_CODE = "postnumre";
		private static final String POLICE_COMMUNITY = "politikredse";

		public void run() {
			if (location != null) {

				//TODO: set default parameters
				
				String latitude = String.valueOf(location.getLatitude());
				String longitude = String.valueOf(location.getLongitude());

				// Get data for Region
				JSONObject jObject = getGeoData(latitude, longitude,
						POLICE_COMMUNITY);
				if (jObject != null) {
					parseRegion(jObject);
				}

				if (region != null) {
					String foreCastText = getTextForecast(region);
					if (foreCastText == null) {
						foreCastText = getString(R.string.forecastTextError);
					}
					
					Bitmap forecastBitmap = getForecastBitmap(region);
					if(forecastBitmap == null){
						Log.d(TAG,"Bitmap is null");
					}

					Intent intent = new Intent(BROADCAST_RECEIVER);
					intent.putExtra(FORECAST_TEXT, foreCastText);
					intent.putExtra(FORECAST_BITMAP, forecastBitmap);
					intent.putExtra(INDEX, INDEX_REGION);
					LocalBroadcastManager.getInstance(getBaseContext())
							.sendBroadcast(intent);
				} else {
					Log.d(TAG, "region is null");
				}

				//TODO: Get data for City
			}
		}

		private String getTextForecast(String region) {

			URL url = null;
			try {
				url = new URL(
						"http://www.dmi.dk/dmi/index/danmark/regionaludsigten/"
								+ region + ".htm");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader reader = null;
			StringBuilder builder = new StringBuilder();
			try {
				reader = new BufferedReader(new InputStreamReader(
						url.openStream(), "ISO-8859-1"));

				for (int i = 1; i < 678; i++) { // skip first 677 lines
					reader.readLine();
				}
				builder.append(reader.readLine().trim()); // Line 678 is the
															// weather Forecast

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException logOrIgnore) {
					}
			}

			try {
				String start = "<td class=\"broedtekst\"><td>";
				String end = "</td>";
				String part = builder.substring(builder.indexOf(start)
						+ start.length() + 1);
				String question = part.substring(0, part.indexOf(end));
				Log.d(TAG, question);

				return question;

			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
			return null;

		}

		private Bitmap getForecastBitmap(String region){
			Bitmap forecastBitmap = null;
			
			try {
			    URL url = new URL("http://www.dmi.dk/dmi/femdgn_" + region +".png");
			    InputStream in = new BufferedInputStream(url.openStream());
			    ByteArrayOutputStream out = new ByteArrayOutputStream();
			    byte[] buf = new byte[1024];
			    int n = 0;
			    while (-1!=(n=in.read(buf)))
			    {
			       out.write(buf, 0, n);
			    }
			    out.close();
			    in.close();
			    byte[] response = out.toByteArray();
			    forecastBitmap = BitmapFactory.decodeByteArray(response , 0, response.length);
			    return forecastBitmap;
			} catch (IOException e) {
				Log.d(TAG,e.toString());
			}
			return null;
		}
		
		public JSONObject getGeoData(String lat, String lng, String type) {
			// Takes types "postnumre" or "politikredse"

			URI myURI = null;

			try {
				myURI = new URI("http://geo.oiorest.dk/" + type + "/" + lat
						+ "," + lng + ".json");
			} catch (URISyntaxException e) {
				Log.d(TAG, e.toString());
			}
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			int timeoutConnection = 4000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 7000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpGet getMethod = new HttpGet(myURI);
			// Depends on your web service
			Log.d(TAG, myURI.getPath());

			InputStream inputStream = null;
			String result = null;
			HttpResponse response = null;
			try {
				response = httpclient.execute(getMethod);
			} catch (ClientProtocolException e) {
				Log.d(TAG, e.toString());
			} catch (SocketTimeoutException e) {
				Log.d(TAG, e.toString());
			} catch (IOException e) {
				Log.d(TAG, e.toString());
			} 
			Log.d(TAG,"http response received");
			if (response == null) {
				Log.d(TAG, "HttpResponse is null");
				// TODO: Internet connection must be enabled!!
				// UnknownHostException
				return null;
			}

			HttpEntity entity = response.getEntity();

			try {
				inputStream = entity.getContent();
			} catch (IllegalStateException e) {
				Log.d(TAG, e.toString());
			} catch (IOException e) {
				Log.d(TAG, e.toString());
			}
			// json is UTF-8 by default i believe
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(inputStream,
						"UTF-8"), 8);
			} catch (UnsupportedEncodingException e) {
				Log.d(TAG, e.toString());
			}
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				Log.d(TAG, e.toString());
			}
			result = sb.toString();
			Log.d(TAG, result);

			JSONObject jObject = null;
			try {
				jObject = new JSONObject(result);
			} catch (JSONException e) {
				Log.d(TAG, e.toString());
			}

			return jObject;
		}

		public void parsePostnumre(JSONObject jObject) {
			try {
				city = jObject.getString("navn");
				postalCode = jObject.getString("fra");
				Log.d(TAG, "By: " + city + ". Postnr: " + postalCode);
			} catch (JSONException e) {
			}

		}

		public void parseRegion(JSONObject jObject) {
			int index = 0;
			try {
				index = Integer.parseInt(jObject.getString("nr"));
			} catch (JSONException e) {
			}

			switch (index) {
			case 1:
				region = getString(R.string.nordj);
				break;
			case 10:
				region = getString(R.string.kbh);
				break;
			case 11:
				region = getString(R.string.kbh);
				break;
			case 12:
				region = getString(R.string.born);
				break;
			case 2:
				region = getString(R.string.ostj);
				break;
			case 3:
				region = getString(R.string.midtj);
				break;
			case 4:
				region = getString(R.string.sydj);
				break;
			case 5:
				region = getString(R.string.sydj);
				break;
			case 6:
				region = getString(R.string.fyn);
				break;
			case 7:
				region = getString(R.string.vestsj);
				break;
			case 8:
				region = getString(R.string.midtj);
				break;
			case 9:
				region = getString(R.string.kbh);
				break;
			default:
				Log.d(TAG, "Errornous region number");
			}
			Log.d(TAG, "Region: " + region);
		}
	};

	private Location getLocation() {
		// Get the location manager
		try {
			locationManager = (LocationManager) getApplicationContext()
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						Log.d(TAG, "Getting location with Network");
					}
				}
				// if GPS Enabled get location using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							Log.d(TAG, "Getting location with GPS");
						}
					}
				}
			}
			// TODO: if canGetLocation == false..

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

		location = getLocation();
		if (location != null) {
			String lat = String.valueOf(location.getLatitude());
			String lng = String.valueOf(location.getLongitude());
			Log.d(TAG, "Lat: " + lat + ". Lng: " + lng);
		}

		Thread backgroundThread = new Thread(downloadTask) {
			@Override
			public void run() {
				try {
					downloadTask.run();
				} finally {
				}
			}
		};
		backgroundThread.start();
	}
}
