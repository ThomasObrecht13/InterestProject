package com.example.interestproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interestproject.gridFilterSearch.GridFilterSearchAdapter;
import com.example.interestproject.gridFilterSearch.GridFilterSearchItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {


    GridView simpleGrid;


    ArrayList userList =new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //TEST GRID VIEW
        for( int i=0 ; i<8 ; i++){
            Uri uri = Uri.parse("https://png.pngtree.com/element_our/20200610/ourlarge/pngtree-character-default-avatar-image_2237203.jpg");
            userList.add(new GridFilterSearchItem("user "+i,uri));
        }
        simpleGrid = (GridView) view.findViewById(R.id.GridFilterSearch); // init GridView

        GridFilterSearchAdapter myAdapter=new GridFilterSearchAdapter(getContext(),R.layout.grid_view_items_filter_search,userList);
        simpleGrid.setAdapter(myAdapter);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}