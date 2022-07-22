package com.federicoberon.estilocafe.ui.login.completeProfile;

import static com.federicoberon.estilocafe.utils.Constants.ENABLE_LOGS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentCompleteProfileBinding;
import com.federicoberon.estilocafe.model.UserEntity;
import com.federicoberon.estilocafe.ui.home.HomeActivity;
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

public class CompleteProfileFragment extends Fragment {
    private static final String LOG_TAG = "<PROFILEFRAGMENT>";
    private CharSequence[] OPTIONS;
    private AlertDialog.Builder mBuilderSelector;
    private AlertDialog mDialog;
    private FragmentCompleteProfileBinding binding;
    private ActivityResultLauncher<Intent> takePictureResultActivity;
    private ActivityResultLauncher<Intent> takePhotoResultActivity;
    private ActivityResultLauncher<String[]> storagePermissionsResultActivity;

    @Inject
    protected SharedPreferences sharedPref;

    @Inject
    CompleteProfileViewModel mViewModel;

    public CompleteProfileFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created login component
        ((EstiloCafeApplication) requireActivity().getApplication()).appComponent.inject(this);

        takePhotoResultActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
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
                try {
                    assert result.getData() != null;
                    mViewModel.setPhotoFile(StorageUtil.from(requireActivity(), result.getData().getData()));
                    binding.circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(
                            mViewModel.getPhotoFilePath()));
                    mViewModel.setChangeProfileImage(true);
                } catch(Exception e) {
                    DialogErrorHelper.getErrorDialog(requireContext()
                            , getResources().getString(R.string.error_generic_title)
                            , getResources().getString(R.string.error_select_image)).show();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCompleteProfileBinding.inflate(inflater, container, false);
        
        OPTIONS = new CharSequence[] {getResources().getString(
                R.string.opt_from_gallery), getResources().getString(R.string.opt_from_camera)};
        mBuilderSelector = new AlertDialog.Builder(requireContext());
        mBuilderSelector.setTitle(getResources().getString(R.string.select_option));

        mDialog = new SpotsDialog.Builder()
                .setContext(requireContext())
                .setMessage(getResources().getString(R.string.wait_msg))
                .setCancelable(false).build();

        binding.circleImageProfile.setOnClickListener(view -> selectOptionImage());
        binding.btnRegister.setOnClickListener(view -> register());

        return binding.getRoot();
    }

    private void selectOptionImage() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            storagePermissionsResultActivity.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            /*ActivityCompat.requestPermissions(requireActivity()
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , StorageUtil.PERMISSION_REQUEST_STORAGE);*/
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
        //startActivityForResult(galleryIntent, requestCode);
        takePictureResultActivity.launch(galleryIntent);
    }

    private void register() {

        mDialog.show();

        String username = Objects.requireNonNull(binding.textInputName.getText()).toString();
        String email = mViewModel.getEmail();

        if (!username.isEmpty()) {

            if (mViewModel.isChangeProfileImage()) {
                mViewModel.savePhotoFileRemote(requireActivity(), mViewModel.getUid())
                        .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mViewModel.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                            final String url = uri.toString();
                            updateUser(username, email, url);
                        });
                    } else {
                        mDialog.dismiss();
                        DialogErrorHelper.getErrorDialog(requireContext(), getResources().getString(
                                R.string.error_generic_title), getResources().getString(
                                R.string.error_save_file)).show();
                    }
                });
            }else {
                updateUser(username, email, null);
            }
        }
        else {
            Toast.makeText(requireContext(), getResources().getString(
                    R.string.error_complete_fields), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(final String username, final String email, final String url) {

        boolean fallo = false;

        if (!username.trim().isEmpty() && ValidatorUtil.isValidText(username)){
            binding.textInputLayoutName.setErrorEnabled(false);
        } else {
            fallo = true;
            binding.textInputLayoutName.setErrorEnabled(true);
            binding.textInputLayoutName.setError(getResources().getString(
                    R.string.error_username));
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
            UserEntity user = new UserEntity();
            user.setNickname(username);
            user.setIdFirebase(mViewModel.getUid());
            user.setEmail(email);
            user.setTimeStamp(new Date().getTime());
            user.setImage_profile(url);

            mViewModel.updateRemoteUser(user).addOnCompleteListener(task -> {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    mViewModel.saveUserLocally(requireContext(), user, mViewModel.getPhotoFilePath());

                    Intent intent = new Intent(requireContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    if(sharedPref.getBoolean(ENABLE_LOGS, false))
                        Log.e(LOG_TAG, task.getException().getMessage(), task.getException());
                    DialogErrorHelper.getErrorDialog(requireContext(),
                            getResources().getString(R.string.error_generic_title),
                            getResources().getString(R.string.error_save_user)).show();
                }
            });
        }
    }
}