package com.example.android.bookcatalog.data;

import android.provider.BaseColumns;

public class BookCatalogContract {

    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";

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
