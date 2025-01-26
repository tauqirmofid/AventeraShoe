package com.example.aventerashoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import android.text.TextWatcher;
import android.text.Editable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;


import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;



public class Homepage extends AppCompatActivity {

    private FirebaseAuth auth;

    private RecyclerView recyclerViewSearchResults;
    private List<Product> searchResultsList;
    private SearchListAdapter searchListAdapter; // Declare as a class-level field

    private ConstraintLayout searchBar;
    private ImageView ivCloseSearch, ivSearchButton;
    private SearchView searchView;


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView  ivStoreLogo, ivManuButton;

    private HorizontalScrollView horizontalScrollView;

    private LinearLayout linearLayoutImages;
    private RecyclerView recyclerViewRecentProducts;
    private RecyclerView recyclerViewCustomerReviews;
    private CustomerReviewAdapter customerReviewAdapter;
    private List<CustomerReview> customerReviewList;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private int scrollPosition = 0; // Initial scroll position
    private int totalWidth = 0; // Total width of all images
    private int viewWidth = 0; // Width of the visible HorizontalScrollView

    private CustomerProductAdapter customerProductAdapter;
    private List<Product> productList;
    private Button btnSeeAllProducts;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences adminPrefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        boolean isAdminLoggedIn = adminPrefs.getBoolean("isAdminLoggedIn", false);
        if (isAdminLoggedIn) {
            // Redirect to AdminActivity for admins
            Intent intent = new Intent(Homepage.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent back navigation
            return;
        }

        setContentView(R.layout.activity_homepage);

        recyclerViewCustomerReviews = findViewById(R.id.recyclerViewCustomerReviews);
        recyclerViewCustomerReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize data
        customerReviewList = new ArrayList<>();
        loadCustomerReviews();



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);





        View headerView = navigationView.getHeaderView(0);

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        ivSearchButton = findViewById(R.id.iv_search_button);
        ivStoreLogo = findViewById(R.id.iv_store_logo);
        ivManuButton = findViewById(R.id.iv_manu_button);
        ivManuButton = findViewById(R.id.iv_manu_button);
        TextView tvUserName = headerView.findViewById(R.id.tv_user_name);

// Setup Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        auth = FirebaseAuth.getInstance();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

// Update Navigation Drawer dynamically when it opens
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Not needed
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Update user name dynamically
                if (auth.getCurrentUser() != null) {
                    String userId = auth.getCurrentUser().getUid(); // Get the logged-in user's ID
                    usersRef.child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.getValue(String.class);
                                tvUserName.setText("Welcome, " + name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            tvUserName.setText("Welcome, Guest /n sorry DatabaseError");
                        }
                    });
                } else {
                    tvUserName.setText("Welcome, Guest");
                }

                // Update Logout/Login text dynamically
                MenuItem logoutMenuItem = navigationView.getMenu().findItem(R.id.nav_logout);
                if (auth.getCurrentUser() != null) {
                    logoutMenuItem.setTitle("Logout");
                } else {
                    logoutMenuItem.setTitle("Login");
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Not needed
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Not needed
            }
        });

// Setup Navigation Drawer Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Homepage.class));
            } else if (id == R.id.nav_all_products) {
                startActivity(new Intent(this, AllProducts.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, DisplayUserActivity.class));
            } else if (id == R.id.nav_logout) {
                if (auth.getCurrentUser() != null) {
                    auth.signOut(); // Log the user out
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class)); // Redirect to login page
                } else {
                    startActivity(new Intent(this, MainActivity.class)); // Redirect to login page
                }
            } else if (auth.getCurrentUser() == null && id == R.id.nav_orders) {
                showLoginDialog();
            } else if (auth.getCurrentUser() != null && id == R.id.nav_orders) {
                startActivity(new Intent(this, ViewOrdersActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

// Handle Menu Button Click
        ivManuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle Store Logo Click
        ivStoreLogo.setOnClickListener(v -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });





        // Initialize search bar components
        searchBar = findViewById(R.id.searchBar);
        ivCloseSearch = findViewById(R.id.iv_close_search);
        ivSearchButton = findViewById(R.id.iv_search_button);
        searchView = findViewById(R.id.searchView);




        // Show search bar when search button is clicked
        ivSearchButton.setOnClickListener(v -> {
            searchBar.setVisibility(View.VISIBLE);
        });






        // Initialize RecyclerView and Adapter
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the data list and adapter
        searchResultsList = new ArrayList<>();
        searchListAdapter = new SearchListAdapter(searchResultsList, product -> {
            // Navigate to Product Details Page
            Intent intent = new Intent(Homepage.this, ProductPageActivity.class);
            intent.putExtra("productId", product.getProductId());
            startActivity(intent);
        });

        // Set adapter to the RecyclerView
        recyclerViewSearchResults.setAdapter(searchListAdapter);

        // Setup the SearchView
        setupSearchView();














        // Set adapter
        customerReviewAdapter = new CustomerReviewAdapter(customerReviewList);
        recyclerViewCustomerReviews.setAdapter(customerReviewAdapter);

        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        linearLayoutImages = findViewById(R.id.linearLayoutImages);
        recyclerViewRecentProducts = findViewById(R.id.recyclerViewRecentProducts);
        btnSeeAllProducts = findViewById(R.id.btnSeeAllProducts);

        // Initialize RecyclerView
        setupRecyclerView();

        // Button click to navigate to AllProducts Activity
        btnSeeAllProducts.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, AllProducts.class);
            startActivity(intent);
        });

        // Initialize the handler for auto-scrolling
        autoScrollHandler = new Handler();

        // Measure the dimensions after the layout is drawn
        horizontalScrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (linearLayoutImages.getChildCount() > 0) {
                viewWidth = horizontalScrollView.getWidth();
                totalWidth = linearLayoutImages.getWidth();

                // Start auto-scrolling
                startAutoScroll();
                horizontalScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(() -> {});
            }
        });

        // Load recent products from the database
        loadRecentProducts();
    }




    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Login Required")
                .setMessage("You need to be logged in to access this page.")
                .setPositiveButton("Login", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class)); // Redirect to login
                    finish(); // Finish current activity
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    startActivity(new Intent(this, Homepage.class)); // Redirect to homepage
                    finish(); // Finish current activity
                })
                .setCancelable(false) // Prevent dismissing by clicking outside
                .show();
    }












    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);

        // Show the search bar and focus on the SearchView when the search button is clicked
        ivSearchButton.setOnClickListener(v -> {
            searchBar.setVisibility(View.VISIBLE);



            // Set initial position for slide-in animation
            searchBar.setTranslationY(-searchBar.getHeight());
            searchBar.setAlpha(0f);

            // Animate slide-in effect
            searchBar.animate()
                    .translationY(0f) // Slide to its original position
                    .alpha(1.0f) // Fade in
                    .setDuration(300) // Duration of the animation
                    .setInterpolator(new android.view.animation.DecelerateInterpolator()) // Smooth deceleration
                    .start();


            searchView.setIconified(false); // Ensure SearchView is expanded
            searchView.requestFocus(); // Request focus on SearchView
            showKeyboard(); // Show the keyboard
        });

        // Close the search bar when the close button (minus icon) is clicked
        ivCloseSearch.setOnClickListener(v -> {
            // Animate slide-out effect
            searchBar.animate()
                    .translationY(-searchBar.getHeight()) // Slide up and hide
                    .alpha(0f) // Fade out
                    .setDuration(300) // Duration of the animation
                    .setInterpolator(new android.view.animation.AccelerateInterpolator()) // Smooth acceleration
                    .withEndAction(() -> {
                        searchBar.setVisibility(View.GONE); // Hide the view after animation
                        searchView.clearFocus(); // Remove focus from the SearchView
                        searchView.setQuery("", false); // Clear the text in the SearchView
                        hideKeyboard(); // Hide the keyboard
                    })
                    .start();
        });

        // Handle search text changes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query); // Perform search when user submits query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText); // Dynamically update search results as the user types
                return true;
            }
        });
    }

    private void showKeyboard() {
        // Show the keyboard programmatically
        searchView.post(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void hideKeyboard() {
        // Hide the keyboard programmatically
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }




    // Update the search logic
    private void performSearch(String query) {
        if (query.isEmpty()) {
            recyclerViewSearchResults.setVisibility(View.GONE);
            searchResultsList.clear();
            searchListAdapter.notifyDataSetChanged();
            return;
        }

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResultsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.getName().toLowerCase().contains(query.toLowerCase())) {
                        product.setProductId(snapshot.getKey());
                        searchResultsList.add(product);
                    }
                }

                if (searchResultsList.isEmpty()) {
                    Toast.makeText(Homepage.this, "No products found!", Toast.LENGTH_SHORT).show();
                    recyclerViewSearchResults.setVisibility(View.GONE);
                } else {
                    recyclerViewSearchResults.setVisibility(View.VISIBLE);
                }

                searchListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Homepage.this, "Search failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }











    private void loadCustomerReviews() {
        customerReviewList.add(new CustomerReview("Anneke B.", 5,
                "These Aventera Winter 2.0 shoes are truly a game-changer. They keep my feet warm even on colder days...",
                "Aventera Winter 2.0", R.drawable.customer1));
        customerReviewList.add(new CustomerReview("Maria V.", 5,
                "I've been wearing my new Aventera shoes for a few weeks now, and I can't believe how comfortable they are!",
                "Vital", R.drawable.customer2));
        customerReviewList.add(new CustomerReview("Johanna S.", 5,
                "The Aventera Winter Pro shoes are now my favorite. They're so lightweight and give my toes plenty of space.",
                "Aventera Winter Pro", R.drawable.customer3));
    }




    private void setupRecyclerView() {
        // Setup the layout manager and adapter for RecyclerView
        recyclerViewRecentProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productList = new ArrayList<>();
        customerProductAdapter = new CustomerProductAdapter(this, productList);

        // Set the item click listener
        customerProductAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(Homepage.this, ProductPageActivity.class);
            intent.putExtra("productId", product.getProductId()); // Pass product ID to ProductPageActivity
            startActivity(intent);
        });

        recyclerViewRecentProducts.setAdapter(customerProductAdapter);
    }


    private void loadRecentProducts() {
        // Load 3 most recent products from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        databaseReference.limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                customerProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void startAutoScroll() {
        final int scrollStep = 2; // The number of pixels to scroll in each step
        final int scrollDelay = 30; // The delay between each scroll step in milliseconds

        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                scrollPosition += scrollStep; // Increment by a fixed step
                horizontalScrollView.smoothScrollTo(scrollPosition, 0);

                // Reset seamlessly when reaching the end of the scrollable area
                if (scrollPosition >= totalWidth - viewWidth) {
                    scrollPosition = 0;
                    horizontalScrollView.scrollTo(scrollPosition, 0);
                }

                // Post the next scroll step with a constant delay
                autoScrollHandler.postDelayed(this, scrollDelay);
            }
        };

        // Start the auto-scroll loop
        autoScrollHandler.post(autoScrollRunnable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable); // Cleanup
        }
    }










}
