package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class FormEntry extends Model {

    public static final String TAG = "FormEntry";

    private long rowId;
    private long interactionId;
    private long formFieldId;
    private long formId;
    private String value = "";

    public FormEntry() {}
    public FormEntry(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getInteractionId() { return interactionId; }
    public long getFormFieldId() { return formFieldId; }
    public long getFormId() { return formId; }
    public String getValue() { return value; }

    /* Setters */
    public void setInteractionId(long id) { interactionId = id; }
    public void setFormFieldId(long id) { formFieldId = id; }
    public void setFormId(long id) { formId = id; }
    public void setValue(String value) { this.value = value; }


    @Override
    boolean insert() {
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.FormEntry.TABLE_NAME, null, getContentValues());
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
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormEntry._ID);
            int interactionIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormEntry.INTERACTION_ID);
            int formFieldIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormEntry.FORM_FIELD_ID);
            int formIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormEntry.FORM_ID);
            int valueIndex = cursor.getColumnIndexOrThrow(OutfieldContract.FormEntry.VALUE);

            rowId = cursor.getLong(rowIndex);
            interactionId = cursor.getLong(interactionIdIndex);
            formFieldId = cursor.getLong(formFieldIdIndex);
            formId = cursor.getLong(formIdIndex);
            value = cursor.getString(valueIndex);
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (interactionId != 0) values.put(OutfieldContract.FormEntry.INTERACTION_ID, interactionId);
        if (formFieldId != 0) values.put(OutfieldContract.FormEntry.FORM_FIELD_ID, formFieldId);
        if (formId != 0) values.put(OutfieldContract.FormEntry.FORM_ID, formId);
        values.put(OutfieldContract.FormEntry.VALUE, value);
        return values;
    }
}
