package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class Comment extends Model {

    public static final String TAG = Comment.class.getSimpleName();

    private long rowId;
    private long commentId;
    private long interactionId;
    private String text = "";
    private String createdAt = "";
    private boolean dirty;
    private boolean destroy;
    private User user;

    public Comment() {}
    public Comment(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return commentId; }
    public String getText() { return text; }
    public String getCreatedAt() { return createdAt; }
    public boolean isDirty() { return dirty; }
    public boolean isDestroy() { return destroy; }
    public User getUser() { return user; }

    /* Setters */
    public void setInteractionId(long id) { interactionId = id; }
    public void setText(String text) { this.text = text; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }
    public void setUser(User user) { this.user = user; }


    public boolean save() {
        // Insert comment values
        insert();

        // Insert user values
        if (user != null) user.save();

        return true;
    }

    @Override
    boolean insert() {

        if (interactionId == 0) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.Comment.TABLE_NAME, null, getContentValues());
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
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment._ID);
            int commentIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.COMMENT_ID);
            int interactionIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.INTERACTION_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.USER_ID);
            int textIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.COMMENT_TEXT);
            int createdAtIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.CREATED_AT);
            int dirtyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.DIRTY);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Comment.DESTROY);

            // Load comment values
            rowId = cursor.getLong(rowIndex);
            commentId = cursor.getLong(commentIdIndex);
            interactionId = cursor.getLong(interactionIdIndex);
            text = cursor.getString(textIndex);
            createdAt = cursor.getString(createdAtIndex);
            dirty = cursor.getInt(dirtyIndex) > 0;
            destroy = cursor.getInt(destroyIndex) > 0;

            // Load user
            long userId = cursor.getLong(userIdIndex);
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
                user = new User(userCursor);
                userCursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (commentId != 0) values.put(OutfieldContract.Comment.COMMENT_ID, commentId);
        if (interactionId != 0) values.put(OutfieldContract.Comment.INTERACTION_ID, interactionId);
        values.put(OutfieldContract.Comment.COMMENT_TEXT, text);
        values.put(OutfieldContract.Comment.CREATED_AT, createdAt);
        values.put(OutfieldContract.Comment.DIRTY, dirty);
        values.put(OutfieldContract.Comment.DESTROY, destroy);
        if (user != null && user.getId() != 0) {
            values.put(OutfieldContract.Comment.USER_ID, user.getId());
        }
        return values;
    }
}
