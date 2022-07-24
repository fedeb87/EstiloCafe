package com.federicoberon.estilocafe.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.HomeItemModel;
import com.federicoberon.estilocafe.model.ProductModel;

import java.util.ArrayList;

public class HomeItemAdapter extends RecyclerView.Adapter {

    private final ArrayList<HomeItemModel> mArrayList;
    private final Context mContext;

    public HomeItemAdapter(ArrayList<HomeItemModel> mArrayList, Context context) {
        this.mArrayList = mArrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_products, parent, false);
                return new PopularViewHolder(view);

            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_products, parent, false);
                return new NormalViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HomeItemModel homeItemModel = mArrayList.get(position);

        // setting the horizontal recyclerviews
        if (homeItemModel != null) {
            switch (homeItemModel.getType()) {
                case "special":
                    ((PopularViewHolder) holder).popular_item_app_recycler_view.setHasFixedSize(true);
                    RecyclerView.LayoutManager popLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    ((PopularViewHolder) holder).popular_item_app_recycler_view.setLayoutManager(popLayoutManager);

                    // recycler de arriba de to do
                    ArrayList<ProductModel> popularList = loadPopularList();
                    OfferProductAdapter popularItemAppAdapter = new OfferProductAdapter(popularList);
                    ((PopularViewHolder) holder).popular_item_app_recycler_view.setAdapter(popularItemAppAdapter);

                    break;
                case "normal":
                    ((NormalViewHolder) holder).tv_card_header.setText(mArrayList.get(position).getHeader());

                    if (!mArrayList.get(position).getSubHeader().equals(""))
                        ((NormalViewHolder) holder).tv_card_sub_header.setText(mArrayList.get(position).getSubHeader());
                    else
                        ((NormalViewHolder) holder).tv_card_sub_header.setVisibility(View.GONE);

                    ((NormalViewHolder) holder).main_item_app_recycler_view.setHasFixedSize(true);
                    RecyclerView.LayoutManager normalLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                    ((NormalViewHolder) holder).main_item_app_recycler_view.setLayoutManager(normalLayoutManager);

                    ArrayList<ProductModel> list = loadNormalList();

                    NormalProductAdapter mainItemAppAdapter = new NormalProductAdapter(list);
                    ((NormalViewHolder) holder).main_item_app_recycler_view.setAdapter(mainItemAppAdapter);

                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    // TODO: 22/07/2022 estos metodos los tiene que traer de un server remoto, sino quedarán hardcodeados 
    private ArrayList<ProductModel> loadNormalList() {
        ArrayList<ProductModel> mArrayList = new ArrayList<>();

        if (mContext != null) {
            // TODO: 22/07/2022 aca irian los productos normales
            mArrayList.add(new ProductModel("id", "Udacity", "4.3", 100, R.drawable.coc, "Long desc"));
        }

        return mArrayList;
    }

    private ArrayList<ProductModel> loadPopularList() {
        ArrayList<ProductModel> mArrayList = new ArrayList<>();

        if (mContext != null) {
            mArrayList.add(new ProductModel("id","Awesome Cricket Games", "4.5",100, R.drawable.card_image_1, "Enjoy seasonal clones and updates"));
            mArrayList.add(new ProductModel("id","World Heath Day","4.3",150 , R.drawable.card_image_2, "Discover clones for a healthy life"));
        }

        return mArrayList;
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_card_header;
        private final TextView tv_card_sub_header;
        private final TextView btn_more;
        private final RecyclerView main_item_app_recycler_view;


        NormalViewHolder(View view) {
            super(view);

            tv_card_header = view.findViewById(R.id.header);
            tv_card_sub_header = view.findViewById(R.id.sub_header);
            btn_more = view.findViewById(R.id.more);
            main_item_app_recycler_view = view.findViewById(R.id.main_item_app_recycler_view);
        }
    }

    class PopularViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView popular_item_app_recycler_view;

        PopularViewHolder(View view) {
            super(view);
            popular_item_app_recycler_view = view.findViewById(R.id.popular_item_app_recycler_view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch (mArrayList.get(position).getType()) {
            case "special":
                return 1;
            case "normal":
                return 2;
            default:
                return -1;
        }
    }
}
