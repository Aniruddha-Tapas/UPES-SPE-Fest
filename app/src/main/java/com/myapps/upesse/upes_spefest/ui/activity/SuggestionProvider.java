package com.myapps.upesse.upes_spefest.ui.activity;

import android.content.SearchRecentSuggestionsProvider;

import java.util.ArrayList;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {

  public final static String AUTHORITY = SuggestionProvider.class.getName();

    public final static int MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_2LINES
      | SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

    //public final static int MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

  public SuggestionProvider() {
    super();
    setupSuggestions(AUTHORITY, MODE);
  }


    public static ArrayList<String> firebase_users = new ArrayList<String>();

    public static ArrayList<String> getFirebaseUsers() {
        return firebase_users;
    }

   public static void addFirebaseUsers(String uname) {
        firebase_users.add(uname);
    }

  public static void setFirebase_users(ArrayList<String> firebase_users) {
    SuggestionProvider.firebase_users = firebase_users;
  }


}
