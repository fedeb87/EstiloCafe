package com.federicoberon.estilocafe.datasource;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectListConverter {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        String[] result = value.split(",");
        return new ArrayList<String>(Arrays.asList(result));
    }

    @TypeConverter
    public static String fromArrayList(List<String> list) {
        String result = list.get(0);
        for(int i = 1;i<list.size();i++)
            result.concat(",").concat(list.get(i));
        return result;
    }
}
