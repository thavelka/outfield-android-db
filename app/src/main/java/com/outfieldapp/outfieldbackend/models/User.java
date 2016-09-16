package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class User extends Model {

    public static final String TAG = User.class.getSimpleName();

    private long rowId;
    private long userId;
    private String name;
    private String email;
    private String authToken;
    private boolean active;
    private boolean dirty;
    private Image image;
    private Organization organization = new Organization();

    public User() {}
    public User(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getToken() { return authToken; }
    public boolean isActive() { return active; }
    public boolean isDirty() { return dirty; }
    public Image getImage() { return image; }
    public String getOrgName() { return organization.name; }
    public String getTimeZone() { return organization.timeZone; }
    public boolean hasTeamActivity() { return organization.hasTeamActivity; }

    /* Setters */
    public void setImage(Image image) { this.image = image; }

    /**
     * Searches Users database table for a row with matching {@link #userId} and uses
     * {@link #loadFromCursor(Cursor)} to create a {@link User} object from that row.
     * @param userId The user's API id ({@link #userId}).
     * @return A completed {@link User} object or null if row could not be found.
     */
    public static User getUserWithId(long userId) {
        if (userId > 0) {
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor userCursor = db.query(
                    OutfieldContract.User.TABLE_NAME,
                    null,
                    OutfieldContract.User.USER_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    "LIMIT 1"
            );

            if (userCursor != null && userCursor.moveToFirst()) {
                User user = new User(userCursor);
                userCursor.close();
                return user;
            }
        }

        return null;
    }

    /* Database Access */

    /**
     * Calls {@link #insert()} method for this user and all submodels. If a user with the
     * same {@link #userId} already exists in the database, that user and its submodels will
     * be deleted and replaced.
     * @return True if save was successful.
     */
    public boolean save() {
        // Insert user values
        insert();

        // Insert image values
        if (image != null) {
            image.setUserId(userId);
            image.insert();
        }

        return true;
    }

    @Override
    boolean insert() {
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert values, get row id
            rowId = db.insertOrThrow(OutfieldContract.User.TABLE_NAME, null, getContentValues());
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
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.User._ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.User.USER_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(OutfieldContract.User.NAME);
            int emailIndex = cursor.getColumnIndexOrThrow(OutfieldContract.User.EMAIL);
            int dirtyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.User.DIRTY);

            // Load user values
            rowId = cursor.getLong(rowIndex);
            userId = cursor.getLong(userIdIndex);
            name = cursor.getString(nameIndex);
            email = cursor.getString(emailIndex);
            dirty = cursor.getInt(dirtyIndex) > 0;

            // Load image
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor imageCursor = db.query(
                    OutfieldContract.Image.TABLE_NAME,
                    null,
                    OutfieldContract.Image.USER_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    "LIMIT 1"
            );

            if (imageCursor != null && imageCursor.moveToFirst()) {
                image = new Image(imageCursor);
                imageCursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (userId != 0) values.put(OutfieldContract.User.USER_ID, userId);
        values.put(OutfieldContract.User.NAME, name);
        values.put(OutfieldContract.User.EMAIL, email);

        if (image != null) {
            values.put(OutfieldContract.User.IMAGE_URL, image.getOriginalUrlString());
            values.put(OutfieldContract.User.THUMBNAIL_URL, image.getThumbnailUrlString());
        }

        return values;
    }

    private static class Organization {
        int organizationId;
        String name;
        String timeZone;
        boolean hasTeamActivity;
    }
}
