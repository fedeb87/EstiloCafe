package com.federicoberon.estilocafe.ui.home;

import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_KEY;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NormalProductAdapter extends RecyclerView.Adapter<NormalProductAdapter.NormalProductViewHolder> {

    private final ArrayList<ProductModel> mArrayList;
    private Context mContext;

    NormalProductAdapter(ArrayList<ProductModel> mArrayList) {
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public NormalProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_product, parent, false);
        return new NormalProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalProductViewHolder holder, int position) {
        /*Picasso.with(mContext).load("file:"
                        + mArrayList.get(position).getAppImage())
                .into(holder.iv_app_image);*/
        ProductModel product = mArrayList.get(position);
        Picasso.with(mContext).load(product.getAppImage())
                .into(holder.iv_app_image);

        holder.tv_app_name.setText(product.getProductName());
        holder.tv_app_rating.setText(product.getRating());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ProductDetailActivity.class);
            intent.putExtra(PRODUCT_KEY, product.getProductID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class NormalProductViewHolder extends RecyclerView.ViewHolder{
        private final ImageView iv_app_image;
        private final TextView tv_app_name;
        private final TextView tv_app_rating;

        NormalProductViewHolder(View view) {
            super(view);
            iv_app_image = view.findViewById(R.id.iv_app_image);
            tv_app_name = view.findViewById(R.id.tv_app_name);
            tv_app_rating = view.findViewById(R.id.tv_app_rating);
        }
    }
}
