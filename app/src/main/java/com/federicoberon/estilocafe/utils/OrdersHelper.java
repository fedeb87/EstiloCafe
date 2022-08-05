package com.federicoberon.estilocafe.utils;

import com.federicoberon.estilocafe.model.ProductEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrdersHelper {

    public static String productListToString(ArrayList<ProductEntity> products){
        Gson gson = new Gson();
        return gson.toJson(products);
    }

    public static String carritoToString(HashMap<Long, Integer> carrito){
        Gson gson = new Gson();
        return gson.toJson(carrito);
    }

    public static ArrayList<ProductEntity> stringToProductList(String products){
        Gson gson = new Gson();
        return new ArrayList<>(Arrays.asList(gson.fromJson(products, ProductEntity[].class)));
    }

    public static HashMap<Long, Integer> stringToProductCount(String products){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Long, Integer>>(){}.getType();
        return new HashMap<Long, Integer>(gson.fromJson(products, type));
    }

    public static String dateToString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR);
    }
}
