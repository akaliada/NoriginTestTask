package test.com.norigintesttask.ui.table.presenter;

import dagger.Binds;
import dagger.Module;
import test.com.norigintesttask.inject.PerFragment;

@Module
public abstract class TablePresenterModule {

    @Binds
    @PerFragment
    abstract TablePresenter tablePresenter(TablePresenterImpl tablePresenterImpl);

}