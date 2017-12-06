package test.com.norigintesttask.ui.table.view;

import java.util.Date;
import java.util.List;

import test.com.norigintesttask.model.Table;
import test.com.norigintesttask.ui.common.view.MVPView;

public interface TableView extends MVPView {

    void setTable(Table table);

    void setDates(List<Date> dates);

}
