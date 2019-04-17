package com.example.android.bookstore.Networking;

import android.content.Context;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.bookstore.Book_Adapter.Book;

import java.util.List;

public class loader extends AsyncTaskLoader<List<Book>>

{
    private String mUrl;
    private static final String LOG_TAG = loader.class.getName();



    public loader(Context context, String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"test:Earthquake Activity onstartloading() called");



        forceLoad();

   }



    @Nullable
    @Override
    public  List<Book> loadInBackground() {

        if (mUrl == null) {
            return null;
        }


        // Perform the network request, parse the response, and extract a list of books.
        List<Book> bookList = Api.fetchBookData(mUrl);
        return bookList;


    }
}
