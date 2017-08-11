package com.luis_santiago.aigol.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import java.util.*;
import android.view.View;
import java.util.ArrayList;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luis_santiago.aigol.R;
import com.luis_santiago.aigol.ui.HomeActivity;
import com.luis_santiago.aigol.utils.tools.adapters.TableAdapter;
import com.luis_santiago.aigol.utils.tools.data.news.score.TableTeam;
import com.luis_santiago.aigol.utils.tools.utils.Utils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.os.Build.VERSION_CODES.M;

/**
 * A simple {@link Fragment} subclass.
 */
public class TablesFragment extends Fragment {

    // This is for debugging
    private String TAG = TablesFragment.class.getSimpleName();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    // This is for the progress bar, when we have conection it's remove
    private LinearLayout mLinearLayout;
    // This thing too
    private ProgressBar isThereInternetConnection;
    //this is for fetching data
    private SwipeRefreshLayout swipeRefreshLayout;
    // The list we are going to display
    ArrayList<TableTeam> mTableTeamArrayList;
    private TableAdapter mTableAdapter;
    private DatabaseReference mDatabase;


    public TablesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tables, container, false);
        //Casting all the UI components
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_table_fragment);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.progress_bar_layout);
        isThereInternetConnection = (ProgressBar) view.findViewById(R.id.progress_bar_interner);
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe);
        // Setting the color
        swipeRefreshLayout.setColorSchemeResources(
                R.color.progress_color,
                R.color.colorAccent,
                R.color.white
                );
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Setting our refreshListener
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        // Creating our list
        mTableTeamArrayList = new ArrayList<>();
        //Setting our adapter
        mTableAdapter = new TableAdapter(mTableTeamArrayList);
        mRecyclerView.setAdapter(mTableAdapter);
        // Creating an instance of the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Finding the correct reference to read our data
        DatabaseReference standing = mDatabase.child("Standings").child("LigaEspañola");
        // To read our data we need to add the value Listener
        Query query = standing.orderByChild("position");
        query.addListenerForSingleValueEvent(valueEventListener);
        return view;
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Toast.makeText(getContext(), "Im refreshing", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(true);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    int min = 65;
                    int max = 50;

                    Random random = new Random();
                    int i = random.nextInt(max - min + 1) + min;
                }
            }, 3000);
        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        ArrayList<TableTeam> finalList = new ArrayList<>();
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Long position = (Long) snapshot.child("position").getValue();
                String name = (String) snapshot.child("name").getValue();
                String logo = (String) snapshot.child("team_logo").getValue();
                Long matchesPlayed = (Long) snapshot.child("matches_played").getValue();
                Long goalDifference = (Long) snapshot.child("goal_difference").getValue();
                Long goalFor = (Long) snapshot.child("goal_for").getValue();
                Long goalAfter = (Long) snapshot.child("goal_afer").getValue();
                Long points = (Long) snapshot.child("points").getValue();

                Log.e("POSITION",""+Long.toString(position));
                TableTeam tableTeam = new TableTeam(
                        Long.toString(position),
                        logo,
                        name,
                        Long.toString(matchesPlayed),
                        Long.toString(goalFor),
                        Long.toString(goalAfter),
                        Long.toString(goalDifference),
                        Long.toString(points)
                        );
                finalList.add(tableTeam);
                Log.e("Tables", "lol"+tableTeam);
            }
            Log.e("MI ARRAY", "LOS DATOS CAMBIARON");
            mTableAdapter.setTableTeams(finalList);
            mLinearLayout.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };

    @Override
    public void onResume() {
      super.onResume();
        if(Utils.checkForInternetConection(getContext())){
            Log.e(TAG, "we have internet");
        }
        else{
            Utils.throwDialogue(getContext());
            Log.e(TAG, "we don't have internet");
            isThereInternetConnection.setVisibility(View.GONE);
        }
    }

    private void init(View view){

    }
}