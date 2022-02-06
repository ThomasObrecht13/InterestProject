package com.example.interestproject.editUserProfilFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.interestproject.R;
import com.example.interestproject.model.User;
import com.example.interestproject.navFragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageInterestFragment extends Fragment {

    CheckBox checkBoxMusique, checkBoxArt, checkBoxSport, checkBoxMode;
    Button button;


    FirebaseUser firebaseUser;
    DatabaseReference reference;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_interest, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        checkBoxArt = view.findViewById(R.id.checkbox_art);
        checkBoxMusique = view.findViewById(R.id.checkbox_musique);
        checkBoxMode = view.findViewById(R.id.checkbox_mode);
        checkBoxSport = view.findViewById(R.id.checkbox_sport);

        button = view.findViewById(R.id.button_showResult);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                assert user != null;
                List<String> interestsList = user.getInterestsList();
                checkInterest(interestsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String interests = getInterestSelected();
                if(interests != null){
                    user.setInterests(interests);
                    Map<String, Object> childUpdates = new HashMap<>();
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    childUpdates.put("/"+firebaseUser.getUid()+"/", user.toMap());
                    reference.updateChildren(childUpdates);

                    ProfileFragment profileFragment = new ProfileFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.nav_fragment, profileFragment)
                            .commit();

                }else {
                    Toast.makeText(getContext(), "Selectionner des intérêts", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void checkInterest(List<String> interestsTab) {
        for (String interest:
             interestsTab) {
            switch (interest){
                case "Mode":
                    checkBoxMode.setChecked(true);
                case "Musique":
                    checkBoxMusique.setChecked(true);
                case "Sport":
                    checkBoxSport.setChecked(true);
                case "Art":
                    checkBoxArt.setChecked(true);
            }
        }
    }

    private String getInterestSelected()  {
        String message = null;
        if(checkBoxMusique.isChecked()) {
            message = checkBoxMusique.getText().toString();
        }
        if(checkBoxMode.isChecked()) {
            if(message== null)  {
                message =  checkBoxMode.getText().toString();
            } else {
                message += "," + checkBoxMode.getText().toString();
            }
        }
        if(checkBoxArt.isChecked()) {
            if(message== null)  {
                message =  checkBoxArt.getText().toString();
            } else {
                message += "," + checkBoxArt.getText().toString();
            }
        }
        if(checkBoxSport.isChecked()) {
            if(message== null)  {
                message =  checkBoxSport.getText().toString();
            } else {
                message += "," + checkBoxSport.getText().toString();
            }
        }
        return  message;
    }
}