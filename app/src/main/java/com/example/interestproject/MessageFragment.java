package com.example.interestproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.adapter.UserAdapterMessage;
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

        mUsers = new ArrayList<>();

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
                        mUsers.add(user);
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
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //Ajout brut des images utilisateurs
        Uri uri = Uri.parse("https://png.pngtree.com/element_our/20200610/ourlarge/pngtree-character-default-avatar-image_2237203.jpg");
        CircleImageView imageView1;


        /*
        imageView1 = view.findViewById(R.id.messPicture1);
        Glide.with(view.getContext())
                .load(uri)
                .into(imageView1);

        imageView1 = view.findViewById(R.id.messPicture2);
        Glide.with(view.getContext())
                .load(uri)
                .into(imageView1);
        
        imageView1 = view.findViewById(R.id.messPicture3);
        Glide.with(view.getContext())
                .load(uri)
                .into(imageView1);

        imageView1 = view.findViewById(R.id.messPicture4);
        Glide.with(view.getContext())
                .load(uri)
                .into(imageView1);*/
    }
}