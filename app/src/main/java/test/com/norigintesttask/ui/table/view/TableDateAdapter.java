package test.com.norigintesttask.ui.table.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import test.com.norigintesttask.R;
import test.com.norigintesttask.inject.PerFragment;
import test.com.norigintesttask.util.DateUtil;

@PerFragment
public class TableDateAdapter extends RecyclerView.Adapter<TableDateAdapter.DateViewHolder> {

    private Context context;
    private List<Date> dates;
    private int currentDatePosition = 2;

    class DateViewHolder extends RecyclerView.ViewHolder {
        TextView weekday, date;

        DateViewHolder(View view) {
            super(view);
            weekday = view.findViewById(R.id.weekday);
            date = view.findViewById(R.id.date);
        }
    }

    @Inject
    TableDateAdapter(Context context, List<Date> dates) {
        this.context = context;
        this.dates = dates;
    }

    void setDates(List<Date> dates) {
        this.dates = dates;
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_list_item, parent, false);

        return new DateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
        Date date = dates.get(position);
        holder.weekday.setText(DateUtil.getWeekdayNameShort(date));
        holder.date.setText(DateUtil.getDayMonth(date));
        if (position == currentDatePosition) {
            holder.weekday.setTextColor(context.getResources().getColor(R.color.epg_event_primary_text));
            holder.date.setTextColor(context.getResources().getColor(R.color.epg_event_primary_text));
        } else {
            holder.weekday.setTextColor(context.getResources().getColor(R.color.epg_event_secondary_text));
            holder.date.setTextColor(context.getResources().getColor(R.color.epg_event_secondary_text));
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
}
