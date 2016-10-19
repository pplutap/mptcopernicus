package com.be4code.mptcopernicus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.be4code.mptcopernicus.json.JSonParse;

public class SplashActivity extends Activity implements LocationListener {

	public boolean isReady = false;

	protected String streetNumber;
	protected String city;
	protected String address;

	static ImageView smallSpinner;
	static TextView progressText;

	public EditText streetNameInput;
	public EditText streetNumberInput;
	public EditText cityInput;
	public EditText phoneInput;
	public EditText timeInput;
	public EditText passwordInput;
	public boolean foundAddress = false;
	
	public Button signUpButton;
	public Button phoneCall;

	String currentDateandTime;
	JSONObject jsonArray;
	MyLocation myLocation;
	
	protected LocationManager locationManager;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

	
	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS jest wyłączony. Czy chcesz włączyć GPS?")
				.setCancelable(false)
				.setPositiveButton("Przejdź do ustawień GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Anuluj",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		phoneCall = (Button) findViewById(R.id.phoneCall);

		streetNameInput = (EditText) findViewById(R.id.street_name);
		streetNumberInput = (EditText) findViewById(R.id.street_number123);
		cityInput = (EditText) findViewById(R.id.city);
		phoneInput = (EditText) findViewById(R.id.phone);
		timeInput = (EditText) findViewById(R.id.time);
		streetNumberInput.requestFocus();
		passwordInput = (EditText) findViewById(R.id.password);
		signUpButton = (Button) findViewById(R.id.signUpButton);
		smallSpinner = (ImageView) findViewById(R.id.imageView1);
		progressText = (TextView) findViewById(R.id.progress_text);

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		currentDateandTime = sdf.format(new Date());
		timeInput.setText(currentDateandTime);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        locationManager.requestLocationUpdates(
        		LocationManager.GPS_PROVIDER, 
        		MINIMUM_TIME_BETWEEN_UPDATES, 
        		MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
        		this
        );
        
		Animation rotation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.rotate_spinner);
		rotation.setRepeatCount(Animation.INFINITE);
		smallSpinner.startAnimation(rotation);
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		} else {
			showGPSDisabledAlertToUser();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		signUpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplication(),
						MessageReceiverActivity.class));
				finish();
				String phoneNo = "691999192";
				String sms = address+" "+streetNumber+", "+city+"\nUwagi: "+passwordInput.getText();
				try {
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phoneNo, null, sms, null, null);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"Wysyłanie wiadomości SMS nie powiodło się. Spróbuj ponownie.",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});

		phoneCall.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:5619191"));
				startActivity(callIntent);
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			streetNameInput.setText(address);
			streetNumberInput.setText(streetNumber);
			cityInput.setText(city);
			
			phoneInput.setText(getMyPhoneNumber());
			phoneInput.setEnabled(true);
			cityInput.setEnabled(true);
			smallSpinner.clearAnimation();
			smallSpinner.setImageResource(R.drawable.action_bar_glyph_done);
			progressText.setText(R.string.success_data);
			signUpButton.setEnabled(true);
		}
	};

	public static String formatAddressCityLast(Address paramAddress) {
		StringBuilder localStringBuilder = new StringBuilder();
		String str1 = paramAddress.getThoroughfare();
		appendConditionaly(localStringBuilder, null, str1, "");
		String str2 = paramAddress.getThoroughfare();
		String str3 = paramAddress.getFeatureName();
		appendConditionaly(localStringBuilder, str2, str3, " ");
		String str4 = paramAddress.getFeatureName();
		String str5 = paramAddress.getLocality();
		appendConditionaly(localStringBuilder, str4, str5, ", ");
		return localStringBuilder.toString();
	}

	private static void appendConditionaly(StringBuilder paramStringBuilder, String paramString1, String paramString2, String paramString3)
	{
	    if (paramString2 == null)
	      return;
	    if (paramString2.length() <= 0)
	      return;
	    if (paramString2.equals(paramString1))
	      return;
	    if (paramString1 != null)
	    	paramStringBuilder.append(paramString3);
	    String str = paramString2.trim();
	    paramStringBuilder.append(str);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Czy wyłączyć aplikację?")
					.setCancelable(false)
					.setPositiveButton("Tak",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SplashActivity.this.finish();
									stopListening();
								}
							})
					.setNegativeButton("Nie",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void stopListening() {
        if (locationManager != null)
        	locationManager.removeUpdates(this);
    }
	
	private String getMyPhoneNumber() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}

	public boolean networkChecking() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (networkChecking()) {
			// headquartersConnection.connectToHeadquarter();
			// mask.setVisibility(View.GONE);
		} else {
			Toast.makeText(getApplicationContext(),
					"Brak połączenia z internetem.", Toast.LENGTH_SHORT).show();
			progressText.setText("Brak połączenia!");
		}
	}

	public void updateAddress(Double latitude, Double longitude) {

	    try {
	        URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true&language=pl");
	        URLConnection urlc = url.openConnection();
	        //BufferedInputStream buffer = new BufferedInputStream(urlc.getInputStream());
	        BufferedReader buffer = new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF8"));
	        StringBuilder builder = new StringBuilder();
	        int byteRead;
	
	        while ((byteRead = buffer.read()) != -1)
	            builder.append((char) byteRead);
	
	        buffer.close();
	
	        String text=builder.toString();
	        JSONObject jsonRes = new JSONObject(text);
	        JSONArray jsonResArray = jsonRes.getJSONArray("results");
	        JSONObject jsonComponents = jsonResArray.getJSONObject(0);
	        JSONArray jsonComponentsArray = jsonComponents.getJSONArray("address_components");

	        int howMuchString = jsonComponentsArray.length();
	        
		        for(int i = 0; i < howMuchString; i++) {
		        	JSONObject temp = new JSONObject(jsonComponentsArray.getString(i));
		        	String types = temp.getString("types");
		        		if(types.indexOf("locality") != -1) {
		        			String tempCity = temp.getString("long_name");
		        			city = tempCity;
		        		}
		        		
		        		if(types.indexOf("route") != -1) {
		        			String tempRoute = temp.getString("long_name");
		        			address = tempRoute;
		        		}
		        		
		        		if(types.indexOf("street_number") != -1) {
		        			String tempNumber = temp.getString("long_name");
		        			streetNumber = tempNumber;
		        		}
		        }
	        

		        handler.sendEmptyMessage(0);
		        
	    } catch(IOException e) {
	
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Nie udało się odnaleźć Twojej lokalizacji.", 3000).show();
	    }

	}
	
	public void onLocationChanged(Location location) {
		//if(foundAddress == false) {
			updateAddress(location.getLatitude(), location.getLongitude());
	    //  foundAddress = true;
		//}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}