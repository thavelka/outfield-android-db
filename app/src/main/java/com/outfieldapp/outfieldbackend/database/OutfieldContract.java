package com.outfieldapp.outfieldbackend.database;

import android.provider.BaseColumns;

public final class OutfieldContract {
    private OutfieldContract() {}

    public static class Contact implements BaseColumns {
        public static final String TABLE_NAME = "contact";
        public static final String CONTACT_ID = "contact_id";
        public static final String CONTACT_TYPE = "contact_type";
        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String COMPANY = "company";
        public static final String WEBSITE = "website";
        public static final String FAVORED = "favored";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String REGION = "region";
        public static final String COUNTRY = "country";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String IMAGE_URL = "image_url";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String DIRTY = "dirty";
        public static final String DESTROY = "destroy";
    }

    public static class Address implements BaseColumns {
        public static final String TABLE_NAME = "address";
        public static final String ADDRESS_ID = "address_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String LABEL = "label";
        public static final String STREET1 = "street1";
        public static final String STREET2 = "street2";
        public static final String CITY = "city";
        public static final String REGION = "region";
        public static final String POSTAL_CODE = "postal_code";
        public static final String COUNTRY = "country";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String DESTROY = "destroy";
    }

    public static class Email implements BaseColumns {
        public static final String TABLE_NAME = "email";
        public static final String EMAIL_ID = "email_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String LABEL = "label";
        public static final String VALUE = "value";
        public static final String DESTROY = "destroy";
    }

    public static class Phone implements BaseColumns {
        public static final String TABLE_NAME = "phone";
        public static final String PHONE_ID = "phone_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String LABEL = "label";
        public static final String VALUE = "value";
        public static final String DESTROY = "destroy";
    }

    public static class Image implements BaseColumns {
        public static final String TABLE_NAME = "image";
        public static final String IMAGE_ID = "image_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String INTERACTION_ID = "interaction_id";
        public static final String USER_ID = "user_id";
        public static final String IMAGE_FILE = "image_file";
        public static final String IMAGE_URI = "image_uri";
        public static final String ORIGINAL_URL = "original_url";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String DESTROY = "destroy";
    }

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String IMAGE_URL = "image_url";
        public static final String THUMBNAIL_URL = "thumbnail_url";
        public static final String DIRTY = "dirty";
    }

    public static class Form implements BaseColumns {
        public static final String TABLE_NAME = "form";
        public static final String FORM_ID = "form_id";
        public static final String TITLE = "title";
    }

    public static class FormField implements BaseColumns {
        public static final String TABLE_NAME = "form_field";
        public static final String FORM_FIELD_ID = "form_field_id";
        public static final String FORM_ID = "form_id";
        public static final String POSITION = "position";
        public static final String REQUIRED = "required";
        public static final String LABEL = "label";
        public static final String FIELD_TYPE = "field_type";
        public static final String CHOICES = "choices";
    }

    public static class FormEntry implements BaseColumns {
        public static final String TABLE_NAME = "form_entry";
        public static final String INTERACTION_ID = "interaction_id";
        public static final String FORM_FIELD_ID = "form_field_id";
        public static final String FORM_ID = "form_id";
        public static final String VALUE = "value";
    }

    public static class Comment implements BaseColumns {
        public static final String TABLE_NAME = "comment";
        public static final String COMMENT_ID = "comment_id";
        public static final String INTERACTION_ID = "interaction_id";
        public static final String USER_ID = "user_name";
        public static final String COMMENT_TEXT = "comment_text";
        public static final String CREATED_AT = "created_at";
        public static final String DIRTY = "dirty";
        public static final String DESTROY = "destroy";
    }

    public static class Notification implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String NOTIFICATION_ID = "notification_id";
        public static final String COMMENT_ID = "comment_id";
        public static final String COMMENT_TEXT = "comment_text";
        public static final String USER_NAME = "user_name";
        public static final String USER_THUMB_URL = "user_thumb_url";
        public static final String INTERACTION_ID = "interaction_id";
        public static final String INTERACTION_TYPE = "interaction_type";
        public static final String CONTACT_NAME = "contact_name";
        public static final String CREATED_AT = "created_at";
    }

    public static class Interaction implements BaseColumns {
        public static final String TABLE_NAME = "interaction";
        public static final String INTERACTION_ID = "interaction_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String USER_ID = "user_id";
        public static final String FORM_IDS = "form_ids";
        public static final String INTERACTION_TYPE = "interaction_type";
        public static final String NOTES = "notes";
        public static final String SHARE_URL = "share_url";
        public static final String DURATION = "duration";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String IMAGE_URL = "image_url";
        public static final String COMMENT_COUNT = "comment_count";
        public static final String IMAGE_COUNT = "image_count";
        public static final String IN_TEAM_ACTIVITY = "in_team_activity";
        public static final String DISPLAY_IN_TEAM_ACTIVITY = "display_in_team_activity";
        public static final String CREATED_AT = "created_at";
        public static final String DRAFT = "draft";
        public static final String DIRTY = "dirty";
        public static final String DESTROY = "destroy";
    }

    public static class PlannedInteraction implements BaseColumns {
        public static final String TABLE_NAME = "planned_interaction";
        public static final String INTERACTION_ID = "interaction_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String USER_ID = "user_id";
        public static final String INTERACTION_TYPE = "interaction_type";
        public static final String NOTES = "notes";
        public static final String SHARE_URL = "share_url";
        public static final String DURATION = "duration";
        public static final String COMMENT_COUNT = "comment_count";
        public static final String DATE = "date";
        public static final String DIRTY = "dirty";
        public static final String DESTROY = "destroy";
    }
}
