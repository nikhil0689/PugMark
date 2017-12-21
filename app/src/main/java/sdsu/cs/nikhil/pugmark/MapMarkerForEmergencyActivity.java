package sdsu.cs.nikhil.pugmark;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarkerForEmergencyActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, PugMarkConstants {
    private GoogleMap mMap;
    private double latitude,longitude;
    private String name,empid,date,time,emergencyType,severity,imageEncoded;
    private LatLng locations;
    private Bitmap imageDecoded;
    private View myContentsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_marker_for_emergency);
        Bundle intent = getIntent().getExtras();
        EmergencyDataPojo emergencyDataPojo = (EmergencyDataPojo) intent.getSerializable(getString(R.string.class_values));
        Log.d("rew", "Emergency from bundle: " + emergencyDataPojo.getEmergencyType() + " LAtitude: "
                + emergencyDataPojo.getLatitude() + " Longitude: " + emergencyDataPojo.getLongitude());
        name = emergencyDataPojo.getName();
        empid = emergencyDataPojo.getEmpid();
        date = emergencyDataPojo.getDate();
        time = emergencyDataPojo.getTime();
        emergencyType = emergencyDataPojo.getEmergencyType();
        severity = emergencyDataPojo.getEmergencySeverity();
        imageEncoded = emergencyDataPojo.getImageUrl();
        if(imageEncoded != null){
            imageDecoded = decodeFromFirebaseBase64(imageEncoded);
            Log.d("rew","dimensions: "+imageDecoded.getWidth()+" X "+imageDecoded.getHeight());
        }
        severity = emergencyDataPojo.getEmergencySeverity();
        latitude = emergencyDataPojo.getLatitude();
        longitude = emergencyDataPojo.getLongitude();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public Bitmap decodeFromFirebaseBase64(String imageEncoded) {
       BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        BitmapFactory.decodeResource(getResources(), R.id.image_in_map, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        byte[] decodedByteArray = android.util.Base64.decode(imageEncoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locations = new LatLng(latitude,longitude);
        Log.d("rew","marker value :"+name);
        mMap.addMarker(new MarkerOptions().position(locations).title(name));
        CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(locations, 15);
        mMap.moveCamera(newLocation);
        mMap.setInfoWindowAdapter(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        myContentsView = getLayoutInflater().inflate(R.layout.map_info_window_emergency,null);
        ImageView imageView = (ImageView) myContentsView.findViewById(R.id.image_in_map);
        imageView.setImageBitmap(imageDecoded);
        TextView emergencyText = (TextView) myContentsView.findViewById(R.id.emergency_type_map);
        emergencyText.setText(emergencyType+" : "+severity);
        TextView dateAndTime = (TextView) myContentsView.findViewById(R.id.date_time_map);
        dateAndTime.setText(date+" : "+time);
        TextView employee = (TextView) myContentsView.findViewById(R.id.employee_map);
        employee.setText(name+" : "+empid);
        return myContentsView;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
