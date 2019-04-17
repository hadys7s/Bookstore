package com.example.android.bookstore.Book_Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bookstore.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class custom_adapter extends ArrayAdapter<Book> {
    private static final String LOG_TAG = custom_adapter.class.getSimpleName();



    public custom_adapter(Activity context, ArrayList<Book> Books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, Books);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_books, parent, false);
        }
        final Book currentBook = getItem(position);


        TextView titleBookTextView = (TextView) listItemView.findViewById(R.id.BName);
        titleBookTextView.setText(currentBook.getName());

        TextView titlepriceTextView = (TextView) listItemView.findViewById(R.id.Bprice);
        titlepriceTextView.setText(currentBook.getPrice());

        ImageView coverImageView = (ImageView) listItemView.findViewById(R.id.BImage);
        Picasso.with(getContext()).load(currentBook.getImages()).into(coverImageView);






        return listItemView;
    }
   /* private String formatPrice(double price) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.00");
        return magnitudeFormat.format(price);
    }*/
}








