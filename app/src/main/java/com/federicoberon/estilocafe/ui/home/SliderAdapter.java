package com.federicoberon.estilocafe.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.federicoberon.estilocafe.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private final Context context;
    private List<String> mImagesPath;

    public SliderAdapter(Context context, List<String> imagesPath) {
        this.context = context;
        mImagesPath = imagesPath;
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        String sliderItem = mImagesPath.get(position);
        if (sliderItem != null) {
            if (!sliderItem.isEmpty()) {
                // todo ver como lo voy a dejar, de donde voya a sacar las fotos
                Picasso.with(context).load(Integer.valueOf(sliderItem)).into(viewHolder.imageViewSlider);
            }
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mImagesPath.size();
    }

    class SliderAdapterVH extends ViewHolder {

        View itemView;
        ImageView imageViewSlider;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewSlider = itemView.findViewById(R.id.imageViewSlider);

            this.itemView = itemView;
        }
    }

}