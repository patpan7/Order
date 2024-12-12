package org.easytech.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CardView cardTables;
    CardView cardSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardTables = findViewById(R.id.cardTables);
        cardSettings = findViewById(R.id.cardSettings);
        WorkerManager.scheduleAppWorker(this);

        cardTables.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TablesActivity.class);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }

}
