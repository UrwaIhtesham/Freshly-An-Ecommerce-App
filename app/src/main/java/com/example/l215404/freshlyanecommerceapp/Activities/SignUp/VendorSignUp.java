package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;

public class VendorSignUp extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, addressEditText, phoneEditText;
    private Button signUpButton;
    private FreshlyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_sign_up);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(), FreshlyDatabase.class, "freshly_db").allowMainThreadQueries().build();

        // Bind UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        signUpButton = findViewById(R.id.signUpButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the Sign-Up button click listener
        signUpButton.setOnClickListener(v -> signUpVendor());
    }

    private void signUpVendor() {
        // Get input values
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Vendor object
        Vendor vendor = new Vendor(0, username, email, password, "", address, phone);

        // Insert vendor into database
        db.vendorDao().insert(vendor);
        Toast.makeText(this, "Vendor signed up successfully", Toast.LENGTH_SHORT).show();

        // Clear fields after signup
        clearFields();
    }

    private void clearFields() {
        usernameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        addressEditText.setText("");
        phoneEditText.setText("");
    }
}
