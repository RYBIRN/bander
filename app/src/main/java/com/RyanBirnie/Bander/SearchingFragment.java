package com.RyanBirnie.Bander;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class SearchingFragment extends Fragment {

    private Button nextProfile;
    private Button prevProfile;

    private DatabaseReference mDatabase;
    private String userId;
    ArrayList<String> dbUserIds = new ArrayList<>();
    private int position;

    private TextView mUserBio;
    private TextView mUserContactInfo;
    private TextView mUserName;
    private TextView mInstrument;
    private TextView mUserType;
    private TextView mGenre;
    private ImageView mUserImage;
    private String image;
    private TextView mLink;
    private TextView mDistance;

    private String latitude;
    private String longitude;
    private double searchingLong;
    private double searchingLat;
    private double userLong;
    private double userLat;

    private String searchingUserId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_searching, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dbUserIds = bundle.getStringArrayList("users");
            position = bundle.getInt("position");
            userId = bundle.getString("id");
            latitude = bundle.getString("lat");
            longitude = bundle.getString("long");
            userLong = Double.parseDouble(longitude);
            userLat = Double.parseDouble(latitude);

            mDatabase = FirebaseDatabase.getInstance().getReference();

            mUserName = (TextView) v.findViewById(R.id.bandNameSrchTextView);
            mUserType = (TextView) v.findViewById(R.id.ArtistTypeSrchTextView);
            mInstrument = (TextView) v.findViewById(R.id.InstrumentSrchTextView);
            mGenre = (TextView) v.findViewById(R.id.GenreSrchTextView);
            mLink = (TextView) v.findViewById(R.id.linkSrchTextView);
            mUserBio = (TextView) v.findViewById(R.id.bioSrchTextView);
            mUserContactInfo = (TextView) v.findViewById(R.id.contactSrchTextView);
            mUserImage = (ImageView) v.findViewById(R.id.SrchImageView);
            mDistance = (TextView) v.findViewById(R.id.distanceTextView);

            Log.v("UserList", dbUserIds.toString());

            searchingUserId = dbUserIds.get(position);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    getData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            nextProfile = (Button) v.findViewById(R.id.nextButton);
            nextProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vw) {
                    switchNextProfile();

                }
            });

            prevProfile = (Button) v.findViewById(R.id.previousButton);
            prevProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vw) {
                    switchPreviousProfile();

                }
            });
        } else {
            // Do nothing if stuff doesn't load
        }

        return v;

    }


    private void getData(DataSnapshot dataSnapshot) {
        UserInfo uInfo = new UserInfo();
        uInfo.setUsername(dataSnapshot.child("users").child(searchingUserId).child("username").getValue().toString());
        uInfo.setBio(dataSnapshot.child("users").child(searchingUserId).child("bio").getValue().toString());
        uInfo.setContact(dataSnapshot.child("users").child(searchingUserId).child("contact").getValue().toString());
        uInfo.setGenre(dataSnapshot.child("users").child(searchingUserId).child("genre").getValue().toString());
        uInfo.setInstrument(dataSnapshot.child("users").child(searchingUserId).child("instrument").getValue().toString());
        uInfo.setLink(dataSnapshot.child("users").child(searchingUserId).child("link").getValue().toString());
        uInfo.setType(dataSnapshot.child("users").child(searchingUserId).child("type").getValue().toString());
        uInfo.setImage(dataSnapshot.child("users").child(searchingUserId).child("image").getValue().toString());
        uInfo.setLongitude(dataSnapshot.child("users").child(searchingUserId).child("longitude").getValue().toString());
        uInfo.setLatitude(dataSnapshot.child("users").child(searchingUserId).child("latitude").getValue().toString());



        mDistance.setText(String.valueOf(getDistance(uInfo.getLongitude(), uInfo.getLatitude())) + " mi away");

        mUserName.setText(uInfo.getUsername());
        mUserType.setText(uInfo.getType());
        mInstrument.setText(uInfo.getInstrument());
        mGenre.setText(uInfo.getGenre());
        mLink.setText(uInfo.getLink());
        mUserBio.setText(uInfo.getBio());
        mUserContactInfo.setText(uInfo.getContact());
        image = uInfo.getImage();
        try {
            Glide.with(this).load(image).into(mUserImage);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public double getDistance ( String uLong, String uLat) {
        searchingLong = Double.valueOf(uLong);
        searchingLat = Double.valueOf(uLat);

        Location searchingLocation = new Location("Searching Location");
        searchingLocation.setLatitude(searchingLat);
        searchingLocation.setLongitude(searchingLong);

        Location userLocation = new Location("User Location");
        userLocation.setLongitude(userLong);
        userLocation.setLatitude(userLat);

        DecimalFormat df = new DecimalFormat("#.#");

        double distance = Double.valueOf(df.format((userLocation.distanceTo(searchingLocation) /1609.344)));
        return distance;
    }

    public void switchNextProfile(){
        position = position + 1;
        if(position >= dbUserIds.size()) {
            position = 0;
        }
        SearchingFragment selectedFragment = new SearchingFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("users", dbUserIds);
        bundle.putInt("position", position);
        bundle.putString("long", longitude);
        bundle.putString("lat", latitude);
        selectedFragment.setArguments(bundle);

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }

    public void switchPreviousProfile(){
        position--;
        if(position <= -1) {
            position = dbUserIds.size() - 1;
        }
        SearchingFragment selectedFragment = new SearchingFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("users", dbUserIds);
        bundle.putInt("position", position);
        bundle.putString("long", longitude);
        bundle.putString("lat", latitude);
        selectedFragment.setArguments(bundle);

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }
}