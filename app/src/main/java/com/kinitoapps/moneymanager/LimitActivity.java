package com.kinitoapps.moneymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LimitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit);
        final EditText limit = (EditText) findViewById(R.id.limit);
        Button saveButton = (Button) findViewById(R.id.save);
        final SharedPreferences sharedPreferences = getSharedPreferences("LIMIT", Context.MODE_PRIVATE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long lim = Long.parseLong(limit.getText().toString());
                sharedPreferences.edit().putLong("limit_today",lim).commit();
            }
        });

    }
}
