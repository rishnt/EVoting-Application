package com.example.onlinevotingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class SignUp extends AppCompatActivity {

    private EditText userName,userPassword,userEmail,userNationalID;
    private Button signUpBtn;
    private FirebaseAuth mAuth;
     public static final String PREFERENCES="prefKey";
     public static final String Name="nameKey";
      public static final String Email="emailKey";
       public static final String Password="passwordKey";
       public static final String NationalId="nationalId";


       SharedPreferences sharedPreferences;
    String name,password,email,nationalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPreferences= getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);


        findViewById(R.id.have_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        userName=findViewById(R.id.user_name);
        userEmail=findViewById(R.id.user_Email);
        userPassword=findViewById(R.id.user_pasword);
        userNationalID=findViewById(R.id.user_national_id);
        signUpBtn=findViewById(R.id.signup_btn);

        mAuth=FirebaseAuth.getInstance();


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 name=userName.getText().toString().trim();
                 password=userPassword.getText().toString().trim();
                 email=userEmail.getText().toString().trim();
                 nationalId=userNationalID.getText().toString().trim();


                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches()
                              && !TextUtils.isEmpty(nationalId)){

                    createUser(email,password);

                }
                else{
                    Toast.makeText(SignUp.this, "Please Enter Your Credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(SignUp.this, "user Created ", Toast.LENGTH_SHORT).show();
                    verifyEmail();

                }
                else{
                    Toast.makeText(SignUp.this, "Failed! Please Try Again", Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void verifyEmail() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        SharedPreferences.Editor pref=sharedPreferences.edit();
                        pref.putString(Name,name);
                        pref.putString(Password,password);
                        pref.putString(Email,email);
                        pref.putString(NationalId,nationalId);
                        pref.commit();

                        //email sent



                        Toast.makeText(SignUp.this, "Email Sent", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SignUp.this,MainActivity.class));
                        finish();
                    }
                    else{
                        mAuth.signOut();
                        finish();
                    }
                }
            });

        }
    }
}