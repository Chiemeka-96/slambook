package com.gmonetix.slambook.helper;

import android.widget.EditText;

import java.util.Calendar;

/**
 * @author Gmonetix
 */

public class Validator {

    public static boolean validateName(EditText editText) {
        boolean valid = true;
        String name = editText.getText().toString().trim();
        if (name.isEmpty() || name.length() < 4 || name.length()>50) {
            editText.setError("enter valid name");
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    public static boolean validateEmail(EditText editText) {
        boolean valid = true;
        String email = editText.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length()<8 || email.length() >50) {
            editText.setError("enter valid email");
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    public static boolean validatePhone(EditText editText) {
        boolean valid = true;
        String phone = editText.getText().toString().trim();
        if (phone.isEmpty() || phone.length() < 8 || phone.length() > 15) {
            editText.setError("enter valid phone number");
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    public static boolean validateDOB(String dob) {
        boolean valid = true;
        String[] date = dob.split("/");
        int year = Integer.parseInt(date[2]);
        int YEAR = Calendar.getInstance().get(Calendar.YEAR) - 10;
        if (year > YEAR) {
            valid =  false;
        }
        return valid;
    }

}
