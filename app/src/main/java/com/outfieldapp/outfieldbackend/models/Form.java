package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

import java.util.ArrayList;
import java.util.List;

public class Form extends Model {

    public static final String TAG = "Form";

    private long rowId;
    private long formId;
    private String title = "";
    private List<FormField> formFields = new ArrayList<>();

    public Form() {}
    public Form(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return formId; }
    public String getTitle() { return title; }
    public List<FormField> getFormFields() { return formFields; }

    public static Form getFormWithId(long formId) {
        if (formId != 0) {
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor formCursor = db.query(
                    OutfieldContract.Form.TABLE_NAME,
                    null,
                    OutfieldContract.Form.FORM_ID + "=?",
                    new String[]{String.valueOf(formId)},
                    null,
                    null,
                    "LIMIT 1"
            );

            if (formCursor != null && formCursor.moveToFirst()) {
                Form form = new Form(formCursor);
                formCursor.close();
                return form;
            }
        }

        return null;
    }

    @Override
    boolean insert() {
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.Form.TABLE_NAME, null, getContentValues());
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error during insert()", e);
        } finally {
            db.endTransaction();
        }
        return rowId >= 0;
    }

    @Override
    void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Form._ID);
            int formIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Form.FORM_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Form.TITLE);

            // Load form values
            rowId = cursor.getLong(rowIndex);
            formId = cursor.getLong(formIdIndex);
            title = cursor.getString(titleIndex);

            // Retrieve form fields
            formFields.clear();
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor formFieldCursor = db.query(
                    OutfieldContract.FormField.TABLE_NAME,
                    null,
                    OutfieldContract.FormField.FORM_ID + "=?",
                    new String[]{String.valueOf(formId)},
                    null,
                    null,
                    OutfieldContract.FormField.POSITION
            );

            while (formFieldCursor != null && formFieldCursor.moveToNext()) {
                formFields.add(new FormField(formFieldCursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (formId != 0) values.put(OutfieldContract.Form.FORM_ID, formId);
        values.put(OutfieldContract.Form.TITLE, title);
        return values;
    }
}