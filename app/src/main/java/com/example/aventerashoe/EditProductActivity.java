package com.example.aventerashoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {
    private ImageView ivEditProductImage;
    private EditText etEditName, etEditPrice, etEditDescription, etEditStock;
    private Button btnChangeImage, btnSaveChanges, btnDeleteProduct;
    private DatabaseReference databaseReference;

    private String productId;
    private String currentImageUrl;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Initialize Views
        ivEditProductImage = findViewById(R.id.iv_edit_product_image);
        etEditName = findViewById(R.id.et_edit_name);
        etEditPrice = findViewById(R.id.et_edit_price);
        etEditDescription = findViewById(R.id.et_edit_description);
        etEditStock = findViewById(R.id.et_edit_stock);
        btnChangeImage = findViewById(R.id.btn_change_image);
        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnDeleteProduct = findViewById(R.id.btn_delete_product);



        // Initialize Sticky Header Views
        ImageView ivBackButton = findViewById(R.id.iv_back_button);
        ImageView ivStoreLogo = findViewById(R.id.iv_store_logo);



        // Back button click listener
        ivBackButton.setOnClickListener(v -> finish());
        // Store logo click listener (Navigate to admin page)
        ivStoreLogo.setOnClickListener(v -> {
            Intent homeIntent = new Intent(EditProductActivity.this, AdminActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        });




        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Get Product ID from Intent
        productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load product details
        loadProductDetails();

        // Handle Change Image
        btnChangeImage.setOnClickListener(v -> chooseImage());

        // Handle Save Changes
        btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Handle Delete Product
        btnDeleteProduct.setOnClickListener(v -> confirmDeleteProduct());
    }

    private void loadProductDetails() {
        databaseReference.child(productId).get().addOnSuccessListener(snapshot -> {
            Product product = snapshot.getValue(Product.class);
            if (product != null) {
                etEditName.setText(product.getName());
                etEditPrice.setText(product.getPrice());
                etEditDescription.setText(product.getDescription());
                etEditStock.setText(String.valueOf(product.getStock()));
                currentImageUrl = product.getImageUrl();

                // Load Product Image
                Glide.with(this)
                        .load(currentImageUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(ivEditProductImage);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

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
            try {
                ivEditProductImage.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadNewImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            Map<String, String> params = new HashMap<>();
            params.put("upload_preset", "ml_default"); // upload preset
            params.put("file", "data:image/jpeg;base64," + base64Image);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://api.cloudinary.com/v1_1/dgqvvnily/image/upload",
                    new JSONObject(params),
                    response -> {
                        try {
                            String newImageUrl = response.getString("secure_url");
                            databaseReference.child(productId).child("imageUrl").setValue(newImageUrl)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProductActivity.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EditProductActivity.this, "Failed to update image URL", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(EditProductActivity.this, "Error parsing upload response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(EditProductActivity.this, "Image upload failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            );

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process selected image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {
        String newName = etEditName.getText().toString().trim();
        String newPrice = etEditPrice.getText().toString().trim();
        String newDescription = etEditDescription.getText().toString().trim();
        String newStock = etEditStock.getText().toString().trim();

        if (newName.isEmpty() || newPrice.isEmpty() || newDescription.isEmpty() || newStock.isEmpty()) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update text fields in Firebase
        databaseReference.child(productId).child("name").setValue(newName);
        databaseReference.child(productId).child("price").setValue(newPrice);
        databaseReference.child(productId).child("description").setValue(newDescription);
        databaseReference.child(productId).child("stock").setValue(Integer.parseInt(newStock));

        // Handle image change
        if (selectedImageUri != null) {
            uploadNewImage();
        } else {
            Toast.makeText(this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }












    private void confirmDeleteProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProduct())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProduct() {
        databaseReference.child(productId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
