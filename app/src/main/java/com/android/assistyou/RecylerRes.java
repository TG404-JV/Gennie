package com.android.assistyou;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecylerRes extends AppCompatActivity {

     RecyclerView recyclerView;
    private PromptResultAdapter adapter;
    private List<PromptResultItem> resultList;

    private ChatApplication chatApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recyler_res);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        resultList = new ArrayList<>();
        adapter = new PromptResultAdapter(this, resultList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ChatApplication

    }
}