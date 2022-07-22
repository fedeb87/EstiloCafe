package com.federicoberon.estilocafe.utils;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidatorUtil {

    public static boolean isValidText(String str) {
        return (str != null
                && str.length() > 3
                && str.trim().length()<51 && (!str.equals(""))
                && (str.matches("^([a-zA-ZáéíóúÁÉÍÓÚ ])+([\\wáéíóúÁÉÍÓÚ ])*"/*"^[a-zA-Z ]*$"*/)));
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static List<Integer> getRandomQuestionsList(int start, int size){
        List<Integer> result = new ArrayList<>();
        for(int i=0 ; i <size ; i++)
            result.add(start+i);
        Collections.shuffle(result);

        return result;
    }


}
