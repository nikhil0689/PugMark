package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaterHoleActivity extends AppCompatActivity implements PugMarkConstants{
    private FloatingActionButton addWaterHoleButton;
    private ListView waterHoleListView;
    private ProgressDialog progressDialog;
    List<WaterHolePojo> waterHoleDataList = new ArrayList<>();
    WaterHoleAdapter listadapter;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_hole);
        progressDialog = new ProgressDialog(this);
        Firebase.setAndroidContext(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        waterHoleListView = (ListView) findViewById(R.id.waterListId);
        addWaterHoleButton = (FloatingActionButton) findViewById(R.id.waterHoleButton);
        addWaterHoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addWaterHole = new Intent(WaterHoleActivity.this,AddWaterHoleActivity.class);
                startActivity(addWaterHole);
            }
        });
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                waterHoleDataList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                        WaterHolePojo waterHolePojo = msgSnapshot.getValue(WaterHolePojo.class);
                        Log.d("rew", ""+msgSnapshot.getKey() );
                        waterHoleDataList.add(waterHolePojo);
                    }
                    Collections.reverse(waterHoleDataList);
                    listadapter= new WaterHoleAdapter(WaterHoleActivity.this,0,waterHoleDataList);
                    waterHoleListView.setAdapter(listadapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(WaterHoleActivity.this, R.string.no_water_hole_data_yet,Toast.LENGTH_SHORT).show();
                }
                waterHoleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("rew", "There are " + dataSnapshot.getKey() + " people");
                        WaterHolePojo waterHolePojo = (WaterHolePojo) parent.getItemAtPosition(position);
                        Log.d("rew",((WaterHolePojo) parent.getItemAtPosition(position)).getWaterHoleName());
                        Intent intent = new Intent(WaterHoleActivity.this, UpdateWaterHoleActivity.class);
                        intent.putExtra(getString(R.string.class_values),waterHolePojo);
                        startActivity(intent);
                    }
                });
                waterHoleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final WaterHolePojo waterHolePojo = (WaterHolePojo) parent.getItemAtPosition(position);
                        Log.d("rew",((WaterHolePojo) parent.getItemAtPosition(position)).getUid());
                        final String uid = ((WaterHolePojo) parent.getItemAtPosition(position)).getUid();
                        AlertDialog.Builder alert = new AlertDialog.Builder(WaterHoleActivity.this);
                        alert.setTitle(ALERT);
                        alert.setMessage(DELETE_RECORD);
                        alert.setPositiveButton(DELETE, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(WATER_HOLE_DATA).child(uid).removeValue();
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
        DatabaseReference people = database.getReference(WATER_HOLE_DATA);
        people.addValueEventListener(valueEventListener);
        progressDialog.setMessage(LOADING);
        progressDialog.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(WATER_HOLE_ACTIONBAR);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
