package com.federicoberon.estilocafe.ui.home;

import static com.federicoberon.estilocafe.utils.Constants.SEARCH_QUERY_KEY;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityHomeBinding;
import com.federicoberon.estilocafe.utils.NetworkChangeReceiver;
import com.federicoberon.estilocafe.utils.NetworkStateManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private NetworkChangeReceiver networkChangeReceiver;
    private Snackbar snackbar;

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

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NetworkStateManager.getInstance().getNetworkConnectivityStatus()
                .observe(this, activeNetworkStateObserver);

        drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_home);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        binding.navView.getMenu().getItem(0).setChecked(true);
        binding.appBarMain.searchView.attachNavigationDrawerToMenuButton(drawer);

        // listener for searchview
        binding.appBarMain.searchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            Fragment currentFragment = getForegroundFragment();
            if(Objects.equals(currentFragment.getClass(), SearchResultFragment.class)){
                ((SearchResultFragment)currentFragment).reloadResults("mi query");
            }else{
                Bundle args = new Bundle();
                args.putString(SEARCH_QUERY_KEY, "mi query");
                navController.navigate(R.id.show_searchResultFragment, args);
            }
        });

        binding.appBarMain.searchView.setOnClearSearchActionListener(() -> {
            binding.appBarMain.searchView.clearSearchFocus();
            if(Objects.equals(getForegroundFragment().getClass(), SearchResultFragment.class))
                navController.popBackStack();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver!=null)
            unregisterReceiver(networkChangeReceiver);
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
}