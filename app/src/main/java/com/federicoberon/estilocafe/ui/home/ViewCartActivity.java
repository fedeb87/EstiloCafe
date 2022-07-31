package com.federicoberon.estilocafe.ui.home;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityViewCartBinding;
import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

// TODO: 31/07/2022 algun boton para confirmar el envio y programar esa parte del bacj
public class ViewCartActivity extends AppCompatActivity implements CartListAdapter.EventListener {

    private ActivityViewCartBinding binding;
    public final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    HomeViewModel mViewModel;

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

        mDisposable.add(mViewModel.getCartProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(products -> {
                    binding.productsList.setAdapter(new CartListAdapter(products, mViewModel.getCart(),this));
                    updteValues();
                }));
    }

    private void updteValues() {
        binding.textViewTotalValue.setText(String.format("$ %s", mViewModel.getTotal()));
        // todo ver como aplicar el tema de los descuentos
        binding.textViewTotalFinal.setText(String.format("$ %s", mViewModel.getTotal()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getTheme().applyStyle(R.style.Theme_EstiloCafe, true);
        binding = null;
    }

    @Override
    public void removeProduct(Long id, float price) {
        mViewModel.removeFromCart(id, price);
        if(mViewModel.getTotal()<1)
            finish();
        else
            updteValues();
    }
}
