package anonymous.notes4u;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class LoginActivity extends Activity {

    private TextView tv_login_register;
    private EditText et_login_email;
    private EditText et_login_password;
    private String login_email;
    private String login_password;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button btn_login_login;


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        if(firebaseAuth.getCurrentUser() != null){
            LoginActivity.this.finish();
            Intent intent = new Intent(LoginActivity.this, GroupsList.class);
            startActivity(intent);
        }

        tv_login_register = (TextView)findViewById(R.id.tv_login_register);
        tv_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        et_login_email = (EditText) findViewById(R.id.et_login_email);
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login_email = et_login_email.getText().toString();
                login_password = et_login_password.getText().toString();

                if(TextUtils.isEmpty(login_email)){
                    Toast.makeText(LoginActivity.this, "Please enter your E-mail!", Toast.LENGTH_SHORT).show();
                    et_login_password.setText("");
                    return;
                }

                if(TextUtils.isEmpty(login_password)){
                    Toast.makeText(LoginActivity.this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
                    et_login_email.setText("");
                    return;
                }

                progressDialog.setMessage("Logging in....");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(login_email, login_password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();

                                if(task.isSuccessful()){
                                    LoginActivity.this.finish();
                                    Intent intent = new Intent(LoginActivity.this, GroupsList.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(LoginActivity.this, "Could not log in, please try again!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })

                        .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }
        });



    }


}
