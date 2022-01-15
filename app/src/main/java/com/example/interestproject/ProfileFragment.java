package com.example.interestproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.interestproject.authentification.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    //Element de la vue
    TextView teName, teEmail, tePrenom, teDescription;
    Button logout,editProfile;
    CircleImageView profilePicture;
    NavigationBarView navigationBarView;
    MenuItem more;

    //Autre
    EditProfileFragment editProfileFragment;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Element de la vue
        profilePicture = (CircleImageView) getView().findViewById(R.id.profilePicture);
        teName = (TextView) getView().findViewById(R.id.teName);
        teEmail = (TextView) getView().findViewById(R.id.teEmail);
        teDescription = (TextView) getView().findViewById(R.id.teDescription);
        //tePrenom = (TextView) getView().findViewById(R.id.tePrenom);
        logout = (Button) getView().findViewById(R.id.logout);

        //Autre
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        /*------------------
            Edit View
        ------------------*/
        if (user != null) {
            // Default data
            String name = user.getDisplayName();
            String email = user.getEmail();
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .into(profilePicture);
            teName.setText(name);
            teEmail.setText(email);

            //Get and set on view custom data
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("getData", "DocumentSnapshot data: " + document.getData());
                            teDescription.setText(document.getString("description"));
                            teName.setText(name+" "+document.getString("prenom"));
                            //tePrenom.setText(document.getString("prenom"));

                        } else {
                            Log.d("getData", "No such document");
                        }
                    } else {
                        Log.d("getData", "get failed with ", task.getException());
                    }
                }
            });
        }else{
            Log.i("user?","non");
        }

        /*
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if(signInAccount != null){
            teName.setText(signInAccount.getDisplayName());
            teEmail.setText(signInAccount.getEmail());
        }*/



        /*------------------
            Redirection
        ------------------*/

        //Modification profile
        editProfile = (Button) getView().findViewById(R.id.toEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile.setVisibility(view.INVISIBLE);

                editProfileFragment = new EditProfileFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.nav_fragment, editProfileFragment)
                        .commit();
            }
        });
        //Logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }
}
