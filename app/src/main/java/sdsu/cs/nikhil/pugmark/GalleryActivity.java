package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements PugMarkConstants{
    private StorageReference storageReference;
    private ListView imageListView;
    private Uri imageForUpload;
    private String imageEncoded;
    FirebaseAuth auth;
    ImageAdapter listAdapter;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private List<ImageDataPojo> imageDataList = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 222;
    private String name,empid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        imageListView = (ListView) findViewById(R.id.imageListId);
        if(auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null){
            name = auth.getCurrentUser().getDisplayName().split("-")[1];
            empid = auth.getCurrentUser().getDisplayName().split("-")[0];
        }
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDataList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                        ImageDataPojo imageDataPojo = msgSnapshot.getValue(ImageDataPojo.class);
                        Log.d("rew", imageDataPojo.getUid());
                        imageDataList.add(imageDataPojo);
                    }
                    Collections.reverse(imageDataList);
                    listAdapter= new ImageAdapter(GalleryActivity.this,0,imageDataList);
                    imageListView.setAdapter(listAdapter);
                    progressDialog.dismiss();
                }else{
                    if(listAdapter != null){
                        listAdapter.notifyDataSetChanged();
                    }
                    progressDialog.dismiss();
                    Toast.makeText(GalleryActivity.this, R.string.no_images_found,Toast.LENGTH_SHORT).show();
                }
                imageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final ImageDataPojo imageDataPojo = (ImageDataPojo) parent.getItemAtPosition(position);
                        Log.d("rew",((ImageDataPojo) parent.getItemAtPosition(position)).getUid());
                        final String uid = ((ImageDataPojo) parent.getItemAtPosition(position)).getUid();
                        AlertDialog.Builder alert = new AlertDialog.Builder(GalleryActivity.this);
                        alert.setTitle(ALERT);
                        alert.setMessage(DELETE_RECORD);
                        alert.setPositiveButton(DELETE, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(IMAGE_DATA).child(uid).removeValue();
                                listAdapter.notifyDataSetChanged();
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
        DatabaseReference people = database.getReference(IMAGE_DATA);
        people.addValueEventListener(valueEventListener);
        progressDialog.setMessage(LOADING);
        progressDialog.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(GALLERY_ACTIONBAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.camera_in_gallery:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(GalleryActivity.this.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == GalleryActivity.this.RESULT_OK) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

