package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class Image extends Model {

    public static final String TAG = "Image";

    private long rowId;
    private long imageId;
    private long contactId;
    private long interactionId;
    private long userId;
    private byte[] imageFile;
    private String uri;
    private String originalUrl;
    private String thumbnailUrl;
    private boolean destroy;

    /* Getters */
    public byte[] getImageFile() { return imageFile; }
    public String getUriString() { return uri; }
    public String getOriginalUrlString() { return originalUrl; }
    public String getThumbnailUrlString() { return thumbnailUrl; }
    public boolean getDestroy() { return destroy; }

    /* Setters */
    public void setContactId(long id) { contactId = id; }
    public void setInteractionId(long id) { interactionId = id; }
    public void setUserId(long id) { userId = id; }
    public void setImageFile(byte[] bytes) { imageFile = bytes; }
    public void setUri(String uri) { this.uri = uri; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }

    public Image() {}
    public Image(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    @Override
    boolean insert() {

        if (contactId == 0 && interactionId == 0 && userId == 0) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.Image.TABLE_NAME, null, getContentValues());
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
                OutfieldContract.Image.TABLE_NAME,
                getContentValues(),
                OutfieldContract.Image._ID + "=?",
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
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image._ID);
            int imageIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.IMAGE_ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.CONTACT_ID);
            int interactionIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.INTERACTION_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.USER_ID);
            int imageFileIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.IMAGE_FILE);
            int imageUriIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.IMAGE_URI);
            int originalUrlIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.ORIGINAL_URL);
            int thumbUrlIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.THUMBNAIL_URL);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Image.DESTROY);

            rowId = cursor.getLong(rowIndex);
            imageId = cursor.getLong(imageIdIndex);
            contactId = cursor.getLong(contactIdIndex);
            interactionId = cursor.getLong(interactionIdIndex);
            userId = cursor.getLong(userIdIndex);
            imageFile = cursor.getBlob(imageFileIndex);
            uri = cursor.getString(imageUriIndex);
            originalUrl = cursor.getString(originalUrlIndex);
            thumbnailUrl = cursor.getString(thumbUrlIndex);
            destroy = cursor.getInt(destroyIndex) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (imageId != 0) values.put(OutfieldContract.Image.IMAGE_ID, imageId);
        if (contactId != 0) values.put(OutfieldContract.Image.CONTACT_ID, contactId);
        if (interactionId != 0) values.put(OutfieldContract.Image.INTERACTION_ID, interactionId);
        if (userId != 0) values.put(OutfieldContract.Image.USER_ID, userId);
        values.put(OutfieldContract.Image.IMAGE_FILE, imageFile);
        values.put(OutfieldContract.Image.IMAGE_URI, uri);
        values.put(OutfieldContract.Image.ORIGINAL_URL, originalUrl);
        values.put(OutfieldContract.Image.THUMBNAIL_URL, thumbnailUrl);
        values.put(OutfieldContract.Image.DESTROY, destroy);
        return values;
    }
}
