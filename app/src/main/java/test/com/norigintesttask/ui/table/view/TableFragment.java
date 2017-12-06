package test.com.norigintesttask.ui.table.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import test.com.norigintesttask.R;
import test.com.norigintesttask.model.Channel;
import test.com.norigintesttask.model.Schedule;
import test.com.norigintesttask.model.Table;
import test.com.norigintesttask.ui.common.view.BaseViewFragment;
import test.com.norigintesttask.ui.table.presenter.TablePresenter;

public class TableFragment extends BaseViewFragment<TablePresenter> implements TableView {

    @Inject
    TableDateAdapter adapter;

    EPG epg;

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        epg = view.findViewById(R.id.epg);
        epg.setEPGClickListener(new EPGClickListener() {
            @Override
            public void onChannelClicked(int channelPosition, Channel channel) {

            }

            @Override
            public void onEventClicked(int channelPosition, int programPosition, Schedule event) {

            }

            @Override
            public void onResetButtonClicked() {
                epg.recalculateAndRedraw(true);
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(activityContext,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setTable(Table table) {
        epg.setEPGData(table);
        epg.recalculateAndRedraw(false);
    }

    @Override
    public void setDates(List<Date> dates) {
        adapter.setDates(dates);
    }
}
