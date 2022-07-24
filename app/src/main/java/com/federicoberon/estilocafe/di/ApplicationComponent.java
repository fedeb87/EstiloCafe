package com.federicoberon.estilocafe.di;

import android.app.Application;
import android.content.Context;

import com.federicoberon.estilocafe.di.module.ApplicationModule;
import com.federicoberon.estilocafe.di.module.DatabaseModule;
import com.federicoberon.estilocafe.ui.about.AboutFragment;
import com.federicoberon.estilocafe.ui.home.HomeFragment;
import com.federicoberon.estilocafe.ui.home.ProductDetailActivity;
import com.federicoberon.estilocafe.ui.home.SearchResultFragment;
import com.federicoberon.estilocafe.ui.login.LoginActivity;
import com.federicoberon.estilocafe.ui.login.LoginFragment;
import com.federicoberon.estilocafe.ui.login.RegisterFragment;
import com.federicoberon.estilocafe.ui.login.completeProfile.CompleteProfileFragment;
import com.federicoberon.estilocafe.ui.splash.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DatabaseModule.class
})
public interface ApplicationComponent {

    void inject (SplashActivity splashActivity);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    void inject(RegisterFragment registerFragment);

    void inject(AboutFragment aboutFragment);

    void inject(LoginActivity loginActivity);

    void inject(LoginFragment loginFragment);

    void inject(CompleteProfileFragment completeProfileFragment);

    void inject(HomeFragment homeFragment);

    void inject(SearchResultFragment searchResultFragment);

    void inject(ProductDetailActivity productDetailActivity);
}
