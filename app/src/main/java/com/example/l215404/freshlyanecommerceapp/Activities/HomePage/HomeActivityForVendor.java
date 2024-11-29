package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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
import com.example.l215404.freshlyanecommerceapp.Activities.Profiles.ProfileActivityForVendor;
import com.example.l215404.freshlyanecommerceapp.Activities.SessionManager.SessionManager;
import com.example.l215404.freshlyanecommerceapp.Activities.Settings.SettingsActivity;
import com.example.l215404.freshlyanecommerceapp.Activities.UploadProduct.UploadProductActivity;
import com.example.l215404.freshlyanecommerceapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivityForVendor extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_for_vendor);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.navigationDrawerForvendor);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
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
                        tab.setIcon(R.drawable.ic_launcher_background);
                        tab.setContentDescription("Profile");
                        break;
                }
            }
        }).attach();

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

        SessionManager sessionManager = new SessionManager(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_profile) {
                    startActivity(new Intent(HomeActivityForVendor.this, ProfileActivityForCustomer.class));
                } else if (itemId == R.id.menu_home) {
                    // Do nothing or add specific action if needed for the home button
                } else if (itemId == R.id.menu_upload) {
                    startActivity(new Intent(HomeActivityForVendor.this, UploadProductActivity.class));
                } else if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(HomeActivityForVendor.this, SettingsActivity.class));
                } else if (itemId == R.id.logout) {
                    sessionManager.logout();
                    startActivity(new Intent(HomeActivityForVendor.this, LoginActivity.class));
                    finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
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