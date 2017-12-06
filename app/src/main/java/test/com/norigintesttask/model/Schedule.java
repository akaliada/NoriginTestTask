package test.com.norigintesttask.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import test.com.norigintesttask.transport.converter.DateConverter;

@JsonObject
public class Schedule {

    @JsonField
    public String id;

    @JsonField
    public String title;

    @JsonField(typeConverter = DateConverter.class)
    public Date start;

    @JsonField(typeConverter = DateConverter.class)
    public Date end;

}
