package com.example.interestproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.adapter.UserAdapterSearch;
import com.example.interestproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {


    GridView simpleGrid;


    private RecyclerView recyclerView;

    private UserAdapterSearch userAdapterSearch;
    private List<User> mUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
/*
        mUsers = new ArrayList<>();
        readUser();
        Log.i("user", String.valueOf(mUsers));

        simpleGrid = (GridView) view.findViewById(R.id.GridFilterSearch); // init GridView*/
        //GridFilterSearchAdapter myAdapter=new GridFilterSearchAdapter(getContext(),R.layout.grid_view_items_filter_search,mUsers);
       //simpleGrid.setAdapter(myAdapter);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.GridFilterSearch);
        recyclerView.setHasFixedSize(true);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        mUsers = new ArrayList<>();

        readUser();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        User newUser = userAdapterSearch.getItem(position);
                        //Toast.makeText(getContext(), newUser.getUsername(),Toast.LENGTH_SHORT).show();

                        ProfileFragment profileFragment = new ProfileFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("idUser",newUser.getId()); // Put anything what you want

                        profileFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.nav_fragment, profileFragment)
                                .commit();
                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );        Log.i("username : ", String.valueOf(mUsers));

        return view;
    }

    private void readUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        ArrayList<User> users = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                }

                userAdapterSearch = new UserAdapterSearch(getContext(), mUsers);
                recyclerView.setAdapter(userAdapterSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("onCancelled","oups");
            }
        });
    }


}