package com.example.interestproject.navFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.ChatActivity;
import com.example.interestproject.R;
import com.example.interestproject.RecyclerItemClickListener;
import com.example.interestproject.adapter.UserAdapterSearch;
import com.example.interestproject.model.Chatlist;
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


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapterSearch userAdapterSearch;

    TextView none_user_connected;
    private List<User> mUsers;
    private List<Chatlist> userList;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.horizontal_recyclerview_home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getContext()) ;
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layout);

        none_user_connected = view.findViewById(R.id.none_user_connected);

        mUsers = new ArrayList<>();
        userList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        User userSelected = userAdapterSearch.getItem(position);

                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("userId",userSelected.getId());
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        return view;
    }
    //Read user and keep user from userList
    private void chatList() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for(Chatlist chatlist : userList){
                        assert user != null;
                        if(user.getId().equals(chatlist.getId())){
                            if(user.getStatus().equals("online")) {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                if(mUsers.isEmpty()){
                    none_user_connected.setVisibility(View.VISIBLE);
                }else{
                    none_user_connected.setVisibility(View.GONE);
                }
                userAdapterSearch = new UserAdapterSearch(getContext(), mUsers,true);
                recyclerView.setAdapter(userAdapterSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}