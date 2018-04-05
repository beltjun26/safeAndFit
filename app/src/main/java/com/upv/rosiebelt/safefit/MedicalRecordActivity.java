package com.upv.rosiebelt.safefit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.upv.rosiebelt.safefit.sql.DBMedicalRecord;
import com.upv.rosiebelt.safefit.utility.MedRecordAdapter;

import java.util.ArrayList;

public class MedicalRecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter rAdpter;
    private RecyclerView.LayoutManager rLayoutManager;
    private DBMedicalRecord dbMedicalRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);



        dbMedicalRecord = new DBMedicalRecord(MedicalRecordActivity.this);

        recyclerView = findViewById(R.id.md_recycleView);
        recyclerView.setHasFixedSize(true);

        rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);
        rAdpter = new MedRecordAdapter(getMdRecordData());
        recyclerView.setAdapter(rAdpter);

        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View promptAdd = (LayoutInflater.from(MedicalRecordActivity.this)).inflate(R.layout.popup_addrecord, null);
                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(MedicalRecordActivity.this);
                alertbuilder.setView(promptAdd);
                alertbuilder.setCancelable(true).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbMedicalRecord.addData(((EditText)promptAdd.findViewById(R.id.add_label)).getText().toString(),((EditText)promptAdd.findViewById(R.id.add_content)).getText().toString());
                        rAdpter = new MedRecordAdapter(getMdRecordData());
                        recyclerView.setAdapter(rAdpter);
                    }
                }).setNegativeButton("Cancel", null);

                Dialog dialog = alertbuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String[]> getMdRecordData(){
        Cursor cursor =dbMedicalRecord.getData(new String[]{DBMedicalRecord.MdRecordEntry.COLUMN_LABEL, DBMedicalRecord.MdRecordEntry.COLUMN_CONTENT});
        ArrayList<String[]> records = new ArrayList<String[]>();
        while(cursor.moveToNext()){
            String[] datapair = new String[2];
            datapair[0] = cursor.getString(cursor.getColumnIndex(DBMedicalRecord.MdRecordEntry.COLUMN_LABEL));
            datapair[1] = cursor.getString(cursor.getColumnIndex((DBMedicalRecord.MdRecordEntry.COLUMN_CONTENT)));
            records.add(datapair);
        }
        cursor.close();
        return records;
    }
}
