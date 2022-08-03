package com.federicoberon.estilocafe.ui.login;

import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.FragmentLoginBinding;
import com.federicoberon.estilocafe.ui.login.completeProfile.CompleteProfileViewModel;
import com.federicoberon.estilocafe.utils.DialogErrorHelper;
import com.federicoberon.estilocafe.utils.NetworkChangeReceiver;
import com.federicoberon.estilocafe.utils.ValidatorUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import javax.inject.Inject;

import dmax.dialog.SpotsDialog;

public class LoginFragment extends Fragment {
    private static final String LOG_TAG = "LoginFragment";
    private AlertDialog mDialog;
    private NetworkChangeReceiver networkChangeReceiver;
    private FragmentLoginBinding binding;
    private ActivityResultLauncher<Intent> signInGoogleResultLauncher;

    @Inject
    CompleteProfileViewModel mViewModel;

    public LoginFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created login component
        ((EstiloCafeApplication)requireActivity().getApplication()).appComponent.inject(this);

        signInGoogleResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Log.e("ERROR", "Google sign in failed", e);
                        }

                    }
                });
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

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        checkInternetConnectivity();
        initListeners();

        return binding.getRoot();
    }


    private void checkInternetConnectivity() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver(binding.linearLayoutNoInternet);
        requireActivity().registerReceiver(networkChangeReceiver, filter);
    }

    private void initListeners() {
        binding.btnLoginGoogle.setOnClickListener(view -> signInGoogle());

        binding.btnLogin.setOnClickListener(view -> signIn());
        binding.textViewRegister.setOnClickListener(view ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_register));

        binding.textViewPassRecover.setOnClickListener(view -> {
            if (ValidatorUtil.isValidEmail(Objects.requireNonNull(
                    binding.textInputEmail.getText()).toString())) {
                sendPassResetEmail(binding.textInputEmail.getText().toString());

            } else Toast.makeText(requireContext(),
                    getResources().getString(
                    R.string.error_complete_email)
                    , Toast.LENGTH_LONG).show();
        });
    }

    private void sendPassResetEmail(String email) {
        mViewModel.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showNotificationSnackBar();
                    if(task.isSuccessful())
                        Log.w(LOG_TAG, getString(R.string.email_send_msg));
                });
    }

    private void showNotificationSnackBar(){
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.reset_email_sended,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.ok_string, v -> snackbar.dismiss());
        snackbar.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mDialog.show();
        mViewModel.googleLogin(acct).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful())
                checkUserExist(mViewModel.getUid());

            else {
                mDialog.dismiss();
                mViewModel.logout();
                DialogErrorHelper.getErrorDialog(requireContext(),
                        getResources().getString(R.string.error_generic_title),
                        getResources().getString(R.string.error_login_google));
                Log.e("ERROR", "signInWithCredential:failure", task.getException());
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkUserExist(final String id) {
        mViewModel.getUser(id).addOnSuccessListener(documentSnapshot -> {
            mDialog.dismiss();
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains(NICKNAME_KEY)) {
                    if (documentSnapshot.get(NICKNAME_KEY) != null
                            && !Objects.requireNonNull(documentSnapshot.get(NICKNAME_KEY)).equals("")) {
                        mViewModel.saveUserInfo(requireContext(), mViewModel.getUid());

                        ((LoginActivity)requireActivity()).goHome();
                    } else
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_completeProfile);

                }else
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_completeProfile);

            } else {
                mViewModel.createRemoteUser(
                        mViewModel.createUser(id, mViewModel.getEmail())).addOnCompleteListener(task -> {
                    mDialog.dismiss();
                    if (task.isSuccessful()){
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_completeProfile);
                    }

                    else
                        DialogErrorHelper.getErrorDialog(requireContext(),
                                getResources().getString(R.string.error_generic_title),
                                getResources().getString(R.string.error_save_user)).show();

                });
            }
        });
    }

    public void signInGoogle() {
        Intent signInIntent = mViewModel.requestGoogleSignIn(requireContext()).getSignInIntent();
        signInGoogleResultLauncher.launch(signInIntent);
    }

    public void signIn() {
        String email = Objects.requireNonNull(binding.textInputEmail.getText()).toString();
        String password = Objects.requireNonNull(binding.textInputPassword.getText()).toString();
        boolean wrongInput = false;

        if (!email.trim().isEmpty() && ValidatorUtil.isValidEmail(email)){
            binding.textInputLayoutEmail.setErrorEnabled(false);
        } else {
            wrongInput = true;
            binding.textInputLayoutEmail.setErrorEnabled(true);
            binding.textInputLayoutEmail.setError(getResources().getString(
                    R.string.error_wrong_email));
        }
        if(!password.isEmpty()){
            if (password.length() < 6) {
                wrongInput = true;
                binding.textInputLayoutPassword.setErrorEnabled(true);
                binding.textInputLayoutPassword.setError(getResources().getString(
                        R.string.error_pass_lenght));
            }else{
                binding.textInputLayoutPassword.setErrorEnabled(false);
            }
        }else{
            wrongInput = true;
            binding.textInputLayoutPassword.setErrorEnabled(true);
            binding.textInputLayoutPassword.setError(getResources().getString(
                    R.string.error_wrong_email));
        }

        if (!wrongInput) {
            mDialog.show();
            mViewModel.login(email, password).addOnCompleteListener(task -> {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    mViewModel.saveUserInfo(requireContext(), mViewModel.getUid());
                    ((LoginActivity)requireActivity()).checkGoHome(mViewModel.getUid(), binding.getRoot());
                } else {
                    Toast.makeText(requireContext(), getResources().getString(
                            R.string.error_username), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (networkChangeReceiver!=null)
            requireActivity().unregisterReceiver(networkChangeReceiver);

    }
}