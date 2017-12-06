package test.com.norigintesttask.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class Table {

    @JsonField
    public List<Channel> channels;

}
