package com.apps.dbm.traveldbm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MainActivity extends AppCompatActivity {

    private static final String LOG = MainActivity.class.getSimpleName();

    private FloatingActionButton fabButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabButton = (FloatingActionButton) findViewById(R.id.search_fab_button);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

    }


    /*public void showDialogForTesting(){

        LayoutInflater factory = getLayoutInflater();

        //View content =  factory.inflate(R.layout.dialog_test, null);
        //ImageView dialogImage = (ImageView) content.findViewById(R.id.dialog_image);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(content)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(new ColorDrawable(Color.WHITE));

        Glide.with(dialogImage.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load("http://md1.libe.com/photo/1095988-magazine-zidane.jpg")
                .into(dialogImage);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.show_favorites:
                Toast.makeText(this,"Testing menu item",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
