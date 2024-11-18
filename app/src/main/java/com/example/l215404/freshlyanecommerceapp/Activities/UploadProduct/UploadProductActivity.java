package com.example.l215404.freshlyanecommerceapp.Activities.UploadProduct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.Activities.SessionManager.SessionManager;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.dao.CategoryDao;
import com.example.l215404.freshlyanecommerceapp.dao.ProductDao;
import com.example.l215404.freshlyanecommerceapp.models.Category;
import com.example.l215404.freshlyanecommerceapp.models.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView logoImageView;
    private ImageView imagePlaceholder;
    private EditText productTitleEditText;
    private EditText productDescriptionEditText;
    private EditText productPriceEditText;
    private Button categoryVegetablesButton, categoryFruitsButton, categoryDryFruitsButton;
    private Button uploadButton;

    private String selectedCategory = "";
    private Bitmap selectedImage;

    private ImageView profileImagePlaceholder;
    private String selectedImagePath = null;

    private SessionManager sessionManager;
     private FreshlyDatabase freshlyDatabase;
     private int vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_product);

        logoImageView = findViewById(R.id.logoImageView);
        imagePlaceholder = findViewById(R.id.imagePlaceholder);
        productTitleEditText = findViewById(R.id.productTitleEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        categoryVegetablesButton = findViewById(R.id.categoryVegetablesButton);
        categoryFruitsButton = findViewById(R.id.categoryFruitsButton);
        categoryDryFruitsButton = findViewById(R.id.categoryDryFruitsButton);
        uploadButton = findViewById(R.id.uploadButton);
        profileImagePlaceholder = findViewById(R.id.imagePlaceholder);

        // Set listeners for category buttons
        categoryVegetablesButton.setOnClickListener(v -> selectCategory("Vegetables"));
        categoryFruitsButton.setOnClickListener(v -> selectCategory("Fruits"));
        categoryDryFruitsButton.setOnClickListener(v -> selectCategory("Dry Fruits"));

        sessionManager = new SessionManager(this);

        freshlyDatabase = FreshlyDatabase.getInstance(this);

        // Set listener for image selection
        imagePlaceholder.setOnClickListener(v -> openImageChooser());

        // Set listener for upload button
        uploadButton.setOnClickListener(v -> uploadProduct());

    }

    private void selectCategory(String category) {
        selectedCategory = category;
        Toast.makeText(this, "Selected Category: " + selectedCategory, Toast.LENGTH_SHORT).show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImagePlaceholder.setImageBitmap(bitmap);
                selectedImagePath = saveImageToExternalStorage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void uploadProduct() {
        String productTitle = productTitleEditText.getText().toString().trim();
        String productDescription = productDescriptionEditText.getText().toString().trim();
        String productPriceStr = productPriceEditText.getText().toString().trim();

        // Validate the input fields
        if (productTitle.isEmpty()) {
            productTitleEditText.setError("Product title is required.");
            return;
        }
        if (productDescription.isEmpty()) {
            productDescriptionEditText.setError("Product description is required.");
            return;
        }
        if (productPriceStr.isEmpty()) {
            productPriceEditText.setError("Product price is required.");
            return;
        }
        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImagePath == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price to integer
        int productPrice = Integer.parseInt(productPriceStr);

        // Execute the AsyncTask to fetch category ID and upload product
        vendorId = sessionManager.getUserId();
        new FetchCategoryTask().execute(productTitle, productDescription, selectedImagePath, String.valueOf(productPrice), selectedCategory, String.valueOf(vendorId));
    }

    private class FetchCategoryTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String categoryName = params[4];
            String title = params[0];
            String description = params[1];
            String image = params[2];
            int price = Integer.parseInt(params[3]);
            int vendorId = Integer.parseInt(params[5]);

            // Log category name to verify
            Log.d("CategoryCheck", "Category Name: " + categoryName);


            // Get category ID
            CategoryDao categoryDao = freshlyDatabase.categoryDao();
            int cid = categoryDao.getCid(categoryName);
            Log.d("CategoryId", "cid: "+cid + title + description + image + price + vendorId);
            if (cid != 0 || cid != 1 || cid != 2) {
                // Insert product into the database
                ProductDao productDao = freshlyDatabase.productDao();
                Product product = new Product(title, description, image, price, cid, vendorId);
                try {
                    productDao.insert(product);
                    return true;
                } catch (Exception e) {
                    Log.e("InsertError", "Error inserting product", e);
                }
            }
            Log.d("CategoryCheck", "Category not found: " + categoryName);
            return false; // Failure, category not found
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(UploadProductActivity.this, "Product uploaded successfully!", Toast.LENGTH_SHORT).show();
                resetForm();
            } else {
                Toast.makeText(UploadProductActivity.this, "Error uploading product. Category not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetForm() {
        productTitleEditText.setText("");
        productDescriptionEditText.setText("");
        productPriceEditText.setText("");
        selectedCategory = "";
        imagePlaceholder.setImageResource(R.drawable.imageupload);
    }
}