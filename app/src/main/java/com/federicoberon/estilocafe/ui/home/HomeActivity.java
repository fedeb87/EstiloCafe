package com.federicoberon.estilocafe.ui.home;

import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_CAT;
import static com.federicoberon.estilocafe.utils.Constants.SEARCH_QUERY_KEY;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityHomeBinding;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.model.ProductModel;
import com.federicoberon.estilocafe.ui.home.cart.ViewCartActivity;
import com.federicoberon.estilocafe.ui.home.search.SearchResultFragment;
import com.federicoberon.estilocafe.ui.login.LoginActivity;
import com.federicoberon.estilocafe.utils.NetworkChangeReceiver;
import com.federicoberon.estilocafe.utils.NetworkStateManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NetworkChangeReceiver networkChangeReceiver;
    private Snackbar snackbar;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public SharedPreferences sharedPref;

    @Inject
    HomeViewModel mViewModel;

    /**
     * Observer for internet connectivity status live-data
     */
    private final Observer<Boolean> activeNetworkStateObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean isConnected) {
            if(!isConnected)
                showNotificationSnackBar();
            else
            if(snackbar!=null)
                snackbar.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((EstiloCafeApplication) getApplicationContext())
                .appComponent.inject(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NetworkStateManager.getInstance().getNetworkConnectivityStatus()
                .observe(this, activeNetworkStateObserver);

        drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.navView.getMenu().getItem(0).setChecked(true);
        binding.appBarMain.searchView.attachNavigationDrawerToMenuButton(drawer);

        // listener for searchview
        binding.appBarMain.searchView.setOnQueryChangeListener((oldQuery, newQuery) -> {

            Fragment currentFragment = getForegroundFragment();
            if(Objects.equals(currentFragment.getClass(), SearchResultFragment.class))
                searchManagement(newQuery, mViewModel.getSelectedCategory());
            else
                searchManagement(newQuery, null);

        });

        // set username in shared preferences
        mViewModel.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
                if (documentSnapshot.contains("nickname"))
                    if (documentSnapshot.get("nickname") != null)
                        sharedPref.edit().putString(NICKNAME_KEY, documentSnapshot.getString("nickname")).apply();
        });

        binding.appBarMain.searchView.setOnClearSearchActionListener(() -> {
            binding.appBarMain.searchView.clearSearchFocus();
            if(Objects.equals(getForegroundFragment().getClass(), SearchResultFragment.class))
                navController.popBackStack();
        });

        ArrayList<ProductEntity> productsList = new ArrayList<>();
        mViewModel.getAll().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()) {
                        ProductModel productModel = d.toObject(ProductModel.class);
                        assert productModel != null;
                        productModel.setProductID(d.getId());
                        productsList.add(convertToRoomProduct(productModel));
                    }
                }
                mDisposable.add(mViewModel.insertAllProducts(productsList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(products -> {
                        Log.w("MIO", "<<<HomeActivity>>> - cantidad de productos insertados " + products.size());
                    })
                );
            }
        });

        if(mViewModel.getCartCount()>0)
            binding.appBarMain.bCart.setVisibility(View.VISIBLE);

        // view cart event
        binding.appBarMain.bCart.setOnClickListener(view ->
                startActivity(new Intent(HomeActivity.this, ViewCartActivity.class)));

        binding.navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId()==R.id.logout_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);

                mViewModel.logout();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            NavigationUI.onNavDestinationSelected(item, navController);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        binding.navView.getMenu().getItem(0).setChecked(true);
    }

    private ProductEntity convertToRoomProduct(ProductModel productModel) {
        ProductEntity pe = new ProductEntity();
        pe.setCategory(productModel.getCategory());
        pe.setDescription(productModel.getDescription());
        pe.setImages(productModel.getImagesAsString());
        pe.setName(productModel.getName());
        pe.setOffer(productModel.getOffer());
        pe.setRating(productModel.getRating());
        pe.setPrice(productModel.getPrice());
        pe.setIdFirebase(productModel.getProductID());
        return pe;
    }

    public void searchManagement(String query, String category) {
        Fragment currentFragment = getForegroundFragment();
        if(Objects.equals(currentFragment.getClass(), SearchResultFragment.class)){
            ((SearchResultFragment)currentFragment).reloadResults(query, category);
        }else{
            Bundle args = new Bundle();
            args.putString(SEARCH_QUERY_KEY, query);
            args.putString(PRODUCT_CAT, category);
            navController.navigate(R.id.show_searchResultFragment, args);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConnectivity();
        updateCartBanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver!=null)
            unregisterReceiver(networkChangeReceiver);
        mDisposable.clear();
    }

    private void showNotificationSnackBar(){
        snackbar = Snackbar.make(binding.getRoot(), R.string.no_internet_title,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAnchorView(binding.getRoot());
        snackbar.setAction(R.string.ok_string, v -> snackbar.dismiss());
        snackbar.show();
    }

    private void checkInternetConnectivity() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver(binding.linearLayoutNoInternet);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Objects.equals(getForegroundFragment(), HomeFragment.class)) finish();
                //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_back_home);

            super.onBackPressed();
        }
    }

    private Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public ActivityHomeBinding getBinding() {
        return binding;
    }

    public NavController getNavController() {
        return navController;
    }

    public void updateCartBanner() {
        if(mViewModel.getCartCount()<1)
            binding.appBarMain.cartView.setVisibility(View.GONE);
        else
            binding.appBarMain.cartView.setVisibility(View.VISIBLE);

        binding.appBarMain.tTotalPrice.setText(String.valueOf(mViewModel.getTotal()));
        binding.appBarMain.tCartCount.setText(String.valueOf(mViewModel.getCartCount()));
    }

    public void clearSearchView() {
        binding.appBarMain.searchView.clearQuery();
        binding.appBarMain.searchView.clearSearchFocus();
    }
}