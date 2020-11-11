package com.galvez.uberclone.providers;

import com.galvez.uberclone.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverProvider {
    DatabaseReference databaseReference;
    public DriverProvider(){
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");

    }

    public Task<Void> create(Driver driver){
        return databaseReference.child(driver.getId()).setValue(driver);
    }

}
