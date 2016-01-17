package com.alladinsane.officelocator;

import android.Manifest;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.AsyncTask;
        import android.widget.ImageView;

        import java.io.InputStream;

        import com.alladinsane.officelocator.OfficeLocation;
        import com.alladinsane.officelocator.R;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.model.LatLng;

/**
 * Created by todd on 1/14/16.
 */
public class OfficeActivity extends Activity {
    GoogleMap googleMap;
    MapView mapView;
    OfficeLocation myLocation = new OfficeLocation();
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);

        Button directionsButton = (Button) findViewById(R.id.directions);
        Button callButton = (Button) findViewById(R.id.call);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        myLocation = bundle.getParcelable("myLocation");
        userLocation = bundle.getParcelable("userLocation");

        createMap(myLocation.latitude, myLocation.longitude);

        fillTextViews(myLocation);

        getOfficeImage(myLocation.image);


        directionsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String url = "http://maps.google.com/maps?saddr=" + userLocation.latitude + "," + userLocation.longitude + "&daddr=" + myLocation.latitude +
                        "," + myLocation.longitude + "&mode=driving";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + myLocation.phone));
                if (ActivityCompat.checkSelfPermission(OfficeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });
    }
    private void createMap(Double latitude, Double longitude)
    {
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude
                + "&markers=" + latitude + "," + longitude + "&zoom=15&size=200x200&sensor=false";
        new DownloadImageTask((ImageView) findViewById(R.id.map))
                .execute(url);
    }
    private void fillTextViews(OfficeLocation myLocation)
    {
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(myLocation.name);

        TextView address = (TextView) findViewById(R.id.address);
        address.setText(myLocation.toStringWithoutName());
    }
    private void getOfficeImage(String imageAddress)
    {
        new DownloadImageTask((ImageView) findViewById(R.id.office_photo))
                .execute(imageAddress);
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

        bmImage.setImageBitmap(result);
    }
}

