package org.easytech.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TableAdapter.OnTableClickListener {

    private RecyclerView recyclerViewTables;
    private TableAdapter adapter;
    List<Table> tableList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewTables = findViewById(R.id.recyclerViewTables);
        recyclerViewTables.setLayoutManager(new GridLayoutManager(this, 3)); // 3 τραπέζια ανά σειρά

        DBHelper dbHelper = new DBHelper(this);

        tableList = dbHelper.getTables();
        adapter = new TableAdapter(tableList, this);
        recyclerViewTables.setAdapter(adapter);
    }

    @Override
    public void onTableClick(int position) {
        // Μετάβαση στην επόμενη οθόνη (π.χ. Activity για κατηγορίες προϊόντων)
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("tableNumber", tableList.get(position).getTable_id());
        startActivity(intent);
    }
}
