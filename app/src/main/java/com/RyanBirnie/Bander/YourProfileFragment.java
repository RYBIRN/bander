package com.RyanBirnie.Bander;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class YourProfileFragment extends Fragment {

    private Button editFilters;
    private Button editProfile;
    private Button signOut;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String userId;
    private StorageReference mStorageRef;

    private TextView mLink;


    private TextView mUserBio;
    private TextView mUserContactInfo;
    private TextView mUserName;
    private TextView mInstrument;
    private TextView mUserType;
    private TextView mGenre;
    private ImageView mUserImage;
    private String image;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_your_profile, container, false);

        // get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // get current user info
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserName = (TextView) v.findViewById(R.id.bandNameSrchTextView);
        mUserType = (TextView) v.findViewById(R.id.ArtistTypeSrchTextView);
        mInstrument = (TextView) v.findViewById(R.id.InstrumentTextView);
        mGenre = (TextView) v.findViewById(R.id.genreTextView);
        mLink = (TextView) v.findViewById(R.id.linkTextView);
        mUserBio = (TextView) v.findViewById(R.id.bioTextView);
        mUserContactInfo = (TextView) v.findViewById(R.id.contactTextView);
        mUserImage = (ImageView) v.findViewById(R.id.srchProfileImageView);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // If there is no logged in user, bring them to the login activity
                    startActivity(new Intent(getActivity(), LogInActivity.class));
                    getActivity().finish();
                }
            }
        };

        signOut = (Button) v.findViewById(R.id.signOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        editProfile = (Button) v.findViewById(R.id.editProfileButton);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();

            }
        });

        return v;

    }

    private void getData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            UserInfo uInfo = new UserInfo();
            uInfo.setBio(ds.child(userId).getValue(UserInfo.class).getBio());
            uInfo.setContact(ds.child(userId).getValue(UserInfo.class).getContact());
            uInfo.setGenre(ds.child(userId).getValue(UserInfo.class).getGenre());
            uInfo.setInstrument(ds.child(userId).getValue(UserInfo.class).getInstrument());
            uInfo.setLink(ds.child(userId).getValue(UserInfo.class).getLink());
            uInfo.setType(ds.child(userId).getValue(UserInfo.class).getType());
            uInfo.setUsername(ds.child(userId).getValue(UserInfo.class).getUsername());
            uInfo.setImage(ds.child(userId).getValue(UserInfo.class).getImage());

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

            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

//    public void goToEditFilters() {
//        Intent intent = new Intent(getActivity(), FiltersActivity.class);
//
//        startActivity(intent);
//    }

    public void goToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);

        startActivity(intent);
    }

    public void signOut() {
        auth.signOut();
        startActivity(new Intent(getActivity(), LogInActivity.class));
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getActivity(), LogInActivity.class));
            getActivity().finish();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getActivity(), LogInActivity.class));
            getActivity().finish();
        }
    }


}