package test.com.norigintesttask.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class Channel {

    @JsonField
    public String id;

    @JsonField
    public String title;

    @JsonField
    public Images images;

    @JsonField
    public List<Schedule> schedules;
}
