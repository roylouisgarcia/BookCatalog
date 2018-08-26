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

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
public class EditorActivity extends AppCompatActivity {

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
    private Spinner mSupplierPhoneTypeSpinner;

    /**
     * Type of phone. The possible values are:
     * 0 for home, 1 for mobile, 2 for work.
     */
    private int mPhoneType = 0;

    private String dataPeeker ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mBookTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_book_supplier_phone_number);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_book_supplier_name);
        mBookPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantityEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupplierPhoneTypeSpinner = (Spinner) findViewById(R.id.spinner_supplier_phone_type);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_phone_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSupplierPhoneTypeSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSupplierPhoneTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.phone_type_mobile))) {
                        mPhoneType = BookEntry.PHONE_TYPE_MOBILE; // Mobile
                    } else if (selection.equals(getString(R.string.phone_type_work))) {
                        mPhoneType = BookEntry.PHONE_TYPE_WORK; // Work
                    } else {
                        mPhoneType = BookEntry.PHONE_TYPE_HOME; // Home
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPhoneType = 0; // Unknown
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
        int suppierPhoneType = mPhoneType;

        dataPeeker = "Recent Book inserted: \n\nTitle: " + mBookTitleString + "\nPrice: $" + bookPrice + "\nQuantity: " + bookQuantity + "\nSupplier Name: " + mSupplierNameString + "\nSupplier Phone: " + mSupplierPhoneString + "\nPhone type: " + mPhoneType;
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
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_TYPE, suppierPhoneType);


        // Insert a new row for a book that is newly inserted to the database and return the ID of that new row.
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        // Show a toast message for insertion result
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving the book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    private void passInfo(){
        Intent i = new Intent(this, CatalogActivity.class);
        i.putExtra("dataPeeker", dataPeeker);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Insert data from form
                insertBooks();
                passInfo();
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
}