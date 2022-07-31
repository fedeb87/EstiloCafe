package com.federicoberon.estilocafe.ui.home;

import static androidx.core.content.ContextCompat.getDrawable;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_CAT;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_DESC;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_IMAGES;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_FIREBASE_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_NAME;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_OFFER;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_PRICING;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_RATING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.ui.customview.RatingTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * recyclerview de todos los items del home, seria cada categoria
 */
public class NormalProductAdapter extends RecyclerView.Adapter<NormalProductAdapter.NormalProductViewHolder> {
    private Context mContext;
    private final CartEventListener listener;
    private ArrayList<ProductEntity> productList;

    NormalProductAdapter(ArrayList<ProductEntity> list, Context context, CartEventListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.productList = list;
    }

    @NonNull
    @Override
    public NormalProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_product, parent, false);
        return new NormalProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull NormalProductViewHolder holder, int position) {
        ProductEntity product = productList.get(position);

        holder.tv_app_name.setText(product.getName());
        holder.tv_app_rating.setRating(Float.parseFloat(product.getRating()));
        holder.textViewPricing.setText(product.getPricingString());

        holder.imageViewPlus.setOnClickListener(view -> {
            listener.addToCart(product.getId(),product.getPrice());
            holder.t_count.setText(String.valueOf(listener.getProductCount(product.getId())));
        });
        holder.imageViewMinus.setOnClickListener(view -> {
            listener.removeFromCart(product.getId(),product.getPrice());
            holder.t_count.setText(String.valueOf(listener.getProductCount(product.getId())));
        });

        holder.t_count.setText(String.valueOf(listener.getProductCount(product.getId())));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ProductDetailActivity.class);
            intent.putExtra(PRODUCT_KEY, product.getId());
            intent.putExtra(PRODUCT_FIREBASE_KEY, product.getIdFirebase());
            intent.putExtra(PRODUCT_NAME, product.getName());
            intent.putExtra(PRODUCT_CAT, product.getCategory());
            intent.putExtra(PRODUCT_DESC, product.getDescription());
            intent.putExtra(PRODUCT_PRICING, product.getPrice());
            intent.putExtra(PRODUCT_RATING, product.getRating());
            intent.putExtra(PRODUCT_OFFER, product.getOffer());
            intent.putExtra(PRODUCT_IMAGES, product.getImages());
            mContext.startActivity(intent);
        });

        String offer = product.getOffer();
        if(offer!=null && !offer.isEmpty()){
            holder.imageViewOffer.setVisibility(View.VISIBLE);
            switch (offer) {
                case "2x1": {
                    holder.imageViewOffer.setBackground(mContext.getDrawable(R.drawable._2x1));
                    break;
                }case "3x2": {
                    holder.imageViewOffer.setImageDrawable(mContext.getDrawable(R.drawable._3x2));
                    break;
                }case "15off":{
                    holder.imageViewOffer.setImageDrawable(mContext.getDrawable(R.drawable.off_15));
                    break;
                }case "20off": {
                    holder.imageViewOffer.setImageDrawable(mContext.getDrawable(R.drawable.off_20));
                    break;
                }case "40off": {
                    holder.imageViewOffer.setImageDrawable(mContext.getDrawable(R.drawable.off_40));
                    break;
                }default: {
                    holder.imageViewOffer.setVisibility(View.GONE);
                    break;
                }
            }
        }else
            holder.imageViewOffer.setVisibility(View.GONE);

        if(product.getImages() != null) {
            for (String s : product.getImages().split(","))
                Picasso.with(mContext).load(s).into(holder.iv_app_image);
        }
    }

    public void setData(ArrayList<ProductEntity> productEntities) {
        productList = productEntities;
        notifyDataSetChanged();
    }

    class NormalProductViewHolder extends RecyclerView.ViewHolder{
        final ImageView iv_app_image;
        final TextView tv_app_name;
        final RatingTextView tv_app_rating;
        final TextView textViewPricing;
        private final TextView t_count;
        final ImageView imageViewPlus;
        final ImageView imageViewMinus;
        final ImageView imageViewOffer;

        NormalProductViewHolder(View view) {
            super(view);
            iv_app_image = view.findViewById(R.id.iv_app_image);
            tv_app_name = view.findViewById(R.id.tv_app_name);
            tv_app_rating = view.findViewById(R.id.tv_app_rating);
            t_count = view.findViewById(R.id.t_count);
            imageViewPlus = view.findViewById(R.id.ImageViewPlus);
            imageViewMinus = view.findViewById(R.id.ImageViewMinus);
            textViewPricing = view.findViewById(R.id.textViewPricing);
            imageViewOffer = view.findViewById(R.id.imageViewOffer);
        }
    }
}
