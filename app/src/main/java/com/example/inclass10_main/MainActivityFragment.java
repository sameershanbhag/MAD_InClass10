package com.example.inclass10_main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class MainActivityFragment extends Fragment{

    private OnFragmentInteractionListener mListener;

    private Button bt_showbyrating;
    private Button bt_showbyyear;
    private Button bt_addmovie;
    private Button bt_deletemovie;
    private Button bt_edit;

    private ArrayList<Movies> movieList = new ArrayList<>();
    private ArrayList<String> movieNames = new ArrayList<>();
    private String[] movienames;

    private FirebaseFirestore movieDB = FirebaseFirestore.getInstance();


    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bt_addmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new AddMovieFragment(), "addMovieFragment")
                        .commit();
            }
        });

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code for edit movie
                movienames = new String[movieNames.size()];
                movienames = movieNames.toArray(movienames);

                if (movieList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Pick a movie")
                            .setItems(movienames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Movies movie = movieList.get(i);
                                    movieList.remove(i);
                                    movieNames.remove(i);

                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.container, new EditMovieFragment(movie, movie.getMovieID()), "editMovieFragment")
                                            .commit();
                                }
                            });

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), "No movies to edit.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bt_deletemovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code for delete movie
                movienames = new String[movieNames.size()];
                movienames = movieNames.toArray(movienames);

                if (movieList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Pick a movie")
                            .setItems(movienames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, final int i) {
                                    final Movies movie = movieList.get(i);

                                    movieDB.collection("Movies").document(movie.getMovieID())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Message message = mHandler.obtainMessage(200, movie.getMovieName() + " Deleted Successfully");
                                                    message.sendToTarget();
                                                    movieList.remove(i);
                                                    movieNames.remove(i);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Message message = mHandler.obtainMessage(300, "Error Deletin " + movie.getMovieName());
                                                    message.sendToTarget();
                                                }
                                            });


                                    Toast.makeText(getActivity(), movienames[i] + "  ", Toast.LENGTH_SHORT).show();
                                }
                            });

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), "No movies to Delete.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_showbyrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ShowByFragment("rating"), "showMovieFragment")
                        .commit();
            }
        });

        bt_showbyyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ShowByFragment("year"), "showMovieFragment")
                        .commit();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity, container, false);

        bt_addmovie = view.findViewById(R.id.bt_addmovie);
        bt_edit = view.findViewById(R.id.bt_edit);
        bt_deletemovie = view.findViewById(R.id.bt_deletemovie);
        bt_showbyrating = view.findViewById(R.id.bt_showbyrating);
        bt_showbyyear = view.findViewById(R.id.bt_showbyyear);


        movieDB.collection("Movies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot: Objects.requireNonNull(task.getResult())){
                                Movies getMovie = new Movies(queryDocumentSnapshot.getData());
                                movieList.add(getMovie);
                                movieNames.add(getMovie.getMovieName());
                                }
                            }
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

    public interface OnFragmentInteractionListener {
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if(message.what == 200){
                Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_SHORT).show();
            } else if(message.what == 300){
                Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
