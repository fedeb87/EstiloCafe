package com.federicoberon.estilocafe.ui.home.search;

import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_CAT;
import static com.federicoberon.estilocafe.utils.Constants.SEARCH_QUERY_KEY;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentSearchResultBinding;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.ui.home.CartEventListener;
import com.federicoberon.estilocafe.ui.home.HomeActivity;
import com.federicoberon.estilocafe.ui.home.HomeViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchResultFragment extends Fragment implements CartEventListener {
    private static final String LOG_TAG = "<SearchResultFragment>";
    private AlertDialog mDialog;
    private FragmentSearchResultBinding binding;
    private ResultProductsAdapter resultProductsAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    HomeViewModel mViewModel;

    @Inject
    SharedPreferences sharedPref;

    public SearchResultFragment() {}

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        String query = null;
        String category = null;
        if (bundle != null) {
            query = bundle.getString(SEARCH_QUERY_KEY, null);
            category = bundle.getString(PRODUCT_CAT, null);
        }

        mViewModel.setSelectedCategory(category);
        configureMainRecyclerView(query, category);
        return binding.getRoot();
    }

    private void configureMainRecyclerView(String query, String category) {
        binding.recyclerResult.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerResult.setLayoutManager(layoutManager);
        reloadResults(query, category);
    }

    public void reloadResults(String queryString, String category) {

        mDisposable.add(mViewModel.getFilteredProducts(queryString, category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(products -> {
                    resultProductsAdapter = new ResultProductsAdapter(new ArrayList<ProductEntity>(products), requireContext(), this);
                    binding.recyclerResult.setAdapter(resultProductsAdapter);
                }));
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