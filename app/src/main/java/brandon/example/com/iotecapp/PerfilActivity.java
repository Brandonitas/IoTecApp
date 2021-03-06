package brandon.example.com.iotecapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilActivity extends AppCompatActivity {

    private TextView nombre, prestados, correo, matricula;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private DatabaseReference mRef;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        nombre = (TextView) findViewById(R.id.nombre);
        prestados = (TextView) findViewById(R.id.prestados);
        correo = (TextView) findViewById(R.id.correo);
        matricula = (TextView) findViewById(R.id.matricula);
        logout = (Button) findViewById(R.id.logoutnew);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(PerfilActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.e("MYLOG",uid);

        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombre.setText(dataSnapshot.child("nombre").getValue(String.class));
                matricula.setText(dataSnapshot.child("matricula").getValue(String.class));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MYLOG", "onCancelled", databaseError.toException());
            }
        });



    }

    private void setDataToView(FirebaseUser user) {

        correo.setText(user.getEmail());

    }

    public void signOut() {
        auth.signOut();
// this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

}
