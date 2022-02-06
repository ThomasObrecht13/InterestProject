package com.example.interestproject.editUserProfilFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.interestproject.R;
import com.example.interestproject.model.User;
import com.example.interestproject.navFragment.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    EditText etFirstname, etLastname, etUsername, etDescription;
    CircleImageView profilePicture,test;
    Button editProfile;

    FirebaseAuth mAuth;
    FirebaseUser userAuth;
    DatabaseReference reference;
    Bitmap bitmap;

    private Activity mActivity;

    Uri pictureSelected;
    User user;

    //Todo:: L'app plante quand on change l'image
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
        //recupère les éléments de la vue
        etUsername = (EditText) view.findViewById(R.id.etUsername);
        etLastname = (EditText) view.findViewById(R.id.etLastname);
        etFirstname = (EditText) view.findViewById(R.id.etFirstname);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        profilePicture = (CircleImageView) view.findViewById(R.id.profilePicture);
        test = (CircleImageView) view.findViewById(R.id.testprofilePicture);


        /*-----------------------------------------------------------------
            Remplie les éléments de la vue avec les data de currentUser
        ------------------------------------------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userAuth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                assert user != null;
                etUsername.setText(user.getUsername());

                etFirstname.setText(user.getFirstname());
                etLastname.setText(user.getLastname());

                etDescription.setText(user.getDescription());

                if(mActivity != null) { //évite un crash
                    if (user.getImageURL().equals("default")) {

                        Glide.with(getContext())
                                .load("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg")
                                .into(profilePicture);
                    } else if (bitmap != null) {
                        Glide.with(getContext())
                                .load(bitmap)
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

        /*-------------
          EDIT USER
         -------------*/

        user =  new User();
        //Edit profile Picture
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open imagePicker
                getImg();
            }
        });

        //Edit default/custom data user
        editProfile = (Button) view.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Data from form
                //String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String firstname = etFirstname.getText().toString();
                String lastname = etLastname.getText().toString();
                String description = etDescription.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(description)) {
                    etUsername.setError("Remplie !");
                }else{
                    user.setUsername(username);
                    user.setFirstname(firstname);
                    user.setLastname(lastname);
                    user.setDescription(description);
                    user.setSearch(username.toLowerCase());
                }

                /*
                Pour que l'image soit modifier avant le retour a ProfileFragment
                Sinon le changement ne se fait pas
                 */
                if (pictureSelected != null) {
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(Objects.requireNonNull(pictureSelected)));
                        putFile(bitmap);

                        Log.i("ProfilePicturePickUp", "GET FROM LOCAL.: OK");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i("ProfilePicturePickUp", "GET FROM LOCAL.: RUIM");
                    }
                }else{
                    //user.setImageURL(userAuth.getPhotoUrl());
                    Map<String, Object> childUpdates = new HashMap<>();
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    childUpdates.put("/"+userAuth.getUid()+"/", user.toMap());
                    reference.updateChildren(childUpdates);
                }

                //return profileFragment
                ProfileFragment profileFragment = new ProfileFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.nav_fragment, profileFragment)
                        .commit();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                //après selection image
                pictureSelected = data.getData();
                try {
                    bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(Objects.requireNonNull(pictureSelected)));

                    Glide.with(getContext())
                            .load(bitmap)
                            .into(profilePicture);

                    profilePicture.setImageBitmap(bitmap);

                    Log.i("ProfilePicturePickUp", "GET FROM LOCAL.: OK");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("ProfilePicturePickUp", "GET FROM LOCAL.: RUIM");
                }
            }
        }
    }

    /*--------------------------
        UPDATE IMAGE FUNCTION
    --------------------------*/
    //Stock l'image et modifie l'image de l'utilisateur
    public void putFile(Bitmap bitmap){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference mountainsRef = storageRef.child("images/"+userAuth.getUid()+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("ProfilePicturePickUp", "Img not set");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(Uri uri) {
                        setProfilePic(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i("ProfilePicturePickUp", "Img set but whereeee");
                        // Handle any errors
                    }
                });
            }
        });
    }
    //modifie l'image de currentUser
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setProfilePic(Uri uri){
        //Modification de User ImageURL
        user.setImageURL(String.valueOf(uri));
        //Push dans la DB
        Map<String, Object> childUpdates = new HashMap<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        childUpdates.put("/"+userAuth.getUid()+"/", user.toMap());
        reference.updateChildren(childUpdates);
    }

    //open imagePickerActivity
    @SuppressLint("IntentReset")
    public void getImg(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        @SuppressLint("IntentReset") Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
}