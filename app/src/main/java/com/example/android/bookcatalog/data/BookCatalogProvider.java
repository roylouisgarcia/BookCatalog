package com.example.android.bookcatalog.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * {@link ContentProvider} for Books app.
 */
public class BookCatalogProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = BookCatalogProvider.class.getSimpleName();
    /** Initialization of a BookCatalogDbHelper */
    private BookCatalogDbHelper mDbHelper;


    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(BookCatalogContract.CONTENT_AUTHORITY, BookCatalogContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookCatalogContract.CONTENT_AUTHORITY, BookCatalogContract.PATH_BOOKS + "/#", BOOK_ID);
    }



    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a BookDbHelper object to gain access to the books database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new BookCatalogDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(BookCatalogContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookCatalogContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookCatalogContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }



    /**
     *  Replaced direct access insert method with one that utilizes URI Matcher and proper ContentProvider concepts
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Sanity Checks
        String bookTitle = values.getAsString(BookCatalogContract.BookEntry.COLUMN_BOOK_TITLE);
        if (bookTitle == null || TextUtils.isEmpty(bookTitle)){
             throw new IllegalArgumentException("Book requires a title");
        }

        Integer bookPrice = values.getAsInteger(BookCatalogContract.BookEntry.COLUMN_BOOK_PRICE);
        if (bookPrice != null && bookPrice < 0){
            throw new IllegalArgumentException("Book requires a valid price");
        }

        Integer bookQuantity = values.getAsInteger(BookCatalogContract.BookEntry.COLUMN_BOOK_QUANTITY);
        if (bookQuantity != null && bookQuantity < 0){
            throw new IllegalArgumentException("Book requires a valid quantity");
        }

        Integer bookType = values.getAsInteger(BookCatalogContract.BookEntry.COLUMN_BOOK_TYPE);
        if (bookType == null || !BookCatalogContract.BookEntry.isValidBookType(bookType)){
            throw new IllegalArgumentException("Book requires a valid type");
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // TODO: Insert a new book into the books database table with the given ContentValues
        long id = db.insert(BookCatalogContract.BookEntry.TABLE_NAME, null, values);

        // error checking incase insertion fails
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookCatalogContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Sanity Checks
        if (values.containsKey(BookCatalogContract.BookEntry.COLUMN_BOOK_TITLE)){
            String bookTitle = values.getAsString(BookCatalogContract.BookEntry.COLUMN_BOOK_TITLE);
            if (bookTitle == null || TextUtils.isEmpty(bookTitle)){
                throw new IllegalArgumentException("Book requires a title");
            }
        }

        if (values.containsKey(BookCatalogContract.BookEntry.COLUMN_BOOK_PRICE)){

            Integer bookPrice = values.getAsInteger(BookCatalogContract.BookEntry.COLUMN_BOOK_PRICE);
            if (bookPrice != null && bookPrice < 0){
                throw new IllegalArgumentException("Book requires a valid price");
            }
        }

        if (values.containsKey(BookCatalogContract.BookEntry.COLUMN_BOOK_QUANTITY)){
            Integer bookQuantity = values.getAsInteger(BookCatalogContract.BookEntry.COLUMN_BOOK_QUANTITY);
            if (bookQuantity != null && bookQuantity < 0){
                throw new IllegalArgumentException("Book requires a valid quantity");
            }
        }

        if (values.containsKey(BookCatalogContract.BookEntry.COLUMN_BOOK_TYPE)){
            Integer bookType = values.getAsInteger(BookCatalogContract.BookEntry.COLUMN_BOOK_TYPE);
            if (bookType == null || !BookCatalogContract.BookEntry.isValidBookType(bookType)){
                throw new IllegalArgumentException("Book requires a valid type");
            }
        }

        if (values.size() == 0){
            return 0;
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // TODO: Update the selected books in the books database table with the given ContentValues
        int updateResult = db.update(BookCatalogContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // TODO: Return the number of rows that were affected
        return updateResult;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}