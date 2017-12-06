package test.com.norigintesttask.ui;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import test.com.norigintesttask.inject.PerActivity;
import test.com.norigintesttask.inject.PerFragment;
import test.com.norigintesttask.ui.table.view.TableFragment;
import test.com.norigintesttask.ui.table.view.TableFragmentModule;
import test.com.norigintesttask.ui.common.BaseActivityModule;

@Module(includes = BaseActivityModule.class)
public abstract class MainActivityModule {

    @PerFragment
    @ContributesAndroidInjector(modules = TableFragmentModule.class)
    abstract TableFragment mainFragmentInjector();

    @Binds
    @PerActivity
    abstract Activity activity(MainActivity mainActivity);

}
