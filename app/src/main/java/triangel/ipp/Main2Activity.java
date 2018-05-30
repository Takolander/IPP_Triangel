package triangel.ipp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {

    //Deklarerar alla variabler som behövs i denna view.
    FirebaseDatabase database;
    DatabaseReference reference;
    public TextView Title;
    public TextView Speed;
    public TextView Information;
    public TextView Class;

    //Main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        //Likadana eventlisteners som finns på start skärmen. Dessa har till uppgift att hämta information om olyckan.
        reference.child("Title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Title=(TextView) findViewById(R.id.text_Title);
                String Title_text = dataSnapshot.getValue(String.class);
                Title.setText(Title_text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("Speed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Speed=(TextView) findViewById(R.id.text_Speed);
                String Speed_text = dataSnapshot.getValue(String.class);
                Speed.setText(Speed_text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("Information").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Information=(TextView) findViewById(R.id.text_Information);
                String Information_text = dataSnapshot.getValue(String.class);
                Information.setText(Information_text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("Class").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Class=(TextView) findViewById(R.id.text_Class);
                String Class_text = dataSnapshot.getValue(String.class);
                Class.setText(Class_text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Gör så att det dycker upp en tillbacka knapp uppe i vänstra hörnet så att det är lättare att förstå att man kan backa tillbacka.
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //Funktionen som sickar tillbacka dig till startsidan om du trycker på tillbacka knappen.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

         if (id == android.R.id.home)
         {
             this.finish();
         }
         return super.onOptionsItemSelected(item);
    }
}