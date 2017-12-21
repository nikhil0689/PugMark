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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements PugMarkConstants {
    private EditText emailId;
    private EditText password;
    private Button loginButton,signUpButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailId = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        Log.d("TAG","just a message");
        loginButton = (Button) findViewById(R.id.login_submit);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validInput()){
                    authenticateUser(emailId.getText().toString().trim(),password.getText().toString().trim());
                }
            }
        });
        Log.d("","just a message");
        signUpButton = (Button) findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(signUp);
                finish();
            }
        });
    }

    private void authenticateUser(String email, String password) {
        Log.d("rew","user info: "+email+" "+password);
        progressDialog.setMessage(LOGGING);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.invalid_email_pw, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                }
            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(emailId.getText().toString())) {
            emailId.setError(ENTER_EMAIL);
            dataValid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(ENTER_PASSWORD);
            dataValid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(PASSWORD_NOT_VALID);
            dataValid = false;
        }

       return dataValid;
    }
}
