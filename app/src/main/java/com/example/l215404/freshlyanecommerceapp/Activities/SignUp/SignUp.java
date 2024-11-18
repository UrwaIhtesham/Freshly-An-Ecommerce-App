package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.Activities.HomePage.HomeActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.Activities.HomePage.HomeActivityForVendor;
import com.example.l215404.freshlyanecommerceapp.Activities.Login.LoginActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.SessionManager.SessionManager;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.dao.CategoryDao;
import com.example.l215404.freshlyanecommerceapp.models.Category;

public class SignUp extends AppCompatActivity {

    private LinearLayout customerLayout;
    private LinearLayout vendorLayout;
    private Button confirm;
    private TextView loginText;

    private boolean isCustomerSelected = false;

    private FreshlyDatabase freshlyDatabase;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        sessionManager = new SessionManager(this);

        boolean session = sessionManager.isLoggedIn();

        if (session) {
            if (sessionManager.isCustomer()) {
                Intent i = new Intent(SignUp.this, HomeActivityForCustomer.class);
                startActivity(i);
            } else {
                Intent i = new Intent(SignUp.this, HomeActivityForVendor.class);
                startActivity(i);
            }
        }

        customerLayout = findViewById(R.id.customerLayout);
        vendorLayout = findViewById(R.id.vendorLayout);
        confirm = findViewById(R.id.confirmButton);
        loginText = findViewById(R.id.loginText);

        confirm.setEnabled(false);
        confirm.setAlpha(0.5f);

        freshlyDatabase = FreshlyDatabase.getInstance(this);
        new InitializeCategoriesAsyncTask().execute();

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

    private class InitializeCategoriesAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            CategoryDao categoryDao = freshlyDatabase.categoryDao();

            if (categoryDao.getAllCategories().isEmpty()) {
                categoryDao.insert(new Category( "Fruits"));
                categoryDao.insert(new Category( "Vegetables"));
                categoryDao.insert(new Category( "Dry Fruits"));
                Toast.makeText(SignUp.this, "Categories initialized", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
        }
    }
}