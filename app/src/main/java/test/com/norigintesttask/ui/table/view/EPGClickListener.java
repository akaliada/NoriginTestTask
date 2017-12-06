package test.com.norigintesttask.ui.table.view;

import test.com.norigintesttask.model.Channel;
import test.com.norigintesttask.model.Schedule;

public interface EPGClickListener {

    void onChannelClicked(int channelPosition, Channel channel);

    void onEventClicked(int channelPosition, int programPosition, Schedule event);

    void onResetButtonClicked();

}
