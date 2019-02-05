package com.apps.dbm.traveldbm.datehelper;

import android.app.DatePickerDialog;
import android.content.Context;

public class MyDatePickerDialog extends DatePickerDialog {

    private String titleDialog;

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth, String title) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        titleDialog = title;
    }

    //Set title of DatePickerDialog
    public void setTitle(CharSequence title) {
        super.setTitle(titleDialog);
    }
}