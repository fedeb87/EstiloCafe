package com.federicoberon.estilocafe.ui.home.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.ProductEntity;

import java.util.HashMap;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.RecyclerViewHolders> {

    private final List<ProductEntity> products;
    private final HashMap<Long, Integer> cart;
    private final EventListener listener;
    private final Context mContext;

    public CartListAdapter(List<ProductEntity> products, HashMap<Long, Integer> cart, Context context, EventListener listener) {
        this.products = products;
        this.cart = cart;
        this.listener = listener;
        this.mContext = context;
    }

    public interface EventListener{
        void removeProduct(Long id, float price);
        boolean isRepeatedOrder();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        private AppCompatImageView iDelete;
        private TextView tName,tPrice,tTotalPrice,tQuantity;


        RecyclerViewHolders(View itemView) {
            super(itemView);

            iDelete = itemView.findViewById(R.id.i_delete);
            tName = itemView.findViewById(R.id.t_name);
            tPrice = itemView.findViewById(R.id.t_price);
            tTotalPrice = itemView.findViewById(R.id.t_total_price);
            tQuantity = itemView.findViewById(R.id.t_quantity);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_cart, null);
        return new RecyclerViewHolders(layoutView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolders holder, int position) {
        ProductEntity productEntity = products.get(holder.getAdapterPosition());
        holder.tName.setText(productEntity.getName());
        holder.tPrice.setText(String.format("$ %s", productEntity.getPrice()));
        Integer cant = cart.get(productEntity.getId());
        holder.tQuantity.setText(String.valueOf(cant));
        holder.tTotalPrice.setText(String.format("$ %s", cant * productEntity.getPrice()));

        if(!listener.isRepeatedOrder()) {
            if (cart.get(productEntity.getId()) > 1)
                holder.iDelete.setBackground(mContext.getDrawable(R.drawable.ic_remove));
            else
                holder.iDelete.setBackground(mContext.getDrawable(R.drawable.ic_cancel));

            holder.iDelete.setOnClickListener(view -> {
                Long productId = productEntity.getId();
                if (cart.get(productId) > 1) {
                    listener.removeProduct(productId, productEntity.getPrice());
                    //cart.put(productId, cart.get(productId) - 1);
                } else {
                    //cart.remove(productId);
                    products.remove(holder.getAdapterPosition());
                    listener.removeProduct(productId, productEntity.getPrice());
                }
                notifyDataSetChanged();
            });
        }else{
            holder.iDelete.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return this.products.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
