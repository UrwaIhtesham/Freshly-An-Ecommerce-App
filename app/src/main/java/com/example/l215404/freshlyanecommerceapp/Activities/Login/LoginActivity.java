package com.example.l215404.freshlyanecommerceapp.Activities.Login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.Activities.HomePage.HomeActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.Activities.SessionManager.SessionManager;
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

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.isCustomer()) {
                startActivity(new Intent(this, HomeActivityForCustomer.class));
            } else {
//                startActivity(new Intent(this, HomeActivitiyForVendor.class));
            }
            finish();
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setEnabled(false);
        loginButton.setAlpha(0.5f);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check if both email and password fields are non-empty
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                loginButton.setEnabled(!email.isEmpty() && !password.isEmpty());
                loginButton.setAlpha(1.0f);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        customerDao = freshlyDatabase.getInstance(this).customerDao();
        vendorDao = freshlyDatabase.getInstance(this).vendorDao();

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            new LoginTask(email, password, this).execute();
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private String email, password;
        private boolean isCustomer;
        private int userId;
        private SessionManager sessionManager;

        public LoginTask(String email, String password, Context context) {
            this.email = email;
            this.password = password;
            this.sessionManager = new SessionManager(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Customer customer = customerDao.findCustomerByEmail(email);
            if (customer != null && customer.getPassword().equals(password)) {
                isCustomer = customer.getIsCustomer();
                return true;
            }
            Vendor vendor = vendorDao.findVendorByEmail(email);
            Toast.makeText(LoginActivity.this, "Bye", Toast.LENGTH_SHORT).show();
            if (vendor != null && vendor.getPassword().equals(password)) {
                Toast.makeText(LoginActivity.this, "Hello", Toast.LENGTH_SHORT).show();
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