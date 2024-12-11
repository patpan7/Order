package org.easytech.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TablesActivity extends AppCompatActivity implements TableAdapter.OnTableClickListener {

    private RecyclerView recyclerViewTables;
    private TableAdapter adapter;
    List<Table> tableList = null;
    DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        SettingsHelper settingsHelper = new SettingsHelper(this);

        int tablesCol = settingsHelper.getCols("tables_col");

        recyclerViewTables = findViewById(R.id.recyclerViewTables);
        recyclerViewTables.setLayoutManager(new GridLayoutManager(this, tablesCol)); // 3 τραπέζια ανά σειρά

        dbHelper = new DBHelper(this);

        tableList = dbHelper.getTables();
        adapter = new TableAdapter(tableList, this, this);
        recyclerViewTables.setAdapter(adapter);
    }

    @Override
    public void onTableClick(int position) {
        // Μετάβαση στην επόμενη οθόνη (π.χ. Activity για κατηγορίες προϊόντων)
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("tableid", tableList.get(position).getTable_id());
        Log.e("table details",tableList.get(position).print());
        startActivity(intent);
    }

    // Μέθοδος για την ανανέωση της λίστας των τραπεζιών
    public void refreshTableList() {
        List<Table> newTableList = dbHelper.getTables();
        for (Table table : newTableList) {
            Log.e("Table Refresh", table.print()); // Εκτύπωσε τα νέα δεδομένα
        }
        tableList = newTableList; // Αντικατέστησε τη λίστα
        adapter.updateTableList(tableList);
        adapter.notifyDataSetChanged();
    }
}
