package com.example.aventerashoe;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AdminActivity extends AppCompatActivity {
    private EditText etProductName, etProductPrice, etProductDescription, etProductStock;
    private ImageView imgProduct;
    private Button btnUploadImage, btnAddProduct, btnManageProducts, btnViewProducts;
    private Uri selectedImageUri;
    private String imageUrl;
    private Button btnViewOrders,btnViewlgout;


    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1/dgqvvnily/image/upload";;
    private static final String CLOUDINARY_UPLOAD_PRESET = "ml_default";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Views
        etProductName = findViewById(R.id.et_product_name);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductStock = findViewById(R.id.et_product_stock);
        imgProduct = findViewById(R.id.img_product);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnManageProducts = findViewById(R.id.btn_manage_products);
        btnViewProducts = findViewById(R.id.btn_view_products);

        // Initialize Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Set Listeners
        btnUploadImage.setOnClickListener(v -> chooseImage());
        btnAddProduct.setOnClickListener(v -> addProduct());
        btnManageProducts.setOnClickListener(v -> openManageProducts());
        btnViewProducts.setOnClickListener(v -> openProductList());
        // Initialize Views
        btnViewOrders = findViewById(R.id.btn_view_orders);
        btnViewlgout = findViewById(R.id.btn_logout);

        // Open Orders Activity
        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, OrdersActivity.class);
            startActivity(intent);
        });


        btnViewlgout.setOnClickListener(v -> {
            // Clear admin login state
            SharedPreferences adminPrefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
            SharedPreferences.Editor adminEditor = adminPrefs.edit();
            adminEditor.putBoolean("isAdminLoggedIn", false);
            adminEditor.apply();

            // Redirect to MainActivity
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close AdminActivity
        });
    }







    // Open Product Management Page
private void openManageProducts() {
    Intent intent = new Intent(AdminActivity.this, ProductListActivity.class);
    startActivity(intent);
}

// Open Product List Page
private void openProductList() {
    Intent intent = new Intent(AdminActivity.this, ProductListActivity.class);
    startActivity(intent);
}

// Choose an Image from Gallery
private void chooseImage() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
        selectedImageUri = data.getData();
        imgProduct.setImageURI(selectedImageUri);
        uploadImageToCloudinary();
    }
}

// Upload Image to Cloudinary
private void uploadImageToCloudinary() {
    try {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Map<String, String> params = new HashMap<>();
        params.put("upload_preset", CLOUDINARY_UPLOAD_PRESET);
        params.put("file", "data:image/jpeg;base64," + base64Image);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, CLOUDINARY_UPLOAD_URL, new JSONObject(params),
                response -> {
                    try {
                        imageUrl = response.getString("secure_url");
                        Toast.makeText(AdminActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("Cloudinary Response Error", e.getMessage(), e);
                    }
                }, error -> {
            if (error.networkResponse != null && error.networkResponse.data != null) {
                String errorMessage = new String(error.networkResponse.data);
                Log.e("Volley Error", error.networkResponse.statusCode + ": " + errorMessage);
            } else {
                Log.e("Volley Error", "Unknown error occurred", error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    } catch (IOException e) {
        e.printStackTrace();
        Log.e("Bitmap Conversion Error", e.getMessage(), e);
    }
}

// Add Product to Firebase
private void addProduct() {
    String productName = etProductName.getText().toString().trim();
    String productPrice = etProductPrice.getText().toString().trim();
    String productDescription = etProductDescription.getText().toString().trim();
    String productStock = etProductStock.getText().toString().trim();

    if (productName.isEmpty() || productPrice.isEmpty() || productDescription.isEmpty() || productStock.isEmpty() || imageUrl == null) {
        Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        return;
    }

    String productId = databaseReference.push().getKey();
    Product product = new Product(productId, productName, productPrice, productDescription, imageUrl, Integer.parseInt(productStock));

    databaseReference.child(productId).setValue(product).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            Toast.makeText(AdminActivity.this, "Product Added Successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(AdminActivity.this, "Failed to Add Product", Toast.LENGTH_SHORT).show();
        }
    });
}

// Clear Input Fields
private void clearFields() {
    etProductName.setText("");
    etProductPrice.setText("");
    etProductDescription.setText("");
    etProductStock.setText("");
    imgProduct.setImageResource(0);
    imageUrl = null;
}



}
