package sdsu.cs.nikhil.pugmark;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

public class MapMarkerForPatrolActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double startLatitude,startLongitude;
    private double endLatitude,endLongitude;
    private String name,empid,startDate,startTime,endDate,endTime;
    private LatLng startLocation,endLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_marker_for_patrol);
        Bundle intent = getIntent().getExtras();
        PatrolDataPojo patrolDataPojo = (PatrolDataPojo) intent.getSerializable(getString(R.string.class_values));
        Log.d("rew", "Emergency from bundle: " + patrolDataPojo.getName() + " Start LAtitude: "
                + patrolDataPojo.getStartLatitude() + " Start Longitude: " + patrolDataPojo.getStartLongitude());
        name = patrolDataPojo.getName();
        empid = patrolDataPojo.getEmpid();
        startLatitude = patrolDataPojo.getStartLatitude();
        startLongitude = patrolDataPojo.getStartLongitude();
        endLatitude = patrolDataPojo.getEndLatitude();
        endLongitude = patrolDataPojo.getEndLongitude();
        startDate = patrolDataPojo.getStartDate();
        startTime = patrolDataPojo.getStartTime();
        endDate = patrolDataPojo.getEndDate();
        endTime = patrolDataPojo.getEndTime();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startLocation = new LatLng(startLatitude,startLongitude);
        endLocation = new LatLng(endLatitude,endLongitude);
        Log.d("rew","marker value :"+name);
        String marker1 = name+" - "+startDate+" - "+startTime;
        String marker2 = name+" - "+endDate+" - "+endTime;
        Marker map = mMap.addMarker(new MarkerOptions().position(startLocation).title(marker1));
        Marker map2 = mMap.addMarker(new MarkerOptions().position(endLocation).title(marker2));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(map.getPosition());
        builder.include(map2.getPosition());
        LatLngBounds bounds = builder.build();
        Log.d("rew","bounds: "+bounds.toString());
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        CameraUpdate newLocation = CameraUpdateFactory.newLatLngZoom(startLocation, 15);
        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dash(30), new Gap(20), new Dash(30), new Gap(20));
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(startLocation)
                .add(endLocation)
                .width(25)
                .color(Color.BLACK)
                .geodesic(true);
        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setPattern(pattern);
        mMap.moveCamera(newLocation);
        mMap.animateCamera(newLocation);
    }
}
