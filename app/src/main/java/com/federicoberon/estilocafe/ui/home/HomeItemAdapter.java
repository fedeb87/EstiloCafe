package com.federicoberon.estilocafe.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.ProductEntity;

import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.NormalViewHolder> {

    private final ArrayList<String> mArrayList;
    private final Context mContext;
    private final HomeFragment parentFragment;

    public HomeItemAdapter(ArrayList<String> mArrayList, Context context, HomeFragment fragment) {
        this.mArrayList = mArrayList;
        this.mContext = context;
        this.parentFragment = fragment;
    }

    @NonNull
    @Override
    public NormalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_products, parent, false);
        return new NormalViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull NormalViewHolder holder, int position) {

        String headerString = mArrayList.get(position);

        // setting the horizontal recyclerviews
        if (headerString != null) {

            ((NormalViewHolder) holder).tv_card_header.setText(mArrayList.get(position));

            ((NormalViewHolder) holder).main_item_app_recycler_view.setHasFixedSize(true);
            RecyclerView.LayoutManager normalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            ((NormalViewHolder) holder).main_item_app_recycler_view.setLayoutManager(normalLayoutManager);

            ((NormalViewHolder) holder).headerLayout.setOnClickListener(view ->
                    ((HomeActivity)mContext).searchManagement(null, mArrayList.get(position)));


            NormalProductAdapter normalProductAdapter = new NormalProductAdapter(new ArrayList<>(), mContext, parentFragment);
            ((NormalViewHolder) holder).main_item_app_recycler_view.setAdapter(normalProductAdapter);

            parentFragment.getProductByCategory(headerString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(products -> {
                                if(products==null || products.isEmpty())
                                    return;
                                normalProductAdapter.setData(new ArrayList<>(products));
                            },
                            throwable -> {
                                //if(sharedPref.getBoolean(ENABLE_LOGS, false))
                                    Log.e("MIO", "Unable to get alarms: ", throwable);
                            });

        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_card_header;
        private final RecyclerView main_item_app_recycler_view;
        private final LinearLayout headerLayout;


        NormalViewHolder(View view) {
            super(view);
            headerLayout = view.findViewById(R.id.sectionHeader);
            tv_card_header = view.findViewById(R.id.header);
            main_item_app_recycler_view = view.findViewById(R.id.main_item_app_recycler_view);
        }
    }
}
