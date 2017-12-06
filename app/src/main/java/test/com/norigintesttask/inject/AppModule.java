package test.com.norigintesttask.inject;

import dagger.Module;
import dagger.android.AndroidInjectionModule;
import dagger.android.ContributesAndroidInjector;
import test.com.norigintesttask.ui.MainActivity;
import test.com.norigintesttask.ui.MainActivityModule;

@Module(includes = AndroidInjectionModule.class)
public abstract class AppModule {

    @PerActivity
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity mainActivityInjector();

}
