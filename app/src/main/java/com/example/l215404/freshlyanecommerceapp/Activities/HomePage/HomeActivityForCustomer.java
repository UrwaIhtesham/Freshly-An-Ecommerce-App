package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

import android.os.Bundle;

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
                        break;
                    case 1:
                        tab.setIcon(R.drawable.home);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_launcher_background);
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
    }
}