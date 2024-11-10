package com.example.l215404.freshlyanecommerceapp.Activities.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.Activities.HomePage.HomeActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.dao.VendorDao;
import com.example.l215404.freshlyanecommerceapp.dao.CustomerDao;
import com.example.l215404.freshlyanecommerceapp.models.Customer;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FreshlyDatabase freshlyDatabase;
    private CustomerDao customerDao;
    private VendorDao vendorDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        customerDao = freshlyDatabase.getInstance(this).customerDao();
        vendorDao = freshlyDatabase.getInstance(this).vendorDao();

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            new loginTask(email, password).execute();
        });
    }
    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private String email, password;
        private boolean isCustomer;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Customer customer = customerDao.findCustomerByEmail(email);
            if (customer != null && customer.getPassword().equals(password)) {
                isCustomer = customer.isCustomer();
                return true;
            }
            Vendor vendor = vendorDao.findVendorByEmail(email);
            if (vendor != null && vendor.getPassword().equals(password)) {
                isCustomer= false;
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                if (isCustomer) {
                    Toast.makeText(LoginActivity.this, "Customer Login Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivityForCustomer.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Vendor Login Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivityForVendor.class);
                    startActivity(i);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}