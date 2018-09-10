package com.example.android.bookcatalog.data;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookcatalog.CatalogActivity;
import com.example.android.bookcatalog.R;
import com.example.android.bookcatalog.data.BookCatalogContract.BookEntry;

import org.w3c.dom.Text;

import static com.example.android.bookcatalog.R.drawable.ic_salebutton;


public class BookCatalogCursorAdapter extends CursorAdapter {

    private Context mContext;

    /**
     * Constructor that always enables auto-requery.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @deprecated This option is discouraged, as it results in Cursor queries
     * being performed on the application's UI thread and thus can cause poor
     * responsiveness or even Application Not Responding errors.  As an alternative,
     * use {@link LoaderManager} with a {@link CursorLoader}.
     */
    public BookCatalogCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        /* Prepare the TextViews from the inflated template that will be populated from the cursor */
        TextView itemTitleTextView = (TextView) view.findViewById(R.id.item_tv_title);
        TextView itemPriceTextView = (TextView) view.findViewById(R.id.item_tv_price);
        TextView itemTypeTextView = (TextView) view.findViewById(R.id.item_tv_type);
        ImageView itemSaleImageView = (ImageView) view.findViewById(R.id.item_img_sale);
        final TextView itemQuantityTextView = (TextView) view.findViewById(R.id.item_tv_quantity);

        /* Find the colums corresponding to the TextViews prepared to populate the item list */
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int typeColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TYPE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        /* Extract the book attributes from the cursor for the current book */
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookPriceMonetarySymbol = context.getResources().getString(R.string.editor_monetary_unit);
        Integer bookPrice = cursor.getInt(priceColumnIndex);
        Double bookPriceToDecimal = (double) bookPrice/100;
        Integer bookType = cursor.getInt(typeColumnIndex);
        Integer bookQuantity = cursor.getInt(quantityColumnIndex);


        /* identify which bookType label to display on an item list based on the numerical value */
        String bookTypeLabel = "";
        switch (bookType){
            case 0:
                bookTypeLabel = context.getResources().getString(R.string.item_book_type_hardcover);
                break;
            case 1:
                bookTypeLabel = context.getResources().getString(R.string.item_book_type_paperback);
                break;
            default:
                bookTypeLabel = context.getResources().getString(R.string.item_book_type_electronic);
                break;
        }
        itemTitleTextView.setText(bookTitle);
        // control the formatting of the price when shown in the listview
        itemPriceTextView.setText(bookPriceMonetarySymbol + String.format("%.2f", bookPriceToDecimal));
        itemTypeTextView.setText(bookTypeLabel);
        itemSaleImageView.setImageResource(R.drawable.ic_salebutton);
        itemQuantityTextView.setText(String.valueOf(bookQuantity));

        ImageButton saleButton = view.findViewById(R.id.item_img_sale);
        int columnIdIndex = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        final Uri contentUri = Uri.withAppendedPath(BookEntry.CONTENT_URI, Integer.toString(columnIdIndex));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(itemQuantityTextView.getText().toString().trim());

                if (quantity > 0){
                    quantity--;
                } else
                    Toast.makeText(context, context.getResources().getString(R.string.editor_quantity_lowest), Toast.LENGTH_LONG).show();

                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

                context.getContentResolver().update(contentUri, values, null, null);
            }
        });


    }


 }
