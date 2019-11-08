package com.example.inclass10_main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditMovieFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;

    private int progress = 0;

    private Movies movies;

    private String primaryKey;

    private Button bt_addmovie;
    private EditText et_name;
    private EditText et_description;
    private EditText et_year;
    private EditText et_imdb;
    private SeekBar sb_rating;
    private Spinner sp_genrelist;
    private TextView tv_progress;
    private String genreSelected;

    private List<String> genresList;


    private String movieName;
    private String description;
    private Integer year;
    private String imdb;

    private boolean isErrorPresent = false;

    private FirebaseFirestore movieDB = FirebaseFirestore.getInstance();

    public EditMovieFragment(Movies movie, String movieNamePk) {
        movies = movie;
        primaryKey = movieNamePk;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_movie, container, false);

        bt_addmovie = view.findViewById(R.id.bt_addmovie);
        et_name = view.findViewById(R.id.et_name);
        et_description = view.findViewById(R.id.et_description);
        et_year = view.findViewById(R.id.et_year);
        et_imdb = view.findViewById(R.id.et_imdb);
        sb_rating = view.findViewById(R.id.sb_rating);
        sp_genrelist = view.findViewById(R.id.sp_genrelist);
        tv_progress = view.findViewById(R.id.tv_progress);


        //Spinner dropdown elements
        genresList = new ArrayList<String>();
        genresList.add(0, "Action");
        genresList.add(1, "Animation");
        genresList.add(2, "Comedy");
        genresList.add(3, "Documentary");
        genresList.add(4,"Family");
        genresList.add(5, "Horror");
        genresList.add(6, "Crime");
        genresList.add(7, "Others");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, genresList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp_genrelist.setAdapter(dataAdapter);

        //set data for edit
        et_name.setText(movies.getMovieName());
        et_description.setText(movies.getDescription());
        et_year.setText(String.valueOf(movies.getYear()));
        et_imdb.setText(movies.getIMDB());
        sb_rating.setProgress(movies.getRating());
        tv_progress.setText(String.valueOf(movies.getRating()));

        progress = movies.getRating();
        String genre = movies.getGenre();

        int spinnerPosition = dataAdapter.getPosition(genre);
        sp_genrelist.setSelection(spinnerPosition);

        //Code for spinner for genre
        sp_genrelist.setOnItemSelectedListener(this);


        //seekbar code
        sb_rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                tv_progress.setText(String.valueOf(progresValue));
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bt_addmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movieName = et_name.getText().toString();
                description = et_description.getText().toString();
                year = Integer.parseInt(et_year.getText().toString());
                imdb = et_imdb.getText().toString();


                if(description.length() > 1000 || String.valueOf(year).length() > 4 || movieName.length() > 50
                        || "".equals(movieName) || "".equals(description) || String.valueOf(year).length() == 0 || "".equals(imdb) || genreSelected == "Select") {
                    isErrorPresent = true;

                    if(movieName.length() == 0){
                        et_name.setError("Please provide a movie name.");
                        Toast.makeText(getActivity(), "Please provide a movie name.", Toast.LENGTH_SHORT).show();
                    }

                    if(description.length() == 0){
                        et_description.setError("Please provide a movie description.");
                        Toast.makeText(getActivity(), "Please provide a movie description.", Toast.LENGTH_SHORT).show();
                    }

                    if(imdb.length() == 0){
                        et_imdb.setError("Please provide a movie imdb.");
                        Toast.makeText(getActivity(), "Please provide a movie imdb.", Toast.LENGTH_SHORT).show();
                    }

                    if(genreSelected == "Select"){
                        Toast.makeText(getActivity(), "Please provide a movie genre.", Toast.LENGTH_SHORT).show();
                    }

                    if(String.valueOf(year).length() == 0){
                        et_name.setError("Please provide a movie year.");
                        Toast.makeText(getActivity(), "Please provide a movie year.", Toast.LENGTH_SHORT).show();
                    }

                    if(movieName.length() > 50) {
                        et_name.setError("Movie name should not exceed 50 characters.");
                        Toast.makeText(getActivity(), "Movie name should not exceed 50 characters.", Toast.LENGTH_SHORT).show();
                    }

                    if(description.length() > 1000) {
                        et_description.setError("Movie description should not exceed 1000 characters.");
                        Toast.makeText(getActivity(), "Movie description should not exceed 1000 characters.", Toast.LENGTH_SHORT).show();
                    }

                    if(String.valueOf(year).length() > 4)
                    {
                        et_year.setError("Invalid year");
                        Toast.makeText(getActivity(), "Invalid year.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    isErrorPresent = false;
                }



                if(!isErrorPresent){
                    Movies movie1 = new Movies();
                    movie1.setMovieName(movieName);
                    movie1.setDescription(description);
                    movie1.setGenre(genreSelected);
                    movie1.setRating(progress);
                    movie1.setYear(year);
                    movie1.setIMDB(imdb);
                    movie1.setMovieID(primaryKey);

                    movieDB.collection("Movies").document(primaryKey)
                            .set(movie1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Message message = mHandler.obtainMessage(200, "Success");
                                        message.sendToTarget();
                                    }
                                }
                            });
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        genreSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if(message.what == 200){
                Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MainActivityFragment(), "mainActivity")
                        .commit();
            }
        }
    };
}
