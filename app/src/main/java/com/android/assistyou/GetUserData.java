package com.android.assistyou;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GetUserData {

    private DatabaseReference databaseRef;

    ArrayList<String>Question = new ArrayList<>();
    ArrayList<String>result = new ArrayList<>();



    public GetUserData() {
        this.databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void getUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = databaseRef.child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        result.add( snapshot.child("result").getValue(String.class));
                        Question.add(snapshot.child("question").getValue(String.class));

                        // Now you have access to the result and question
                    }
                } else {
                    Log.d("FirebaseData", "No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "Error getting data", databaseError.toException());
            }
        });
    }
}
