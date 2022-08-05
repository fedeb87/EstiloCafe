package com.federicoberon.estilocafe.ui.home.search;

import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_CAT;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_DESC;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_IMAGES;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_FIREBASE_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_NAME;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_OFFER;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_PRICING;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_RATING;

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
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.ui.customview.RatingTextView;
import com.federicoberon.estilocafe.ui.home.CartEventListener;
import com.federicoberon.estilocafe.ui.home.detail.ProductDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * este seria para el recycler de la busqueda por texto
 */
public class ResultProductsAdapter extends RecyclerView.Adapter<ResultProductsAdapter.NormalProductViewHolder> {

    private Context mContext;
    private final CartEventListener listener;
    private ArrayList<ProductEntity> productList;

    ResultProductsAdapter(ArrayList<ProductEntity> list, Context context, CartEventListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.productList = list;
    }

    @NonNull
    @Override
    public ResultProductsAdapter.NormalProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal_product_result, parent, false);
        return new ResultProductsAdapter.NormalProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ResultProductsAdapter.NormalProductViewHolder holder, int position) {
        ProductEntity product = productList.get(position);

        holder.tv_app_name.setText(product.getName());
        holder.textViewPricing.setText(product.getPricingString());
        holder.tv_app_rating.setRating(Float.parseFloat(product.getRating()));

        holder.imageViewPlus.setOnClickListener(view -> {
            listener.addToCart(product.getId(),product.getPrice());
            holder.t_count.setText(String.valueOf(listener.getProductCount(product.getId())));
        });
        holder.imageViewMinus.setOnClickListener(view -> {
            listener.removeFromCart(product.getId(),product.getPrice());
            holder.t_count.setText(String.valueOf(listener.getProductCount(product.getId())));
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ProductDetailActivity.class);
            intent.putExtra(PRODUCT_KEY, product.getId());
            intent.putExtra(PRODUCT_FIREBASE_KEY, product.getIdFirebase());
            intent.putExtra(PRODUCT_NAME, product.getName());
            intent.putExtra(PRODUCT_CAT, product.getCategory());
            intent.putExtra(PRODUCT_DESC, product.getDescription());
            intent.putExtra(PRODUCT_PRICING, product.getPricingString());
            intent.putExtra(PRODUCT_RATING, product.getRating());
            intent.putExtra(PRODUCT_OFFER, product.getOffer());
            intent.putExtra(PRODUCT_IMAGES, product.getImages());
            mContext.startActivity(intent);
        });

        if(product.getImages() != null)
            Picasso.with(mContext).load(product.getImages().split(",")[0]).into(holder.iv_app_image);
    }

    class NormalProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_app_image;
        private final TextView tv_app_name;
        private final RatingTextView tv_app_rating;
        private final TextView textViewPricing;
        private final TextView t_count;
        final ImageView imageViewPlus;
        final ImageView imageViewMinus;

        NormalProductViewHolder(View view) {
            super(view);
            iv_app_image = view.findViewById(R.id.iv_app_image);
            tv_app_name = view.findViewById(R.id.tv_app_name);
            tv_app_rating = view.findViewById(R.id.tv_app_rating);
            t_count = view.findViewById(R.id.t_count);
            imageViewPlus = view.findViewById(R.id.i_plus);
            imageViewMinus = view.findViewById(R.id.i_minus);
            textViewPricing = view.findViewById(R.id.textViewPricing);
        }
    }
}
