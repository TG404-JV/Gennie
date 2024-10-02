package com.android.assistyou;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class histroy extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<PromptResultItem> resultList;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histroy);

        // Initialize database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("prompt_results");

        // Get a reference to the RecyclerView from the layout
        recyclerView = findViewById(R.id.recyclerView);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list to hold the data
        resultList = new ArrayList<>();

        // Initialize the adapter with context and data list
        adapter = new HistoryAdapter(this, resultList);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase and populate the RecyclerView
        fetchResultsFromFirebase();
    }

    private void fetchResultsFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            databaseRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    resultList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PromptResultItem item = dataSnapshot.getValue(PromptResultItem.class);
                        if (item != null) {
                            resultList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("HistoryActivity", "Failed to fetch data from Firebase: " + error.getMessage());
                }
            });
        } else {
            Log.e("HistoryActivity", "User not authenticated");
        }
    }
}
