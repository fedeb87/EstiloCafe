package com.federicoberon.estilocafe.ui.home;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityProductDetailBinding;
import com.federicoberon.estilocafe.ui.login.LoginActivity;
import com.federicoberon.estilocafe.ui.splash.SplashActivity;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.disposables.CompositeDisposable;

public class ProductDetailActivity extends AppCompatActivity implements CartEventListener {
    private static final String LOG_TAG = "<<<ProductDetailFragment>>>";
    private ActivityProductDetailBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SliderAdapter mSliderAdapter;

    @Inject
    SharedPreferences sharedPref;

    @Inject
    HomeViewModel mViewModel;

    public ProductDetailActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        ((EstiloCafeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().getStringExtra(PRODUCT_NAME)!=null){
            mViewModel.setProduct_id(getIntent().getLongExtra(PRODUCT_KEY,0));
            mViewModel.setProduct_FirebaseId(getIntent().getStringExtra(PRODUCT_FIREBASE_KEY));
            mViewModel.setProduct_name(getIntent().getStringExtra(PRODUCT_NAME));
            mViewModel.setProduct_cat(getIntent().getStringExtra(PRODUCT_CAT));
            mViewModel.setProduct_desc(getIntent().getStringExtra(PRODUCT_DESC));
            mViewModel.setProduct_price(getIntent().getFloatExtra(PRODUCT_PRICING,0));
            mViewModel.setProduct_rating(String.valueOf(getIntent().getStringExtra(PRODUCT_RATING)));
            mViewModel.setProduct_offer(getIntent().getStringExtra(PRODUCT_OFFER));

            mViewModel.setProduct_images(getIntent().getStringExtra(PRODUCT_IMAGES));
        }

        binding.detailContent.ImageViewPlus.setOnClickListener(view -> addToCart(mViewModel.getProduct_id(),mViewModel.getProduct_price()));
        binding.detailContent.ImageViewMinus.setOnClickListener(view -> removeFromCart(mViewModel.getProduct_id(),mViewModel.getProduct_price()));

        if(mViewModel.getCartCount()>0)
            binding.bCart.setVisibility(View.VISIBLE);

        binding.bCart.setOnClickListener(view ->
                startActivity(new Intent(ProductDetailActivity.this, ViewCartActivity.class)));
        
        instanceSlider();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onResume() {
        super.onResume();
        if(mViewModel.getProduct_offer()!=null && !mViewModel.getProduct_offer().isEmpty()) {
            binding.imageViewOffer.setVisibility(View.VISIBLE);
            switch (mViewModel.getProduct_offer()) {
                case "2x1": {
                    binding.imageViewOffer.setImageDrawable(getResources().getDrawable(R.drawable._2x1));
                    break;
                }case "3x2": {
                    binding.imageViewOffer.setImageDrawable(getResources().getDrawable(R.drawable._3x2));
                    break;
                }case "15off":{
                    binding.imageViewOffer.setImageDrawable(getResources().getDrawable(R.drawable.off_15));
                    break;
                }case "20off": {
                    binding.imageViewOffer.setImageDrawable(getResources().getDrawable(R.drawable.off_20));
                    break;
                }case "40off": {
                    binding.imageViewOffer.setImageDrawable(getResources().getDrawable(R.drawable.off_40));
                    break;
                }default: {
                    binding.imageViewOffer.setVisibility(View.GONE);
                    break;
                }
            }
        }

        binding.imageGoBack.setOnClickListener(view -> finish());

        // chart view
        binding.tCartCount.setText(String.valueOf(mViewModel.getCartCount()));
        binding.tTotalPrice.setText(String.valueOf(mViewModel.getTotal()));

        binding.detailContent.tCount.setText(String.valueOf(getProductCount(mViewModel.getProduct_id())));
        binding.detailContent.productName.setText(mViewModel.getProduct_name());
        binding.detailContent.productDesc.setText(mViewModel.getProduct_desc());
        binding.detailContent.productPricing.setText(String.format("$%s", mViewModel.getProduct_price()));
        binding.detailContent.productDescription.setText(mViewModel.getProduct_desc());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // clear all the subscriptions
        mDisposable.clear();
        binding = null;
    }

    private void instanceSlider() {
        List<String> mSliderItems = new ArrayList<>();
        if(mViewModel.getProduct_images() != null && mViewModel.getProduct_images().size()>0)
            mSliderItems.addAll(mViewModel.getProduct_images());

        Picasso.with(this).load(mSliderItems.get(0))
                .into(binding.detailContent.productIcon);

        mSliderAdapter = new SliderAdapter(this, mSliderItems);
        binding.imageSlider.setSliderAdapter(mSliderAdapter);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(5);
        binding.imageSlider.setAutoCycle(true);
        binding.imageSlider.startAutoCycle();
    }

    @Override
    public void addToCart(Long id, float price){
        mViewModel.addToCart(id, price);
        binding.detailContent.tCount.setText(String.valueOf(getProductCount(id)));
        updateCartBanner();
    }

    @Override
    public void removeFromCart(Long id, float price){
        mViewModel.removeFromCart(id, price);
        updateCartBanner();
    }

    public void updateCartBanner() {
        if(mViewModel.getCartCount()<1)
            binding.cartView.setVisibility(View.GONE);
        else
            binding.cartView.setVisibility(View.VISIBLE);

        binding.tTotalPrice.setText(String.valueOf(mViewModel.getTotal()));
        binding.tCartCount.setText(String.valueOf(mViewModel.getCartCount()));
    }

    @Override
    public int getProductCount(Long id) {
        return mViewModel.getProductCount(id);
    }
}