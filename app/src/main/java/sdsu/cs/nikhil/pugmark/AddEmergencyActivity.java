package sdsu.cs.nikhil.pugmark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddEmergencyActivity extends AppCompatActivity implements PugMarkConstants {
    private EditText newEmergencyData;
    private Button submitEmergency;
    private DatabaseReference mDatabase;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        newEmergencyData = (EditText) findViewById(R.id.emergency_type_text);
        submitEmergency = (Button) findViewById(R.id.submit_emergency);
        submitEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emergencyValidate = newEmergencyData.getText().toString();
                if(TextUtils.isEmpty(emergencyValidate)){
                    Log.d("rew","new data: "+emergencyValidate);
                    newEmergencyData.setError(ENTER_EMERGENCY_TYPE);
                }else if(emergencyValidate.matches(CHECK_NUMBERS_REGEX)){
                    newEmergencyData.setError(NO_NUMBERS_MESSAGE);
                }else{
                    Log.d("rew","new data: "+emergencyValidate);
                    mDatabase.child(EMERGENCY_TYPE_DATA).push().setValue(emergencyValidate);
                    finish();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
