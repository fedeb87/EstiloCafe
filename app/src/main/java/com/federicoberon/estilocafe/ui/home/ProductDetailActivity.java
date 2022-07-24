package com.federicoberon.estilocafe.ui.home;

import static com.federicoberon.estilocafe.utils.Constants.PRODUCT_KEY;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityProductDetailBinding;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = "<<<ProductDetailFragment>>>";
    private ActivityProductDetailBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SliderAdapter mSliderAdapter;

    @Inject
    SharedPreferences sharedPref;

    public ProductDetailActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String productId = getIntent().getStringExtra(PRODUCT_KEY);

        if(productId!=null){
            // TODO: 23/07/2022 recuperar los datos de la base de datos o de firebase
        }
        
        instanceSlider();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        mSliderItems.add(String.valueOf(R.drawable.card_image_1));
        mSliderItems.add(String.valueOf(R.drawable.card_image_2));


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
}