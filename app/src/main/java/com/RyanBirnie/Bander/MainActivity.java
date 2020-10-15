package com.RyanBirnie.Bander;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private String longitude;
    private String latitude;

    ArrayList<String> dbUserIds = new ArrayList<>();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String userId;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        position = 0;

        // Get the Firebase auth instance
        auth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new YourProfileFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()){
                        case R.id.nav_yourProfile:
                            selectedFragment = new YourProfileFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    selectedFragment).commit();
                            break;
                        case R.id.nav_searchProfiles:

                            checkLocationPermission();


                            break;

//                        case R.id.nav_matches:
//                            selectedFragment = new MatchesFragment();
//                           break;
                    }

                    return true;
                }
            };

//    public void goToEditFilters() {
//        Intent intent = new Intent(this, FiltersActivity.class);
//        startActivity(intent);
//    }

    private class GetUserList extends AsyncTask<Void, Void, Bundle> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bundle doInBackground(Void... voids) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    getUsers(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("users", dbUserIds);
            bundle.putString("id", userId);
            bundle.putInt("position", position);
            bundle.putString("long", longitude);
            bundle.putString("lat", latitude);

            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            Fragment selectedFragment = new SearchingFragment();
            selectedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        }
    }


    private void getUsers(DataSnapshot dataSnapshot) {

        for(DataSnapshot child : dataSnapshot.getChildren() ){

            for(DataSnapshot child2 : child.getChildren() ){
                dbUserIds.add(child2.getKey());
            }
        }
        dbUserIds.remove(userId);

    }

    public boolean checkLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this).setTitle("Location Permissions").setMessage("Bander requires location services").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                }).create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } return false;
        } else {
            // Permission has already been granted
            getLocation();

        } return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new YourProfileFragment()).commit();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            longitude = String.valueOf(location.getLongitude());
                            latitude = String.valueOf(location.getLatitude());

                            setLocations(longitude, latitude);
                            GetUserList startSearching = new GetUserList();
                            startSearching.execute();
                        }
                    }
                });
    }

    private void setLocations(String uLong, String uLat) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("users").child(userId).child("longitude").setValue(uLong);
        db.child("users").child(userId).child("latitude").setValue(uLat);
    }

}
