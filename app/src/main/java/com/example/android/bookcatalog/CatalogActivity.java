package com.example.android.bookcatalog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookcatalog.data.BookCatalogContract.BookEntry;
import com.example.android.bookcatalog.data.BookCatalogDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private BookCatalogDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new BookCatalogDbHelper(this);

        displayDatabaseInfo();

    }


    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        BookCatalogDbHelper mDbHelper = new BookCatalogDbHelper(this);



        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER,
                BookEntry.COLUMN_BOOK_TYPE
        };

        /*
        Perform a query on the provider using the ContentResolver instead of directly querying the database
        Use the {@link BookEntry#CONTENT_URI}  to access the book data
        */
        Cursor cursor = getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null, null);



        TextView displayView = (TextView) findViewById(R.id.text_view_book);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // books table in the database).

            displayView.setText("Using Cursor to see the what is inside the database: \n\n\tNumber of rows in books database table: " + cursor.getCount() + "\n");

            // Creating a header in the Text View
            displayView.append("\n\tCurrently the database has:\n\t" + BookEntry._ID + " - " + BookEntry.COLUMN_BOOK_TITLE + " - " + BookEntry.COLUMN_BOOK_PRICE + " - " + BookEntry.COLUMN_BOOK_QUANTITY + " - " +  BookEntry.COLUMN_BOOK_SUPPLIER + " - " + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER + " - " + BookEntry.COLUMN_BOOK_TYPE + "\n");

            // Figure out the index of each column

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int bookTitleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);
            int bookTypeColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TYPE);


            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentBookTitle = cursor.getString(bookTitleColumnIndex);
                int currentBookPrice = cursor.getInt(bookPriceColumnIndex);
                int currentBookQuantity = cursor.getInt(bookQuantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);
                int currentBookType = cursor.getInt(bookTypeColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n\t" + currentID + " - " +
                        currentBookTitle + " - " +
                        currentBookPrice + " - " +
                        currentBookQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber + " - " +
                        currentBookType));
            }
            displayView.append("\n\n");

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //This will insert a dummy data
    private void insertBook(){
        // Gets the database in write mode
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and a made values are inserted.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Harry Potter");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "JK Rowling");
        values.put(BookEntry.COLUMN_BOOK_TYPE, BookEntry.BOOK_TYPE_HARDCOVER);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, "333-222-1111");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "10");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);

        // Insert a new row for Harry Potter in the database, returning the ID of that new row.
        // The first argument for db.insert() is the books table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Harry Potter
        //long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
        Uri insertUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // String-holder to display the returned URI after the insert to the Toast Message
        String uriString = insertUri.toString();

        Toast toast = Toast.makeText(CatalogActivity.this, "Dummy Book data was inserted" + uriString, Toast.LENGTH_SHORT);
        toast.show();
    }

    // This is used to delete all the books from the table
    private void deleteAllBooks(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("delete from " + BookEntry.TABLE_NAME);
        Toast toast2 = Toast.makeText(CatalogActivity.this, "Dummy Book entries will be deleted", Toast.LENGTH_SHORT);
        toast2.show();
    }


    // Refreshes the displayDatabaseInfo() from altering the database via FAB
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertBook();
                displayDatabaseInfo();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                displayDatabaseInfo();
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
