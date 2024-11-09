package com.example.l215404.freshlyanecommerceapp.Activities.Cart;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.R;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        cartItemsContainer = findViewById(R.id.cartoption);

//        List<CartItem> cartItems = getCartItems();
//
//        for (CartItem item: cartItems){
//            addCartItemToLayout(item);
//        }
    }

//    private List<CartItem> getCartItems() {
//        List<CartItem> items = new ArrayList<>();
//
//        return items;
//    }

    private void addCartItemToLayout(CartItem item) {
        LinearLayout cartItemLayout = new LinearLayout(this);
        cartItemLayout.setOrientation(LinearLayout.HORIZONTAL);
        cartItemLayout.setPadding(16, 16, 16, 16);
        cartItemLayout.setBackgroundResource(R.drawable.item_background);

        // Create TextViews for the item name and price
        TextView itemName = new TextView(this);
        itemName.setText(item.getName());
        itemName.setTextSize(18);
        itemName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        itemName.setPadding(16, 0, 0, 0);

        TextView itemPrice = new TextView(this);
        itemPrice.setText(item.getPrice());
        itemPrice.setTextSize(18);
        itemPrice.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add the TextViews to the cart item layout
        cartItemLayout.addView(itemName);
        cartItemLayout.addView(itemPrice);

        // Add the cart item layout to the container
        cartItemsContainer.addView(cartItemLayout);
    }

    private static class CartItem {
        private String name;
        private String price;
        private int quantity;

        public CartItem(String name, String price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}