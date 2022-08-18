package com.federicoberon.estilocafe.di.component;

import com.federicoberon.estilocafe.di.ApplicationComponent;
import com.federicoberon.estilocafe.di.TestDatabaseModule;
import com.federicoberon.estilocafe.di.module.ApplicationModule;
import com.federicoberon.estilocafe.ui.ViewProductTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        TestDatabaseModule.class
})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(ViewProductTest viewProductTest);
}