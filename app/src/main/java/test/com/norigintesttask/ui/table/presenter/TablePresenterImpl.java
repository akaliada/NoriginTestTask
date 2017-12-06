package test.com.norigintesttask.ui.table.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import test.com.norigintesttask.inject.PerFragment;
import test.com.norigintesttask.model.Table;
import test.com.norigintesttask.transport.restapi.ApiService;
import test.com.norigintesttask.ui.table.view.TableView;
import test.com.norigintesttask.ui.common.presenter.BasePresenter;
import test.com.norigintesttask.util.DateUtil;

@PerFragment
public class TablePresenterImpl extends BasePresenter<TableView> implements TablePresenter {

    private Disposable disposable;

    @Inject
    ApiService apiService;

    @Inject
    TablePresenterImpl(TableView view) {
        super(view);
    }

    @Override
    public void onStart(@Nullable Bundle savedInstanceState) {
        super.onStart(savedInstanceState);
        loadChannels();
        updateDates();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void loadChannels() {
        disposable = apiService.loadChannelsTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Table>() {

                    @Override
                    public void onSuccess(Table table) {
                        view.setTable(table);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public void updateDates() {
        List<Date> dates = new ArrayList<>();
        long now = DateUtil.getCurrentTimeInMillis();
        dates.add(DateUtil.getDateFromDate(now, -2));
        dates.add(DateUtil.getDateFromDate(now, -1));
        dates.add(DateUtil.getDateFromDate(now, -0));
        dates.add(DateUtil.getDateFromDate(now, 1));
        dates.add(DateUtil.getDateFromDate(now, 2));
        view.setDates(dates);
    }
}
