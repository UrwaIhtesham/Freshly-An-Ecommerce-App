package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

import static androidx.core.util.TypedValueCompat.dpToPx;

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
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.l215404.freshlyanecommerceapp.Activities.Login.LoginActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.Profiles.ProfileActivityForCustomer;
import com.example.l215404.freshlyanecommerceapp.Activities.SessionManager.SessionManager;
import com.example.l215404.freshlyanecommerceapp.FreshlyDatabase;
import com.example.l215404.freshlyanecommerceapp.R;
import com.example.l215404.freshlyanecommerceapp.dao.VendorDao;
import com.example.l215404.freshlyanecommerceapp.models.Customer;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.l215404.freshlyanecommerceapp.Activities.History.HistoryActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.Settings.SettingsActivity;
import com.example.l215404.freshlyanecommerceapp.dao.CustomerDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivityForCustomer extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_for_customer);

        hamburgerMenu = findViewById(R.id.hamburger);
        navigationView = findViewById(R.id.navigationDrawer);

        greetingText = findViewById(R.id.greetingText);
        profileImageView = findViewById(R.id.profileImageView);
        sessionManager = new SessionManager(this);
        freshlyDatabase = FreshlyDatabase.getInstance(this);

        loadProfileImage();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1, false);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position) {
                    case 0:
                        tab.setIcon(R.drawable.cart);
                        tab.setContentDescription("Cart Tab");
                        break;
                    case 1:
                        tab.setIcon(R.drawable.home);
                        tab.setContentDescription("Home Tab");
                        break;
                    case 2:
                        tab.setCustomView(R.layout.fragment_profile);  // Custom layout for Profile Tab
                        int userId = sessionManager.getUserId();
                        boolean isCustomer = sessionManager.isCustomer();

                        // Start a new AsyncTask to load profile image in the profile tab
                        new LoadProfileImageForTabTask(userId, isCustomer, tab).execute();
                        tab.setContentDescription("Profile");
                        break;
                }
            }
        }).attach();

        List<Product> productList = new ArrayList<>();
        productList.add(new Product("1 kg Fresh Apples", "Fresh Apples imported from Washington", 400, R.drawable.customer));
        productList.add(new Product("Premium Dry Fruits", "Almonds, pistachios, and hazelnuts", 2500, R.drawable.vendor));
        productList.add(new Product("1 Kg Fresh Mangoes", "Juicy and sweet mangoes", 400, R.drawable.customer));
        productList.add(new Product("1 Kg Fresh Potatoes", "Ideal for cooking meals", 350, R.drawable.customer));

        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        ImageView blurOverlay = findViewById(R.id.blurOverlay);
        blurOverlay.setOnClickListener(v -> {
            blurOverlay.setVisibility(View.GONE);
            findViewById(R.id.navigationDrawer).setVisibility(View.GONE);
        });

        ImageView hamburger = findViewById(R.id.hamburger);
        hamburger.setOnClickListener(v -> {
            applyBlur();

            findViewById(R.id.blurOverlay).setVisibility(View.VISIBLE);
            findViewById(R.id.navigationDrawer).setVisibility(View.VISIBLE);
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    startActivity(new Intent(HomeActivityForCustomer.this, ProfileActivityForCustomer.class));
                } else if (itemId == R.id.menu_home) {
                    // Do nothing or add specific action if needed for the home button
                } else if (itemId == R.id.menu_cart) {
                    startActivity(new Intent(HomeActivityForCustomer.this, CartActivity.class));
                } else if (itemId == R.id.menu_history) {
                    startActivity(new Intent(HomeActivityForCustomer.this, HistoryActivity.class));
                } else if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(HomeActivityForCustomer.this, SettingsActivity.class));
                } else if (itemId == R.id.logout) {
                    sessionManager.logout();
                    startActivity(new Intent(HomeActivityForCustomer.this, LoginActivity.class));
                    finish();
                }
                return true;
            }
        });

        if (sessionManager.isLoggedIn() && sessionManager.isCustomer()) {
            int userId = sessionManager.getUserId();
            Log.d("USER", "userdi: "+ userId);
            new FetchUsernameTask(userId).execute(); // Start AsyncTask to fetch username
        } else {
            greetingText.setText("Hi, Guest");
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
                Customer customer = freshlyDatabase.customerDao().findCustomerById(userId);
                Log.d("Customer", "Customer: "+customer);
                username = customer.getUsername();
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