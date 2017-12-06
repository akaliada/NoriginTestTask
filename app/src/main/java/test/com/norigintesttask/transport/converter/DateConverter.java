package test.com.norigintesttask.transport.converter;

import com.bluelinelabs.logansquare.typeconverters.DateTypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateConverter extends DateTypeConverter {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZZ";


    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault());
    }
}
