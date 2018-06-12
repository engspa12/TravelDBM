package com.apps.dbm.traveldbm.datehelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DateListener mCallback;

    public interface DateListener{
        void setDate(String textDate,int dayValue, int monthValue, int yearValue);
    }

    public static DatePickerFragment newInstance(boolean isCheckIn) {
        DatePickerFragment f = new DatePickerFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("is_check_in", isCheckIn);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (DateListener) context;
        } catch(ClassCastException e){
            Log.e("DatePickerFragment",e.getMessage());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Bundle args = getArguments();
        boolean isCheckInDate = args.getBoolean("is_check_in", false);

        String titleDialog;
        if(isCheckInDate) {
            titleDialog = "Select Check-in Date";
        } else{
            titleDialog = "Select Check-out Date";
        }

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new MyDatePickerDialog(getActivity(), this, year, month, day,titleDialog);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(month < 9) {
            if(day < 10) {
                mCallback.setDate("0" + day + "-0" + (month + 1) + "-" + year,day,month+1,year);
            } else{
                mCallback.setDate(day + "-0" + (month + 1) + "-" + year,day,month+1,year);
            }
        } else{
            if(day < 10) {
                mCallback.setDate("0" + day + "-" + (month + 1) + "-" + year,day,month+1,year);
            } else{
                mCallback.setDate(day + "-" + (month + 1) + "-" + year,day,month+1,year);
            }
        }
    }


}
