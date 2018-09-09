/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.bookcatalog;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookcatalog.data.BookCatalogContract.BookEntry;
import com.example.android.bookcatalog.data.BookCatalogDbHelper;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the book's title */
    private EditText mBookTitleEditText;

    /** EditText field to enter the book supplier's name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the book supplier's name */
    private EditText mSupplierPhoneEditText;

    /** EditText field to enter the book's price */
    private EditText mBookPriceEditText;

    /** EditText field to enter the book's quantity */
    private EditText mBookQuantityEditText;

    /** Spinner input to select the phone type */
    private Spinner mBookTypeSpinner;

    /**
     * Type of book. The possible values are:
     * 0 for hardcover, 1 for paperback, 2 for electronic.
     */
    private int mBookType = 0;

    private Uri mCurrentBookUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Use getIntent() and getData() to get the associated URI
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // if currentBookUri is null, we set the title to "Add a Pet", else, we se the title to "Edit Book"
        if (mCurrentBookUri == null){
            setTitle("Add a Book");
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
        }

        // Find all relevant views that we will need to read user input from
        mBookTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone_number);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplier_name);
        mBookPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mBookTypeSpinner = (Spinner) findViewById(R.id.spinner_book_ype);

        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.book_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mBookTypeSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mBookTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.book_type_hardcover))) {
                        mBookType = BookEntry.BOOK_TYPE_HARDCOVER; // Hardcover
                    } else if (selection.equals(getString(R.string.book_type_paperback))) {
                        mBookType = BookEntry.BOOK_TYPE_PAPERBACK; // Paperback
                    } else {
                        mBookType = BookEntry.BOOK_TYPE_ELECTRONIC; // Electronic
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBookType = 0; // Hardcover
            }
        });
    }


    private void insertBooks(){

        // Reading the inputs
        String mBookTitleString = mBookTitleEditText.getText().toString().trim();
        String mBookPriceString = mBookPriceEditText.getText().toString().trim();
        int bookPrice = Integer.parseInt(mBookPriceString);
        String mBookQuantityString = mBookQuantityEditText.getText().toString().trim();
        int bookQuantity = Integer.parseInt(mBookQuantityString);
        String mSupplierNameString = mSupplierNameEditText.getText().toString().trim();
        String mSupplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        int bookType = mBookType;

        // Create an instance of a BookCataglogDbHelper
        BookCatalogDbHelper mDbHelper = new BookCatalogDbHelper(this);

        // Create an instance of the writable database of books
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Use ContentValues Object to prepare the paring of columns and value strings
        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_BOOK_TITLE, mBookTitleString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, mSupplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, mSupplierPhoneString);
        values.put(BookEntry.COLUMN_BOOK_TYPE, bookType);


        // Insert a new row for a book that is newly inserted to the database and return the ID of that new row.
//        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        Uri insertResults = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        // String-holder to display the returned URI after the insert to the Toast Message
        String uriString = insertResults.toString();
        String newRowId = String.valueOf(ContentUris.parseId(insertResults));

        // Show a toast message for insertion result
        if (newRowId == null) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this,R.string.editor_insert_book_failed, Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, R.string.editor_insert_book_successful + newRowId + "\n" + uriString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Insert data from form
                insertBooks();
                finish();
                Toast toast_save = Toast.makeText(EditorActivity.this, "New Book Saved" , Toast.LENGTH_SHORT);
                toast_save.show();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                Toast toast_delete = Toast.makeText(EditorActivity.this, "New Book Deleted", Toast.LENGTH_SHORT);
                toast_delete.show();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // This is a projection that will be used to construct a book item row
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_TYPE,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1){
            return;
        }

        // Populate the input forms with data fetched from the database
        if(data.moveToFirst()){
            // Finding the comouns of book attributes that we need
            int titleColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int typeColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_TYPE);
            int supplierColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int supplierPhoneNumberColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Extracting the value of the cursor for every column index
            String title = data.getString(titleColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            int type = data.getInt(typeColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            String supplierPhoneNumber = data.getString(supplierPhoneNumberColumnIndex);

            // Updating the views from the screen with the values from the database
            mBookTitleEditText.setText(title);
            mBookPriceEditText.setText(price);
            mBookQuantityEditText.setText(quantity);
            mSupplierNameEditText.setText(supplier);
            mSupplierPhoneEditText.setText(supplierPhoneNumber);

            // determine which dropdown is selected based on the value of book type from the database
            switch (type){
                case BookEntry.BOOK_TYPE_HARDCOVER:
                    mBookTypeSpinner.setSelection(BookEntry.BOOK_TYPE_HARDCOVER);
                    break;
                case BookEntry.BOOK_TYPE_PAPERBACK:
                    mBookTypeSpinner.setSelection(BookEntry.BOOK_TYPE_PAPERBACK);
                    break;
                default:
                    mBookTypeSpinner.setSelection(BookEntry.BOOK_TYPE_ELECTRONIC);
                    break;

            }
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mBookTitleEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSupplierNameEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityEditText.setText("");
        mBookTypeSpinner.setSelection(0);
    }
}