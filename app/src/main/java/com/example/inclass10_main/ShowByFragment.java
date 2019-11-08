package com.example.inclass10_main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ShowByFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private String criteria;

    private TextView tv_title;
    private TextView tv_static_mby;
    private TextView tv_description;
    private TextView tv_genre;
    private TextView tv_rating;
    private TextView tv_year;
    private TextView tv_imdb;
    private ImageView iv_first;
    private ImageView iv_last;
    private ImageView iv_previous;
    private ImageView iv_next;
    private Button bt_finish;
    private int count = 0;

    private ArrayList<Movies> commonList = null;

    private FirebaseFirestore movieDB = FirebaseFirestore.getInstance();

    public ShowByFragment(String crit) {
        this.criteria = crit;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieDB.collection("Movies")
                .orderBy(criteria, (criteria == "rating")?Query.Direction.DESCENDING:Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            commonList = new ArrayList<>();
                            for(QueryDocumentSnapshot queryDocumentSnapshot: Objects.requireNonNull(task.getResult())){
                                Movies getMovie = new Movies(queryDocumentSnapshot.getData());
                                commonList.add(getMovie);
                            }
                            handleUI();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_by, container, false);


        tv_title = view.findViewById(R.id.tv_title);
        tv_description = view.findViewById(R.id.tv_description);
        tv_genre = view.findViewById(R.id.tv_genre);
        tv_rating = view.findViewById(R.id.tv_rating);
        tv_year = view.findViewById(R.id.tv_year);
        tv_imdb = view.findViewById(R.id.tv_imdb);
        iv_first = view.findViewById(R.id.iv_first);
        iv_last = view.findViewById(R.id.iv_last);
        iv_next = view.findViewById(R.id.iv_next);
        iv_previous = view.findViewById(R.id.iv_previous);
        tv_static_mby = view.findViewById(R.id.tv_static_mby);
        bt_finish = view.findViewById(R.id.bt_finish);

        if (criteria != null && criteria.equals("year")) {
            tv_static_mby.setText("Movies By Year"); // Handle View for Movies By Year
        } else if (criteria != null && criteria.equals("rating")) {
            tv_static_mby.setText("Movies By Rating"); // Handle single image being sent
        }

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

    public interface OnFragmentInteractionListener {
    }

    private void handleUI() {
        if(commonList != null) {
            count = 0;
            tv_title.setText(commonList.get(0).getMovieName());
            tv_description.setText(commonList.get(0).getDescription());
            tv_genre.setText(commonList.get(0).getGenre());
            tv_rating.setText(String.valueOf(commonList.get(0).getRating()));
            tv_year.setText(String.valueOf(commonList.get(0).getYear()));
            tv_imdb.setText(commonList.get(0).getIMDB());

            iv_first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = 0;
                    tv_title.setText(commonList.get(0).getMovieName());
                    tv_description.setText(commonList.get(0).getDescription());
                    tv_genre.setText(commonList.get(0).getGenre());
                    tv_rating.setText(String.valueOf(commonList.get(0).getRating()));
                    tv_year.setText(String.valueOf(commonList.get(0).getYear()));
                    tv_imdb.setText(commonList.get(0).getIMDB());
                }
            });


            iv_last.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count = commonList.size() - 1;
                    tv_title.setText(commonList.get(count).getMovieName());
                    tv_description.setText(commonList.get(count).getDescription());
                    tv_genre.setText(commonList.get(count).getGenre());
                    tv_rating.setText(String.valueOf(commonList.get(count).getRating()));
                    tv_year.setText(String.valueOf(commonList.get(count).getYear()));
                    tv_imdb.setText(commonList.get(count).getIMDB());
                }
            });

            iv_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(count + 1 == commonList.size()){
                        Toast.makeText(getActivity(), "Last Movie Reached", Toast.LENGTH_SHORT);
                    } else {
                        count = count + 1;
                        tv_title.setText(commonList.get(count).getMovieName());
                        tv_description.setText(commonList.get(count).getDescription());
                        tv_genre.setText(commonList.get(count).getGenre());
                        tv_rating.setText(String.valueOf(commonList.get(count).getRating()));
                        tv_year.setText(String.valueOf(commonList.get(count).getYear()));
                        tv_imdb.setText(commonList.get(count).getIMDB());
                    }
                }
            });

            iv_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(count - 1 < 0){
                        Toast.makeText(getActivity(), "This is the First Movie", Toast.LENGTH_SHORT);
                    } else {
                        count = count - 1;
                        tv_title.setText(commonList.get(count).getMovieName());
                        tv_description.setText(commonList.get(count).getDescription());
                        tv_genre.setText(commonList.get(count).getGenre());
                        tv_rating.setText(String.valueOf(commonList.get(count).getRating()));
                        tv_year.setText(String.valueOf(commonList.get(count).getYear()));
                        tv_imdb.setText(commonList.get(count).getIMDB());
                    }
                }
            });

            bt_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new MainActivityFragment(), "mainActivity")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
