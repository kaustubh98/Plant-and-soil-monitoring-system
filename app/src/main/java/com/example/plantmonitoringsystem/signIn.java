package com.example.plantmonitoringsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class signIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText e_email,e_pass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = findViewById(R.id.toolbar_sign);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign In");

        auth = FirebaseAuth.getInstance();
        e_email = findViewById(R.id.email_edit);
        e_pass = findViewById(R.id.pass_edit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signin In...");
    }

    //sign in
    public void signin(View view){
        //get the details
        String email = e_email.getText().toString();
        String pass = e_pass.getText().toString();

        //sign in
        if(validateUI(email,pass)){
            progressDialog.show();
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Signed in",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(signIn.this,MainActivity.class);
                            startActivity(intent);
                        }else {
                            progressDialog.dismiss();
                            try{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }catch(NullPointerException ne){
                                ne.printStackTrace();
                                Toast.makeText(getApplicationContext(),"System Error occured while signin in...", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        }

    //validating Entries in UI
    private boolean validateUI(String email, String pass) {
        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            e_email.setError("Please Enter a valid Email Address");
            return false;
        }

        if(TextUtils.isEmpty(pass)){
            e_pass.setError("Please Enter Password");
            return false;
        }
        return true;
    }

    //create account
    public void CreateAccount (View view){
        Intent i = new Intent(signIn.this,CreateAccount.class);
        startActivity(i);
    }

}