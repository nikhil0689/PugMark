package sdsu.cs.nikhil.pugmark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UpdateWaterHoleActivity extends AppCompatActivity implements PugMarkConstants{
    private EditText waterHoleName;
    private Spinner waterHoleSpinner;
    List<String> waterLevelList = new ArrayList<>();
    private Button update;
    private String waterHoleLevel;
    private DatabaseReference mDatabase;
    private String name=EMPTY_STRING,empid=EMPTY_STRING;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_water_hole);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null){
            name = auth.getCurrentUser().getDisplayName().split("-")[1];
            empid = auth.getCurrentUser().getDisplayName().split("-")[0];
        }
        waterHoleName = (EditText) findViewById(R.id.update_text_waterhole);
        waterHoleSpinner = (Spinner) findViewById(R.id.spinner_waterhole_update);
        Bundle intent = getIntent().getExtras();
        final WaterHolePojo waterDataPojo = (WaterHolePojo) intent.getSerializable(getString(R.string.class_values));
        Log.d("rew", "Emergency from bundle: " + waterDataPojo.getName() + " Start LAtitude: "
                + waterDataPojo.getWaterHoleName()+ " Start Longitude: " + waterDataPojo.getUid());
        waterHoleName.setText(waterDataPojo.getWaterHoleName());
        waterLevelList.add(0,waterDataPojo.getWaterLevel());
        if(waterDataPojo.getWaterLevel().equalsIgnoreCase(HIGH)){
            waterLevelList.add(1,MODERATE);
            waterLevelList.add(2,LOW);
            waterLevelList.add(3,DANGER);
        }else if(waterDataPojo.getWaterLevel().equalsIgnoreCase(MODERATE)){
            waterLevelList.add(1,HIGH);
            waterLevelList.add(2,LOW);
            waterLevelList.add(3,DANGER);
        }else if(waterDataPojo.getWaterLevel().equalsIgnoreCase(LOW)){
            waterLevelList.add(1,HIGH);
            waterLevelList.add(2,MODERATE);
            waterLevelList.add(3,DANGER);
        }else if(waterDataPojo.getWaterLevel().equalsIgnoreCase(DANGER)){
            waterLevelList.add(1,HIGH);
            waterLevelList.add(2,MODERATE);
            waterLevelList.add(3,LOW);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, waterLevelList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterHoleSpinner.setAdapter(dataAdapter);
        waterHoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                waterHoleLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        update = (Button) findViewById(R.id.update_button);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("rew","name: "+waterHoleName.getText().toString()
                        +" level: "+waterHoleLevel+" key: "+mDatabase.child(WATER_HOLE_DATA));
                if(inputIsValid()){
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    Date date = new Date();
                    String dateValue = dateFormat.format(date);
                    WaterHolePojo waterHolePojo = new WaterHolePojo();
                    waterHolePojo.setWaterHoleName(waterHoleName.getText().toString());
                    waterHolePojo.setWaterLevel(waterHoleLevel);
                    waterHolePojo.setName(name);
                    waterHolePojo.setEmpid(empid);
                    waterHolePojo.setDateValue(dateValue);
                    waterHolePojo.setUid(waterDataPojo.getUid());
                    mDatabase.child(WATER_HOLE_DATA).child(waterHolePojo.getUid()).setValue(waterHolePojo);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),R.string.enter_fields,Toast.LENGTH_SHORT).show();
                }


            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean inputIsValid() {
        boolean valid = true;
        if(TextUtils.isEmpty(waterHoleName.getText().toString())){
            waterHoleName.setError(getString(R.string.empty_name));
            valid = false;
        }
        if(waterHoleName.getText().toString().matches(CHECK_NUMBERS_REGEX)){
            waterHoleName.setError(NO_NUMBERS_MESSAGE);
            valid = false;
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
