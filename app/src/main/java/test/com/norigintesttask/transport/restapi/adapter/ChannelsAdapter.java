package test.com.norigintesttask.transport.restapi.adapter;

import io.reactivex.Single;
import retrofit2.http.GET;
import test.com.norigintesttask.model.Table;

public interface ChannelsAdapter {

    String CHANNELS_PATH = "/epg";

    @GET(CHANNELS_PATH)
    Single<Table> loadChannelsTable();
}
