package com.federicoberon.estilocafe.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentHomeBinding;
import com.federicoberon.estilocafe.model.ProductEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dmax.dialog.SpotsDialog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements CartEventListener{
    private static final String LOG_TAG = "<<< HomeFragment >>>";
    private AlertDialog mDialog;
    private FragmentHomeBinding binding;
    private HomeItemAdapter homeItemAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    HomeViewModel mViewModel;

    @Inject
    SharedPreferences sharedPref;

    public HomeFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created login component
        ((EstiloCafeApplication)requireActivity().getApplication()).appComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new SpotsDialog.Builder()
                .setContext(requireContext())
                .setMessage(getResources().getString(
                        R.string.wait_msg))
                .setCancelable(false).build();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        configureMainRecyclerView();
        return binding.getRoot();
    }

    private void configureMainRecyclerView() {
        binding.forYouRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.forYouRecyclerView.setLayoutManager(layoutManager);
        //loadMainItemsData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDisposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)requireActivity()).clearSearchView();
        loadMainItemsData();
    }

    private void loadMainItemsData() {
        mDisposable.add(mViewModel.getAllCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(products -> {
                if(products.contains(getString(R.string.cat_promotions))) {
                    products.remove(getString(R.string.cat_promotions));
                    products.add(0, getString(R.string.cat_promotions));
                }

                homeItemAdapter = new HomeItemAdapter(new ArrayList<>(products), requireActivity(), this);
                binding.forYouRecyclerView.setAdapter(homeItemAdapter);
            }));
    }

    public Flowable<List<ProductEntity>> getProductByCategory(String cat) {
        return mViewModel.getProductsLocallyByCat(cat);
    }

    @Override
    public void addToCart(Long id, float price){
        mViewModel.addToCart(id, price);
        ((HomeActivity)requireActivity()).updateCartBanner();
    }

    @Override
    public void removeFromCart(Long id, float price){
        mViewModel.removeFromCart(id, price);
        ((HomeActivity)requireActivity()).updateCartBanner();
    }

    @Override
    public int getProductCount(Long id) {
        return mViewModel.getProductCount(id);
    }
}