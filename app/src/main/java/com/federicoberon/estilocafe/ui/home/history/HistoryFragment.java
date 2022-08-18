package com.federicoberon.estilocafe.ui.home.history;

import static com.federicoberon.estilocafe.utils.Constants.ENABLE_LOGS;
import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentHistoryBinding;
import com.federicoberon.estilocafe.model.OrderEntity;
import com.federicoberon.estilocafe.model.ProductEntity;
import com.federicoberon.estilocafe.ui.home.HomeActivity;
import com.federicoberon.estilocafe.ui.home.HomeViewModel;
import com.federicoberon.estilocafe.utils.DialogErrorHelper;
import com.federicoberon.estilocafe.utils.EmailUtils;
import com.federicoberon.estilocafe.utils.OrdersHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryFragment extends Fragment implements HistoryEventListener, DialogInterface.OnClickListener {
    private static final String LOG_TAG = "<<< HomeFragment >>>";
    private AlertDialog mDialog;
    private FragmentHistoryBinding binding;
    private HistoryAdapter historyAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    HomeViewModel mViewModel;

    @Inject
    SharedPreferences sharedPref;

    public HistoryFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        ((HomeActivity)requireActivity()).getBinding().appBarMain.searchView.setVisibility(View.GONE);
        // Injects this activity to the just created login component
        ((EstiloCafeApplication)requireActivity().getApplication()).appComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new SpotsDialog.Builder()
                .setContext(requireContext())
                .setMessage(getResources().getString(
                        R.string.wait_msg))
                .setCancelable(false).build();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        configureMainRecyclerView();
        return binding.getRoot();
    }

    private void configureMainRecyclerView() {
        binding.historyRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.historyRecyclerView.setLayoutManager(layoutManager);
        //loadMainItemsData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((HomeActivity)requireActivity()).getBinding().appBarMain.searchView.setVisibility(View.VISIBLE);
        mDisposable.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMainItemsData();
    }

    private void loadMainItemsData() {

        historyAdapter = new HistoryAdapter(new ArrayList<>(), requireActivity(), this);
        binding.historyRecyclerView.setAdapter(historyAdapter);

        mDisposable.add(mViewModel.getAllOrders()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(orders -> {
                historyAdapter.setData(new ArrayList<>(orders));
            }));
    }

    @Override
    public void resendOrder(OrderEntity order) {
        ArrayList<ProductEntity> productList = OrdersHelper.stringToProductList(order.getProductsList());
        HashMap<Long, Integer> carrito = OrdersHelper.stringToProductCount(order.getProductsCant());
        mDisposable.add(mViewModel.getAllProductsIds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productsIds -> {
                    ArrayList<Object> ids = new ArrayList<>();
                    for(ProductEntity product: productList)
                        ids.add(product.getIdFirebase());

                    if(productsIds.containsAll(ids)){
                        mViewModel.setEmailBody(EmailUtils.productsToBody(productList, carrito, order.getTotal()));
                        OrderEntity newOrder = new OrderEntity();

                        newOrder.setDate(new Date());
                        newOrder.setTitle(order.getTitle());
                        newOrder.setProductsList(order.getProductsList());
                        newOrder.setProductsCant(order.getProductsCant());
                        newOrder.setTotal(order.getTotal());

                        mViewModel.setOrder(newOrder);
                        DialogErrorHelper.getWarningDialog(requireContext(),
                                getString(R.string.warning_pricing),
                                getString(R.string.warning_pricing_body), this).show();
                    }else{
                        // show error msg
                        DialogErrorHelper.getErrorDialog(requireContext(),
                                getString(R.string.error_sending_order),
                                getString(R.string.error_sending_order_body)).show();
                    }
                }));
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        EmailUtils.sendMessage(mViewModel.getEmailBody(),
                String.format(getString(R.string.new_request), sharedPref.getString(NICKNAME_KEY, " ")), sharedPref.getBoolean(ENABLE_LOGS, false));

        Toast.makeText(requireContext(), getString(R.string.order_sended), Toast.LENGTH_LONG).show();
        mDisposable.add(mViewModel.saveOrder(mViewModel.getOrder())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderId -> {
                    Log.w("MIO", "Id de orden guardada: " + orderId);
                })
        );

        Toast.makeText(requireContext(), R.string.order_sended, Toast.LENGTH_LONG).show();
    }
}