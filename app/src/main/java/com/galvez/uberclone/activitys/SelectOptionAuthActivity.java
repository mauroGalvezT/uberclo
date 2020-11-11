package com.galvez.uberclone.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.galvez.uberclone.R;
import com.galvez.uberclone.activitys.client.registroActivity;
import com.galvez.uberclone.activitys.driver.RegisterDriverActivity;
import com.galvez.uberclone.includes.MyToolbar;

public class SelectOptionAuthActivity extends AppCompatActivity {
    SharedPreferences mPref;
    Button btnLogin,btnReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);

        MyToolbar.show(this,"Selecciona una opcion",true);

        mPref= getApplicationContext().getSharedPreferences("typeUser",MODE_PRIVATE);

        btnLogin=findViewById(R.id.btnLogin);
        btnReg=findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToREgister();
            }
        });
    }

    private void goToLogin() {
        Intent intent=new Intent(SelectOptionAuthActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    private void goToREgister() {
        String typeUser= mPref.getString("user","");
        assert typeUser != null;
        if(typeUser.equals("client")){
            Intent intent = new Intent(SelectOptionAuthActivity.this, registroActivity.class);
            startActivity(intent);
        }else if(typeUser.equals("driver")){
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }
    }
}