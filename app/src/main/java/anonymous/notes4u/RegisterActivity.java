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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class RegisterActivity extends Activity {

    private EditText et_register_email;
    private EditText et_register_password;
    private EditText et_register_repass;
    private Button btn_register_register;
    private String register_password;
    private String register_repass;
    private String register_email;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        et_register_email = (EditText) findViewById(R.id.et_register_email);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_repass = (EditText) findViewById(R.id.et_register_repass);
        btn_register_register = (Button) findViewById(R.id.btn_register_register);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn_register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                register_email = et_register_email.getText().toString();
                register_password = et_register_password.getText().toString();
                register_repass = et_register_repass.getText().toString();

                if(TextUtils.isEmpty(register_email)){
                    Toast.makeText(RegisterActivity.this, "Please enter your E-mail!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(register_password)){
                    Toast.makeText(RegisterActivity.this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(register_password.equals(register_repass)){
                    progressDialog.setMessage("Registering user....");
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(register_email, register_password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        DatabaseReference users = mDatabase.child("users");
                                        DatabaseReference users_info = mDatabase.child("database");
                                        firebaseUser = firebaseAuth.getCurrentUser();
                                        String[] new_mail = firebaseUser.getEmail().split("@");
                                        users_info.child(new_mail[0]).setValue(firebaseUser.getUid());
                                        users.child(firebaseUser.getUid());
                                        Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                        RegisterActivity.this.finish();
                                        Intent intent = new Intent(RegisterActivity.this, GroupsList.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Could not register.Please try again later!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(RegisterActivity.this, "Make sure you are entering correct password again!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}
