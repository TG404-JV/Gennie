package com.android.assistyou;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataFetcher {

    // Reference to your Firebase database

    String username,email,password,mobNumber;
    private DatabaseReference mDatabase;

    public FirebaseDataFetcher() {
        // Get reference to your Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void fetchData() {
        // Construct a query to retrieve data from the "Users" node
        DatabaseReference usersRef = mDatabase.child("Users");

        // Attach a listener to the query to handle the results
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial data
                // Retrieve data from the dataSnapshot and process it
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve user data from the snapshot
                     username = userSnapshot.getKey();
                     email = userSnapshot.child("email").getValue(String.class);
                     password = userSnapshot.child("password").getValue(String.class);
                     mobNumber = userSnapshot.child("mobNumber").getValue(String.class);

                    // Process the retrieved data as needed
                    // For example, you can create User objects or display the data in a UI
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors that occur while fetching data
                // For example, you can log the error message
            }
        });
    }
}
