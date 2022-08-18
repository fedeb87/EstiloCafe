package com.federicoberon.estilocafe.ui.home.cart;

import static com.federicoberon.estilocafe.utils.Constants.ENABLE_LOGS;
import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCTS_COUNT_STRING_KEY;
import static com.federicoberon.estilocafe.utils.Constants.PRODUCTS_STRING_KEY;
import static com.federicoberon.estilocafe.utils.Constants.TOTAL_KEY;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityViewCartBinding;
import com.federicoberon.estilocafe.model.OrderEntity;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.ui.home.HomeViewModel;
import com.federicoberon.estilocafe.utils.EmailUtils;
import com.federicoberon.estilocafe.utils.OrdersHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ViewCartActivity extends AppCompatActivity implements CartListAdapter.EventListener {

    private final String LOG_TAG = "<ViewCartActivity>";
    private ActivityViewCartBinding binding;
    public final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    HomeViewModel mViewModel;

    @Inject
    SharedPreferences sharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((EstiloCafeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);
        binding = ActivityViewCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getTheme().applyStyle(R.style.AppThemeDark, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.productsList.setLayoutManager(mLayoutManager);

        // if are a repeated order
        if(getIntent().hasExtra(TOTAL_KEY)){
            float total = getIntent().getFloatExtra(TOTAL_KEY, 0);
            ArrayList<ProductEntity> products = OrdersHelper.stringToProductList(getIntent().getStringExtra(PRODUCTS_STRING_KEY));
            HashMap<Long, Integer> carrito = OrdersHelper.stringToProductCount(getIntent().getStringExtra(PRODUCTS_COUNT_STRING_KEY));

            binding.productsList.setAdapter(new CartListAdapter(products, carrito,this, this));
            binding.emptyButton.setVisibility(View.GONE);
            binding.textViewTotalFinal.setText(String.format("$ %s", total));

            binding.sendButton.setOnClickListener(view -> {
                EmailUtils.sendMessage(EmailUtils.productsToBody(products, carrito, total),
                        String.format(getString(R.string.new_request), sharedPref.getString(NICKNAME_KEY, " "))
                        , sharedPref.getBoolean(ENABLE_LOGS, false));
                Toast.makeText(this, getString(R.string.order_sended), Toast.LENGTH_LONG).show();
                // save order
                saveOrderToDatabase(products, total, carrito);
                finish();
            });
        }else {
            mDisposable.add(mViewModel.getCartProducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(products -> {
                        binding.productsList.setAdapter(new CartListAdapter(products, mViewModel.getCart(), this, this));
                        binding.emptyButton.setOnClickListener(view -> emptyCart());
                        updateValues();
                    }));

            binding.sendButton.setOnClickListener(view -> cartToBody());
        }
    }

    private void emptyCart() {
        mViewModel.emptyCart();
        finish();
    }

    private void cartToBody() {
        mDisposable.add(mViewModel.getCartProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(products -> {
                String text = EmailUtils.productsToBody(products, mViewModel.getCart(), mViewModel.getTotal());
                EmailUtils.sendMessage(text,
                        String.format(getString(R.string.new_request), sharedPref.getString(NICKNAME_KEY, " ")), sharedPref.getBoolean(ENABLE_LOGS, false));
                Toast.makeText(this, getString(R.string.order_sended), Toast.LENGTH_LONG).show();
                saveOrderToDatabase(products, mViewModel.getTotal(), mViewModel.getCart());
                mViewModel.emptyCart();
        }));
        finish();
    }

    private void saveOrderToDatabase(List<ProductEntity> products, float total, HashMap<Long, Integer> cart) {
        // save order
        OrderEntity order = new OrderEntity();
        order.setDate(new Date());

        String title = products.get(0).getName();
        for(int i = 1;i<products.size(); i++)
            title.concat(", ").concat(products.get(i).getName());
        order.setTitle(title);
        order.setTotal(total);
        order.setProductsList(OrdersHelper.productListToString(new ArrayList<>(products)));
        order.setProductsCant(OrdersHelper.carritoToString(cart));
        mDisposable.add(mViewModel.saveOrder(order)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderId -> {})
        );
    }

    private void updateValues() {
        binding.textViewTotalValue.setText(String.format("$ %s", mViewModel.getTotal()));
        binding.textViewTotalFinal.setText(String.format("$ %s", mViewModel.getTotal()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getTheme().applyStyle(R.style.Theme_EstiloCafe, true);
        binding = null;
        mDisposable.clear();
    }

    @Override
    public void removeProduct(Long id, float price) {
        mViewModel.removeFromCart(id, price);
        if(mViewModel.getTotal()<1) {
            finish();
        }else
            updateValues();
    }

    @Override
    public boolean isRepeatedOrder(){
        return getIntent().hasExtra(TOTAL_KEY);
    }
}
