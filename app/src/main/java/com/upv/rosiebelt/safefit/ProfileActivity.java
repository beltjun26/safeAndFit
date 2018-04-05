package com.upv.rosiebelt.safefit;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.upv.rosiebelt.safefit.sql.DBUser;

public class ProfileActivity extends AppCompatActivity {
    DBUser dbUser;
    EditText fullname, sex, email;
    Button btn;
    final String save = "Save";
    final String edit = "Edit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dbUser = new DBUser(ProfileActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fullname = findViewById(R.id.content_name);
        sex = findViewById(R.id.content_sex);
        email = findViewById(R.id.content_email);
        btn = findViewById(R.id.button);

        fullname.setFocusable(false);
        sex.setFocusable(false);
        email.setFocusable(false);


        Cursor cursor = dbUser.getData(new String[]{DBUser.UserEntry.COLUMN_NAME_EMAIL, DBUser.UserEntry.COLUMN_NAME_FULLNAME, DBUser.UserEntry.COLUMN_NAME_SEX});

        fullname.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_FULLNAME)));
        sex.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_SEX)));
        email.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_EMAIL)));
        cursor.close();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, btn.getText().toString(), Toast.LENGTH_SHORT).show();
                Button localBtn = (Button)view;
                if(localBtn.getText().toString().equalsIgnoreCase("Edit")){
                    fullname.setFocusableInTouchMode(true);
                    sex.setFocusableInTouchMode(true);
                    email.setFocusableInTouchMode(true);
                    fullname.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(fullname, InputMethodManager.SHOW_IMPLICIT);
                    localBtn.setText(String.valueOf(save));

                }
                else if(localBtn.getText().toString().equalsIgnoreCase("Save")){
                    int id = getIntent().getIntExtra("USER_ID", -1);
                    if(id!=-1){
                        dbUser.setData(DBUser.UserEntry.COLUMN_NAME_FULLNAME, fullname.getText().toString(), id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_NAME_SEX, sex.getText().toString(), id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_NAME_EMAIL, email.getText().toString(), id);
                        localBtn.setText(String.valueOf(edit));
                    }
                }
            }
        });

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
