package com.example.android.bookcatalog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM books"
        // to get a Cursor that contains all rows from the books table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + BookEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // books table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_book);
            displayView.setText("Number of rows in books database table: " + cursor.getCount());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

        // Added to erase the dataPeeker message when other menu items other than the one inserting data from a populated activity_editor form
        TextView displayView = (TextView) findViewById(R.id.text_view_data_peek);
        displayView.setText("");
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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and a made values are inserted.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Harry Potter");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "JK Rowling");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_TYPE, BookEntry.PHONE_TYPE_HOME);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, "333-222-1111");
        values.put(BookEntry.COLUMN_BOOK_PRICE, "$10");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);

        // Insert a new row for Harry Potter in the database, returning the ID of that new row.
        // The first argument for db.insert() is the books table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Harry Potter
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        Toast toast = Toast.makeText(CatalogActivity.this, "Dummy Book data was inserted", Toast.LENGTH_SHORT);
        toast.show();
    }

    // This is used to delete all the books from the table
    private void deleteAllBooks(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("delete from " + BookEntry.TABLE_NAME);
        Toast toast2 = Toast.makeText(CatalogActivity.this, "Dummy Book entries will be deleted", Toast.LENGTH_SHORT);
        toast2.show();
    }

    // This method extracts a string passed from the EditorActivity class; Exception catching is used to bypass a Null Pointer Exception when the app runs for the first time and has not visited the EditorActivity yet, thus dataPeeker value is NULL
    private void displayDataPeek(){
        Intent intent = getIntent();
        String dataPeeker = "";
        TextView textViewPeek = (TextView) findViewById(R.id.text_view_data_peek);
        try{
            dataPeeker = intent.getExtras().getString("dataPeeker");
            textViewPeek.setText(dataPeeker);
        }catch (NullPointerException nullPointer){
            textViewPeek.setText("");
        }

    }

    // Refreshes the displayDatabaseInfo() from altering the database via FAB
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
        displayDataPeek();
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
