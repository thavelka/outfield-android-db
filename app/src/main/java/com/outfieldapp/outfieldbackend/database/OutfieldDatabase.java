package com.outfieldapp.outfieldbackend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.outfieldapp.outfieldbackend.database.OutfieldContract.*;

public class OutfieldDatabase extends SQLiteOpenHelper {

    private static OutfieldDatabase instance;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Outfield.db";


    public static synchronized OutfieldDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new OutfieldDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private OutfieldDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + Contact.TABLE_NAME + " ("
                + Contact._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contact.CONTACT_ID + " INTEGER,"
                + Contact.CONTACT_TYPE + " TEXT,"
                + Contact.NAME + " TEXT,"
                + Contact.TITLE + " TEXT,"
                + Contact.COMPANY + " TEXT,"
                + Contact.WEBSITE + " TEXT,"
                + Contact.FAVORED + " INTEGER,"
                + Contact.ADDRESS + " TEXT,"
                + Contact.CITY + " TEXT,"
                + Contact.REGION + " TEXT,"
                + Contact.COUNTRY + " TEXT,"
                + Contact.LATITUDE + " REAL,"
                + Contact.LONGITUDE + " REAL,"
                + Contact.EMAIL + " TEXT,"
                + Contact.PHONE + " TEXT,"
                + Contact.IMAGE_URL + " TEXT,"
                + Contact.THUMBNAIL_URL + " TEXT,"
                + Contact.DIRTY + " INTEGER DEFAULT 0,"
                + Contact.DESTROY + " INTEGER DEFAULT 0,"
                + "UNIQUE (" + Contact.CONTACT_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Address.TABLE_NAME + " ("
                + Address._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Address.ADDRESS_ID + " INTEGER,"
                + Address.CONTACT_ID + " INTEGER,"
                + Address.LABEL + " TEXT,"
                + Address.STREET1 + " TEXT,"
                + Address.STREET2 + " TEXT,"
                + Address.CITY + " TEXT,"
                + Address.REGION + " TEXT,"
                + Address.POSTAL_CODE + " TEXT,"
                + Address.COUNTRY + " TEXT,"
                + Address.LATITUDE + " REAL,"
                + Address.LONGITUDE + " REAL,"
                + Address.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + Address.CONTACT_ID + ") REFERENCES " + Contact.TABLE_NAME
                + " (" + Contact.CONTACT_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + Address.ADDRESS_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Email.TABLE_NAME + " ("
                + Email._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Email.EMAIL_ID + " INTEGER,"
                + Email.CONTACT_ID + " INTEGER,"
                + Email.LABEL + " TEXT,"
                + Email.VALUE + " TEXT,"
                + Email.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + Email.CONTACT_ID + ") REFERENCES " + Contact.TABLE_NAME
                + " (" + Contact.CONTACT_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + Email.EMAIL_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Phone.TABLE_NAME + " ("
                + Phone._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Phone.PHONE_ID + " INTEGER,"
                + Phone.CONTACT_ID + " INTEGER,"
                + Phone.LABEL + " TEXT,"
                + Phone.VALUE + " TEXT,"
                + Phone.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + Phone.CONTACT_ID + ") REFERENCES " + Contact.TABLE_NAME
                + " (" + Contact.CONTACT_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + Phone.PHONE_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Image.TABLE_NAME + " ("
                + Image._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Image.IMAGE_ID + " INTEGER,"
                + Image.CONTACT_ID + " INTEGER,"
                + Image.USER_ID + " INTEGER,"
                + Image.INTERACTION_ID + " INTEGER,"
                + Image.IMAGE_FILE + " BLOB,"
                + Image.IMAGE_URI + " TEXT,"
                + Image.ORIGINAL_URL + " TEXT,"
                + Image.THUMBNAIL_URL + " TEXT,"
                + Image.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + Image.CONTACT_ID + ") REFERENCES " + Contact.TABLE_NAME
                + " (" + Contact.CONTACT_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + Image.INTERACTION_ID + ") REFERENCES " + Interaction.TABLE_NAME
                + " (" + Interaction.INTERACTION_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + Image.USER_ID + ") REFERENCES " + User.TABLE_NAME
                + " (" + User.USER_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + Image.IMAGE_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + User.TABLE_NAME + " ("
                + User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + User.USER_ID + " INTEGER,"
                + User.NAME + " TEXT,"
                + User.EMAIL + " TEXT,"
                + User.IMAGE_URL + " TEXT,"
                + User.THUMBNAIL_URL + " TEXT,"
                + User.DIRTY + " INTEGER DEFAULT 0,"
                + "UNIQUE (" + User.USER_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Form.TABLE_NAME + " ("
                + Form._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Form.FORM_ID + " INTEGER,"
                + Form.TITLE + " TEXT,"
                + "UNIQUE (" + Form.FORM_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + FormField.TABLE_NAME + " ("
                + FormField._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FormField.FORM_FIELD_ID + " INTEGER,"
                + FormField.FORM_ID + " INTEGER,"
                + FormField.POSITION + " INTEGER,"
                + FormField.REQUIRED + " INTEGER DEFAULT 0,"
                + FormField.LABEL + " TEXT,"
                + FormField.FIELD_TYPE + " TEXT,"
                + FormField.CHOICES + " TEXT,"
                + "FOREIGN KEY (" + FormField.FORM_ID + ") REFERENCES " + Form.TABLE_NAME
                + " (" + Form.FORM_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + FormField.FORM_FIELD_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + FormEntry.TABLE_NAME + " ("
                + FormEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FormEntry.INTERACTION_ID + " INTEGER,"
                + FormEntry.FORM_FIELD_ID + " INTEGER,"
                + FormEntry.FORM_ID + " INTEGER,"
                + FormEntry.VALUE + " TEXT,"
                + "FOREIGN KEY (" + FormEntry.INTERACTION_ID + ") REFERENCES " + Interaction.TABLE_NAME
                + " (" + Interaction.INTERACTION_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + " UNIQUE (" + FormEntry.INTERACTION_ID + ", "
                + FormEntry.FORM_FIELD_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Comment.TABLE_NAME + " ("
                + Comment._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Comment.COMMENT_ID + " INTEGER,"
                + Comment.INTERACTION_ID + " INTEGER,"
                + Comment.USER_ID + " INTEGER,"
                + Comment.COMMENT_TEXT + " TEXT,"
                + Comment.CREATED_AT + " INTEGER,"
                + Comment.DIRTY + " INTEGER DEFAULT 0,"
                + Comment.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + Comment.INTERACTION_ID + ") REFERENCES " + Interaction.TABLE_NAME
                + " (" + Interaction.INTERACTION_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "UNIQUE (" + Comment.COMMENT_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Notification.TABLE_NAME + " ("
                + Notification._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Notification.NOTIFICATION_ID + " INTEGER,"
                + Notification.COMMENT_ID + " INTEGER,"
                + Notification.COMMENT_TEXT + " TEXT,"
                + Notification.USER_NAME + " TEXT,"
                + Notification.USER_THUMB_URL + " TEXT,"
                + Notification.INTERACTION_ID + " INTEGER,"
                + Notification.INTERACTION_TYPE + " TEXT,"
                + Notification.CONTACT_NAME + " TEXT,"
                + Notification.CREATED_AT + " INTEGER,"
                + "UNIQUE (" + Notification.NOTIFICATION_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + Interaction.TABLE_NAME + " ("
                + Interaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Interaction.INTERACTION_ID + " INTEGER,"
                + Interaction.CONTACT_ID + " INTEGER,"
                + Interaction.USER_ID + " INTEGER,"
                + Interaction.FORM_IDS + " TEXT,"
                + Interaction.INTERACTION_TYPE + " TEXT,"
                + Interaction.NOTES + " TEXT,"
                + Interaction.SHARE_URL + " TEXT,"
                + Interaction.DURATION + " INTEGER,"
                + Interaction.LATITUDE + " REAL,"
                + Interaction.LONGITUDE + " REAL,"
                + Interaction.IMAGE_URL + " TEXT,"
                + Interaction.COMMENT_COUNT + " INTEGER,"
                + Interaction.IMAGE_COUNT + " INTEGER,"
                + Interaction.IN_TEAM_ACTIVITY + " INTEGER DEFAULT 1,"
                + Interaction.DISPLAY_IN_TEAM_ACTIVITY + " INTEGER DEFAULT 0,"
                + Interaction.CREATED_AT + " INTEGER,"
                + Interaction.DRAFT + " INTEGER DEFAULT 0,"
                + Interaction.DIRTY + " INTEGER DEFAULT 0,"
                + Interaction.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + Interaction.CONTACT_ID + ") REFERENCES " + Contact.TABLE_NAME
                + " (" + Contact.CONTACT_ID + ") ON UPDATE CASCADE,"
                + "UNIQUE (" + Interaction.INTERACTION_ID + ") ON CONFLICT REPLACE)"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + PlannedInteraction.TABLE_NAME + " ("
                + PlannedInteraction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlannedInteraction.INTERACTION_ID + " INTEGER,"
                + PlannedInteraction.CONTACT_ID + " INTEGER,"
                + PlannedInteraction.USER_ID + " INTEGER,"
                + PlannedInteraction.INTERACTION_TYPE + " TEXT,"
                + PlannedInteraction.NOTES + " TEXT,"
                + PlannedInteraction.SHARE_URL + " TEXT,"
                + PlannedInteraction.DURATION + " INTEGER,"
                + PlannedInteraction.COMMENT_COUNT + " INTEGER,"
                + PlannedInteraction.DATE + " INTEGER,"
                + PlannedInteraction.DIRTY + " INTEGER DEFAULT 0,"
                + PlannedInteraction.DESTROY + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY (" + PlannedInteraction.CONTACT_ID + ") REFERENCES " + Contact.TABLE_NAME
                + " (" + Contact.CONTACT_ID + ") ON UPDATE CASCADE,"
                + "UNIQUE (" + PlannedInteraction.INTERACTION_ID + ") ON CONFLICT REPLACE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
