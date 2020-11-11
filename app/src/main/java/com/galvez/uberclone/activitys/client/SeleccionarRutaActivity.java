package com.galvez.uberclone.activitys.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.galvez.uberclone.R;
import com.galvez.uberclone.activitys.MainActivity;
import com.galvez.uberclone.activitys.driver.MapDriverActivity;

public class SeleccionarRutaActivity extends AppCompatActivity {


    Button btnEmp1,btnEmp2,btnEmp3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_ruta);
        btnEmp1=findViewById(R.id.btnEmpresa1);
        btnEmp2=findViewById(R.id.btnEmpresa2);
        btnEmp3=findViewById(R.id.btnEmpresa3);
        btnEmp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeleccionarRutaActivity.this, MapClientActivity.class);
                startActivity(intent);

            }
        });
        btnEmp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeleccionarRutaActivity.this, MapClientActivity.class);
                startActivity(intent);
            }
        });
        btnEmp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SeleccionarRutaActivity.this, MapClientActivity.class);
                startActivity(intent);
            }
        });
    }
}