package com.example.interestproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.adapter.UserAdapterMessage;
import com.example.interestproject.model.Chat;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment  {

    private RecyclerView recyclerView;

    private UserAdapterMessage userAdapterMessage;
    private List<User> mUsers;


    private List<String> userList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_message_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsers = new ArrayList<>();
        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getSender().equals(firebaseUser.getUid())){
                        userList.add(chat.getReceiver());
                    }
                    if( chat.getReceiver().equals((firebaseUser.getUid()))){
                        userList.add(chat.getSender());
                    }
                }
                readUser();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        User userSelected = userAdapterMessage.getItem(position);
                        //Toast.makeText(getContext(), newUser.getUsername(),Toast.LENGTH_SHORT).show();

                        ProfileFragment profileFragment = new ProfileFragment();
                        Intent intent = new Intent(getActivity(),ChatActivity.class);
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

    private void readUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    if(!user.getId().equals(firebaseUser.getUid())){
                        for(String id : userList){
                            if(user.getId().equals(id)) {
                                if(!mUsers.contains(user)){
                                    mUsers.add(user);
                                }
                            }
                        }
                    }
                }

                userAdapterMessage = new UserAdapterMessage(getContext(), mUsers);
                recyclerView.setAdapter(userAdapterMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}