package com.example.android.bookstore.Book_Adapter;

import android.provider.MediaStore;

public class Book {

    private String Name;
    private String images;
    private String Price;
    private String Url;





    public Book(String name,String img,String price,String url)
    {
        Name=name;

        images=img;
        Price=price;
        Url=url;



    }
    public Book()
    {}






    public String getName() {
        return Name;
    }

    public String getImages() {
        return images;
    }

    public String getUrl() {
        return Url;
    }

    public String getPrice() { return Price;}
}
