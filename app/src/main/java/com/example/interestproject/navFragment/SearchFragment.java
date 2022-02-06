package com.example.interestproject.navFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.R;
import com.example.interestproject.RecyclerItemClickListener;
import com.example.interestproject.adapter.InterestAdapter;
import com.example.interestproject.adapter.UserAdapterSearch;
import com.example.interestproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;

    private RecyclerView interestRecyclerView;

    private InterestAdapter interestAdapter;
    List<String> mInterest;
    List<String> interestFitler;
    String interest;

    private UserAdapterSearch userAdapterSearch;
    private List<User> mUsers;

    private SearchView searchUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        );

        searchUser = view.findViewById(R.id.search_user_bar);
        searchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchUser(s);
                return false;
            }
        });

        interestRecyclerView = view.findViewById(R.id.interestRecyclerView);
        interestRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        interestRecyclerView.setLayoutManager(linearLayoutManager);

        mInterest = new ArrayList<>();
        interestFitler = new ArrayList<>();

        getInterest();
        interestRecyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(getContext(), interestRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                @Override public void onItemClick(View view, int position) {
                    interest = interestAdapter.getItem(position);
                    if(interestFitler.contains(interest)){
                        interestFitler.remove(interest);
                    }else{
                        interestFitler.add(interest);
                    }
                    searchUser(searchUser.getQuery().toString());
                }

                @Override public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            })
        );

        return view;
                }

                private void readUser(){

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    ArrayList<User> users = new ArrayList<>();
                    assert firebaseUser != null;

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mUsers.clear();
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                User user = snapshot.getValue(User.class);

                                assert user != null;
                                if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                }

                userAdapterSearch = new UserAdapterSearch(getContext(), mUsers,false);
                recyclerView.setAdapter(userAdapterSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("onCancelled","oups");
            }
        });
    }

    private void searchUser(String s) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if(!interestFitler.isEmpty()) {
                        List<String> interestUser = new ArrayList<>(user.getInterestsList());
                        if(!interestUser.isEmpty()) {
                            for (String interest : interestUser) {
                                if(interestFitler.contains(interest)){
                                    if (!user.getId().equals(firebaseUser.getUid())) {
                                        if(!mUsers.contains(user))
                                            mUsers.add(user);
                                    }
                                }
                            }
                        }
                    }else{
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapterSearch = new UserAdapterSearch(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapterSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getInterest() {
        mInterest.clear();

        mInterest.add("Musique");
        mInterest.add("Art");
        mInterest.add("Sport");
        mInterest.add("Mode");

        interestAdapter = new InterestAdapter(getContext(), mInterest,true);
        interestRecyclerView.setAdapter(interestAdapter);

    }


}