package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity implements View.OnClickListener, PugMarkConstants{
    private Spinner emergencyTypeSpinner,emergencySeveritySpinner;
    private Button submitEmergencyData;
    private FloatingActionButton mapButton,cameraButton;
    private TextView latitudeFromMap,longitudeFromMap;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private String emergencyType,emergencySeverity,imageEncoded;
    List<String> emergencyTypeList = new ArrayList<>();
    FirebaseAuth auth;
    private static final int REQUEST_IMAGE_CAPTURE = 222;
    private static final int INTENT = 111;
    private ImageView mImageLabel;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Uri imageForUpload;
    private String name=EMPTY_STRING,empid=EMPTY_STRING;
    private boolean defaultImage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        progressDialog = new ProgressDialog(this);
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique


        }
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mImageLabel = (ImageView) findViewById(R.id.emergency_image_view);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emergencyTypeList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                        Log.d("rew", "There are " + msgSnapshot.getValue());
                        emergencyTypeList.add(msgSnapshot.getValue().toString());
                    }
                    if(!emergencyTypeList.isEmpty()){
                        emergencyTypeList.add(0,EMERGENCY_TYPE);
                        emergencyTypeList.add(1,FOREST_FIRE);
                        emergencyTypeList.add(2,TIGER_DEATH);
                        emergencyTypeList.add(3,SUSPICIOUS_ACTIVITY);
                        loadEmergencyTypeSpinner();
                    }
                }else{
                    emergencyTypeList.add(0,EMERGENCY_TYPE);
                    emergencyTypeList.add(1,FOREST_FIRE);
                    emergencyTypeList.add(2,TIGER_DEATH);
                    emergencyTypeList.add(3,SUSPICIOUS_ACTIVITY);
                    loadEmergencyTypeSpinner();
                    //Toast.makeText(EmergencyActivity.this,"No emergency type Data",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(EMERGENCY_TYPE_DATA);
        people.addValueEventListener(valueEventListener);

        loadEmergencySeveritySpinner();
        submitEmergencyData = (Button) findViewById(R.id.submit_information);
        submitEmergencyData.setOnClickListener(this);
        latitudeFromMap = (TextView) findViewById(R.id.latitude_text);
        longitudeFromMap = (TextView) findViewById(R.id.longitude_text);
        mapButton = (FloatingActionButton) findViewById(R.id.floating_select_Location);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLocation = new Intent(EmergencyActivity.this, MapActivity.class);
                startActivityForResult(getLocation,INTENT);
            }
        });

        cameraButton = (FloatingActionButton) findViewById(R.id.floating_camera_open);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(EmergencyActivity.this.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadEmergencySeveritySpinner() {
        emergencySeveritySpinner = (Spinner) findViewById(R.id.emergency_severity_spinner);
        List<String> list = new ArrayList<String>();
        list.add(EMERGENCY_SEVERITY);
        list.add(HIGH);
        list.add(MEDIUM);
        list.add(LOW);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergencySeveritySpinner.setAdapter(dataAdapter);
        emergencySeveritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emergencySeverity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadEmergencyTypeSpinner() {
        emergencyTypeSpinner = (Spinner) findViewById(R.id.emergency_type_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, emergencyTypeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergencyTypeSpinner.setAdapter(dataAdapter);
        emergencyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emergencyType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == INTENT) {
                    double latitudeFromMapIntent = data.getDoubleExtra(LATITUDE,0.0);
                    double longitudeFromMapIntent = data.getDoubleExtra(LONGITUDE,0.0);
                    latitudeFromMap.setText(String.valueOf(latitudeFromMapIntent));
                    longitudeFromMap.setText(String.valueOf(longitudeFromMapIntent));
                }
                else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == EmergencyActivity.this.RESULT_OK) {
                    imageForUpload = data.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get(getString(R.string.data));
                    Log.d("rew","dimen"+imageBitmap.getWidth()+" "+imageBitmap.getHeight());
                    mImageLabel.setMinimumWidth(imageBitmap.getWidth()*2);
                    mImageLabel.setMinimumHeight(imageBitmap.getHeight()*2);
                    mImageLabel.setImageBitmap(imageBitmap);
                    mImageLabel.setDrawingCacheEnabled(true);
                    mImageLabel.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    mImageLabel.layout(0, 0, mImageLabel.getMeasuredWidth(), mImageLabel.getMeasuredHeight());
                    mImageLabel.buildDrawingCache();
                    encodeBitmapAndSaveToFirebase(imageBitmap);
                }
                break;
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //bitmap = Bitmap.createBitmap(mImageLabel.getDrawingCache());
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        Log.d("rew","image encoded"+imageEncoded);
    }
    @Override
    public void onClick(View v) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        Date date = new Date();
        final String dateValue = dateFormat.format(date);
        final String timeValue = timeFormat.format(date);
        Log.d("rew","date: "+dateValue+" Time: "+timeValue);
        if(auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null){
            name = auth.getCurrentUser().getDisplayName().split("-")[1];
            empid = auth.getCurrentUser().getDisplayName().split("-")[0];
        }
        if(TextUtils.isEmpty(latitudeFromMap.getText().toString()) || TextUtils.isEmpty(longitudeFromMap.getText().toString())){
            Toast.makeText(EmergencyActivity.this, R.string.location_not_set,Toast.LENGTH_SHORT).show();
            return;
        }
        final double latitude = Double.valueOf(latitudeFromMap.getText().toString());
        final double longitude = Double.valueOf(longitudeFromMap.getText().toString());
        Log.d("rew","name: "+name+" EmpID: "+empid+" emergency type: "+emergencyType+" emergency Severity: "+emergencySeverity);
        Log.d("rew","Image String: "+imageEncoded);
        if(inputIsvalid()){
            progressDialog.setMessage(UPLOADING);
            progressDialog.show();
            Log.d("rew","image for upload"+imageForUpload);
            EmergencyDataPojo emergencyDataPojo = new EmergencyDataPojo();
            emergencyDataPojo.setDate(dateValue);
            emergencyDataPojo.setTime(timeValue);
            emergencyDataPojo.setName(name);
            emergencyDataPojo.setEmpid(empid);
            emergencyDataPojo.setEmergencyType(emergencyType);
            emergencyDataPojo.setEmergencySeverity(emergencySeverity);
            emergencyDataPojo.setLatitude(latitude);
            emergencyDataPojo.setLongitude(longitude);
            emergencyDataPojo.setImageUrl(imageEncoded);
            emergencyDataPojo.setUid(mDatabase.child(EMERGENCY_DATA).push().getKey());
            mDatabase.child(EMERGENCY_DATA).child(emergencyDataPojo.getUid()).setValue(emergencyDataPojo);
            if(defaultImage == false){
                ImageDataPojo imageDataPojo = new ImageDataPojo();
                imageDataPojo.setImageUrl(imageEncoded);
                imageDataPojo.setDate(dateValue);
                imageDataPojo.setTime(timeValue);
                imageDataPojo.setName(name);
                imageDataPojo.setUid(mDatabase.child(IMAGE_DATA).push().getKey());
                mDatabase.child(IMAGE_DATA).child(imageDataPojo.getUid()).setValue(imageDataPojo);
            }
            finish();
            progressDialog.dismiss();

        }else{
            Toast.makeText(EmergencyActivity.this, R.string.emergency_data_not_set,Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private boolean inputIsvalid() {
        boolean valid = true;
        if(emergencyType.equalsIgnoreCase(EMERGENCY_TYPE)){
            valid = false;
        }
        else if(emergencySeverity.equalsIgnoreCase(EMERGENCY_SEVERITY)){
            valid = false;
        }
        else if(imageEncoded == null){
            Bitmap original = null;
            try {
                defaultImage = true;
                original = BitmapFactory.decodeStream(getAssets().open(DEFAULT_IMAGE_EMERGENCY));
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                original.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Log.d("rew","image dim: "+original.getWidth()+" "+original.getHeight());
                imageEncoded = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
                Log.d("rew","image encoded in validation: "+imageEncoded);
                valid = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_emergency_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.emergency_type_update:
                Intent addEmergencyIntent = new Intent(EmergencyActivity.this,AddEmergencyActivity.class);
                startActivity(addEmergencyIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
