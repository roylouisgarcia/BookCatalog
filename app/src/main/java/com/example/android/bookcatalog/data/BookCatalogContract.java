package com.example.android.bookcatalog.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class BookCatalogContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.bookcatalog";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";


    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_TITLE = "title";
        public static final String COLUMN_BOOK_SUPPLIER = "supplier";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_TYPE = "type";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "phone";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";


        public static final int PHONE_TYPE_HOME = 0;
        public static final int PHONE_TYPE_MOBILE = 1;
        public static final int PHONE_TYPE_WORK = 2;

    }
}
