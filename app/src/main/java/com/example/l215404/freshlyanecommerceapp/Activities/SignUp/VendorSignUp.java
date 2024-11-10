package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.AsyncTaskLoader;

import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VendorSignUp extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, addressEditText, phoneEditText;
    private ImageView profileImagePlaceholder;
    private FreshlyDatabase freshlyDatabase;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = null;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_sign_up);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        profileImagePlaceholder = findViewById(R.id.profileImagePlaceholder);

        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setEnabled(false);
        signUpButton.setAlpha(0.5f);

        TextWatcher inputWatcher = new InputWatcher();
        usernameEditText.addTextChangedListener(inputWatcher);
        emailEditText.addTextChangedListener(inputWatcher);
        passwordEditText.addTextChangedListener(inputWatcher);
        addressEditText.addTextChangedListener(inputWatcher);
        phoneEditText.addTextChangedListener(inputWatcher);

        freshlyDatabase = FreshlyDatabase.getInstance(this);

        profileImagePlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void checkAllFieldsFilled() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Enable signUpButton if all fields and image are filled
        signUpButton.setEnabled(!TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(phone) &&
                selectedImagePath != null);
    }

    private class InputWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkAllFieldsFilled(); // Check all fields every time there's text change
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void openImagePicker() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImagePlaceholder.setImageBitmap(bitmap);
                selectedImagePath = saveImageToExternalStorage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToExternalStorage(Bitmap bitmap) throws IOException {
        File directory = new File(getExternalFilesDir(null), "profile_pics");
        if(!directory.exists()) {
            directory.mkdirs();
        }

        String filename= System.currentTimeMillis() + ".png";
        File imageFile = new File(directory, filename);

        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        return imageFile.getAbsolutePath();
    }

    private void signUp() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImagePath == null) {
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        Vendor vendor = new Vendor(username, email, password, selectedImagePath, address, phone);
        Log.d("SignUp", "Attempting vendor sign-up...");
        new InsertVendorTask().execute(vendor);
    }

    private class InsertVendorTask extends AsyncTask<Vendor, Void, String> {
        @Override
        protected String doInBackground(Vendor... vendors) {
            Vendor vendor = vendors[0];
            Vendor existingVendor = freshlyDatabase.vendorDao().checkIfEmailExists(vendor.getEmail());
            if(existingVendor != null) {
                return "Email already registered for vendor";
            } else {
                freshlyDatabase.vendorDao().insertVendor(vendor);
                return "Account created successfully!";
            }
        }

        @Override
        protected void OnPostExecute(String result) {
            Toast.makeText(VendorSignUp.this, result, Toast.LENGTH_SHORT).show();
            if(result.equals("Account created successfully!")) {
                Intent i = new Intent(VendorSignUp.this, HomeActivityForVendor.class);
                startActivity(i);
                finish();
            }
        }
    }
}