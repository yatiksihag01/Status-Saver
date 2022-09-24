package com.yatik.statussaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ImageData> imageData;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        imageData = new ArrayList<>();

        imageData.add(new ImageData(R.drawable.family_mother, R.drawable.family_older_sister));
        imageData.add(new ImageData(R.drawable.family_older_sister, R.drawable.family_younger_sister));
        imageData.add(new ImageData(R.drawable.family_son, R.drawable.family_younger_sister));

        CustomAdapter adapter = new CustomAdapter(imageData);

    }
}