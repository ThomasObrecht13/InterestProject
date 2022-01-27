package com.example.interestproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.interestproject.adapter.UserAdapterMessage;
import com.example.interestproject.adapter.UserAdapterSearch;
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


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapterSearch userAdapterSearch;

    private List<User> mUsers;
    private List<String> userList;

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
        LinearLayoutManager layout = new LinearLayoutManager(getContext()) ;
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(layout);

        mUsers = new ArrayList<>();
        userList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

        readUser();

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

                UserAdapterSearch userAdapterSearch = new UserAdapterSearch(getContext(), mUsers,true);
                recyclerView.setAdapter(userAdapterSearch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}