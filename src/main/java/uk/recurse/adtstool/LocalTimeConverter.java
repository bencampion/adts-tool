package uk.recurse.adtstool;

import com.beust.jcommander.IStringConverter;

import java.time.LocalTime;

public class LocalTimeConverter implements IStringConverter<LocalTime> {

    @Override
    public LocalTime convert(String value) {
        return LocalTime.parse(value);
    }
}
