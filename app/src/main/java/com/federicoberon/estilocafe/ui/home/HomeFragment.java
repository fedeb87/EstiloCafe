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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentHomeBinding;
import com.federicoberon.estilocafe.model.HomeItemModel;

import java.util.ArrayList;

import javax.inject.Inject;
import dmax.dialog.SpotsDialog;

public class HomeFragment extends Fragment {
    private static final String LOG_TAG = "<<< HomeFragment >>>";
    private AlertDialog mDialog;
    private FragmentHomeBinding binding;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //todo listener for view clicks
    }

    private void configureMainRecyclerView() {
        binding.forYouRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.forYouRecyclerView.setLayoutManager(layoutManager);
        loadMainItemsData();
    }

    private void loadMainItemsData() {

        ArrayList<HomeItemModel> mArrayList = new ArrayList<>();

        // TODO: 22/07/2022 estos header deberia sacarlos de firebase, para que sean dinamicos si crean una nueva categoria
        
        if (getActivity() != null) {
            // estos son los 4 principales, tienen que ser como el normal
            mArrayList.add(new HomeItemModel("Promociones", "", "special"));
            mArrayList.add(new HomeItemModel("Recomendados", "", "normal"));
            mArrayList.add(new HomeItemModel("Sandwiches", "", "normal"));
            mArrayList.add(new HomeItemModel("Menu", "", "normal"));
        }

        HomeItemAdapter homeItemAdapter = new HomeItemAdapter(mArrayList, requireActivity());
        binding.forYouRecyclerView.setAdapter(homeItemAdapter);

    }
}