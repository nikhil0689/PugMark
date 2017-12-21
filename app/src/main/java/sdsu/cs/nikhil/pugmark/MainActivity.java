package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,PugMarkConstants {
    private ListView emergencyListView;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private Uri imageForUpload;
    private String imageEncoded;
    private FloatingActionButton addEmergencyButton;
    EmergencyAdapter listadapter;
    List<EmergencyDataPojo> emergencyDataList = new ArrayList<>();
    private TextView displayName,displayEmpId;
    private DatabaseReference mDatabase;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    String empid = null;
    String name = null;
    public static final String PREFS_NAME = SHARED_PREFERENCE_FILE;
    private static final int REQUEST_IMAGE_CAPTURE = 222;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        if(auth.getCurrentUser() != null){
            String nameFromServer = auth.getCurrentUser().getDisplayName();
            Log.d("rew","empid: "+nameFromServer);
            if (nameFromServer != null) {
                empid = nameFromServer.split("-")[0];
                name = nameFromServer.split("-")[1].toUpperCase();
            }
            Log.d("rew","empid: "+empid+" name: "+name);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        addEmergencyButton = (FloatingActionButton) findViewById(R.id.emergencyButton);
        addEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fromEmergencyButton = new Intent(MainActivity.this, EmergencyActivity.class);
                startActivity(fromEmergencyButton);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        displayEmpId = (TextView) header.findViewById(R.id.display_empid);
        displayName = (TextView) header.findViewById(R.id.display_name);
        displayName.setText(name);
        displayEmpId.setText(empid);

        emergencyListView = (ListView) findViewById(R.id.emergencyListId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emergencyDataList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                        EmergencyDataPojo emergencyDataPojo = msgSnapshot.getValue(EmergencyDataPojo.class);
                        Log.d("rew", emergencyDataPojo.getEmergencyType());
                        emergencyDataList.add(emergencyDataPojo);
                    }
                    Collections.reverse(emergencyDataList);
                    listadapter= new EmergencyAdapter(MainActivity.this,0,emergencyDataList);
                    emergencyListView.setAdapter(listadapter);
                    progressDialog.dismiss();
                }else{
                    if(listadapter != null){
                        listadapter.notifyDataSetChanged();
                    }
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, R.string.no_emergency_data_yet,Toast.LENGTH_SHORT).show();
                }
                emergencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        EmergencyDataPojo emergencyDataPojo = (EmergencyDataPojo) parent.getItemAtPosition(position);
                        Log.d("rew",((EmergencyDataPojo) parent.getItemAtPosition(position)).getEmergencyType());
                        Intent intent = new Intent(MainActivity.this, MapMarkerForEmergencyActivity.class);
                        intent.putExtra(getString(R.string.class_values),emergencyDataPojo);
                        startActivity(intent);
                    }
                });
                emergencyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final EmergencyDataPojo emergencyDataPojo = (EmergencyDataPojo) parent.getItemAtPosition(position);
                        Log.d("rew",((EmergencyDataPojo) parent.getItemAtPosition(position)).getUid());
                        final String uid = ((EmergencyDataPojo) parent.getItemAtPosition(position)).getUid();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle(ALERT);
                        alert.setMessage(DELETE_RECORD);
                        alert.setPositiveButton(DELETE, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(EMERGENCY_DATA).child(uid).removeValue();
                                listadapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        alert.show();
                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(EMERGENCY_DATA);
        people.addValueEventListener(valueEventListener);
        progressDialog.setMessage(LOADING);
        progressDialog.show();
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        getSupportActionBar().setTitle(EMERGENCY_DATA_ACTIONBAR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            Log.d("rew","here"+size);
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_patrol_menu:
                Intent addPatrolData = new Intent(MainActivity.this,AddPatrollingDetailsActivity.class);
                startActivity(addPatrolData);
                break;
            case R.id.camera_menu:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.emergency:
                Intent emergency = new Intent(MainActivity.this, EmergencyActivity.class);
                startActivity(emergency);
                break;
            case R.id.patrol:
                Intent patrol = new Intent(MainActivity.this,PatrolDataActivity.class);
                startActivity(patrol);
                break;
            case R.id.water_holes:
                Intent waterHoles = new Intent(MainActivity.this,WaterHoleActivity.class);
                startActivity(waterHoles);
                break;
            case R.id.gallery_id:
                Intent gallery = new Intent(MainActivity.this,GalleryActivity.class);
                startActivity(gallery);
                break;
            case R.id.logout:
                auth.signOut();
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == MainActivity.this.RESULT_OK) {
                    imageForUpload = data.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get(getString(R.string.data));
                    Log.d("rew","dimen"+imageBitmap.getWidth()+" "+imageBitmap.getHeight());
                    encodeBitmapAndSaveToFirebase(imageBitmap);
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    DateFormat timeFormat = DateFormat.getTimeInstance();
                    Date date = new Date();
                    final String dateValue = dateFormat.format(date);
                    final String timeValue = timeFormat.format(date);
                    Log.d("rew","date: "+dateValue+" Time: "+timeValue);
                    ImageDataPojo imageDataPojo = new ImageDataPojo();
                    imageDataPojo.setDate(dateValue);
                    imageDataPojo.setTime(timeValue);
                    imageDataPojo.setName(name);
                    imageDataPojo.setImageUrl(imageEncoded);
                    imageDataPojo.setUid(mDatabase.child(IMAGE_DATA).push().getKey());
                    mDatabase.child(IMAGE_DATA).child(imageDataPojo.getUid()).setValue(imageDataPojo);
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

}
