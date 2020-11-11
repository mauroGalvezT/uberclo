package com.galvez.uberclone.activitys.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.galvez.uberclone.R;
import com.galvez.uberclone.includes.MyToolbar;
import com.galvez.uberclone.models.Client;
import com.galvez.uberclone.models.Driver;
import com.galvez.uberclone.providers.AuthProvider;
import com.galvez.uberclone.providers.ClientProvider;
import com.galvez.uberclone.providers.DriverProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterDriverActivity extends AppCompatActivity {


    AuthProvider authProvider;
    DriverProvider driverProvider;

    Button btnRegister;
    TextInputEditText textNombre,textEmail,textPass,textMarca,textPlaca,textidRuta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this,"Registro Conductor",true);


        textNombre=findViewById(R.id.edtNombre);
        textEmail=findViewById(R.id.edtEmail);
        textPass=findViewById(R.id.edtPass);
        textMarca=findViewById(R.id.edtMarca);
        textPlaca=findViewById(R.id.edtPlaca);
        textidRuta=findViewById(R.id.edtEmpresa);
        btnRegister=findViewById(R.id.btnRegistroUser);

        authProvider=new AuthProvider();
        driverProvider =new DriverProvider();

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
        final String marca = textMarca.getText().toString();
        final String placa = textPlaca.getText().toString();
        final String password = textPass.getText().toString();
        final String empresa = textidRuta.getText().toString();
        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()&&!marca.isEmpty() && !placa.isEmpty()) {
            if (password.length() >= 6) {
                register(name,email,password,marca,placa,empresa);
            } else {
                Toast.makeText(this, "contraseña 6 a mas", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ingrese campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(final String name, final String email, String password, final String marca, final String placa,final String empresa) {
        authProvider.Register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver=new Driver(id,name,email,marca,placa,empresa);
                    create(driver);
                } else {
                    Toast.makeText(RegisterDriverActivity.this, "no se pudo registrar", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void create(Driver driver){
        driverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(RegisterDriverActivity.this, "exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this,MapDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{

                    Toast.makeText(RegisterDriverActivity.this, "fallo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}