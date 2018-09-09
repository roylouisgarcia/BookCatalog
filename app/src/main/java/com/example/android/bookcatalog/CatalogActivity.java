package com.example.android.bookcatalog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookcatalog.data.BookCatalogContract;
import com.example.android.bookcatalog.data.BookCatalogContract.BookEntry;
import com.example.android.bookcatalog.data.BookCatalogCursorAdapter;
import com.example.android.bookcatalog.data.BookCatalogDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCatalogCursorAdapter mCursorAdapter;

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

        // Find the ListView which will be populated with the book data
        ListView bookListView = (ListView) findViewById(R.id.list);

        //Find and set the empty view on the List View
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCatalogCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        /* Prepare the loader */
        getSupportLoaderManager().initLoader(0, null, this);


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

        Toast toast = Toast.makeText(CatalogActivity.this, "Book data was inserted" + uriString, Toast.LENGTH_SHORT);
        toast.show();
    }

    // This is used to delete all the books from the table
    private void deleteAllBooks(){
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Toast toast2 = Toast.makeText(CatalogActivity.this, "All books were deleted", Toast.LENGTH_SHORT);
        toast2.show();
    }


    // Refreshes the displayDatabaseInfo() from altering the database via FAB
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // This is a projection that will be used to construct a book item row
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_TYPE };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}
