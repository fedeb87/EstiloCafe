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

/**
 * este es el recyclerview de las promociones, el que esta arriba de todo
 *
 */
public class OfferProductAdapter extends RecyclerView.Adapter<OfferProductAdapter.OfferProductViewHolder> {

    private final ArrayList<ProductModel> mArrayList;
    private Context mContext;

    OfferProductAdapter(ArrayList<ProductModel> mArrayList) {
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public OfferProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_product, parent, false);
        return new OfferProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferProductViewHolder holder, int position) {
        /*Picasso.with(mcontext).load("file:"
                        + mArrayList.get(position).getItemImage())
                .into(holder.iv_card_graphics);*/

        ProductModel product = mArrayList.get(position);
        Picasso.with(mContext).load(product.getAppImage())
                .into(holder.iv_card_graphics);

        holder.tv_card_header.setText(product.getProductName());
        holder.tv_card_sub_header.setText(product.getDesc());
        holder.tv_card_pricing.setText(product.getPricingString());

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

    class OfferProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_card_graphics;
        private final TextView tv_card_header;
        private final TextView tv_card_sub_header;
        private final TextView tv_card_pricing;

        OfferProductViewHolder(View view) {
            super(view);
            iv_card_graphics = view.findViewById(R.id.pop_item_image);
            tv_card_header = view.findViewById(R.id.pop_item_header);
            tv_card_sub_header = view.findViewById(R.id.pop_item_sub_header);
            tv_card_pricing = view.findViewById(R.id.tv_card_pricing);
        }
    }
}
