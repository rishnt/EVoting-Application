package com.example.onlinevotingapplication;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText userEmail,userPassword;
    private Button loginBtn;
    private TextView forgetPassword;
    private FirebaseAuth mAuth;
    public static final String PREFERENCES="prefKey";
    public static final String Name="nameKey";
    public static final String Email="emailKey";
    public static final String Password="passwordKey";
    public static final String NationalId="nationalId";
    public static final String UploadData="uploaddata";

    SharedPreferences sharedPreferences;

    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences= getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
         firebaseFirestore=FirebaseFirestore.getInstance();
        findViewById(R.id.dont_have_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignUp.class));
            }
        });

        userEmail=findViewById(R.id.user_Email);
        userPassword=findViewById(R.id.user_pasword);
        loginBtn=findViewById(R.id.login_btn);
        forgetPassword=findViewById(R.id.forget_password);
        mAuth=FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=userEmail.getText().toString().trim();
                String password=userPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            verifyEmail();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "User not Present", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
           findViewById(R.id.forget_password).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
startActivity(new Intent(MainActivity.this, ForgetPassword.class));
               }
           });
    }

    private void verifyEmail() {


        FirebaseUser user=mAuth.getCurrentUser();
        assert user !=null;
        if(user.isEmailVerified()) {

            boolean bol = sharedPreferences.getBoolean(UploadData, false);
            if (bol) {
                // if eail is verified and data already uploaded
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            } else {


                String name = sharedPreferences.getString(Name, null);
                String password = sharedPreferences.getString(Password, null);
                String email = sharedPreferences.getString(Email, null);
                String nationalid = sharedPreferences.getString(NationalId, null);

                //verify email by sending email,then store data in sharedd preferences and login then we upload data to firebase

                if (name != null && password != null && email != null && nationalid != null) {


                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("nationalId", nationalid);

                    firebaseFirestore.
                            collection("Users").
                            document("peronal details").set(map).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                                    SharedPreferences.Editor pref = sharedPreferences.edit();
                                    pref.putBoolean(UploadData, true);
                                    pref.commit();


                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Data Not Stored", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                } else {
                    Toast.makeText(this, "user Data not found", Toast.LENGTH_SHORT).show();
                }
            }


            }


        else{
            mAuth.signOut();
            Toast.makeText(this, "please verify your email", Toast.LENGTH_SHORT).show();
        }
    }
}