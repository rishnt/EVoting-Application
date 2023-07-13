package com.example.onlinevotingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

public class Create_Candidate extends AppCompatActivity {

    private EditText candidateName,candidateParty;
    private Spinner candidateSpinner;
    private Button submitBtn;
    private String [] candPost ={"President","Vice President"};

    FirebaseFirestore firebaseFirestore;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_candidate);


        candidateName=findViewById(R.id.candidate_name);
        candidateParty=findViewById(R.id.candidate_party_name);
        candidateSpinner=findViewById(R.id.candidate_spinner);
        submitBtn=findViewById(R.id.candidate_submit_btn);

        ArrayAdapter<String> adapter=new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line,candPost);

        candidateSpinner.setAdapter(adapter);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=candidateName.getText().toString().trim();
                String party=candidateParty.getText().toString().trim();
                String post=candidateSpinner.getSelectedItem().toString();

                if(!TextUtils.isEmpty(name)  && !TextUtils.isEmpty(party) && !TextUtils.isEmpty(post)){
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("party", party);
                    map.put("post", post);
                    map.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.
                            collection("Candidate")
                            .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(Create_Candidate.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Create_Candidate.this, "Data Not Stored", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }

                else{
                    Toast.makeText(Create_Candidate.this, "Enter Details", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}