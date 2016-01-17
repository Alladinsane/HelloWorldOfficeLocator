package com.alladinsane.officelocator;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DataManager extends AsyncTask{

	static InputStream is = null;
	private static String url;
	private static boolean finished = false;
	private static ArrayList<OfficeLocation> officeLocations = new ArrayList<OfficeLocation>();
	Context context;

	DataManager(Context context)
	{
		this.context = context;
	}

	protected Object doInBackground(Object... params) {
        if(isInternetAvailable()) {
            try {
                getJSONFromUrl(url);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            getJSONfromPrefs();
        }
		return null;

	}
	private void getJSONFromUrl(String address) throws MalformedURLException {

		URL url = new URL(address);

		HttpURLConnection urlConnection;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			if(urlConnection!=null)
				is = urlConnection.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			readJsonStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    private void getJSONfromPrefs()
    {
        JSONObject jsonObject = new JSONObject();
        String Jstring = PreferenceManager.
                getDefaultSharedPreferences(context).getString("theJson","");
        try {
            jsonObject = new JSONObject(Jstring);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            readArray(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	private void readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	    StringBuilder responseStrBuilder = new StringBuilder();

	    String inputStr;
	    while ((inputStr = streamReader.readLine()) != null)
	        responseStrBuilder.append(inputStr);

	    try {
			JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
			saveJSONtoPrefs(jsonObject);
			readArray(jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	private void saveJSONtoPrefs(JSONObject jsonObject)
	{
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString("theJson",jsonObject.toString()).apply();
	}
	private void readArray(JSONObject array) throws IOException {

		JSONArray jArray;
		officeLocations.clear();
		try {
			jArray = array.getJSONArray("locations");
			
			for(int i=0; i<jArray.length(); i++){
			    JSONObject json_data = jArray.getJSONObject(i);

			    String name = json_data.getString("name");
			    String address = json_data.getString("address");
			    String address2 = json_data.getString("address2");
			    String city = json_data.getString("city");
			    String state = json_data.getString("state");
			    String zipcode = json_data.getString("zip_postal_code");
			    String phone = json_data.getString("phone");
			    String fax = json_data.getString("fax");
			    double latitude = json_data.getDouble("latitude");
			    double longitude = json_data.getDouble("longitude");
			    String image = json_data.getString("office_image");
			    
			    OfficeLocation myOfficeLocation = new OfficeLocation(name, address, address2, city, state,
			    		zipcode, phone, fax, latitude, longitude, image);
			    
			    officeLocations.add(myOfficeLocation);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finished = true;
	}
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

			return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
	public void setUrl(String url)
	{
		DataManager.url = url;
	}
	public ArrayList<OfficeLocation> getLocations()
	{
		return officeLocations;
	}
	public boolean getFinished()
	{
		return finished;
	}
}



