package com.galvez.uberclone.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.galvez.uberclone.R;
import com.galvez.uberclone.activitys.client.MapClientActivity;
import com.galvez.uberclone.activitys.driver.MapDriverActivity;
import com.galvez.uberclone.activitys.driver.RegisterDriverActivity;
import com.galvez.uberclone.includes.MyToolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences mPref;
    TextInputEditText edtemail,edtpass;
    Button btnLogin;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    AlertDialog malertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyToolbar.show(this,"Login Usuario",true);
        mPref= getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);

        edtemail=findViewById(R.id.edtInputEmail);
        edtpass=findViewById(R.id.edtInputPass);
        btnLogin=findViewById(R.id.btnIngresar);

        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        malertDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("ESpere un momento").build();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    private void login() {
        String email= edtemail.getText().toString();
        String pass= edtpass.getText().toString();

        if(!email.isEmpty()&&!pass.isEmpty()){
            if(pass.length()>=6){
                malertDialog.show();
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String user=mPref.getString("user","");
                            if(user.equals("client")){
                                Intent intent = new Intent(LoginActivity.this, MapClientActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else if(user.equals("driver")){
                                Intent intent = new Intent(LoginActivity.this, MapDriverActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "DAtos incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        malertDialog.dismiss();
                    }
                });
            }
        }
    }
}