package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Abstract class containing the methods required to insert a Java object to the SQLite database and
 * reconstruct the object from a cursor to the database.
 */
public abstract class Model {

    /**
     * Inserts values obtained from {@link #getContentValues()} into the model's respective
     * database table. Existing rows with the same API id will be deleted and replaced.
     * Note that this method will only insert this object's fields and will not
     * insert the submodels' values.
     * @return True if insertion was successful.
     */
    abstract boolean insert();

    /**
     * Loads values for object and submodels from the cursor's current row. This method can
     * potentially run slowly, and should not be used in
     * {@link android.widget.CursorAdapter CursorAdapters}. Access the cursor's columns directly to
     * avoid slowdowns.
     * @param cursor A cursor to the model's respective database table.
     */
    abstract void loadFromCursor(Cursor cursor);

    /**
     * Generates {@link ContentValues} from this object's fields for insertion into the database.
     * Note that this does not generate ContentValues for submodels.
     * @return The ContentValues for this object's fields.
     */
    abstract ContentValues getContentValues();
}
