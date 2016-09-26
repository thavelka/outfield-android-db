package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

import java.util.ArrayList;
import java.util.List;

public class Notification extends Model {

    public static final String TAG = Notification.class.getSimpleName();

    private transient long rowId;

    @SerializedName(Keys.Notification.ID)
    private long notificationId;
    @SerializedName(Keys.Notification.CREATED_AT)
    private String createdAt = "";
    @SerializedName(Keys.Notification.NOTIFICATION_DETAILS)
    private NotificationDetails notificationDetails = new NotificationDetails();

    /* Constructors */
    public Notification() {}
    public Notification(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public Comment getComment() { return notificationDetails.comment; }
    public Interaction getInteraction() { return notificationDetails.interaction; }

    /* Database Access */
    /**
     * Calls {@link #insert()} method for this Notification object and all submodels. If a
     * Notification with the same {@link #notificationId} already exists in the database, that
     * Notification and its submodels will be deleted and replaced.
     * @return True if save was successful.
     */
    public boolean save() {
        // Insert notification
        insert();

        // Insert interaction
        if (getInteraction() != null) getInteraction().save();

        return true;
    }

    @Override
    protected boolean insert() {
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
    protected void loadFromCursor(Cursor cursor) {
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

            // Load comment
            long commentId = cursor.getLong(commentIdIndex);
            notificationDetails.comment = Comment.getCommentWithId(commentId);

            // Load interaction
            long interactionId = cursor.getLong(interactionIdIndex);
            notificationDetails.interaction = Interaction.getInteractionWithId(interactionId);

        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
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
        @SerializedName(Keys.Notification.NotificationDetails.COMMENT)
        Comment comment;
        @SerializedName(Keys.Notification.NotificationDetails.INTERACTION)
        Interaction interaction;
    }

    /**
     * Wrapper class required for correct JSON deserialization of arrays.
     */
    public static class ArrayWrapper {
        @SerializedName(Keys.Notification.PLURAL_NAME)
        private List<Notification> notifications = new ArrayList<>();

        public List<Notification> getNotifications() {
            return notifications;
        }
    }
}
