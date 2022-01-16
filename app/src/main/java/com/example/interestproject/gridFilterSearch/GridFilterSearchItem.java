package com.example.interestproject.gridFilterSearch;

import android.net.Uri;

public class GridFilterSearchItem {

        String userListName;
        Uri userListImage;

        public GridFilterSearchItem(String userName, Uri userPicture)
        {
            this.userListName=userName;
            this.userListImage=userPicture;
        }
        public String getUserName()
        {
            return userListName;
        }
        public Uri getUserImage()
        {
            return userListImage;
        }
    
}
