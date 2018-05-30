package triangel.ipp;

//Importerar alla bibliotek som behövs.
import android.app.Activity;
import android.content.Intent;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Main
public class MainActivity extends AppCompatActivity {

    //Deklarerar alla variabler som behövs i applikationen.
    FirebaseDatabase database;
    DatabaseReference reference;
    public Button button;
    public TextView text;
    public int times;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public boolean permission;
    double c_lat, c_long;
    double i_lat, i_long;

    //Allt som ska köras dvs funktioner och databas hantering.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //Deklarerar databasen.
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        //Skappar en eventlistener som kollar efter förändringar i databasen på ett objekt.
        reference.child("Lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Om datan ändras i databasen så går den och sparar ner den nya ändringen i string form och sickar sedan till
                // variabeln i_lat som ska användas i beräkning av avstånd.
                String temp = dataSnapshot.getValue(String.class);
                i_lat = Double.parseDouble(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Samma som för evenlistner men nu för longitud.
        reference.child("Long").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue(String.class);
                i_long = Double.parseDouble(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Kallar på funktionen runtime_permissions som kollar om applikationen har tillgång till telefonens gps.
        permission = runtime_permissions();

        //Deklarar locationmangern och locationlisternern.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Om din position ändras så går locationlisternern och hämtar longitud och latitud och sparar ner i variabler.
                c_lat = location.getLatitude();
                c_long = location.getLongitude();

                //Kallar på två funktioner som båda beräknar avståndet vara en är vår algoritm och den andra är en inbygd funktion i java.
                String First_distance =  calculateDistance();
                double Second_distance = calculateDistanceAlgorithm(c_lat, c_long, i_lat, i_long);

                //Sickar anvstånden till funktionen som kollar om du ligger inom en viss radie av olyckan.
                switch_view(First_distance, Second_distance);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            //Om gps enheten i mobilen är avslagen så sickas man till att slå på den.
            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }

    //Funktionen som kollar om vi har tillgång till gps modulen.
    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    //Kollar om vi har fått dom tillgångar som vi behöver annnars frågar vi igen.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                init();
            }
            runtime_permissions();
        }
    }

    //Första funktionen som räknar ut avståndet mellan två gps positioner. Detta är den inbyggda funktionen i java.
    public String calculateDistance() {
        float results[] = new float[10];
        Location.distanceBetween(c_lat, c_long, i_lat, i_long, results);
        float distance = results[0];
        String text = Float.toString(distance);
        return text;
    }

    //Andra funktionen som beräknar avståndet. Denna bygger på haversine formeln som används vid bärkning mellan två punkter på ett sfäriska objekt.
    public double calculateDistanceAlgorithm(double initialLat, double initialLong, double finalLat, double finalLong){
        double R = 63730;
        double deltaLat = finalLat - initialLat;
        double deltaLong = finalLong - initialLong;

        double A = (Math.sin(deltaLat/2) * Math.sin(deltaLat/2)) + Math.cos(initialLat) * Math.cos(finalLat) * (Math.sin(deltaLong/2) * Math.sin(deltaLong/2));
        double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1 - A));
        double distance2 = R*C;

        return distance2;
    }

    //Funktionen som kollar om vi är inom olyckans område.
    public void switch_view(String First_distance, double Second_distance)
    {
        double Third_distance = Double.parseDouble(First_distance);
        if ((Third_distance+Second_distance/2) < 1000 && times < 1)
        {
            times++;

            Intent toy = new Intent(MainActivity.this,Main2Activity.class);

            recreateActivityCompat(MainActivity.this);

            startActivity(toy);
        }
    }

    //Denna funktion startar om den aktiviet då vi har blivit sickade till nästa view.
    public static final void recreateActivityCompat(final Activity a)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            a.recreate();
        }
        else
        {
                final Intent intent = a.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                a.finish();
                a.overridePendingTransition(0, 0);
                a.startActivity(intent);
                a.overridePendingTransition(0,0);
        }
    }


    //Funktioen initiate som körs då kanppen på applikationen trycks ner.
    public void init(){

        //Använder mig av knappen.
        button=(Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Kallar på location managern som hämtar gps kordinater var 5 sekund.
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

                //Sätter texten på texview så att användaren förstår att applikationen arbetar.
                String message = "Söker efter olyckor...";
                text=(TextView) findViewById(R.id.text_box);
                text.setText(message);

                //Informativ toast som säger åt användaren att appen faktiskt jobbar på att hitta en olycka.
                Toast.makeText(getApplicationContext(), "Appen fortsätter söka tills den hittar en olycka", Toast.LENGTH_LONG).show();
            }
        });
    }
}