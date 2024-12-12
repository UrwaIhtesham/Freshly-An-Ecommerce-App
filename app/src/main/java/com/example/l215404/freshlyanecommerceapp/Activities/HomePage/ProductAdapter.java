package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l215404.freshlyanecommerceapp.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private String pageType;

    public ProductAdapter(List<Product> productList, Context context, String pageType) {
        this.productList = productList;
        this.context=context;
        this.pageType=pageType;
    }

    private int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_products, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        Log.d("ProductAdapter", "productTitle: " + holder.productTitle);
        Log.d("ProductAdapter", "productDescription: " + holder.productDescription);
        Log.d("ProductAdapter", "productPrice: " + holder.productPrice);

        Bitmap bitmap = null;
        try {
            if (product.getImageSource() != null && !product.getImageSource().isEmpty()) {
                // If the image source is a URL or file path, you can load it into a Bitmap
                bitmap = BitmapFactory.decodeFile(product.getImageSource());  // Decode from file path (or use URL if needed)
            }

            // If the bitmap is not null, apply transformations
            if (bitmap != null) {
                // Convert 200dp to pixels based on screen density
                int sizeInPx = dpToPx(holder.itemView.getContext(), 200);  // 200dp converted to pixels

                // Scale the bitmap to 200dp x 200dp
                Bitmap scaledBitmap = getScaledBitmap(bitmap, sizeInPx, sizeInPx);  // Scaling the image to 200x200 dp in pixels

                // Set the circular cropped image to the ImageView
                holder.productImage.setImageBitmap(scaledBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.productTitle.setText(truncateText(product.getName(), 15));
        holder.productDescription.setText(truncateText(product.getDescription(), 40));
        holder.productPrice.setText("Rs. " + product.getPrice());
    }

    private String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productDescription, productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.ProductPrice);
        }
    }

    // Method to scale the image to the desired size (200dp x 200dp)
    private Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);  // Resize the image
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
}
