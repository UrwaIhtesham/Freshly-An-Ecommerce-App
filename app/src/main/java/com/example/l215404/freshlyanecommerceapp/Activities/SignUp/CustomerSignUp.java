package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.Activities.HomePage.HomeActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.models.Customer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomerSignUp extends AppCompatActivity {

    private Spinner genderSpinner;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private FreshlyDatabase database;
    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] selectedImageBytes = null;
    private ImageView profileImagePlaceholder;
    private String selectedImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_sign_up);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        signUpButton = findViewById(R.id.signUpButton);
        profileImagePlaceholder = findViewById(R.id.profileImagePlaceholder);

        database = FreshlyDatabase.getInstance(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) view;
                if (position == 0) {
                    Toast.makeText(CustomerSignUp.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    genderSpinner.setSelection(0);
                    selectedText.setTextColor(0xFF808080);
                } else {
                    String selectedGender = parent.getItemAtPosition(position).toString();
                    Toast.makeText(CustomerSignUp.this, "Selected: "+selectedGender, Toast.LENGTH_SHORT).show();
                    selectedText.setTextColor(0xFF000000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

    private byte[] convertImageToByteArray(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String saveImageToExternalStorage(Bitmap bitmap) throws IOException {
        File directory = new File(getExternalFilesDir(null), "profile_pics");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = System.currentTimeMillis() + ".png";
        File imageFile = new File(directory, filename);

        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        return imageFile.getAbsolutePath();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void signUp() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImagePath == null) {
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "please select a valid gender", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = new Customer(username, email, password, selectedImagePath, gender, true);
        Log.d("SignUp", "Attempting sign-up...");
        Log.d("SignUp", "Username: " + username + " Email: " + email);
        new InsertCustomerTask().execute(customer);
    }

    private class InsertCustomerTask extends AsyncTask<Customer, Void, String> {
        @Override
        protected String doInBackground(Customer... customers) {
            Customer customer = customers[0];
            Customer existingCustomer = database.customerDao().checkIFEmailExists(customer.getEmail());
            Log.d("InsertCustomerTask", "Existing customer: " + (existingCustomer != null));
            if (existingCustomer!=null) {
                return "Email already registered";
            } else {
                database.customerDao().insertCustomer(customer);
                return "Account created successfully!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(CustomerSignUp.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("Account created successfully!")) {
                Intent i = new Intent(CustomerSignUp.this, HomeActivityForCustomer.class);
                startActivity(i);
                finish();
            }
        }
    }
}