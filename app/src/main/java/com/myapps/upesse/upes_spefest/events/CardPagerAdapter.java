package com.myapps.upesse.upes_spefest.events;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.Models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    Button btnRegister;
    private DatabaseReference mUserRef;
    private DatabaseReference mEventRef;
    private DatabaseReference mIndividualEventRef;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseUser user;
    private String currentTitle;
    private String userId;
    private String userName;
    private String userEmail;
    /*
    private static boolean isRegistered;

    public static void setIsRegistered(){
        isRegistered = true;
    }
    */

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.events_adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final CardItem item, final View view) {
        ImageView imgEvent = (ImageView) view.findViewById(R.id.imgEvent);
        final TextView titleEvent = (TextView) view.findViewById(R.id.titleEvent);
        TextView descEvent = (TextView) view.findViewById(R.id.descEvent);
        TextView contact1Event = (TextView) view.findViewById(R.id.contact1Event);
        TextView contact2Event = (TextView) view.findViewById(R.id.contact2Event);
        TextView contact3Event = (TextView) view.findViewById(R.id.contact3Event);
        TextView phoneNo1 = (TextView) view.findViewById(R.id.phoneNo1);
        TextView phoneNo2 = (TextView) view.findViewById(R.id.phoneNo2);
        TextView phoneNo3 = (TextView) view.findViewById(R.id.phoneNo3);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        /*
        mIndividualEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    Toast.makeText(view.getContext(), "You've already been registered in this event.", Toast.LENGTH_LONG).show();
                    //isRegistered = true;
                    setIsRegistered();
                    btnRegister.setEnabled(false);
                }
                else{
                    isRegistered = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (!(isOnline())) {
                    Toast.makeText(view.getContext(), "Could not register for the event. No internet access.", Toast.LENGTH_SHORT).show();
                } else {

                    //Register
                    currentTitle = titleEvent.getText().toString();
                    //Toast.makeText(view.getContext(), "Current Title : " + currentTitle, Toast.LENGTH_SHORT).show();

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    /*
                    if (user != null) {
                        Toast.makeText(view.getContext(), user.getUid() + " " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                    }
                    */
                    //Event e = new Event(titleEvent.getText().toString());
                    //mEventRef.child(titleEvent.getText().toString()).setValue(e);

                    mFirebaseInstance = FirebaseDatabase.getInstance();
                    // get reference to 'events' node
                    mEventRef = mFirebaseInstance.getReference("events");
                    mIndividualEventRef = mFirebaseInstance.getReference("events").child(currentTitle);

                    userId = user.getUid();
                    userName = user.getDisplayName();
                    userEmail = user.getEmail();

                    final User user = new User(userName, userEmail);


                    mIndividualEventRef.child(userId).setValue(user);
                    Toast.makeText(view.getContext(), "Registered!", Toast.LENGTH_LONG).show();

                    addUserChangeListener();


                    /*}else if(btnRegister.getText().toString().equals("Unregister")) {
                        //Unregister
                        currentTitle = titleEvent.getText().toString();
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        Toast.makeText(view.getContext(), "Current Title : " + currentTitle, Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference("events").child(currentTitle).child(user.getUid()).removeValue();
                        btnRegister.setText("Register");
                    }*/

                    //toggleButton();

                /*
                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(userName, userEmail);
                } else {
                    updateUser(userName,userEmail);
                }

                toggleButton();
                */
                }
            }
        });

        imgEvent.setImageResource(item.getmImgEvent());
        titleEvent.setText(item.getmTitleEvent());
        descEvent.setText(item.getmTitleDesc());
        contact1Event.setText(item.getmContact1Event());
        contact2Event.setText(item.getmContact2Event());
        contact3Event.setText(item.getmContact3Event());
        phoneNo1.setText(item.getmPhoneNo1());
        phoneNo2.setText(item.getmPhoneNo2());
        phoneNo3.setText(item.getmPhoneNo3());

    }

    /*
    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnRegister.setText("Register");
        } else {
            btnRegister.setText("Update");
        }
    }
    */


    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name, String email) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        User user = new User(name, email);

        //mUserRef.child(userId).setValue(user);
        //mIndividualEventRef.child(userId).setValue(user);
        //mEventRef.child(titleEvent.getText().toString()).setValue(e);

        //addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mIndividualEventRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e("Register", "User data is null!");
                    return;
                }

                Log.e("Register", "User data is changed!" + user.name + ", " + user.email);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Register", "Failed to read user", error.toException());
            }

        });
    }


    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}
