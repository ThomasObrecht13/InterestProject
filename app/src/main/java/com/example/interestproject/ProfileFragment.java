package com.example.interestproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.interestproject.authentification.LoginActivity;
import com.example.interestproject.authentification.RegisterActivity;
import com.example.interestproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    //Element de la vue
    TextView teName, teEmail, tePrenom, teDescription;
    Button logout, editProfile;
    CircleImageView profilePicture;
    NavigationBarView navigationBarView;
    MenuItem more;

    ImageButton backButton;
    private Activity mActivity;

    //Autre
    EditProfileFragment editProfileFragment;
    FirebaseUser user;
    FirebaseAuth mAuth;

    DatabaseReference reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //Element de la vue
        profilePicture = (CircleImageView) getView().findViewById(R.id.profilePicture);
        teName = (TextView) getView().findViewById(R.id.teName);
        teEmail = (TextView) getView().findViewById(R.id.teEmail);
        teDescription = (TextView) getView().findViewById(R.id.teDescription);
        //tePrenom = (TextView) getView().findViewById(R.id.tePrenom);



        Bundle bundle = this.getArguments();
        if(bundle != null){
            String id = (String) bundle.get("idUser");
            reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
            backButton = getView().findViewById(R.id.back_button_profil);
            backButton.setVisibility(View.VISIBLE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment searchFragment= new SearchFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.nav_fragment, searchFragment)
                            .commit();
                }
            });
        }else {
            //Pour voir son profil

            /*------------------
                Redirection
            ------------------*/
            //Option Menu
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            toolbar.setTitle("");

            toolbar.inflateMenu(R.menu.menu_option_profil);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //Modification profile
                    if (item.getItemId() == R.id.edit_profile_btn) {


                        editProfileFragment = new EditProfileFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.nav_fragment, editProfileFragment)
                                .commit();
                    }
                    //Logout
                    if (item.getItemId() == R.id.logout_btn) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    //Logout
                    if (item.getItemId() == R.id.forgotPasswordBtn) {
                        Intent ForgotPasswordActivity = new Intent(getContext(), com.example.interestproject.authentification.ForgotPasswordActivity.class);
                        startActivity(ForgotPasswordActivity);
                    }
                    return false;
                }
            });

            //recupère l'user dans la DB
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        }


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        teName.setText("Veuillez modifier votre profile");
                        return;
                    }
                    String name = user.getUsername();
                    if (user.getPrenom() != null)
                        name += " " + user.getPrenom();
                    teName.setText(name);

                    teDescription.setText(user.getDescription());
                    if (mActivity != null) {
                        if (user.getImageURL().equals("default")) {

                            Glide.with(getContext())
                                    .load("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg")
                                    .into(profilePicture);
                        } else {
                            Glide.with(getContext())
                                    .load(user.getImageURL())
                                    .into(profilePicture);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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
