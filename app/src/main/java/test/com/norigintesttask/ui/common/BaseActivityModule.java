package test.com.norigintesttask.ui.common;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import test.com.norigintesttask.inject.PerActivity;

@Module
public abstract class BaseActivityModule {

    @Binds
    @PerActivity
    abstract Context activityContext(Activity activity);

    @Provides
    @PerActivity
    static FragmentManager activityFragmentManager(Activity activity) {
        return activity.getFragmentManager();
    }
}
