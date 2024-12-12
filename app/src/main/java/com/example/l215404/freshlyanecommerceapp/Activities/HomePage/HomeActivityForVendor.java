package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.l215404.freshlyanecommerceapp.Activities.Cart.CartActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.History.HistoryActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.Login.LoginActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.Profiles.ProfileActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.Activities.Profiles.ProfileActivityForVendor;
import com.example.l215404.freshlyanecommerceapp.Activities.SessionManager.SessionManager;
import com.example.l215404.freshlyanecommerceapp.Activities.Settings.SettingsActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.UploadProduct.UploadProductActivity;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.dao.VendorDao;
import com.example.l215404.freshlyanecommerceapp.models.Customer;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.l215404.freshlyanecommerceapp.dao.CustomerDao;

public class HomeActivityForVendor extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView greetingText;
    private ImageView profileImageView;
    private FreshlyDatabase freshlyDatabase;
    private ImageView hamburgerMenu;

    private SessionManager sessionManager;

    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_for_vendor);

        navigationView = findViewById(R.id.navigationDrawerForvendor);
        hamburgerMenu = findViewById(R.id.hamburger);

        greetingText = findViewById(R.id.greetingText);
        profileImageView = findViewById(R.id.profileImageView);
        sessionManager = new SessionManager(this);
        freshlyDatabase = FreshlyDatabase.getInstance(this);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        loadProfileImage();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.d("Tab Setup", "Setting up tab at position: " + position);
                switch(position) {
                    case 0:
                        tab.setIcon(R.drawable.icon_upload);
                        tab.setContentDescription("Upload Tab");
                        break;
                    case 1:
                        tab.setIcon(R.drawable.home);
                        tab.setContentDescription("Home Tab");
                        break;
                    case 2:
                        tab.setCustomView(R.layout.fragment_profile);
                        int userId = sessionManager.getUserId();
                        boolean isCustomer = sessionManager.isCustomer();
                        new LoadProfileImageForTabTask(userId, isCustomer, tab).execute();
                        tab.setContentDescription("Profile");
                        break;
                }
            }
        }).attach();

        Log.d("TabLayoutMediator", "TabLayoutMediator attached successfully!");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tab.getPosition();
                Log.d("Tab Selected", "Selected Tab Position: " + selectedTabPosition);
                switch(selectedTabPosition) {
                    case 0:
                        openUploadActivity();
                        break;
                    case 1:
                        openHomeActivity();
                        break;
                    case 2:
                        openProfileActivity();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (sessionManager.isLoggedIn() && sessionManager.isCustomer()==false) {
            int userId = sessionManager.getUserId();
            new FetchVendorProductsTask(userId).execute();
        }

        productAdapter = new ProductAdapter(productList, this, "Page2");
        recyclerView.setAdapter(productAdapter);

        ImageView blurOverlay = findViewById(R.id.blurOverlay);
        blurOverlay.setOnClickListener(v -> {
            blurOverlay.setVisibility(View.GONE);
            findViewById(R.id.navigationDrawerForvendor).setVisibility(View.GONE);
        });

        ImageView hamburger = findViewById(R.id.hamburger);
        hamburger.setOnClickListener(v -> {
            applyBlur();

            findViewById(R.id.blurOverlay).setVisibility(View.VISIBLE);
            findViewById(R.id.navigationDrawerForvendor).setVisibility(View.VISIBLE);
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    startActivity(new Intent(HomeActivityForVendor.this, ProfileActivityForVendor.class));
                } else if (itemId == R.id.menu_home) {
                    // Do nothing
                } else if (itemId == R.id.menu_upload) {
                    startActivity(new Intent(HomeActivityForVendor.this, UploadProductActivity.class));
                } else if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(HomeActivityForVendor.this, SettingsActivity.class));
                } else if (itemId == R.id.logout) {
                    sessionManager.logout();
                    startActivity(new Intent(HomeActivityForVendor.this, LoginActivity.class));
                    finish();
                }
                return true;
            }
        });

        if (sessionManager.isLoggedIn() && sessionManager.isCustomer() == false) {
            int userId = sessionManager.getUserId();
            Log.d("USER", "userdi: "+ userId);
            new HomeActivityForVendor.FetchUsernameTask(userId).execute(); // Start AsyncTask to fetch username
        } else {
            greetingText.setText("Hi, Guest");
        }
    }

    private class FetchVendorProductsTask extends AsyncTask<Void, Void, List<Product>> {
        private int vendorId;

        public FetchVendorProductsTask(int vendorId) {
            this.vendorId = vendorId;
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            List<com.example.l215404.freshlyanecommerceapp.models.Product> dbproducts = freshlyDatabase.productDao().getAllProducts();

            List<Product> productsfinal = new ArrayList<>();

            for (com.example.l215404.freshlyanecommerceapp.models.Product dbproduct : dbproducts) {
                if (dbproduct.getVendor_id() == vendorId) {
                    Log.d("Product match", "Product Id: "+ dbproduct.getId());
                    Log.d("Product match", "Product Title: "+ dbproduct.getTitle());
                    Log.d("Product match", "Product Description: "+ dbproduct.getDescription());
                    Log.d("Product match", "Product Vendor id: "+ dbproduct.getVendor_id());
                    Log.d("Product match", "Product Price: "+ dbproduct.getPrice());
                    Log.d("Product match", "Product : "+ dbproduct.getImage());

                    Product adapterProduct = new Product(
                            dbproduct.getId(),
                            dbproduct.getTitle(),
                            dbproduct.getDescription(),
                            dbproduct.getVendor_id(),
                            dbproduct.getPrice(),
                            dbproduct.getImage()
                    );

                    productsfinal.add(adapterProduct);

                }
            }

            return productsfinal;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            super.onPostExecute(products);
            Log.d("Fetched Products", "Fetched Products: " + products.size());
            if (products.isEmpty()) {
                Log.d("No Products", "No products to display.");
            }
            productList.clear();
            productList.addAll(products);
            productAdapter.notifyDataSetChanged();
        }
    }

    private class FetchUsernameTask extends AsyncTask<Void, Void, String> {
        private int userId;

        public FetchUsernameTask(int userID) {
            this.userId = userID;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String username = null;
            try {
                Log.d("UserId", "userId: "+userId);
                Vendor vendor = freshlyDatabase.vendorDao().getVendorById(userId);
                Log.d("Customer", "Customer: "+vendor);
                username = vendor.getUsername();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return username;
        }

        @Override
        protected void onPostExecute(String username) {
            super.onPostExecute(username);
            if (username != null) {
                greetingText.setText("Hi, " + username);
            } else {
                greetingText.setText("Hi, Guest");
            }
        }
    }

    private class LoadProfileImageForTabTask extends AsyncTask<Void, Void, Bitmap> {
        private int userId;
        private boolean isCustomer;
        private TabLayout.Tab profileTab;

        public LoadProfileImageForTabTask(int userId, boolean isCustomer, TabLayout.Tab profileTab) {
            this.userId = userId;
            this.isCustomer = isCustomer;
            this.profileTab = profileTab;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            String profilePicturePath = null;

            if (isCustomer) {
                CustomerDao customerDao = freshlyDatabase.customerDao();
                Customer customer = customerDao.findCustomerById(userId);
                if (customer != null) {
                    profilePicturePath = customer.getProfilePicture();
                }
            } else {
                VendorDao vendorDao = freshlyDatabase.vendorDao();
                Vendor vendor = vendorDao.getVendorById(userId);
                if (vendor != null) {
                    profilePicturePath = vendor.getProfilePicture();
                }
            }

            if (profilePicturePath != null) {
                File imgFile = new File(profilePicturePath);
                if(imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    // Resize and crop the image into a circle
                    return getCircularBitmap(getScaledBitmap(bitmap, dpToPx(25), dpToPx(25)));  // 50dp x 50dp
                }
            }
            return null;  // Return null if image not found
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (profileTab != null && profileTab.getCustomView() != null) {
                // Find the ImageView within the custom view of the profile tab
                ImageView profileImageView = profileTab.getCustomView().findViewById(R.id.profileText);

                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);  // Set the circular cropped image to the ImageView
                } else {
                    // If no profile image found, set a default image
                    profileImageView.setImageResource(R.drawable.customer);  // Set a default image if no image is found
                }
            }
        }
    }

    // Methods to open corresponding activities when tabs are selected
    private void openUploadActivity() {
        Intent intent = new Intent(HomeActivityForVendor.this, UploadProductActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(HomeActivityForVendor.this, HomeActivityForVendor.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(HomeActivityForVendor.this, ProfileActivityForVendor.class);
        startActivity(intent);
    }

    private void loadProfileImage() {
        String profilePicturePath;
        int userId = sessionManager.getUserId();
        boolean isCustomer =sessionManager.isCustomer();

        new LoadProfileImageTask(userId, isCustomer).execute();
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private class LoadProfileImageTask extends AsyncTask<Void, Void, Bitmap> {
        private int userId;
        private boolean isCustomer;

        public LoadProfileImageTask(int userId, boolean isCustomer) {
            this.userId = userId;
            this.isCustomer = isCustomer;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            String profilePicturePath = null;

            if (isCustomer) {
                CustomerDao customerDao = freshlyDatabase.customerDao();
                Customer customer = customerDao.findCustomerById(userId);
                if (customer != null) {
                    profilePicturePath = customer.getProfilePicture();
                }
            } else {
                VendorDao vendorDao = freshlyDatabase.vendorDao();
                Vendor vendor = vendorDao.getVendorById(userId);
                if (vendor != null) {
                    profilePicturePath = vendor.getProfilePicture();
                }
            }

            if (profilePicturePath != null) {
                File imgFile = new File(profilePicturePath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    // Resize and crop the image into a circle
                    return getCircularBitmap(getScaledBitmap(bitmap, dpToPx(50), dpToPx(50)));  // 50dp x 50dp
                }
            }
            return null;  // Return null if image not found
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap);  // Set the circular cropped image to the ImageView
            } else {
                // If no profile image found, set a default image
                profileImageView.setImageResource(R.drawable.customer);  // Replace with your default image
            }
        }
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);  // Resize the image to fit 50x50 dp
    }

    // Method to apply a circular crop to the image
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);  // Ensure the circle fits within the image dimensions

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // Define a paint object to fill the circle with the bitmap's content
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Define the circular shape
        Rect rect = new Rect(0, 0, diameter, diameter);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);  // Draw the circle

        // Set the image to be clipped into the circular shape
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;  // Return the circular cropped bitmap
    }

    private void applyBlur() {
        View rootView = findViewById(R.id.main);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenshot);
        rootView.draw(canvas);

        RenderScript rs = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap(rs, screenshot);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blurScript.setRadius(10f);
        blurScript.setInput(input);
        blurScript.forEach(output);
        output.copyTo(screenshot);

        ImageView blurOverlay = findViewById(R.id.blurOverlay);
        blurOverlay.setImageBitmap(screenshot);
        blurOverlay.setVisibility(View.VISIBLE);

        input.destroy();
        output.destroy();
        blurScript.destroy();
        rs.destroy();
    }
}