package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPatrollingDetailsActivity extends AppCompatActivity implements View.OnClickListener,PugMarkConstants {
    private Button startPatrolButton,resetButton,endPatrolButton,submitData;
    public static final String PREFS_NAME = SHARED_PREFERENCE_FILE;
    private TextView startLatitude,startLongitude,endLatitude,endLongitude,locationText,startDateTextView,startTimeTextView;
    private TextView endLocationText,endDateTextView,endTimeTextView;
    private String startLatitudePref,startLongitudePref,endLatitudePref,startDatePref,endDatePref,startTimePref,endTimePref;
    private String endLongitudePref,startLocationTextPref,endLocationTextPref;
    private SharedPreferences sharedpreferences;
    private String startDateValue,startTimeValue,endDateValue,endTimeValue;
    private double latitudeFromMapIntent,longitudeFromMapIntent,endLatitudeFromMapIntent,endLongitudeFromMapIntent;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private int START_PATROL_INTENT = 111;
    private int END_PATROL_INTENT = 222;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patrolling_details);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        locationText = (TextView) findViewById(R.id.location_text_id);
        startLatitude = (TextView) findViewById(R.id.start_latitude);
        startLongitude = (TextView) findViewById(R.id.start_longitude);
        endLatitude = (TextView) findViewById(R.id.end_latitude);
        endLongitude = (TextView) findViewById(R.id.end_longitude);
        startDateTextView = (TextView) findViewById(R.id.start_date_id);
        startTimeTextView = (TextView) findViewById(R.id.start_time_id);
        endDateTextView = (TextView) findViewById(R.id.end_date_id);
        endTimeTextView = (TextView) findViewById(R.id.end_time_id);
        endLocationText = (TextView) findViewById(R.id.location_text_end_id);
        resetButton = (Button) findViewById(R.id.clearData_id);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLatitude.setText(EMPTY_STRING);
                startLongitude.setText(EMPTY_STRING);
                endLatitude.setText(EMPTY_STRING);
                endLongitude.setText(EMPTY_STRING);
                startDateTextView.setText(EMPTY_STRING);
                startTimeTextView.setText(EMPTY_STRING);
                endDateTextView.setText(EMPTY_STRING);
                startDateTextView.setText(EMPTY_STRING);
                endTimeTextView.setText(EMPTY_STRING);
                locationText.setText(EMPTY_STRING);
                endLocationText.setText(EMPTY_STRING);
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
            }
        });
        startPatrolButton = (Button) findViewById(R.id.startPatrolButton);
        startPatrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLocation = new Intent(AddPatrollingDetailsActivity.this, MapActivity.class);
                startActivityForResult(getLocation,START_PATROL_INTENT);
            }
        });
        endPatrolButton = (Button) findViewById(R.id.endPatrolButton);
        endPatrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startLatitude.getText().toString().equalsIgnoreCase(EMPTY_STRING)){
                    Log.d("rew","latitude from map intent: "+latitudeFromMapIntent);
                    Toast.makeText(AddPatrollingDetailsActivity.this, "Start Patrol",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent getLocation = new Intent(AddPatrollingDetailsActivity.this, MapActivity.class);
                startActivityForResult(getLocation,END_PATROL_INTENT);
            }
        });
        submitData = (Button) findViewById(R.id.submitData_id);
        submitData.setOnClickListener(this);

        startLatitudePref = sharedpreferences.getString(START_LATITUDE,null);
        startLongitudePref= sharedpreferences.getString(START_LONGITUDE,null);
        endLatitudePref= sharedpreferences.getString(END_LATITUDE,null);
        endLongitudePref=sharedpreferences.getString(END_LONGITUDE,null);
        startLocationTextPref = sharedpreferences.getString(START_LOCATION_TEXT,null);
        endLocationTextPref=sharedpreferences.getString(END_LOCATION_TEXT,null);
        startDatePref = sharedpreferences.getString(START_DATE,null);
        endDatePref = sharedpreferences.getString(END_DATE,null);
        startTimePref = sharedpreferences.getString(START_TIME,null);
        endTimePref = sharedpreferences.getString(END_TIME,null);
        if(sharedpreferences != null){
            startLatitude.setText(startLatitudePref);
            startLongitude.setText(startLongitudePref);
            endLatitude.setText(endLatitudePref);
            endLongitude.setText(endLongitudePref);
            locationText.setText(startLocationTextPref);
            endLocationText.setText(endLocationTextPref);
            startDateTextView.setText(startDatePref);
            endDateTextView.setText(endDatePref);
            startTimeTextView.setText(startTimePref);
            endTimeTextView.setText(endTimePref);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ADD_PATROL_DATA_ACTIONBAR);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == START_PATROL_INTENT) {
                    latitudeFromMapIntent = data.getDoubleExtra(LATITUDE, 0.0);
                    longitudeFromMapIntent = data.getDoubleExtra(LONGITUDE, 0.0);
                    locationText.setText(R.string.start_location_textView);
                    startLatitude.setText(String.valueOf(latitudeFromMapIntent));
                    startLongitude.setText(String.valueOf(longitudeFromMapIntent));
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    DateFormat timeFormat = DateFormat.getTimeInstance();
                    Date date = new Date();
                    startDateValue = dateFormat.format(date);
                    startTimeValue = timeFormat.format(date);
                    Log.d("rew","date: "+startDateValue+" Time: "+startTimeValue);
                    startDateTextView.setText(startDateValue);
                    startTimeTextView.setText(startTimeValue);
                    editor.putString(START_LATITUDE,startLatitude.getText().toString());
                    editor.putString(START_LONGITUDE,startLongitude.getText().toString());
                    editor.putString(START_DATE,startDateTextView.getText().toString());
                    editor.putString(START_TIME,startTimeTextView.getText().toString());
                    editor.putString(START_LOCATION_TEXT,locationText.getText().toString());
                    editor.commit();
                }else if (requestCode == END_PATROL_INTENT) {
                    endLatitudeFromMapIntent = data.getDoubleExtra(LATITUDE, 0.0);
                    endLongitudeFromMapIntent = data.getDoubleExtra(LONGITUDE, 0.0);
                    endLocationText.setText(R.string.end_location_textView);
                    endLatitude.setText(String.valueOf(endLatitudeFromMapIntent));
                    endLongitude.setText(String.valueOf(endLongitudeFromMapIntent));
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    DateFormat timeFormat = DateFormat.getTimeInstance();
                    Date date = new Date();
                    endDateValue = dateFormat.format(date);
                    endTimeValue = timeFormat.format(date);
                    Log.d("rew","date: "+endDateValue+" Time: "+endTimeValue);
                    endDateTextView.setText(endDateValue);
                    endTimeTextView.setText(endTimeValue);
                    editor.putString(END_LATITUDE,endLatitude.getText().toString());
                    editor.putString(END_LONGITUDE,endLongitude.getText().toString());
                    editor.putString(END_DATE,endDateTextView.getText().toString());
                    editor.putString(END_TIME,endTimeTextView.getText().toString());
                    editor.putString(END_LOCATION_TEXT,endLocationText.getText().toString());
                    editor.commit();
                }
        }
    }

    @Override
    public void onClick(View v) {
        String name="",empid="";
        if(auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null){
            name = auth.getCurrentUser().getDisplayName().split("-")[1];
            empid = auth.getCurrentUser().getDisplayName().split("-")[0];
        }
        if(inputIsValid()){
            PatrolDataPojo patrolDataPojo = new PatrolDataPojo();
            patrolDataPojo.setName(name);
            patrolDataPojo.setEmpid(empid);
            patrolDataPojo.setStartLatitude(Double.valueOf(startLatitude.getText().toString()));
            patrolDataPojo.setStartLongitude(Double.valueOf(startLongitude.getText().toString()));
            patrolDataPojo.setStartDate(startDateTextView.getText().toString());
            patrolDataPojo.setStartTime(startTimeTextView.getText().toString());
            patrolDataPojo.setEndDate(endDateTextView.getText().toString());
            patrolDataPojo.setEndTime(endTimeTextView.getText().toString());
            patrolDataPojo.setEndLatitude(Double.valueOf(endLatitude.getText().toString()));
            patrolDataPojo.setEndLongitude(Double.valueOf(endLongitude.getText().toString()));
            mDatabase.child(PATROL_DATA).push().setValue(patrolDataPojo);
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            finish();
        }else{
            Toast.makeText(AddPatrollingDetailsActivity.this, R.string.data_not_set,Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private boolean inputIsValid() {
        boolean valid = true;
        if(startLatitude.getText().toString().equals(EMPTY_STRING)
                || startLongitude.getText().toString().equalsIgnoreCase(EMPTY_STRING)){
            valid = false;
        }else if(endLatitude.getText().toString().equals(EMPTY_STRING)
                || endLongitude.getText().toString().equalsIgnoreCase(EMPTY_STRING)){
            valid = false;
        }
        return valid;
    }
}
