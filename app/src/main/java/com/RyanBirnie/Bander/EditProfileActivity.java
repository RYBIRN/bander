package com.RyanBirnie.Bander;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {
    // Database variable
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mUserName;

    private Spinner youAreSpin;
    private Spinner musicGenreSpin;
    private Spinner whatInstrumentSpin;

    private EditText mLink;


    private EditText mUserBio;
    private EditText mUserContactInfo;

    private ImageButton mAddImage;


    private Button cancel;
    private Button done;
    private Button upload;

    //this is here so that we can hide this layout if musician is not selected from the youAre spinner
    private LinearLayout whatInstrumentLayout;

    private FirebaseUser user;
    private String userId;

    private StorageReference mStorageRef;
    Uri filePath;
    int PICK_IMAGE_REQUEST = 111;
    ProgressDialog pd;
    String imageUri;
    private String image;

    private static final String TAG = "Edit Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialise database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DataSnapshot dataSnapshot;
        // User information
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();


        mStorageRef = FirebaseStorage.getInstance().getReference();


        final Boolean profileAlreadyCompleted = false;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserName = findViewById(R.id.userNameEditText);

        youAreSpin = (Spinner) findViewById(R.id.youAreSpinner);
        musicGenreSpin = (Spinner) findViewById(R.id.musicGenreSpinner);
        whatInstrumentSpin = (Spinner) findViewById(R.id.whatInstrumentSpinner);

        //sets adapter for the youAre spinner
        ArrayAdapter<String> youAreAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.profileTypeArray));
        youAreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        youAreSpin.setAdapter(youAreAdapter);


        //sets adapter for the musicGenre spinner
        ArrayAdapter<String> musicGenreAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.musicGenreArray));
        musicGenreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicGenreSpin.setAdapter(musicGenreAdapter);


        //sets adapter for whatInstrument spinner
        ArrayAdapter<String> whatInstrumentAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.whatInstrumentArray));
        whatInstrumentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whatInstrumentSpin.setAdapter(whatInstrumentAdapter);

        mAddImage = (ImageButton) findViewById(R.id.profileImageButton);


        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/");
                i.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(i, "Select Image"), PICK_IMAGE_REQUEST);

            }
        });

        mLink = findViewById(R.id.linkEditText1);
        mUserBio = findViewById(R.id.userBioEditText);
        mUserContactInfo = findViewById(R.id.userContactEditText);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
        upload = (Button) findViewById(R.id.uploadButton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {
                    pd.show();

                    StorageReference childRef = mStorageRef.child(userId);

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            mStorageRef.child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUri = uri.toString();
                                    mDatabase.child("users").child(userId).child("image").setValue(imageUri);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        cancel = (Button) findViewById(R.id.editProfileCancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileAlreadyCompleted)
                    goToProfile();
                else
                    goToLogin();
            }
        });

        done = (Button) findViewById(R.id.editProfileDoneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(userId, mUserName.getText().toString(), String.valueOf(youAreSpin.getSelectedItem()),String.valueOf(musicGenreSpin.getSelectedItem()),String.valueOf(whatInstrumentSpin.getSelectedItem()), mLink.getText().toString(), mUserBio.getText().toString(), mUserContactInfo.getText().toString());

                goToProfile();
            }
        });
    }

    private void getData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
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
            mLink.setText(uInfo.getLink());
            mUserBio.setText(uInfo.getBio());
            mUserContactInfo.setText(uInfo.getContact());
            image = uInfo.getImage();
            if (image != null & image != "") {
                Glide.with(this).load(image).into(mAddImage);

            }
            if (uInfo.getType() != "" & uInfo.getType() != null) {
                youAreSpin.setSelection(((ArrayAdapter<String>)youAreSpin.getAdapter()).getPosition(uInfo.getType()));
            }
            if (uInfo.getGenre() != "" & uInfo.getGenre() != null) {
                musicGenreSpin.setSelection(((ArrayAdapter<String>)musicGenreSpin.getAdapter()).getPosition(uInfo.getGenre()));
            }
            if (uInfo.getInstrument() != "" & uInfo.getInstrument() != null) {
                whatInstrumentSpin.setSelection(((ArrayAdapter<String>)whatInstrumentSpin.getAdapter()).getPosition(uInfo.getInstrument()));
            }
        }
    }

    public void goToLogin() {
        Intent intent = new Intent(this, LogInActivity.class);

        startActivity(intent);
    }

    public void goToProfile() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    public void updateProfile(String userId, String name, String type, String genre, String instrument, String link, String bio, String contact) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mUserName.getText().toString())
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User profile updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mDatabase.child("users").child(userId).child("username").setValue(name);
        mDatabase.child("users").child(userId).child("type").setValue(type);
        mDatabase.child("users").child(userId).child("genre").setValue(genre);
        mDatabase.child("users").child(userId).child("instrument").setValue(instrument);
        mDatabase.child("users").child(userId).child("link").setValue(link);
        mDatabase.child("users").child(userId).child("bio").setValue(bio);
        mDatabase.child("users").child(userId).child("contact").setValue(contact);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                mAddImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}