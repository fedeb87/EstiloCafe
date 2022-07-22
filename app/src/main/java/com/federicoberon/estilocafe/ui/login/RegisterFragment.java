package com.federicoberon.estilocafe.ui.login;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentRegisterBinding;
import com.federicoberon.estilocafe.ui.about.TermsDialogFragment;
import com.federicoberon.estilocafe.ui.login.completeProfile.CompleteProfileViewModel;
import com.federicoberon.estilocafe.utils.DialogErrorHelper;
import com.federicoberon.estilocafe.utils.StorageUtil;
import com.federicoberon.estilocafe.utils.ValidatorUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import javax.inject.Inject;

import dmax.dialog.SpotsDialog;

public class RegisterFragment extends Fragment {

    private CharSequence[] OPTIONS;
    private FragmentRegisterBinding binding;
    private AlertDialog mDialog;
    private AlertDialog.Builder mBuilderSelector;
    private ActivityResultLauncher<Intent> takePictureResultActivity;
    private ActivityResultLauncher<Intent> takePhotoResultActivity;
    private ActivityResultLauncher<String[]> storagePermissionsResultActivity;

    @Inject
    CompleteProfileViewModel mViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created login component
        ((EstiloCafeApplication) requireActivity().getApplication()).appComponent.inject(this);

        takePhotoResultActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes

                        mViewModel.setPhotoFile(new File(mViewModel.getmAbsolutePhotoPath()));
                        Picasso.with(requireContext()).load("file:"
                                        + mViewModel.getPhotoFilePath())
                                .into(binding.circleImageProfile);
                        mViewModel.setChangeProfileImage(true);
                    }
                });

        takePictureResultActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        try {
                            assert result.getData() != null;
                            mViewModel.setPhotoFile(StorageUtil.from(requireActivity(), result.getData().getData()));
                            binding.circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(
                                    mViewModel.getPhotoFilePath()));
                            mViewModel.setChangeProfileImage(true);
                        } catch (Exception e) {
                            DialogErrorHelper.getErrorDialog(requireContext()
                                    , getResources().getString(R.string.error_generic_title)
                                    , getResources().getString(R.string.error_select_image)).show();
                        }
                    }
                });

        storagePermissionsResultActivity = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean isGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if(!granted) {
                            isGranted = false;
                            break;
                        }
                    }
                    if (isGranted) {
                        mBuilderSelector.setItems(OPTIONS, (dialogInterface, i) -> {
                            if (i == 0) {
                                openGallery();
                            } else if (i == 1) {
                                takePhoto();
                            }
                        });
                        mBuilderSelector.show();
                    } else {
                        // permission denied!
                        DialogErrorHelper.getErrorDialog(requireContext(), getResources().getString(
                                R.string.error_storage_permission_title), getResources().getString(
                                R.string.error_storage_permissions)).show();
                    }

                });
    }

    public RegisterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new SpotsDialog.Builder()
                .setContext(requireContext())
                .setMessage(getResources().getString(
                        R.string.wait_msg))
                .setCancelable(false).build();

        OPTIONS = new CharSequence[] {getResources().getString(R.string.opt_from_gallery),
                getResources().getString(R.string.opt_from_camera)};

        mBuilderSelector = new AlertDialog.Builder(requireContext());
        mBuilderSelector.setTitle(getResources().getString(
                R.string.select_option));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.circleImageProfile.setOnClickListener(v -> selectOptionImage());
        binding.btnRegister.setOnClickListener(v -> register());
        binding.circleImageBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
        binding.textViewTerms.setOnClickListener(v -> showTermsOfService());
        binding.textViewPolicy.setOnClickListener(v -> showPolicies());

    }

    private void showTermsOfService() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        TermsDialogFragment termsDialogFragment = TermsDialogFragment.newInstance(0);
        termsDialogFragment.show(fm, "fragment_terms_dialog");
    }

    private void showPolicies() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        TermsDialogFragment termsDialogFragment = TermsDialogFragment.newInstance(1);
        termsDialogFragment.show(fm, "fragment_terms_dialog");
    }

    private void selectOptionImage() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            storagePermissionsResultActivity.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        } else {
            mBuilderSelector.setItems(OPTIONS, (dialogInterface, i) -> {
                if (i == 0) {
                    openGallery();
                }
                else if (i == 1){
                    takePhoto();
                }
            });
            mBuilderSelector.show();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            } catch(Exception e) {
                DialogErrorHelper.getErrorDialog(requireContext(), getResources().getString(
                        R.string.error_generic_title), getResources().getString(
                        R.string.error_create_file)).show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(requireContext()
                        , "com.federicoberon.mesuena.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePhotoResultActivity.launch(takePictureIntent);
            }
        }
    }

    private File createPhotoFile() throws IOException {
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        mViewModel.setmAbsolutePhotoPath(photoFile.getAbsolutePath());

        return photoFile;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        takePictureResultActivity.launch(galleryIntent);
    }

    private void register() {
        String username = Objects.requireNonNull(binding.textInputName.getText()).toString();
        String email = Objects.requireNonNull(binding.textInputEmail.getText()).toString();
        String password  = Objects.requireNonNull(binding.textInputPassword.getText()).toString();
        String confirmPassword = Objects.requireNonNull(binding.textInputConfirmPassword.getText()).toString();
        boolean fallo = false;

        if (!username.trim().isEmpty() && ValidatorUtil.isValidText(username)){
            binding.textInputLayoutName.setErrorEnabled(false);
        } else {
            fallo = true;
            binding.textInputLayoutName.setErrorEnabled(true);
            binding.textInputLayoutName.setError(getResources().getString(R.string.error_username));
        }

        if (!email.trim().isEmpty() && ValidatorUtil.isValidEmail(email)){
            binding.textInputLayoutEmail.setErrorEnabled(false);
        } else {
            fallo = true;
            binding.textInputLayoutEmail.setErrorEnabled(true);
            binding.textInputLayoutEmail.setError(getResources().getString(R.string.error_wrong_email));
        }

        if(!password.isEmpty() && !confirmPassword.isEmpty()){
            if (password.length() >= 6) {
                if (password.equals(confirmPassword)) {
                    binding.textInputLayoutPass.setErrorEnabled(false);
                    binding.textInputLayoutConfirmPass.setErrorEnabled(false);
                } else {
                    fallo = true;
                    binding.textInputLayoutPass.setErrorEnabled(true);
                    binding.textInputLayoutConfirmPass.setErrorEnabled(true);
                    binding.textInputLayoutPass.setError(getResources().getString(
                            R.string.error_pass_no_match));
                }
            }else{
                fallo = true;
                binding.textInputLayoutPass.setErrorEnabled(true);
                binding.textInputLayoutConfirmPass.setErrorEnabled(true);
                binding.textInputLayoutPass.setError(getResources().getString(
                        R.string.error_pass_lenght));
            }
        }else{
            fallo = true;
            binding.textInputLayoutPass.setErrorEnabled(true);
            binding.textInputLayoutConfirmPass.setErrorEnabled(true);
            binding.textInputLayoutPass.setError(getResources().getString(R.string.error_no_pass));
        }

        if (!fallo) {
            if (!binding.checkboxTerms.isChecked()) {
                DialogErrorHelper.getErrorDialog(requireContext(),
                        getResources().getString(R.string.terms_of_services),
                        getResources().getString(R.string.error_must_accept_terms)).show();
                fallo = true;
            }

            if (!binding.checkboxPolicy.isChecked()) {
                DialogErrorHelper.getErrorDialog(requireContext(),
                        getResources().getString(R.string.privacy_policy),
                        getResources().getString(R.string.error_must_accept_policy)).show();
                fallo = true;
            }
        }

        if (!fallo) {
            mDialog.show();
            mViewModel.checkUserExists(email).addOnCompleteListener(
                    task -> {
                if (Objects.requireNonNull(task.getResult().getSignInMethods()).size() == 0){
                    // email not existed
                    binding.textInputLayoutEmail.setErrorEnabled(false);
                    mViewModel.createRemoteUser(requireContext(), username, email, password);

                }else {
                    binding.textInputLayoutEmail.setErrorEnabled(true);
                    binding.textInputLayoutEmail.setError(getResources().getString(
                            R.string.error_email_registered));
                }
                mDialog.dismiss();
            }).addOnFailureListener(e -> {
                mDialog.dismiss();
                e.printStackTrace();
            });

        }
    }
}