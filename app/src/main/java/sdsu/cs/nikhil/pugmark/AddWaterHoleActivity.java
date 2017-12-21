package sdsu.cs.nikhil.pugmark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddWaterHoleActivity extends AppCompatActivity implements PugMarkConstants{
    private EditText waterHoleName;
    private Spinner waterLevelSpinner;
    private String waterLevel;
    private Button submitWaterHole;
    private DatabaseReference mDatabase;
    private String name=EMPTY_STRING,empid=EMPTY_STRING;
    FirebaseAuth auth;
    List<String> waterLevelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water_hole);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null){
            name = auth.getCurrentUser().getDisplayName().split("-")[1];
            empid = auth.getCurrentUser().getDisplayName().split("-")[0];
        }
        waterHoleName = (EditText) findViewById(R.id.waterHoleName);
        waterLevelSpinner = (Spinner) findViewById(R.id.spinnerWaterLevel);
        populateWaterLevelSpinner();
        submitWaterHole = (Button) findViewById(R.id.submitWaterHole);
        submitWaterHole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputIsValid()){
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    Date date = new Date();
                    String dateValue = dateFormat.format(date);
                    WaterHolePojo waterHolePojo = new WaterHolePojo();
                    waterHolePojo.setWaterHoleName(waterHoleName.getText().toString());
                    waterHolePojo.setWaterLevel(waterLevel);
                    waterHolePojo.setName(name);
                    waterHolePojo.setEmpid(empid);
                    waterHolePojo.setDateValue(dateValue);
                    waterHolePojo.setUid(mDatabase.child(WATER_HOLE_DATA).push().getKey());
                    mDatabase.child(WATER_HOLE_DATA).child(waterHolePojo.getUid()).setValue(waterHolePojo);
                    finish();
                }else{
                    Toast.makeText(AddWaterHoleActivity.this,getString(R.string.data_not_set),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean inputIsValid() {
        boolean valid = true;
        if(TextUtils.isEmpty(waterHoleName.getText().toString())){
            waterHoleName.setError(ENTER_NAME_MESSAGE);
            valid = false;
        }
        if(waterHoleName.getText().toString().matches(CHECK_NUMBERS_REGEX)){
            waterHoleName.setError(NO_NUMBERS_MESSAGE);
            valid = false;
        }
        if(waterLevel.equalsIgnoreCase(SELECT_LEVEL)){
            valid = false;
        }
        return valid;
    }

    private void populateWaterLevelSpinner() {
        waterLevelList.add(0,SELECT_LEVEL);
        waterLevelList.add(1,HIGH);
        waterLevelList.add(2,MODERATE);
        waterLevelList.add(3,LOW);
        waterLevelList.add(4,DANGER);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, waterLevelList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterLevelSpinner.setAdapter(dataAdapter);
        waterLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                waterLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
