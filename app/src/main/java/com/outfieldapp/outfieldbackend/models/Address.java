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

public class Address extends Model {

    public static final String TAG = Address.class.getSimpleName();

    private transient long rowId;
    private transient long contactId;

    @SerializedName(Keys.Address.ID)
    private long addressId;
    @SerializedName(Keys.Address.LABEL)
    private String label = "";
    @SerializedName(Keys.Address.STREET_1)
    private String street1 = "";
    @SerializedName(Keys.Address.STREET_2)
    private String street2 = "";
    @SerializedName(Keys.Address.CITY)
    private String city = "";
    @SerializedName(Keys.Address.REGION)
    private String region = "";
    @SerializedName(Keys.Address.POSTAL_CODE)
    private String postalCode = "";
    @SerializedName(Keys.Address.COUNTRY)
    private String country = "";
    @SerializedName(Keys.Address.LATITUDE)
    private float latitude;
    @SerializedName(Keys.Address.LONGITUDE)
    private float longitude;
    @SerializedName(Keys.Address.DESTROY)
    private boolean destroy;

    /* Constructors */
    public Address() {}
    public Address(Cursor cursor) {
        if (cursor != null) loadFromCursor(cursor);
    }

    /* Getters */
    public long getId() { return addressId; }
    public long getContactId() { return contactId; }
    public String getLabel() { return label; }
    public String getStreet1() { return street1; }
    public String getStreet2() { return street2; }
    public String getCity() { return city; }
    public String getRegion() { return region; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public float getLatitude() { return latitude; }
    public float getLongitude() { return  longitude; }
    public boolean isDestroy() { return destroy; }

    /* Setters */
    public void setContactId(long id) { contactId = id; }
    public void setLabel(String label) { this.label = label; }
    public void setStreet1(String street1) { this.street1 = street1; }
    public void setStreet2(String street2) { this.street2 = street2; }
    public void setCity(String city) { this.city = city; }
    public void setRegion(String region) { this.region = region; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country) { this.country = country; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }

    /**
     * Generates and returns address as one-line string in the form "Street 1 Street2 City, State"
     * @return Address as a string
     */
    @Override
    public String toString() {
        String address = "";

        if (!TextUtils.isEmpty(street1)) {
            address += street1;
        }
        if (!TextUtils.isEmpty(street2)) {
            address += " " + street2;
        }
        if (!TextUtils.isEmpty(city)) {
            address += " " + city;
        }
        if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(region)) {
            address += ',';
        }
        if (!TextUtils.isEmpty(region)) {
            address += " " + region;
        }

        return address;
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
            rowId = db.insertOrThrow(OutfieldContract.Address.TABLE_NAME, null, getContentValues());
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
            int rowIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address._ID);
            int addressIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.ADDRESS_ID);
            int contactIdIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.CONTACT_ID);
            int labelIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.LABEL);
            int street1Index = cursor.getColumnIndexOrThrow(OutfieldContract.Address.STREET1);
            int street2Index = cursor.getColumnIndexOrThrow(OutfieldContract.Address.STREET2);
            int cityIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.CITY);
            int regionIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.REGION);
            int postalCodeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.POSTAL_CODE);
            int countryIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.COUNTRY);
            int latitudeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.LATITUDE);
            int longitudeIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.LONGITUDE);
            int destroyIndex = cursor.getColumnIndexOrThrow(OutfieldContract.Address.DESTROY);

            rowId = cursor.getLong(rowIndex);
            addressId = cursor.getLong(addressIdIndex);
            contactId = cursor.getLong(contactIdIndex);
            label = cursor.getString(labelIndex);
            street1 = cursor.getString(street1Index);
            street2 = cursor.getString(street2Index);
            city = cursor.getString(cityIndex);
            region = cursor.getString(regionIndex);
            postalCode = cursor.getString(postalCodeIndex);
            country = cursor.getString(countryIndex);
            latitude = cursor.getFloat(latitudeIndex);
            longitude = cursor.getFloat(longitudeIndex);
            destroy = cursor.getInt(destroyIndex) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error during loadFromCursor()", e);
        }
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (addressId != 0) values.put(OutfieldContract.Address.ADDRESS_ID, addressId);
        if (contactId != 0) values.put(OutfieldContract.Address.CONTACT_ID, contactId);
        values.put(OutfieldContract.Address.LABEL, label);
        values.put(OutfieldContract.Address.STREET1, street1);
        values.put(OutfieldContract.Address.STREET2, street2);
        values.put(OutfieldContract.Address.CITY, city);
        values.put(OutfieldContract.Address.REGION, region);
        values.put(OutfieldContract.Address.POSTAL_CODE, postalCode);
        values.put(OutfieldContract.Address.COUNTRY, country);
        values.put(OutfieldContract.Address.LATITUDE, latitude);
        values.put(OutfieldContract.Address.LONGITUDE, longitude);
        values.put(OutfieldContract.Address.DESTROY, destroy);
        return values;
    }
}
