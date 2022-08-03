package com.federicoberon.estilocafe.ui.home;

import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityViewCartBinding;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.utils.EmailUtils;

import java.util.Map;

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0)
            if (grantResults.length <= 0 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                getErrorDialog(this, getResources().getString(
                        R.string.error_generic_title), getResources().getString(
                        R.string.error_send_request)).show();
                finish();
            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static AlertDialog.Builder getErrorDialog(Context context, String title, String body){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(body)
                //.setMessage("Are you sure you want to delete this entry?")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.ok, null)
                .setIcon(context.getResources().getDrawable(
                        R.drawable.ic_warning));
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
                    binding.productsList.setAdapter(new CartListAdapter(products, mViewModel.getCart(),this, this));
                    updateValues();
                }));

        binding.sendButton.setOnClickListener(view -> cartToBody());
        binding.emptyButton.setOnClickListener(view -> emptyCart());
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

                String text=
                        "<html><font size='5' face='Courier New' >" +
                                "<table border='1' align='center'>"
                                + "<tr align='center'>"
                                + "<td><b>Product Name <b></td>"
                                + "<td><b>Unit price<b></td>"
                                + "<td><b>Count<b></td>"
                                + "</tr>";

                for(ProductEntity product : products) {
                    System.out.println(product.getName() + " :" + product.getPrice());
                    text = text + "<tr align='center'>" + "<td>" + product.getName() + "</td>"
                            + "<td>" + product.getPrice() + "</td>" + "<td>" + mViewModel.getCart().get(product.getId()) + "</td>" + "</tr>";
                }

                text += "\n TOTAL: " + mViewModel.getTotal();
                text += "</font></html>";

                EmailUtils.sendMessage(text,
                        String.format(getString(R.string.new_request), sharedPref.getString(NICKNAME_KEY, " ")));
        }));
        finish();
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
}
