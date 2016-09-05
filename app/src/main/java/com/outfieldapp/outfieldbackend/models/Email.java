package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class Email extends Model {

    public static final String TAG = "Email";

    private long rowId;
    private long emailId;
    private long contactId;
    private String label = "";
    private String value = "";
    private boolean destroy;

    public Email() {}
    public Email(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return emailId; }
    public String getLabel() { return label; }
    public String getValue() { return value; }
    public boolean isDestroy() { return destroy; }

    /* Setters */
    public void setContactId(long id) { contactId = id; }
    public void setLabel(String label) { this.label = label; }
    public void setValue(String value) { this.value = value; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }

    @Override
    public String toString() {
        return value;
    }

    @Override
    boolean insert() {

        if (contactId == 0) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.Email.TABLE_NAME, null, getContentValues());
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error during insert()", e);
        } finally {
            db.endTransaction();
        }
        return rowId >= 0;
    }

    public boolean update() {

        if (rowId <= 0) {
            Log.e(TAG, "Error: You must insert before updating");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();

        db.beginTransaction();
        int rows = db.update(
                OutfieldContract.Email.TABLE_NAME,
                getContentValues(),
                OutfieldContract.Email._ID + "=?",
                new String[]{String.valueOf(rowId)}
        );
        db.endTransaction();
        return rows > 0;
    }

    public boolean insertOrUpdate() {
        return (rowId > 0) ? update() : insert();
    }

    @Override
    void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Email._ID);
            int emailIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Email.EMAIL_ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Email.CONTACT_ID);
            int labelIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Email.LABEL);
            int valueIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Email.VALUE);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Email.DESTROY);

            rowId = cursor.getLong(rowIndex);
            emailId = cursor.getLong(emailIdIndex);
            contactId = cursor.getLong(contactIdIndex);
            label = cursor.getString(labelIndex);
            value = cursor.getString(valueIndex);
            destroy = cursor.getInt(destroyIndex) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (emailId != 0) values.put(OutfieldContract.Email.EMAIL_ID, emailId);
        if (contactId != 0) values.put(OutfieldContract.Email.CONTACT_ID, contactId);
        values.put(OutfieldContract.Email.LABEL, label);
        values.put(OutfieldContract.Email.VALUE, value);
        values.put(OutfieldContract.Email.DESTROY, destroy);
        return values;
    }
}
