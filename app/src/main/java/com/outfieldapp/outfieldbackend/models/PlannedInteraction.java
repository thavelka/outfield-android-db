package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

import java.util.ArrayList;
import java.util.List;

public class PlannedInteraction extends Model {

    public static final String TAG = PlannedInteraction.class.getSimpleName();

    private long rowId;
    private long interactionId;
    private String interactionType = "";
    private String notes = "";
    private String shareUrl = "";
    private String date = "";
    private boolean dirty;
    private boolean destroy;

    InteractionDetails interactionDetails = new InteractionDetails();
    User user;
    List<Contact> contacts = new ArrayList<>();
    List<Long> contactIds = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();

    public PlannedInteraction() {}
    public PlannedInteraction(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return interactionId; }
    public Type getInteractionType() { return Type.valueOf(interactionType.toUpperCase()); }
    public String getNotes() { return notes; }
    public String getShareUrl() { return shareUrl; }
    public String getDate() { return date; }
    public boolean isDirty() { return dirty; }
    public boolean isDestroy() { return destroy; }
    public float getDuration() { return interactionDetails.duration; }
    public List<Comment> getComments() { return comments; }
    public User getUser() { return user; }
    public Contact getContact() { return (!contacts.isEmpty()) ? contacts.get(0) : null; }

    /* Setters */
    public void setInteractionType(Type type) { interactionType = type.toString(); }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDuration(float duration) { interactionDetails.duration = duration; }
    public void setDate(String date) { this.date = date; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }
    public void setContactId(long id) { contactIds.set(0, id); }
    public void setUser(User user) { this.user = user; }

    /**
     * Searches PlannedInteractions database table for a row with matching {@link #interactionId}
     * and uses {@link #loadFromCursor(Cursor)} to create a {@link PlannedInteraction} object from
     * that row.
     * @param interactionId The interaction's API id ({@link #interactionId}).
     * @return A completed {@link PlannedInteraction} object or null if row could not be found.
     */
    public static PlannedInteraction getPlannedInteractionWithId(long interactionId) {
        if (interactionId != 0) {
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor interactionCursor = db.query(
                    OutfieldContract.PlannedInteraction.TABLE_NAME,
                    null,
                    OutfieldContract.PlannedInteraction.INTERACTION_ID + "=?",
                    new String[]{String.valueOf(interactionId)},
                    null,
                    null,
                    null,
                    "1"
            );

            if (interactionCursor != null && interactionCursor.moveToFirst()) {
                PlannedInteraction interaction = new PlannedInteraction(interactionCursor);
                interactionCursor.close();
                return interaction;
            }
        }

        return null;
    }

    /**
     * Calls {@link #insert()} method for this PlannedInteraction object and all submodels. If a
     * PlannedInteraction with the same {@link #interactionId} already exists in the database, that
     * PlannedInteraction and its submodels will be deleted and replaced.
     * @return True if save was successful.
     */
    public boolean save() {
        // Insert interaction
        insert();

        // Insert user
        if (user != null) user.save();

        // Insert contact
        if (getContact() != null) {
            getContact().save();
        }

        // Insert comments
        for (Comment comment : comments) {
            comment.setInteractionId(interactionId);
            comment.save();
        }

        return true;
    }

    @Override
    protected boolean insert() {
        if (contactIds.isEmpty()) {
            Log.e(TAG, "Object has no parent");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert values, get row id
            rowId = db.insertOrThrow(OutfieldContract.PlannedInteraction.TABLE_NAME, null, getContentValues());

            // If contact does not have an id, generate negative id from row id and update
            if (interactionId == 0) {
                interactionId = rowId * -1;
                ContentValues values = new ContentValues();
                values.put(OutfieldContract.PlannedInteraction.INTERACTION_ID, interactionId);
                db.update(
                        OutfieldContract.Interaction.TABLE_NAME,
                        values,
                        OutfieldContract.Interaction._ID + "=?",
                        new String[]{String.valueOf(rowId)}
                );
            }

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
                OutfieldContract.PlannedInteraction.TABLE_NAME,
                getContentValues(),
                OutfieldContract.PlannedInteraction._ID + "=?",
                new String[]{String.valueOf(rowId)}
        );
        db.setTransactionSuccessful();
        db.endTransaction();
        return rows > 0;
    }

    @Override
    protected void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction._ID);
            int interactionIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.INTERACTION_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.USER_ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.CONTACT_ID);
            int typeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.INTERACTION_TYPE);
            int notesIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.NOTES);
            int shareUrlIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.SHARE_URL);
            int dateIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.DATE);
            int dirtyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.DIRTY);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.DESTROY);
            int durationIndex = cursor.getColumnIndexOrThrow(OutfieldContract.PlannedInteraction.DURATION);

            // Load interaction values
            rowId = cursor.getLong(rowIndex);
            interactionId = cursor.getLong(interactionIdIndex);
            interactionType = cursor.getString(typeIndex);
            notes = cursor.getString(notesIndex);
            shareUrl = cursor.getString(shareUrlIndex);
            date = cursor.getString(dateIndex);
            dirty = cursor.getInt(dirtyIndex) > 0;
            destroy = cursor.getInt(destroyIndex) > 0;

            // Load interaction detail values
            interactionDetails.duration = cursor.getFloat(durationIndex);

            // Load user
            long userId = cursor.getLong(userIdIndex);
            if (userId != 0) user = User.getUserWithId(userId);

            // Load contact
            long contactId = cursor.getLong(contactIdIndex);
            if (contactId != 0) {
                contactIds.set(0, contactId);
                contacts.set(0, Contact.getContactWithId(contactId));
            }

            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();

            // Load comments
            comments.clear();
            Cursor commentCursor = db.query(
                    OutfieldContract.Comment.TABLE_NAME,
                    null,
                    OutfieldContract.Comment.INTERACTION_ID + "=?",
                    new String[]{String.valueOf(interactionId)},
                    null,
                    null,
                    OutfieldContract.Comment.CREATED_AT
            );

            while (commentCursor != null && commentCursor.moveToNext()) {
                comments.add(new Comment(commentCursor));
            }

            if (commentCursor != null) commentCursor.close();

        } catch (Exception e) {
                Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        // Put interaction values
        if (interactionId != 0) values.put(OutfieldContract.PlannedInteraction.INTERACTION_ID, interactionId);
        values.put(OutfieldContract.PlannedInteraction.INTERACTION_TYPE, interactionType);
        values.put(OutfieldContract.PlannedInteraction.NOTES, notes);
        values.put(OutfieldContract.PlannedInteraction.SHARE_URL, shareUrl);
        values.put(OutfieldContract.PlannedInteraction.DATE, date);
        values.put(OutfieldContract.PlannedInteraction.DIRTY, dirty);
        values.put(OutfieldContract.PlannedInteraction.DESTROY, destroy);
        values.put(OutfieldContract.PlannedInteraction.DURATION, getDuration());

        // Put contact values
        if (!contacts.isEmpty() && contacts.get(0).getId() != 0) {
            values.put(OutfieldContract.PlannedInteraction.CONTACT_ID, contacts.get(0).getId());
        }

        // Put user values
        if (user != null && user.getId() != 0) {
            values.put(OutfieldContract.PlannedInteraction.USER_ID, user.getId());
        }

        // Put comment values
        if (!comments.isEmpty()) {
            values.put(OutfieldContract.PlannedInteraction.COMMENT_COUNT, comments.size());
        }

        return values;
    }

    public enum Type {
        PLANNED_CHECK_IN, PLANNED_MEETING;

        @Override
        public String toString() { return name().toLowerCase(); }

        public String getPrettyName() {
            switch (this) {
                case PLANNED_CHECK_IN:
                    return "check-in";
                case PLANNED_MEETING:
                    return "meeting";
                default:
                    return toString();
            }
        }
    }

    private static class InteractionDetails {
        int id;
        float duration;
    }
}
