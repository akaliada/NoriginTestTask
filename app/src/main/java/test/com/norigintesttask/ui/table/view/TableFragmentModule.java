package test.com.norigintesttask.ui.table.view;

import android.app.Fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import test.com.norigintesttask.inject.PerFragment;
import test.com.norigintesttask.ui.table.presenter.TablePresenterModule;

@Module(includes = {
        TablePresenterModule.class
})
public abstract class TableFragmentModule {

    @Binds
    @PerFragment
    abstract Fragment fragment(TableFragment tableFragment);

    @Binds
    @PerFragment
    abstract TableView tableView(TableFragment tableFragment);

    @Provides
    static List<Date> dates() {
        return new ArrayList<>();
    }

}
