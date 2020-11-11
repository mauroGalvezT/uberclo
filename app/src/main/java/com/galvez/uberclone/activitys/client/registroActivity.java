package com.galvez.uberclone.activitys.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.galvez.uberclone.R;
import com.galvez.uberclone.activitys.driver.MapDriverActivity;
import com.galvez.uberclone.activitys.driver.RegisterDriverActivity;
import com.galvez.uberclone.includes.MyToolbar;
import com.galvez.uberclone.models.Client;
import com.galvez.uberclone.providers.AuthProvider;
import com.galvez.uberclone.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class registroActivity extends AppCompatActivity {

    SharedPreferences mPref;

    AuthProvider authProvider;
    ClientProvider clientProvider;

    Button btnRegister;
    TextInputEditText textNombre,textEmail,textPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        MyToolbar.show(this,"Registro Usuario",true);


        textNombre=findViewById(R.id.edtNombre);
        textEmail=findViewById(R.id.edtEmail);
        textPass=findViewById(R.id.edtPass);
        btnRegister=findViewById(R.id.btnRegistroUser);

        authProvider=new AuthProvider();
        clientProvider=new ClientProvider();
        mPref= getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRegister();
            }
        });

    }

    private void clickRegister() {

        final String name = textNombre.getText().toString();
        final String email = textEmail.getText().toString();
        final String password = textPass.getText().toString();
        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                register(name,email,password);
            } else {
                Toast.makeText(this, "contraseña 6 a mas", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ingrese campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(final String name, final String email, String password) {
        authProvider.Register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Client client=new Client(id,name,email);
                    create(client);
                } else {
                    Toast.makeText(registroActivity.this, "no se pudo registrar", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void create(Client client){
        clientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(registroActivity.this, MapClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{

                    Toast.makeText(registroActivity.this, "fallo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*private void saveUser(String id,String name,String email) {

        String selectedUser=mPref.getString("user","");
        User user =new User();
        user.setEmail(email);
        user.setName(name);
        if (selectedUser.equals("driver")){
            databaseReference.child("Users").child("Drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(registroActivity.this, "registro exitoso", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(registroActivity.this, "fallos el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (selectedUser.equals("client")) {
            databaseReference.child("Users").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(registroActivity.this, "registro exitoso", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(registroActivity.this, "fallos el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }*/
}