package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class FormEntry extends Model {

    public static final String TAG = FormEntry.class.getSimpleName();

    private transient long rowId;
    private transient long interactionId;
    private transient long formId;

    @SerializedName(Keys.Interaction.FormEntry.ID)
    private long formFieldId;
    @SerializedName(Keys.Interaction.FormEntry.VALUE)
    private String value = "";

    /* Constructors */
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

    /* Database Access */
    @Override
    protected boolean insert() {
        if (interactionId == 0 || formId == 0) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

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
    protected void loadFromCursor(Cursor cursor) {
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
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (interactionId != 0) values.put(OutfieldContract.FormEntry.INTERACTION_ID, interactionId);
        if (formFieldId != 0) values.put(OutfieldContract.FormEntry.FORM_FIELD_ID, formFieldId);
        if (formId != 0) values.put(OutfieldContract.FormEntry.FORM_ID, formId);
        values.put(OutfieldContract.FormEntry.VALUE, value);
        return values;
    }
}
