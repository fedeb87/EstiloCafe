package com.federicoberon.estilocafe.ui.home;


import android.annotation.SuppressLint;
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

    public CartListAdapter(List<ProductEntity> products, HashMap<Long, Integer> cart, EventListener listener) {
        this.products = products;
        this.cart = cart;
        this.listener = listener;
    }

    public interface EventListener{
        void removeProduct(Long id, float price);
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

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolders holder, int position) {
        ProductEntity productEntity = products.get(holder.getAdapterPosition());
        holder.tName.setText(productEntity.getName());
        holder.tPrice.setText("$ "+productEntity.getPrice());
        Integer cant = cart.get(productEntity.getId());
        holder.tQuantity.setText(String.valueOf(cant));
        holder.tTotalPrice.setText(String.format("$ %s", cant * productEntity.getPrice()));

        holder.iDelete.setOnClickListener(view -> {
            listener.removeProduct(productEntity.getId(), productEntity.getPrice());
            products.remove(holder.getAdapterPosition());
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return this.products.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
