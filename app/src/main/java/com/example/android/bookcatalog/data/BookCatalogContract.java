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
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        public static final String COLUMN_BOOK_SUPPLIER = "supplier";
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "phone";

        public static final String COLUMN_BOOK_TYPE = "type";

        public static final int BOOK_TYPE_HARDCOVER = 0;
        public static final int BOOK_TYPE_PAPERBACK = 1;
        public static final int BOOK_TYPE_ELECTRONIC = 2;

        /**
         * Returns whether or not the given supplier phone type {@link #BOOK_TYPE_HARDCOVER}, {@link #BOOK_TYPE_PAPERBACK}, or {@link #BOOK_TYPE_ELECTRONIC}
         */
        public static boolean isValidBookType(int bookType){
            if (bookType == BOOK_TYPE_HARDCOVER || bookType == BOOK_TYPE_PAPERBACK || bookType == BOOK_TYPE_ELECTRONIC){
                return true;
            }
            return false;
        }

    }
}
