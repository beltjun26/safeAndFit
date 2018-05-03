package com.upv.rosiebelt.safefit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.upv.rosiebelt.safefit.sql.DBUser;

public class ProfileActivity extends AppCompatActivity {
    DBUser dbUser;
    EditText fullname, email, contactP, heightFeet, heightInch, weight;
    RadioButton radioSex;
    RadioGroup radioGroup;
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
//      linking all the views to a variable
        fullname = findViewById(R.id.content_name);
        radioGroup = findViewById(R.id.content_sex);
        email = findViewById(R.id.content_email);
        contactP = findViewById(R.id.content_contactP);
        heightFeet = findViewById(R.id.content_height_feet);
        heightInch = findViewById(R.id.content_height_inch);
        weight = findViewById(R.id.content_weight);

        btn = findViewById(R.id.button);

//      set the views unfocusable and unclickable
        fullname.setFocusable(false);
        radioGroup.setClickable(false);
        email.setFocusable(false);
        contactP.setFocusable(false);
        weight.setFocusable(false);
        heightInch.setFocusable(false);
        heightFeet.setFocusable(false);


//        initialization of the views values base on database
        Cursor cursor = dbUser.getData(new String[]{DBUser.UserEntry.COLUMN_NAME_EMAIL, DBUser.UserEntry.COLUMN_NAME_FULLNAME, DBUser.UserEntry.COLUMN_NAME_SEX, DBUser.UserEntry.COLUMN_HEIGHT, DBUser.UserEntry.COLUMN_WEIGHT, DBUser.UserEntry.COLUMN_CONTACT_PERSON});

        fullname.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_FULLNAME)));
        String sex = cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_SEX));
        if(sex!=null){
            if(sex.equals("Male")){
                ((RadioButton)findViewById(R.id.radio_male)).setChecked(true);
            }
            if(sex.equals("Female")){
                ((RadioButton)findViewById(R.id.radio_female)).setChecked(true);
            }
        }
        String height = cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_HEIGHT));
        if(height != null && height.trim().length()> 0){
            String[] heightArray = height.split(" ");
            heightFeet.setText(heightArray[0]);
            heightInch.setText(heightArray[1]);
        }
        email.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_EMAIL)));
        contactP.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_CONTACT_PERSON)));
        weight.setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_WEIGHT)));

        cursor.close();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, btn.getText().toString(), Toast.LENGTH_SHORT).show();
                Button localBtn = (Button)view;
                if(localBtn.getText().toString().equalsIgnoreCase("Edit")){
                    fullname.setFocusableInTouchMode(true);
                    radioGroup.setFocusableInTouchMode(true);
                    email.setFocusableInTouchMode(true);
                    contactP.setFocusableInTouchMode(true);
                    weight.setFocusableInTouchMode(true);
                    heightFeet.setFocusableInTouchMode(true);
                    heightInch.setFocusableInTouchMode(true);
                    fullname.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(fullname, InputMethodManager.SHOW_IMPLICIT);
                    localBtn.setText(String.valueOf(save));

                }
                else if(localBtn.getText().toString().equalsIgnoreCase("Save")){
                    int id = getIntent().getIntExtra("USER_ID", -1);
                    if(id!=-1){
                        String sex = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                        String height = heightFeet.getText().toString() + " " + heightInch.getText().toString();
                        dbUser.setData(DBUser.UserEntry.COLUMN_NAME_FULLNAME, fullname.getText().toString(), id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_NAME_SEX, sex, id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_NAME_EMAIL, email.getText().toString(), id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_WEIGHT, weight.getText().toString(), id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_CONTACT_PERSON, contactP.getText().toString(), id);
                        dbUser.setData(DBUser.UserEntry.COLUMN_HEIGHT, height, id);
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
