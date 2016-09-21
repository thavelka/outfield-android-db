package com.outfieldapp.outfieldbackend.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.outfieldapp.outfieldbackend.OutfieldApp;
import com.outfieldapp.outfieldbackend.api.Constants.Keys;
import com.outfieldapp.outfieldbackend.database.OutfieldContract;

import java.util.ArrayList;
import java.util.List;

public class Contact extends Model {

    public static final String TAG = Contact.class.getSimpleName();

    private transient long rowId;
    private transient boolean dirty;
    private transient boolean destroy;

    @SerializedName(Keys.Contact.ID)
    private long contactId;
    @SerializedName(Keys.Contact.CONTACT_TYPE)
    private String contactType = "";
    @SerializedName(Keys.Contact.NAME)
    private String name = "";
    @SerializedName(Keys.Contact.TITLE)
    private String title = "";
    @SerializedName(Keys.Contact.COMPANY)
    private String company = "";
    @SerializedName(Keys.Contact.WEBSITE)
    private String website = "";
    @SerializedName(Keys.Contact.IS_FAVORED)
    private boolean favored;
    @SerializedName(Keys.Contact.ADDRESSES)
    private List<Address> addresses = new ArrayList<>();
    @SerializedName(Keys.Contact.EMAILS)
    private List<Email> emails = new ArrayList<>();
    @SerializedName(Keys.Contact.PHONES)
    private List<Phone> phones = new ArrayList<>();
    @SerializedName(Keys.Contact.IMAGES)
    private List<Image> images = new ArrayList<>();

    /* Constructors */
    public Contact() {}
    public Contact(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return contactId; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getWebsite() { return website; }
    public boolean isFavored() { return favored; }
    public boolean isDirty() { return dirty; }
    public boolean isDestroy() { return destroy; }
    public List<Address> getAddresses() { return addresses; }
    public List<Email> getEmails() { return emails; }
    public List<Phone> getPhones() { return phones; }
    public List<Image> getImages() { return images; }

    public Type getContactType() {
        if (!TextUtils.isEmpty(contactType)) {
            return Type.valueOf(contactType.toUpperCase());
        }
        return null;
    }

    /* Setters */
    public void setId(long id) { contactId = id; }
    public void setContactType(Type type) { contactType = type.toString(); }
    public void setName(String name) { this.name = name; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setWebsite(String website) { this.website = website; }
    public void setFavored(boolean favored) { this.favored = favored; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    public void setEmails(List<Email> emails) { this.emails = emails; }
    public void setPhones(List<Phone> phones) { this.phones = phones; }
    public void setImages(List<Image> images) { this.images = images; }

    /* Database Access */
    /**
     * Searches Contacts database table for a row with matching {@link #contactId} and uses
     * {@link #loadFromCursor(Cursor)} to create a {@link Contact} object from that row.
     * @param contactId The contact's API id ({@link #contactId}).
     * @return A completed {@link Contact} object or null if row could not be found.
     */
    public static Contact getContactWithId(long contactId) {
        if (contactId != 0) {
            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();
            Cursor contactCursor = db.query(
                    OutfieldContract.Contact.TABLE_NAME,
                    null,
                    OutfieldContract.Contact.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null,
                    null,
                    null,
                    "1"
            );

            if (contactCursor != null && contactCursor.moveToFirst()) {
                Contact contact = new Contact(contactCursor);
                contactCursor.close();
                return contact;
            }
        }

        return null;
    }

    /**
     * Calls {@link #insert()} method for this Contact object and all submodels. If a contact with the
     * same {@link #contactId} already exists in the database, that contact and its submodels will
     * be deleted and replaced.
     * @return True if save was successful.
     */
    public boolean save() {

        // Insert contact values
        insert();

        // Insert addresses
        if (!addresses.isEmpty()) {
            for (Address address : addresses) {
                address.setContactId(contactId);
                address.insert();
            }
        }

        // Insert emails
        if (!emails.isEmpty()) {
            for (Email email: emails) {
                email.setContactId(contactId);
                email.insert();
            }
        }

        // Insert phones
        if (!phones.isEmpty()) {
            for (Phone phone : phones) {
                phone.setContactId(contactId);
                phone.insert();
            }
        }

        // Insert images
        if (!images.isEmpty()) {
            for (Image image : images) {
                image.setContactId(contactId);
                image.insert();
            }
        }

        return true;
    }

    @Override
    protected boolean insert() {

        if (getContactType() == null) {
            Log.e(TAG, "Contact must have contact type");
            return false;
        }

        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        db.beginTransaction();
        try {
            // Insert values, get row id
            rowId = db.insertOrThrow(OutfieldContract.Contact.TABLE_NAME, null, getContentValues());

            // If contact does not have an id, generate negative id from row id and update
            if (contactId == 0) {
                contactId = rowId * -1;
                ContentValues values = new ContentValues();
                values.put(OutfieldContract.Contact.CONTACT_ID, contactId);
                db.update(
                        OutfieldContract.Contact.TABLE_NAME,
                        values,
                        OutfieldContract.Contact._ID + "=?",
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
                OutfieldContract.Contact.TABLE_NAME,
                getContentValues(),
                OutfieldContract.Contact._ID + "=?",
                new String[]{String.valueOf(rowId)}
        );
        db.setTransactionSuccessful();
        db.endTransaction();
        return rows > 0;
    }

    public boolean delete() {
        if (rowId <= 0) return false;
        SQLiteDatabase db = OutfieldApp.getDatabase().getWritableDatabase();
        int rows = db.delete(
                OutfieldContract.Contact.TABLE_NAME,
                OutfieldContract.Contact._ID,
                new String[]{String.valueOf(rowId)}
        );
        return rows > 0;
    }

    @Override
    protected void loadFromCursor(Cursor cursor) {
        try {
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact._ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.CONTACT_ID);
            int contactTypeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.CONTACT_TYPE);
            int nameIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.NAME);
            int titleIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.TITLE);
            int companyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.COMPANY);
            int websiteIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.WEBSITE);
            int favoredIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.FAVORED);
            int dirtyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.DIRTY);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Contact.DESTROY);

            // Load contact fields
            rowId = cursor.getLong(rowIndex);
            contactId = cursor.getInt(contactIdIndex);
            contactType = cursor.getString(contactTypeIndex);
            name = cursor.getString(nameIndex);
            title = cursor.getString(titleIndex);
            company = cursor.getString(companyIndex);
            website = cursor.getString(websiteIndex);
            favored = cursor.getInt(favoredIndex) > 0;
            dirty = cursor.getInt(dirtyIndex) > 0;
            destroy = cursor.getInt(destroyIndex) > 0;

            SQLiteDatabase db = OutfieldApp.getDatabase().getReadableDatabase();

            addresses = new ArrayList<>();
            emails = new ArrayList<>();
            phones = new ArrayList<>();
            images = new ArrayList<>();

            // Load addresses
            Cursor addressCursor = db.query(
                    OutfieldContract.Address.TABLE_NAME,
                    null,
                    OutfieldContract.Address.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null,
                    null,
                    OutfieldContract.Address.ADDRESS_ID
            );

            if (addressCursor != null) {
                while (addressCursor.moveToNext()) {
                    addresses.add(new Address(addressCursor));
                }
                addressCursor.close();
            }

            // Load emails
            Cursor emailCursor = db.query(
                    OutfieldContract.Email.TABLE_NAME,
                    null,
                    OutfieldContract.Email.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null,
                    null,
                    OutfieldContract.Email.EMAIL_ID
            );

            if (emailCursor != null) {
                while (emailCursor.moveToNext()) {
                    emails.add(new Email(emailCursor));
                }
                emailCursor.close();
            }

            // Load phones
            Cursor phoneCursor = db.query(
                    OutfieldContract.Phone.TABLE_NAME,
                    null,
                    OutfieldContract.Phone.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null,
                    null,
                    OutfieldContract.Phone.PHONE_ID
            );

            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    phones.add(new Phone(phoneCursor));
                }
                phoneCursor.close();
            }

            // Load images
            Cursor imageCursor = db.query(
                    OutfieldContract.Image.TABLE_NAME,
                    null,
                    OutfieldContract.Image.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null,
                    null,
                    OutfieldContract.Image.IMAGE_ID
            );

            if (imageCursor != null) {
                while (imageCursor.moveToNext()) {
                    images.add(new Image(imageCursor));
                }
                imageCursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        // Contact values
        if (contactId != 0) values.put(OutfieldContract.Contact.CONTACT_ID, contactId);
        values.put(OutfieldContract.Contact.CONTACT_TYPE, contactType);
        values.put(OutfieldContract.Contact.NAME, name);
        values.put(OutfieldContract.Contact.TITLE, title);
        values.put(OutfieldContract.Contact.COMPANY, company);
        values.put(OutfieldContract.Contact.WEBSITE, website);
        values.put(OutfieldContract.Contact.FAVORED, favored);
        values.put(OutfieldContract.Contact.DIRTY, dirty);
        values.put(OutfieldContract.Contact.DESTROY, destroy);

        // Address values
        if (!addresses.isEmpty()) {
            Address address = addresses.get(0);
            values.put(OutfieldContract.Contact.ADDRESS, address.toString());
            values.put(OutfieldContract.Contact.CITY, address.getCity());
            values.put(OutfieldContract.Contact.REGION, address.getRegion());
            values.put(OutfieldContract.Contact.COUNTRY, address.getCountry());
            values.put(OutfieldContract.Contact.LATITUDE, address.getLatitude());
            values.put(OutfieldContract.Contact.LONGITUDE, address.getLongitude());
        }

        // Email values
        if (!emails.isEmpty()) {
            values.put(OutfieldContract.Contact.EMAIL, emails.get(0).toString());
        }

        // Phone values
        if (!phones.isEmpty()) {
            values.put(OutfieldContract.Contact.PHONE, phones.get(0).toString());
        }

        // Image values
        if (!images.isEmpty()) {
            Image image = images.get(0);
            values.put(OutfieldContract.Contact.IMAGE_URL, image.getOriginalUrlString());
            values.put(OutfieldContract.Contact.THUMBNAIL_URL, image.getThumbnailUrlString());
        }

        return values;
    }

    public enum Type {
        PLACE, PERSON;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Wrapper class required for correct JSON serialization and deserialization of individual
     * objects. Wrap single objects with wrap() when creating POST payloads for API requests.
     */
    public static class Wrapper {
        @SerializedName(Keys.Contact.CLASS_NAME)
        private Contact contact;

        public Wrapper(Contact contact) {
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public Wrapper wrap() {
        return new Wrapper(this);
    }
}
