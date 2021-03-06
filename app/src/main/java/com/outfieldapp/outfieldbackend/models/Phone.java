package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class Phone extends Model {

    public static final String TAG = Phone.class.getSimpleName();

    private long rowId;
    private long contactId;

    @SerializedName(Keys.Phone.ID)
    private long phoneId;
    @SerializedName(Keys.Phone.LABEL)
    private String label = "";
    @SerializedName(Keys.Phone.VALUE)
    private String value = "";
    @SerializedName(Keys.Phone.DESTROY)
    private boolean destroy;

    /* Constructors */
    public Phone() {}
    public Phone(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return phoneId; }
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

    /* Database Access */
    @Override
    protected boolean insert() {

        if (contactId == 0) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.Phone.TABLE_NAME, null, getContentValues());
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
                OutfieldContract.Phone.TABLE_NAME,
                getContentValues(),
                OutfieldContract.Phone._ID + "=?",
                new String[]{String.valueOf(rowId)}
        );
        db.endTransaction();
        return rows > 0;
    }

    @Override
    protected void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Phone._ID);
            int phoneIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Phone.PHONE_ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Phone.CONTACT_ID);
            int labelIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Phone.LABEL);
            int valueIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Phone.VALUE);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Phone.DESTROY);

            rowId = cursor.getLong(rowIndex);
            phoneId = cursor.getLong(phoneIdIndex);
            contactId = cursor.getLong(contactIdIndex);
            label = cursor.getString(labelIndex);
            value = cursor.getString(valueIndex);
            destroy = cursor.getInt(destroyIndex) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (phoneId != 0) values.put(OutfieldContract.Phone.PHONE_ID, phoneId);
        if (contactId != 0) values.put(OutfieldContract.Phone.CONTACT_ID, contactId);
        values.put(OutfieldContract.Phone.LABEL, label);
        values.put(OutfieldContract.Phone.VALUE, value);
        values.put(OutfieldContract.Phone.DESTROY, destroy);
        return values;
    }
}
