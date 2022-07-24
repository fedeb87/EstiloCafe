package com.federicoberon.estilocafe.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentSearchResultBinding;
import com.federicoberon.estilocafe.model.ProductModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dmax.dialog.SpotsDialog;

public class SearchResultFragment extends Fragment{
    private static final String LOG_TAG = "<SearchResultFragment>";
    private AlertDialog mDialog;
    private FragmentSearchResultBinding binding;

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
        configureMainRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // todo click en los items
    }

    private void configureMainRecyclerView() {
        binding.recyclerResult.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerResult.setLayoutManager(layoutManager);
        reloadResults("primra vez");
    }

    public void reloadResults(String query) {
        ArrayList<ProductModel> mArrayList = new ArrayList<>();
        if (getActivity() != null) {
            // todo aca iria el array de resultados para le query del usuario
            mArrayList.add(new ProductModel("id","Milanesa", "4", 100, R.drawable.coc, "Long desc"));
            mArrayList.add(new ProductModel("id","Arrollado", "3.3", 200, R.drawable.coc, "Long desc"));
            mArrayList.add(new ProductModel("id","Arroz con atún", "2", 150, R.drawable.coc, "Long desc"));
            mArrayList.add(new ProductModel("id","Menu", "5", 200, R.drawable.coc, "Long desc"));
        }

        ResultProductsAdapter resultProductsAdapter = new ResultProductsAdapter(mArrayList);
        binding.recyclerResult.setAdapter(resultProductsAdapter);
    }
}