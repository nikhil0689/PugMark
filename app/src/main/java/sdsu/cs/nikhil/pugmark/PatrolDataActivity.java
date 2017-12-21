package sdsu.cs.nikhil.pugmark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatrolDataActivity extends AppCompatActivity implements PugMarkConstants{
    private FloatingActionButton addPatrol;
    private ListView patrolListView;
    private ProgressDialog progressDialog;
    List<PatrolDataPojo> patrolDataList = new ArrayList<>();
    PatrolAdapter listadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_data);
        progressDialog = new ProgressDialog(this);
        addPatrol = (FloatingActionButton) findViewById(R.id.patrolButton);
        addPatrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPatrolData = new Intent(PatrolDataActivity.this,AddPatrollingDetailsActivity.class);
                startActivity(addPatrolData);
            }
        });
        patrolListView = (ListView) findViewById(R.id.patrolListId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patrolDataList.clear();
                Log.d("rew", "There are " + dataSnapshot.getChildrenCount() + " people");
                if(dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                        PatrolDataPojo patrolDataPojo = msgSnapshot.getValue(PatrolDataPojo.class);
                        Log.d("rew", patrolDataPojo.getStartDate());
                        patrolDataList.add(patrolDataPojo);
                    }
                    Collections.reverse(patrolDataList);
                    listadapter= new PatrolAdapter(PatrolDataActivity.this,0,patrolDataList);
                    patrolListView.setAdapter(listadapter);
                   patrolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            PatrolDataPojo patrolDataPojo = (PatrolDataPojo) parent.getItemAtPosition(position);
                            Log.d("rew",((PatrolDataPojo) parent.getItemAtPosition(position)).getStartDate());
                            Intent intent = new Intent(PatrolDataActivity.this, MapMarkerForPatrolActivity.class);
                            intent.putExtra(getString(R.string.class_values),patrolDataPojo);
                            startActivity(intent);
                        }
                    });
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(PatrolDataActivity.this, R.string.no_patrol_data_yet,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference people = database.getReference(PATROL_DATA);
        people.addValueEventListener(valueEventListener);
        progressDialog.setMessage(LOADING);
        progressDialog.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(PATROL_DATA_ACTIONBAR);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
