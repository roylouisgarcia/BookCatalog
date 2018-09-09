package com.example.android.bookcatalog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bookcatalog.data.BookCatalogContract.BookEntry;

public class BookCatalogDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Books.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" + BookEntry._ID + " INTEGER PRIMARY KEY, " + BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL," + BookEntry.COLUMN_BOOK_SUPPLIER + " TEXT, " + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + " TEXT, " + BookEntry.COLUMN_BOOK_TYPE + " INTEGER NOT NULL, " + BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, " + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;

    public BookCatalogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
