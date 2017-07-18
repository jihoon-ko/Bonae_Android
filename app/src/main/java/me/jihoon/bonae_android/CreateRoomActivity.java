package me.jihoon.bonae_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        Button saveButton = (Button) findViewById(R.id.saveNewRoomButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText foodType = (EditText) findViewById(R.id.foodType);
                EditText foodPrice = (EditText) findViewById(R.id.foodPrice);
                EditText content_text = (EditText) findViewById(R.id.content_text);
                final String FoodType = foodType.getText().toString();
                final String FoodPrice = foodPrice.getText().toString();
                final String Content_text = content_text.getText().toString();

                Intent intent = getIntent();
                intent.putExtra("foodType", FoodType);
                intent.putExtra("foodPrice", FoodPrice);
                intent.putExtra("content_text", Content_text);
                intent.putExtra("divide", true);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
