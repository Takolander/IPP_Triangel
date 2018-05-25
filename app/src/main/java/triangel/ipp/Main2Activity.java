package triangel.ipp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    public TextView titel;
    public TextView speed;
    public TextView info;
    public TextView klass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("Titel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titel=(TextView) findViewById(R.id.text_titel);
                String Titel_text = dataSnapshot.getValue(String.class);
                titel.setText(Titel_text);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("Hastighet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                speed=(TextView) findViewById(R.id.text_hastighet);
                String Hastighet = dataSnapshot.getValue(String.class);
                speed.setText(Hastighet);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                info=(TextView) findViewById(R.id.text_info);
                String information = dataSnapshot.getValue(String.class);
                info.setText(information);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("Klass").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                klass=(TextView) findViewById(R.id.text_klass);
                String Klass = dataSnapshot.getValue(String.class);
                klass.setText(Klass);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}