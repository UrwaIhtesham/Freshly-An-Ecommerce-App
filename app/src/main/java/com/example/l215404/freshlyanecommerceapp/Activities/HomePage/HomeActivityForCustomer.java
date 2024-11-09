package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

import android.database.CursorWindowAllocationException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RenderEffect;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.l215404.freshlyanecommerceapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeActivityForCustomer extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_for_customer);

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
                        tab.setIcon(R.drawable.cart);
                        tab.setContentDescription("Cart Tab");
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

        NavigationView navigationView = findViewById(R.id.navigationDrawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_profile:

                        break;
                    case R.id.menu_home:

                        break;
                    case R.id.menu_cart:

                        break;
                    case R.id.menu_history:

                        break;
                    case R.id.menu_settings:

                        break;
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