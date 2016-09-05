package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

public class Notification extends Model {

    public static final String TAG = Notification.class.getSimpleName();

    private long rowId;
    private long notificationId;
    private String createdAt = "";
    private NotificationDetails notificationDetails = new NotificationDetails();

    public Notification() {}
    public Notification(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public Comment getComment() { return notificationDetails.comment; }
    public Interaction getInteraction() { return notificationDetails.interaction; }

    public boolean save() {
        // Insert notification
        insert();

        // Insert interaction
        if (getInteraction() != null) getInteraction().save();

        return true;
    }

    @Override
    boolean insert() {
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            rowId = db.insertOrThrow(OutfieldContract.Notification.TABLE_NAME, null, getContentValues());
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
            // Load notification values
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Notification._ID);
            int notificationIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Notification.NOTIFICATION_ID);
            int commentIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Notification.COMMENT_ID);
            int interactionIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Notification.INTERACTION_ID);
            int createdAtIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Notification.CREATED_AT);

            rowId = cursor.getLong(rowIndex);
            notificationId = cursor.getLong(notificationIdIndex);
            createdAt = cursor.getString(createdAtIndex);

            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();

            // Load comment
            long commentId = cursor.getLong(commentIdIndex);
            if (commentId != 0) {
                Cursor commentCursor = db.query(
                        OutfieldContract.Comment.TABLE_NAME,
                        null,
                        OutfieldContract.Comment.COMMENT_ID + "=?",
                        new String[]{String.valueOf(commentId)},
                        null,
                        null,
                        "LIMIT 1"
                );

                if (commentCursor != null && commentCursor.moveToFirst()) {
                    notificationDetails.comment = new Comment(commentCursor);
                    commentCursor.close();
                }
            }

            // Load interaction
            long interactionId = cursor.getLong(interactionIdIndex);
            if (interactionId != 0) {
                Cursor interactionCursor = db.query(
                        OutfieldContract.Interaction.TABLE_NAME,
                        null,
                        OutfieldContract.Interaction.INTERACTION_ID + "=?",
                        new String[]{String.valueOf(interactionId)},
                        null,
                        null,
                        "LIMIT 1"
                );

                if (interactionCursor != null && interactionCursor.moveToFirst()) {
                    notificationDetails.interaction = new Interaction(interactionCursor);
                    interactionCursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor", e);
        }
    }

    @Override
    ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        // Put notification values
        if (notificationId != 0) values.put(OutfieldContract.Notification.NOTIFICATION_ID, notificationId);
        values.put(OutfieldContract.Notification.CREATED_AT, createdAt);

        // Put comment values
        if (notificationDetails.comment != null) {
            Comment comment = notificationDetails.comment;
            if (comment.getId() != 0) values.put(OutfieldContract.Notification.COMMENT_ID, comment.getId());
            values.put(OutfieldContract.Notification.COMMENT_TEXT, comment.getText());
            // Put user values
            if (comment.getUser() != null) {
                User user = comment.getUser();
                values.put(OutfieldContract.Notification.USER_NAME, user.getName());
                // Put image values
                if (user.getImage() != null) {
                    Image image = user.getImage();
                    values.put(OutfieldContract.Notification.USER_THUMB_URL, image.getThumbnailUrlString());
                }
            }
        }

        // Put Interaction values
        if (notificationDetails.interaction != null) {
            Interaction interaction = notificationDetails.interaction;
            values.put(OutfieldContract.Notification.INTERACTION_ID, interaction.getId());
            values.put(OutfieldContract.Notification.INTERACTION_TYPE, interaction.getInteractionType().toString());
            // Put contact values
            if (interaction.getContact() != null) {
                Contact contact = interaction.getContact();
                values.put(OutfieldContract.Notification.CONTACT_NAME, contact.getName());
            }
        }

        return values;
    }

    private static class NotificationDetails {
        Comment comment;
        Interaction interaction;
    }
}
