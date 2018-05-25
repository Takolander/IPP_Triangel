package triangel.ipp;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    public Button button;
    public TextView text;
    public int times;


    public void init(){
        button=(Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                String message = "Söker efter olyckor...";
                text=(TextView) findViewById(R.id.text_box);
                text.setText(message);

            }
        });

    }

    private LocationManager locationManager;
    private LocationListener locationListener;
    public boolean permission;
    double c_lat, c_long;
    double i_lat, i_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("Lat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue(String.class);
                i_lat = Double.parseDouble(temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        permission = runtime_permissions();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                c_lat = location.getLatitude();
                c_long = location.getLongitude();
                String text =  calculateDistance();
                double distance = calculateDistanceAlgorithm(c_lat, c_long, i_lat, i_long);
                switch_view(text, distance);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        init();
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

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

    public String calculateDistance() {
        float results[] = new float[10];
        Location.distanceBetween(c_lat, c_long, i_lat, i_long, results);
        float distance = results[0];
        String text = Float.toString(distance);
        return text;
    }

    public double calculateDistanceAlgorithm(double initialLat, double initialLong, double finalLat, double finalLong){
        double R = 63730;
        double deltaLat = finalLat - initialLat;
        double deltaLong = finalLong - initialLong;

        double A = (Math.sin(deltaLat/2) * Math.sin(deltaLat/2)) + Math.cos(initialLat) * Math.cos(finalLat) * (Math.sin(deltaLong/2) * Math.sin(deltaLong/2));
        double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1 - A));
        double distance2 = R*C;

        return distance2;
    }

    public void switch_view(String distance, double distance2)
    {
        double distance3 = Double.parseDouble(distance);
        if ((distance3+distance2/2) < 1000 && times < 1)
        {
            times++;

            Intent toy = new Intent(MainActivity.this,Main2Activity.class);

            recreateActivityCompat(MainActivity.this);

            startActivity(toy);
        }
    }

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
}
