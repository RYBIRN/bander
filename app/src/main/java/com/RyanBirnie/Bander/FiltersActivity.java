package com.RyanBirnie.Bander;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class FiltersActivity extends AppCompatActivity {

    private CheckBox lookingForBands;
    private CheckBox lookingForMusicians;
    private CheckBox lookingForProducers;
    private CheckBox lookingForVenues;

    private Spinner musicGenreSpin2;
    private Spinner whatInstrumentSpin2;

    private EditText yourLocation;

    private SeekBar milesAwaySeek;

    private TextView milesFilterText;

    private Button cancel;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        lookingForBands = (CheckBox) findViewById(R.id.lookingForCheckBox1);
        lookingForMusicians = (CheckBox) findViewById(R.id.lookingForCheckBox2);
        lookingForProducers = (CheckBox) findViewById(R.id.lookingForCheckBox3);
        lookingForVenues = (CheckBox) findViewById(R.id.lookingForCheckBox4);


        musicGenreSpin2 = (Spinner) findViewById(R.id.musicGenreSpinner);

        ArrayAdapter<String> musicGenreAdapter2 = new ArrayAdapter<String>(FiltersActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.musicGenreArray));
        musicGenreAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        musicGenreSpin2.setAdapter(musicGenreAdapter2);

        whatInstrumentSpin2 = (Spinner) findViewById(R.id.instrumentSpinner);

        ArrayAdapter<String> whatInstrumentAdapter2 = new ArrayAdapter<String>(FiltersActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.whatInstrumentArray));
        whatInstrumentAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whatInstrumentSpin2.setAdapter(whatInstrumentAdapter2);

        yourLocation = (EditText) findViewById(R.id.yourlocationEditText);

        milesAwaySeek = (SeekBar) findViewById(R.id.maxDistanceSlider);

        milesFilterText = findViewById(R.id.milesFilterTextView);

        cancel = (Button) findViewById(R.id.filtersCancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileCancel();

            }
        });

        done = (Button) findViewById(R.id.filtersDoneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileDone();

            }
        });



    }

    public void goToProfileCancel() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    public void goToProfileDone() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}