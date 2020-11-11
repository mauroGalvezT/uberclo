package com.galvez.uberclone.providers;

import com.galvez.uberclone.models.Client;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {
    DatabaseReference databaseReference;
    public ClientProvider(){
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");

    }

    public Task<Void> create(Client client){

        Map<String,Object> map=new HashMap<>();
        map.put("nombre",client.getNombre());
        map.put("email",client.getEmail());
        return databaseReference.child(client.getId()).setValue(map);
    }

}
