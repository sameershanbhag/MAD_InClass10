package com.example.inclass10_main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class AddMovieFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;

    private int progress = 0;

    private Button bt_addmovie;
    private EditText et_name;
    private EditText et_description;
    private EditText et_year;
    private EditText et_imdb;
    private SeekBar sb_rating;
    private Spinner sp_genrelist;
    private String genreSelected;
    private TextView tv_progress;

    private List<String> genresList;


    private String movieName;
    private String description;
    private Integer year;
    private String imdb;

    private boolean isErrorPresent = false;

    private FirebaseFirestore movieDB = FirebaseFirestore.getInstance();


    public AddMovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SeekBar
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

        sp_genrelist.setOnItemSelectedListener(this);

        //Spinner dropdown elements
        genresList = new ArrayList<>();
        genresList.add(0, "Select");
        genresList.add(1, "Action");
        genresList.add(2, "Animation");
        genresList.add(3, "Comedy");
        genresList.add(4, "Documentary");
        genresList.add(5,"Family");
        genresList.add(6, "Horror");
        genresList.add(7, "Crime");
        genresList.add(8, "Others");

        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, genresList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setting the Adapter
        sp_genrelist.setAdapter(dataAdapter);


        bt_addmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add movie code
                movieName = et_name.getText().toString();
                description = et_description.getText().toString();
                if("".equals(et_year.getText().toString())){
                    year = Integer.parseInt("123455555");
                } else {
                    year = Integer.parseInt(et_year.getText().toString());
                }

                imdb = et_imdb.getText().toString();

                if(movieName.length() > 50) {
                    et_name.setError("Movie name should not exceed 50 characters.");
                    Toast.makeText(getActivity(), "Movie name should not exceed 50 characters.", Toast.LENGTH_SHORT).show();
                    isErrorPresent = true;
                }
                else {
                    isErrorPresent = false;
                }

                if(description.length() > 1000 || String.valueOf(year).length() > 4 || movieName.length() > 50
                        || movieName.length() == 0 || description.length() == 0 || String.valueOf(year).length() == 0 || imdb.length() == 0 || genreSelected == "Select") {
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
                    Movies movie = new Movies();
                    movie.setMovieName(movieName);
                    movie.setDescription(description);
                    movie.setGenre(genreSelected);
                    movie.setRating(progress);
                    movie.setYear(year);
                    movie.setIMDB(imdb);
                    String hexCode = movieName + Math.random();
                    movie.setMovieID(String.valueOf(hexCode.hashCode()));

                    movieDB.collection("Movies").document(movie.getMovieID())
                            .set(movie)
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        genreSelected = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_movie, container, false);

        bt_addmovie = view.findViewById(R.id.bt_addmovie);
        et_name = view.findViewById(R.id.et_name);
        et_description = view.findViewById(R.id.et_description);
        et_year = view.findViewById(R.id.et_year);
        et_imdb = view.findViewById(R.id.et_imdb);
        sb_rating = view.findViewById(R.id.sb_rating);
        sp_genrelist = view.findViewById(R.id.sp_genrelist);
        tv_progress = view.findViewById(R.id.tv_progress);

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

    interface OnFragmentInteractionListener {

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
