package com.gmonetix.slambook.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    int yearX, monthX, dayX;
    private OnDateChosenListener onDateChosenListener;

    public interface OnDateChosenListener {
        void onDateChose(String date);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onDateChosenListener = (OnDateChosenListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        yearX = c.get(Calendar.YEAR)-10;
        monthX = c.get(Calendar.MONTH);
        dayX = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, yearX, monthX, dayX);
    }

    @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        this.yearX = year;
        this.monthX = month;
        this.dayX = day;
        c.set(yearX, monthX, dayX);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(c.getTime());
        onDateChosenListener.onDateChose(formattedDate);
    }

}