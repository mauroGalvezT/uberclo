package com.galvez.uberclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AuthProvider {
    FirebaseAuth firebaseAuth;
    public AuthProvider(){
        firebaseAuth=FirebaseAuth.getInstance();
    }
    public Task<AuthResult> Register(String email, String password){
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }
    public Task<AuthResult> Login(String email, String password){
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public void logout(){
        firebaseAuth.signOut();
    }

    public String getId(){
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    }

    public boolean existSession(){
        boolean exis=false;
        if(firebaseAuth.getCurrentUser()!=null){
            exis=true;
        }
        return exis;
    }

}
