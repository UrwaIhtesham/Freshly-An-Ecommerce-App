package com.example.l215404.freshlyanecommerceapp.Activities.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.l215404.freshlyanecommerceapp.Activities.HomePage.HomeActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.R;

public class OrderConfirmationActivity extends AppCompatActivity {

    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_confirmation);

        returnButton = findViewById(R.id.returnButton);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderConfirmationActivity.this, HomeActivityForCustomer.class);
                startActivity(i);
            }
        });
    }
}