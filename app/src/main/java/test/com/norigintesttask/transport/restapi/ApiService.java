package test.com.norigintesttask.transport.restapi;

import io.reactivex.Single;
import retrofit2.Retrofit;
import test.com.norigintesttask.model.Table;
import test.com.norigintesttask.transport.restapi.adapter.ChannelsAdapter;

public class ApiService implements ChannelsAdapter {

    private final ChannelsAdapter channelsAdapter;


    public ApiService(Retrofit retrofit) {
        channelsAdapter = retrofit.create(ChannelsAdapter.class);
    }

    @Override
    public Single<Table> loadChannelsTable() {
        return channelsAdapter.loadChannelsTable();
    }
}
