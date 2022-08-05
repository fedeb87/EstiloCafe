package com.federicoberon.estilocafe.ui.home.history;

import static com.federicoberon.estilocafe.utils.Constants.PRODUCTS_COUNT_STRING_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCTS_STRING_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_CAT;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_DESC;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_FIREBASE_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_IMAGES;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_NAME;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_OFFER;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_PRICING;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_RATING;
import static com.federicoberon.estilocafe.utils.Constants.TOTAL_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.OrderEntity;
import com.federicoberon.estilocafe.ui.home.CartEventListener;
import com.federicoberon.estilocafe.ui.home.cart.ViewCartActivity;
import com.federicoberon.estilocafe.ui.home.detail.ProductDetailActivity;
import com.federicoberon.estilocafe.utils.OrdersHelper;

import java.util.ArrayList;

/**
 * recyclerview de todos los items del home, seria cada categoria
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.NormalProductViewHolder> {
    private Context mContext;
    private final HistoryEventListener listener;
    private ArrayList<OrderEntity> orderList;

    HistoryAdapter(ArrayList<OrderEntity> list, Context context, HistoryEventListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.orderList = list;
    }

    @NonNull
    @Override
    public NormalProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new NormalProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull NormalProductViewHolder holder, int position) {
        if(!orderList.isEmpty()) {
            OrderEntity order = orderList.get(position);
            holder.productName.setText(order.getTitle());
            holder.totalPricing.setText("$".concat(String.valueOf(order.getTotal())));
            holder.orderDate.setText(OrdersHelper.dateToString(order.getDate()));

            holder.repeatButton.setOnClickListener(view -> listener.resendOrder(order));

            holder.viewDetailsButton.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, ViewCartActivity.class);
                intent.putExtra(PRODUCTS_STRING_KEY, order.getProductsList());
                intent.putExtra(PRODUCTS_COUNT_STRING_KEY, order.getProductsCant());
                intent.putExtra(TOTAL_KEY, order.getTotal());
                mContext.startActivity(intent);
            });
        }
    }

    public void setData(ArrayList<OrderEntity> orderEntities) {
        orderList = orderEntities;
        notifyDataSetChanged();
    }

    class NormalProductViewHolder extends RecyclerView.ViewHolder{
        final TextView productName;
        final TextView totalPricing;
        final TextView orderDate;
        final Button repeatButton;
        final Button viewDetailsButton;

        NormalProductViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.productName);
            totalPricing = view.findViewById(R.id.totalPricing);
            orderDate = view.findViewById(R.id.orderDate);
            repeatButton = view.findViewById(R.id.repeatButton);
            viewDetailsButton = view.findViewById(R.id.viewDetailsButton);
        }
    }
}
