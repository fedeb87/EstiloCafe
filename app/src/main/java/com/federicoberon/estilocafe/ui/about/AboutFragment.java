package com.federicoberon.estilocafe.ui.about;

import static com.federicoberon.estilocafe.utils.Constants.TYPE_OF_CONTENT;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentAboutBinding;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((EstiloCafeApplication) requireActivity().getApplicationContext())
                .appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        requireContext().getTheme().applyStyle(R.style.AppThemeDark, true);
        loadAbout();

        return root;
    }

    private void loadAbout() {

        String url = "https://www.paypal.com/donate/?hosted_button_id=XAKHYHY3HUFQG";
        Intent donateIntent = new Intent(Intent.ACTION_VIEW);
        donateIntent.setData(Uri.parse(url));

        AboutBuilder builder = AboutBuilder.with(requireActivity())
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setLinksAnimated(true)
                .setDividerDashGap(12)
                .setName("Federico Beron")
                .setSubTitle("Mobile Developer")
                .setBrief(R.string.brew_personal_desc)
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setActionsColumnsCount(2)
                .addFeedbackAction("soporte.alarme@gmail.com")
                .addPrivacyPolicyAction(v -> {

                    Bundle args = new Bundle();
                    args.putLong(TYPE_OF_CONTENT, 1L);
                    // TODO: 22/07/2022 hacer los navigation, ver bien como tener uno principal y varios secundarios
                    /*Navigation.findNavController(
                            binding.getRoot()).navigate(R.id.action_aboutFragment_to_termsDialogFragment, args);*/
                })
                .addDonateAction(donateIntent)
                .setWrapScrollView(true)
                .setShowAsCard(true);

        AboutView view = builder.build();
        binding.about.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requireContext().getTheme().applyStyle(R.style.Theme_EstiloCafe, true);

        binding = null;
    }
}