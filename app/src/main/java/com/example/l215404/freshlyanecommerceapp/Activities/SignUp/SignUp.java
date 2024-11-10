package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.Activities.Login.LoginActivity;
import com.example.l215404.freshlyanecommerceapp.R;

public class SignUp extends AppCompatActivity {

    private LinearLayout customerLayout;
    private LinearLayout vendorLayout;
    private Button confirm;
    private TextView loginText;

    private boolean isCustomerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        customerLayout = findViewById(R.id.customerLayout);
        vendorLayout = findViewById(R.id.vendorLayout);
        confirm = findViewById(R.id.confirmButton);
        loginText = findViewById(R.id.loginText);

        confirm.setEnabled(false);
        confirm.setAlpha(0.5f);

        customerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomerSelected = true;

                customerLayout.setBackground(getResources().getDrawable(R.drawable.signup));
                vendorLayout.setBackground(getResources().getDrawable(R.drawable.signupbutton));

                confirm.setEnabled(true);
                confirm.setAlpha(1.0f);
            }
        });

        vendorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomerSelected= false;
                vendorLayout.setBackground(getResources().getDrawable(R.drawable.signup));
                customerLayout.setBackground(getResources().getDrawable(R.drawable.signupbutton));

                confirm.setEnabled(true);
                confirm.setAlpha(1.0f);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCustomerSelected) {
                    Intent intent = new Intent(SignUp.this, CustomerSignUp.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SignUp.this, VendorSignUp.class);
                    startActivity(intent);
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(SignUp.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}