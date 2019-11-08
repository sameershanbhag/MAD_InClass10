package com.example.inclass10_main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnFragmentInteractionListener, AddMovieFragment.OnFragmentInteractionListener, EditMovieFragment.OnFragmentInteractionListener, ShowByFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new MainActivityFragment(), "mainActivity")
                .addToBackStack(null)
                .commit();
    }
}
