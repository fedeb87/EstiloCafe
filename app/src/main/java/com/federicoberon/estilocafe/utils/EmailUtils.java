package com.federicoberon.estilocafe.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.federicoberon.estilocafe.model.ProductEntity;

import java.util.HashMap;
import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class EmailUtils {

    public static void sendMessage(String body, String subject) {
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.m = new Mail("mipajarito2021@gmail.com", "iaknjfoybdyjthgw");
        email.m.set_from("mipajarito2021@gmail.com");
        email.m.setBody(body);
        email.m.set_to(new String[] {"estilocafe.pilar@gmail.com"});
        email.m.set_subject(subject);
        email.execute();
    }

    static class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Mail m;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    Log.w("MIO", "Email sent");
                } else {
                    Log.w("MIO", "Error sending email");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e("MIO", "Authentication failed", e);
                return false;
            } catch (MessagingException e) {
                Log.e("MIO", "Email failed", e);
                return false;
            } catch (Exception e) {
                Log.e("MIO", "Unexpected error occured", e);
                return false;
            }
        }
    }

    public static String productsToBody(List<ProductEntity> products, HashMap<Long, Integer> cart, float total){

        String text=
                "<html><font size='5' face='Courier New' >" +
                        "<table border='1' align='center'>"
                        + "<tr align='center'>"
                        + "<td><b>Product Name <b></td>"
                        + "<td><b>Unit price<b></td>"
                        + "<td><b>Count<b></td>"
                        + "</tr>";

        for(ProductEntity product : products) {
            text = text + "<tr align='center'>" + "<td>" + product.getName() + "</td>"
                    + "<td>" + product.getPrice() + "</td>" + "<td>" + cart.get(product.getId()) + "</td>" + "</tr>";
        }

        text += "\n TOTAL: " + total;
        text += "</font></html>";
        return text;
    }
}
