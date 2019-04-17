package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.Book_Adapter.Book;
import com.example.android.bookstore.Book_Adapter.custom_adapter;
import com.example.android.bookstore.Networking.loader;
import com.example.android.bookstore.Settings_splash.SettingsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    public static final String LOG_TAG = MainActivity.class.getName();

    private static final String GOOGLE_STORE_RES = "https://www.googleapis.com/books/v1/volumes?q=all&maxResults=40";

    private custom_adapter mAdapter;
    private static final int BOOK_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private String mUrlRequestGoogleBooks = "";
    private SearchView mSearchViewField;
    private View loadingIndicator;
    private String orderBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        loadingIndicator = findViewById(R.id.progress_bar);
        mSearchViewField = findViewById(R.id.search_view_field);
        mSearchViewField.setQueryHint("Enter a book title");


        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        ListView booksView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        if (activeNetwork != null && activeNetwork.isConnected()) {
            //  Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            Log.i(LOG_TAG, "test:initloader");


            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {

            loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);


        }
// Create a fake list of Book locations.
        mEmptyStateTextView = findViewById(R.id.empty_view);
        booksView.setEmptyView(mEmptyStateTextView);


        // Create a new adapter that takes an empty list of books as input
        mAdapter = new custom_adapter(this, new ArrayList<Book>());


        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        booksView.setAdapter(mAdapter);
        Button mSearchButton = findViewById(R.id.search_button);

        // Search field

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check connection status

                if (isConnected) {
                    // Update URL and restart loader to displaying new result of searching
                    updateQueryUrl(mSearchViewField.getQuery().toString());
                    restartLoader();
                    Log.i(LOG_TAG, "Search value: " + mSearchViewField.getQuery().toString());
                } else {
                    // Clear the adapter of previous book data
                    mAdapter.clear();
                    // Set mEmptyStateTextView visible
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    // ...and display message: "No internet connection."
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }

            }

        });
        booksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);


                // Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentBook != null;
                try {
                    Uri buyBookUri = Uri.parse(currentBook.getUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, buyBookUri);

                    startActivity(websiteIntent);

                    // Create a new intent to view buy the book URI
                } catch (Exception e) {

                    Toast.makeText(getBaseContext(), "Not available in your country",
                            Toast.LENGTH_SHORT).show();

                }


                // Send the intent to launch a new activity
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if the selected part in app was menu open menu activity
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //its arandom method that show different topics each time you open the app
    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    //when user hit space it will act like "+"
    private String updateQueryUrl(String searchValue) {
        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");

        }


        Log.v("value", orderBy);


        //here is the differnt topics
        if (searchValue.isEmpty()) {
            String[] Recommend = new String[7];
            Recommend[0] = "Android";
            Recommend[1] = "Ios";
            Recommend[2] = "ComputerScience";
            Recommend[3] = "English";
            Recommend[4] = "Algorithms";
            Recommend[5] = "Database";
            Recommend[6] = "Software";
            searchValue += getRandom(Recommend);
            Log.v("Random", searchValue);

        }

        switch (orderBy) {


            case "Free":


                StringBuilder sb = new StringBuilder();

                sb.append("https://www.googleapis.com/books/v1/volumes?q=")
                        .append(searchValue).append("&filter=free-ebooks&maxResults=40");

                mUrlRequestGoogleBooks = sb.toString();
                break;
            case "Paid":

                StringBuilder sb1 = new StringBuilder();
                sb1.append("https://www.googleapis.com/books/v1/volumes?q=").append(searchValue).append("&maxResults=40");
                mUrlRequestGoogleBooks = sb1.toString();
                break;
        }


        return mUrlRequestGoogleBooks;

    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "test:Book Activity oncreateloader() called");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        updateQueryUrl(mSearchViewField.getQuery().toString());


        return new loader(this, mUrlRequestGoogleBooks);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> earths) {
        Log.i(LOG_TAG, "test:Earthquake Activity onloadfinished() called");
        mEmptyStateTextView.setText(R.string.no_books);
        View indicator2 = findViewById(R.id.progress_bar);
        indicator2.setVisibility(View.GONE);


        mAdapter.clear();

        // If there is a valid list of  Book then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earths != null && !earths.isEmpty()) {
            mAdapter.addAll(earths);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG, "test:Earthquake Activity onloader reset() called");

        mAdapter.clear();


    }

    public void restartLoader() {
        mEmptyStateTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
    }
}
