package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    EditText e_email,e_pass,e_name,e_contact,e_address,e_des;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //intialize variables
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        e_email = findViewById(R.id.email_create);
        e_pass = findViewById(R.id.password_create);
        e_name = findViewById(R.id.name);
        e_contact = findViewById(R.id.contactNo);

        //signout if a user is signed in
        if(user != null){
            auth.signOut();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Creating Account...");

    }

    public void createAccount (View view){
        String email = e_email.getText().toString().trim();
        String pass = e_pass.getText().toString().trim();
        String name = e_name.getText().toString().trim();
        String contact = e_contact.getText().toString().trim();


        if(Validation(email,pass,name,contact)){
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();

                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Created Your Account Successfully",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CreateAccount.this,MainActivity.class);
                        startActivity(i);
                    }

                }
            });

        }


    }

    private boolean Validation(String email,String pass,String name,String contact){

        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            e_email.setError("Please Enter a valid Email Address");
            return false;
        }

        if(TextUtils.isEmpty(pass) || pass.length()<6){
            e_pass.setError("Please Enter Password of more than 6 characters");
            return false;
        }

        if(TextUtils.isEmpty(name)){
            e_name.setError("Please Enter name");
            return false;
        }

        if(TextUtils.isEmpty(contact) || !TextUtils.isDigitsOnly(contact) || contact.length()!=10){
            e_contact.setError("Please Enter a valid contact number");
            return false;
        }

        return true;
    }
}
