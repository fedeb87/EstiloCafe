package com.federicoberon.estilocafe.utils;

import android.os.AsyncTask;
import android.util.Log;
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
}
