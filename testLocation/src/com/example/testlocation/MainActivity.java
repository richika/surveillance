package com.example.testlocation;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.app.Activity;
import android.view.MenuItem;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.widget.*;


@SuppressWarnings("unused")
public class MainActivity extends Activity implements LocationListener{
	
	protected LocationManager locationManager;
	TextView showCoordinates;
	TextView showPost;
	String latitude, longitude;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Setting up the View.
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showCoordinates = (TextView) findViewById(R.id.coordinates);

		// Establishing Connection with Location Manager.
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
		
	}
	void postCoordinates(String latitude, String longitude){
		String data = "latitude=" + latitude + "&longitude=" + longitude;
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://phonelocation.herokuapp.com/phone_coordinates");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
	        nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        notification("Coordinates Posted to Server!");
			//showPost = (TextView) findViewById(R.id.request);
			//showPost.setText(data);
		}
		catch(Exception e){
			notification("It snapped :( ");
			//showPost = (TextView) findViewById(R.id.request);
			//Toast toast = Toast.makeText(getApplicationContext(), "Post Unsuccessful!", Toast.LENGTH_LONG);
			//toast.show();
			e.printStackTrace();
		}
	}
	private void notification(String msg){
		Message obj = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString("notification", msg);
		obj.setData(b);
		handler.sendMessage(obj);
	}
	private final Handler handler = new Handler() {
		 
        public void handleMessage(Message msg) {
             
            String notification = msg.getData().getString("notification");
                Toast.makeText(getBaseContext(), notification ,Toast.LENGTH_LONG).show();
            }
    };


	@Override
	public void onLocationChanged(Location location) {
		// Get Location using getLatitude() and getLongitude(). 
		latitude = Double.toString(location.getLatitude());
		longitude = Double.toString(location.getLongitude());
		showCoordinates.setText("Latitude:" + latitude + ", Longitude:" + longitude);
		new Thread(new Runnable() {
		    public void run() {
		    	postCoordinates(latitude, longitude);
		    }
		  }).start();
	}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	@Override
	public void onProviderDisabled(String provider) {}
	
}