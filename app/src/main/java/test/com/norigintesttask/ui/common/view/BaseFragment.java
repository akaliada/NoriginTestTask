package test.com.norigintesttask.ui.common.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public abstract class BaseFragment extends Fragment {

    @Inject
    protected Context activityContext;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Perform injection before M, L (API 22) and below because onAttach(Context)
            // is not yet available at L.
            AndroidInjection.inject(this);
        }
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Perform injection for M (API 23) due to deprecation of onAttach(Activity).
            AndroidInjection.inject(this);
        }
        super.onAttach(context);
    }
}
