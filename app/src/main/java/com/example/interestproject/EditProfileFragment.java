package com.example.interestproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    EditText etName,etEmail,etPrenom,etDescription;
    CircleImageView profilePicture;
    Button editProfile;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Uri pictureSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //recupère les éléments de la vue
        etName = (EditText) view.findViewById(R.id.etName);
        //etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPrenom = (EditText) view.findViewById(R.id.etPrenom);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        profilePicture = (CircleImageView) view.findViewById(R.id.profilePicture);

        db = FirebaseFirestore.getInstance();

        /*-----------------------------------------------------------------
            Remplie les éléments de la vue avec les data de currentUser
        ------------------------------------------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null) {
            //default data
            etName.setText(user.getDisplayName());
            //etEmail.setText(user.getEmail());

            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .into(profilePicture);

            //get custom data from currentUser
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("getData", "DocumentSnapshot data: " + document.getData());
                            //remplit éléments vue avec les data de currentUser
                            etDescription.setText(document.getString("description"));
                            etPrenom.setText(document.getString("prenom"));

                        } else {
                            Log.d("getData", "No such document");
                        }
                    } else {
                        Log.d("getData", "get failed with ", task.getException());
                    }
                }
            });
        }

        /*-------------
          EDIT USER
         -------------*/
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
                String name = etName.getText().toString();
                String prenom = etPrenom.getText().toString();
                String description = etDescription.getText().toString();

                //if data != empty
               /* if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email cannot be empty");
                    etEmail.requestFocus();
                } else*/ if (TextUtils.isEmpty(name)) {
                    etName.setError("Password cannot be empty");
                    etName.requestFocus();
                } else if (TextUtils.isEmpty(prenom)) {
                    etPrenom.setError("Name cannot be empty");
                    etPrenom.requestFocus();
                } else if (TextUtils.isEmpty(description)) {
                    etDescription.setError("Name cannot be empty");
                    etDescription.requestFocus();
                } else {
                    FirebaseUser userAuth = mAuth.getCurrentUser();

                    //edit default user data
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    //execute default update
                    userAuth.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("updateUser", "User profile updated.");
                                    }
                                }
                            });

                    //edit custom user data
                    DocumentReference documentReference = db.collection("users").document(userAuth.getUid());

                    Map<String, Object> user = new HashMap<>();
                    user.put("prenom", prenom);
                    user.put("description", description);

                    //execute custom update
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            Log.d("editUser", "onSuccess: user Profile edited");
                        }
                    });
                }
                if (pictureSelected != null) {
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(Objects.requireNonNull(pictureSelected)));
                        putFile(bitmap);
                        Log.i("ProfilePicturePickUp", "GET FROM LOCAL.: OK");
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("ProfilePicturePickUp", "GET FROM LOCAL.: RUIM");
                    }
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
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(Objects.requireNonNull(pictureSelected)));
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
        final StorageReference mountainsRef = storageRef.child("images/"+user.getUid()+".jpg");

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
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(profileUpdates);
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