package com.example.android.bookstore.Networking;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.bookstore.Book_Adapter.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class Api {
    public static final String LOG_TAG = Api.class.getSimpleName();

    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of  Books
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of  Books
        return books;

    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static List<Book> extractFeatureFromJson(String booksjson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksjson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksjson);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray booklistarray = baseJsonResponse.getJSONArray("items");

            // For each earthquake in the BooksArray, create an Book object
            for (int i = 0; i < booklistarray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentEarthquake = booklistarray.getJSONObject(i);

                // For a given Book, extract the JSONObject associated with the
                // key called "saleinfo", which represents a list of all saleinfo
                // for Books.

                JSONObject saleinfo = currentEarthquake.getJSONObject("saleInfo");
                String priceString = "";
                String buyLink = "";
                String saleability = saleinfo.getString("saleability");

                // check saleability if the book for free print free if for sale print the price 
                //if not for sale print not for sale 


                switch (saleability) {

                    case "FOR_SALE": {
                        buyLink = (String) saleinfo.get("buyLink");
                        JSONObject listPrice = saleinfo.getJSONObject("listPrice");
                        priceString = listPrice.getString("amount");
                        priceString = priceString + " EGP";
                        break;
                    }

                    case "NOT_FOR_SALE": {

                        priceString = "Not for Sale";
                        continue;
                    }

                    case "FREE": {
                        buyLink = (String) saleinfo.get("buyLink");
                        priceString = "FREE";
                        break;


                    }
                }
              /* try {

                   JSONObject listPrice = saleinfo.getJSONObject("listPrice");
                   priceString = listPrice.getString("amount");
                   priceString="price: "+priceString+" EGP";


               } catch (Exception e)
               {
                   priceString="Not for Sale";



               }*/
                JSONObject volumeInfo = currentEarthquake.getJSONObject("volumeInfo");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");


                // Extract the value for the key called "title"
                String Name = volumeInfo.getString("title");

                // Extract the value for the key called "thimbinal"
                String image = imageLinks.getString("thumbnail");


                // double amount=Double.parseDouble(priceString);


                // Create a new Book obj object with the Name, imageurl, price,buylik


                Book book = new Book(Name, image, priceString, buyLink);
                books.add(book);


            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.


            Log.e("QueryUtils", "Problem parsing the Books JSON results", e);

        }


        // Return the list of books
        return books;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


}
