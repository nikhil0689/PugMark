package sdsu.cs.nikhil.pugmark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity implements PugMarkConstants{
    private EditText emailId,password,name,employeeId;
    private Button submitData,resetData;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        emailId = (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        name = (EditText) findViewById(R.id.signup_name);
        employeeId = (EditText) findViewById(R.id.signup_eid);
        submitData = (Button) findViewById(R.id.submit_information);
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String dispName = name.getText().toString().trim();
                String eid = employeeId.getText().toString().trim();
                final String displayName = eid.concat("-").concat(dispName);
                if(validInput()){
                    Log.d("rew","user data: "+email+" "+pass+" "+displayName);
                    progressDialog.setMessage(LOGGING);
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                                emailId.setError(getString(R.string.invalid_email_id));
                                progressDialog.dismiss();
                            } else {
                                FirebaseUser user = auth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build();
                                try {
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("rew", "User profile updated.");
                                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                        progressDialog.dismiss();
                                                        finish();
                                                    }
                                                }
                                            });
                                } catch (NullPointerException e) {
                                    Log.d("rew", "failed");
                                }
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), R.string.enter_fields,Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        resetData = (Button) findViewById(R.id.reset_button);
        resetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId.setText(EMPTY_STRING);
                password.setText(EMPTY_STRING);
                name.setText(EMPTY_STRING);
                employeeId.setText(EMPTY_STRING);
            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(emailId.getText().toString())) {
            emailId.setError(getString(R.string.enter_email_id));
            dataValid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(getString(R.string.enter_password));
            dataValid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(getString(R.string.min_pw_characters));
            Toast.makeText(getApplicationContext(), R.string.password_short,
                    Toast.LENGTH_SHORT).show();
            dataValid = false;
        }

        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError(ENTER_NAME_MESSAGE);
            dataValid = false;
        }
        if(name.getText().toString().matches(CHECK_NUMBERS_REGEX)){
            name.setError(NO_NUMBERS_MESSAGE);
            dataValid = false;
        }
        if(TextUtils.isEmpty(employeeId.getText().toString())){
            employeeId.setError(getString(R.string.enter_empId));
            dataValid = false;
        }
        return dataValid;
    }
}
