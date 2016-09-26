package com.outfieldapp.outfieldbackend.api;

import com.outfieldapp.outfieldbackend.BuildConfig;
import com.outfieldapp.outfieldbackend.OutfieldApp;

/**
 * Contains constants necessary for interaction with the Outfield REST API.
 */
public class Constants {
    private Constants() {}

    public static final String BASE_URL = BuildConfig.DEBUG
            ? "https://infield.outfieldapp.com/api/v2/"
            : "https://run.outfieldapp.com/api/v2/";

    /* Intent */
    public static class Intents {
        public static final String SYNC_BEGIN_FILTER = OutfieldApp.appName + "_syncBegin";
        public static final String SYNC_END_FILTER = OutfieldApp.appName + "_syncEnd";
        public static final String SYNC_PROGRESS_FILTER = OutfieldApp.appName + "_syncProgress";
        public static final String SYNC_PROGRESS_KEY = "progress";
        public static final String SYNC_TIME_REMAINING_KEY = "timeRemaining";
        public static final String SYNC_SUCCESS_KEY = "success";
    }
    /* Shared Prefs Keys*/
    public static class Prefs {
        public static final String HAS_TEAM_ACTIVITY = "hasTeamActivity";
        public static final String CURRENT_USER_ID = "currentUserId";
        public static final String CURRENT_FORM_IDS = "currentFormIds";
        public static final String SHOW_LOADING_SCREEN = "showLoadingScreen";
    }

    /* HTTP Header Keys */
    public static class Headers {
        public static final String EMAIL = "X-User-Email";
        public static final String AUTH_TOKEN = "X-Auth-Token";
        public static final String SYNC_TOKEN = "X-Sync-Token";
        public static final String SYNC_STATUS = "X-Sync-Status";
    }

    /* URL Endpoints */
    public static class Endpoints {
        public static final String SYNC = "sync";
        public static final String CONTACTS = "contacts";
        public static final String MY_CONTACTS = "my_contacts";
        public static final String EXPLORE = "contacts/explore";
        public static final String INTERACTIONS = "interactions";
        public static final String SIGN_UP = "sign_up";
        public static final String SIGN_IN = "sign_in";
        public static final String COMMENTS = "comments";
        public static final String LEADERBOARD = "analytics/team_activity_leaderboard";
        public static final String FORMS = "forms";
        public static final String NOTIFICATIONS = "notifications";
        public static final String ME = "me";
        public static final String ACCOUNT_EXISTS = "me/new";
        public static final String PASSWORD_RESET = "password_reset";
        public static final String PUSH_CREDENTIALS = "push_notification_credentials";
    }

    /* URL Parameters */
    public static class Params {
        public static class SignIn {
            public static final String EMAIL = "user[email]";
            public static final String PASSWORD = "user[password]";
        }

        public static class SignUp {
            public static final String NAME = "user[name]";
            public static final String EMAIL = "user[email]";
            public static final String PASSWORD = "user[password]";
            public static final String PASSWORD_CONFIRMATION = "user[password_confirmation]";
            public static final String ORG_NAME = "user[organization_name]";
        }

        public static class Me {
            public static final String IMAGE_FILE = "user[image_file]";
        }

        public static class Contacts {
            public static final String CONTACT_TYPE = "contact_type";
            public static final String SORT_BY = "sort_by";
            public static final String SEARCH = "search";
            public static final String GLOBAL = "global";
            public static final String PAGE = "page";
            public static final String PER_PAGE = "per_page";
            public static final String INCLUDES = "includes";
            public static final String LOCATION = "ll";
            public static final String RADIUS = "radius";
            public static final String CONTACT_ID = "contact_id";
            public static final String IMAGE_FILES = "contact[image_files][]";
        }

        public static class Explore {
            public static final String LOCATION = "ll";
            public static final String QUERY = "query";
        }

        public static class Interactions {
            public static final String CONTACT_ID = "interacted_with";
            public static final String INTERACTION_TYPE = "interaction_type";
            public static final String SEARCH = "search";
            public static final String PAGE = "page";
            public static final String PER_PAGE = "per_page";
            public static final String ONLY_ME = "only_me";
            public static final String IMAGE_FILES = "interaction[image_files][]";
        }

        public static class Notifications {
            public static final String PAGE = "page";
            public static final String PER_PAGE = "per_page";
        }

        public static class AccountExists {
            public static final String EMAIL = "email";
        }

        public static class PasswordReset {
            public static final String EMAIL = "email";
        }

        public static class PushNotificationCredential {
            public static final String TOKEN = "push_notification_credential[token]";
        }

        public static class Sync {
            public static final String SYNC_TOKEN = "token";
            public static final String PER_SYNC = "per_sync";
            public static final String ONLY_ME = "only_me";
        }

        public static class Leaderboard {
            public static final String START_DATE = "start_date";
            public static final String END_DATE = "end_date";
        }
    }

    /* JSON Keys */
    public static class Keys {
        public static class Contact {
            public static final String CLASS_NAME = "contact";
            public static final String PLURAL_NAME = "contacts";
            public static final String ID = "id";
            public static final String CLIENT_ID = "client_id";
            public static final String IS_ACTIVE = "is_active";
            public static final String IS_FAVORED = "is_favored";
            public static final String COMPANY = "company";
            public static final String TITLE = "title";
            public static final String CONTACT_TYPE = "contact_type";
            public static final String DISTANCE = "distance";
            public static final String LAST_CHECK_IN = "last_check_in_date";
            public static final String LAST_MEETING = "last_meeting_date";
            public static final String NAME = "name";
            public static final String NOTE = "note";
            public static final String WEBSITE = "website";
            public static final String CREATED_AT = "created_at";
            public static final String FAVORED_AT = "favored_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String DELETED_AT = "deleted_at";
            public static final String METADATA = "metadata";
            public static final String ADDRESSES = "addresses";
            public static final String PHONES = "phones";
            public static final String EMAILS = "emails";
            public static final String COMMENTS = "comments";
            public static final String IMAGES = "images";
            public static final String LINKED_CONTACTS = "linked_contacts";
            public static final String RECENT_INTERACTIONS = "recent_interactions";
            public static final String IMAGE_FILES = "image_files";
            public static final String DESTROY = "_destroy";
        }

        public static class Address {
            public static final String CLASS_NAME = "address";
            public static final String ID = "id";
            public static final String LABEL = "label";
            public static final String STREET_1 = "street1";
            public static final String STREET_2 = "street2";
            public static final String CITY = "city";
            public static final String REGION = "region";
            public static final String POSTAL_CODE = "postal_code";
            public static final String COUNTRY = "country";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String DESTROY = "_destroy";
        }

        public static class Email {
            public static final String CLASS_NAME = "email";
            public static final String ID = "id";
            public static final String LABEL = "label";
            public static final String VALUE = "value";
            public static final String PREFERRED = "preferred";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String DESTROY = "_destroy";
        }

        public static class Phone {
            public static final String CLASS_NAME = "phone";
            public static final String ID = "id";
            public static final String LABEL = "label";
            public static final String VALUE = "value";
            public static final String PREFERRED = "preferred";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String DESTROY = "_destroy";
        }

        public static class Interaction {
            public static final String CLASS_NAME = "interaction";
            public static final String PLURAL_NAME = "interactions";
            public static final String ID = "id";
            public static final String IN_TEAM_ACTIVITY = "in_team_activity";
            public static final String CONTACT_IDS = "contact_ids";
            public static final String CONTACT_NAME = "interacted_with_label";
            public static final String CONTACT_CITY = "interacted_with_city";
            public static final String CONTACT_REGION = "interacted_with_region";
            public static final String INTERACTION_TYPE = "interaction_type";
            public static final String CITY = "city";
            public static final String REGION = "region";
            public static final String COUNTRY = "country";
            public static final String USER = "user";
            public static final String FORMS = "forms";
            public static final String CONTACTS = "contacts";
            public static final String COMMENTS = "comments";
            public static final String IMAGES = "images";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String SHARE_URL = "share_url";
            public static final String METADATA = "metadata";
            public static final String NOTES = "notes";
            public static final String FORM_IDS = "form_ids";
            public static final String FORM_ENTRIES = "form_entries";
            public static final String IMAGE_FILES = "image_files";
            public static final String INTERACTION_DETAILS = "interaction_details";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String DESTROY = "_destroy";

            public static class InteractionDetails {
                public static final String CLASS_NAME = "interaction_details";
                public static final String ID = "id";
                public static final String EDITED_DURATION = "edited_duration";
                public static final String PATH = "path";
                public static final String CREATED_AT = "created_at";
                public static final String UPDATED_AT = "updated_at";

                public static class Location {
                    public static final String CLASS_NAME = "path";
                    public static final String LONGITUDE = "longitude";
                    public static final String LATITUDE = "latitude";
                    public static final String ACCURACY = "accuracy";
                    public static final String RECORDED_AT = "recorded_at";
                }
            }

            public static class FormEntryGroup {
                public static final String FORM_ID = "form_id";
                public static final String ENTRIES = "entries";
            }

            public static class FormEntry {
                public static final String ID = "id";
                public static final String VALUE = "value";
                public static final String DESTROY = "_destroy";
            }
        }

        public static class Image {
            public static final String CLASS_NAME = "image";
            public static final String ID = "id";
            public static final String HASH_ID = "hash_id";
            public static final String ORIGINAL_URL = "original_url";
            public static final String THUMBNAIL_URL = "thumbnail_url";
            public static final String FORMAT = "format";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String DESTROY = "_destroy";
        }

        public static class Comment {
            public static final String CLASS_NAME = "comment";
            public static final String ID = "id";
            public static final String USER_ID = "user_id";
            public static final String USER_NAME = "name";
            public static final String USER_EMAIL = "email";
            public static final String USER_THUMBNAIL = "thumbnail";
            public static final String COMMENT_TEXT = "comment";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String USER = "user";
            public static final String DESTROY = "_destroy";
        }

        public static class Notification {
            public static final String CLASS_NAME = "notification";
            public static final String PLURAL_NAME = "notifications";
            public static final String ID = "id";
            public static final String NOTIFICATION_TYPE = "notification_type";
            public static final String NOTIFICATION_DETAILS = "notification_details";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";

            public static class NotificationDetails {
                public static final String CLASS_NAME = "notification_details";
                public static final String COMMENT_ID = "comment_id";
                public static final String COMMENT = "comment";
                public static final String INTERACTION = "interaction";
            }
        }

        public static class PushNotificationCredential {
            public static final String CLASS_NAME = "push_notification_credential";
            public static final String TOKEN = "token";
            public static final String ID = "id";
            public static final String USER_ID = "user_id";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
        }

        public static class Form {
            public static final String CLASS_NAME = "form";
            public static final String PLURAL_NAME = "forms";
            public static final String ID = "id";
            public static final String ORGANIZATION_ID = "organization_id";
            public static final String USER_ID = "created_by_id";
            public static final String TITLE = "title";
            public static final String INTERACTION_TYPE = "interaction_type";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String FORM_FIELDS = "form_fields";

            public static class FormField {
                public static final String CLASS_NAME = "form_field";
                public static final String ID = "id";
                public static final String FORM_ID = "form_id";
                public static final String FIELD_TYPE = "field_type";
                public static final String LABEL = "label";
                public static final String IS_REQUIRED = "is_required";
                public static final String CHOICES = "choices";
                public static final String CREATED_AT = "created_at";
                public static final String UPDATED_AT = "updated_at";
                public static final String POSITION = "position";
            }
        }

        public static class User {
            public static final String CLASS_NAME = "user";
            public static final String PLURAL_NAME = "users";
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String EMAIL = "email";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String NOTIFICATIONS_READ_AT = "notifications_read_at";
            public static final String NOTIFICATIONS_COUNT = "notifications_count";
            public static final String USER_TOKEN = "user_token";
            public static final String IS_ACTIVE = "is_active";
            public static final String IMAGE = "image";
            public static final String IMAGE_FILE = "image_file";
            public static final String ORGANIZATION = "organization";
            public static final String THUMBNAIL_URL = "thumbnail_url";
            public static final String CHECK_IN_COUNT = "check_in_count";
            public static final String MEETING_COUNT = "meeting_count";
            public static final String NOTE_COUNT = "note_count";
        }

        public static class Organization {
            public static final String CLASS_NAME = "organization";
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String HAS_TEAM_ACTIVITY = "has_team_activity";
            public static final String TIME_ZONE = "time_zone";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
        }

        public static class LeaderboardUser {
            public static final String NAME = "name";
            public static final String USERS = "users";
            public static final String THUMBNAIL_URL = "thumbnail_url";
            public static final String CHECK_IN_COUNT = "check_in_count";
            public static final String MEETING_COUNT = "meeting_count";
            public static final String NOTE_COUNT = "note_count";
        }

        public static class Response {
            public static class Contacts {
                public static final String PER_PAGE = "per_page";
                public static final String PAGE = "page";
                public static final String PAGES_COUNT = "pages_count";
                public static final String CONTACTS_COUNT = "contacts_count";
                public static final String CONTACTS = "contacts";
            }

            public static class Interactions {
                public static final String PER_PAGE = "per_page";
                public static final String PAGE = "page";
                public static final String PAGES_COUNT = "pages_count";
                public static final String INTERACTIONS_COUNT = "interactions_count";
                public static final String INTERACTIONS = "interactions";
            }

            public static class Sync {
                public static final String SYNC_COUNT = "sync_count";
                public static final String REMAINING_COUNT = "remaining_count";
                public static final String TOKEN = "token";
                public static final String CONTACTS_COUNT = "contacts_count";
                public static final String INTERACTIONS_COUNT = "interactions_count";
                public static final String CONTACTS = "contacts";
                public static final String INTERACTIONS = "interactions";
                public static final String CREATE_COUNT = "create_count";
                public static final String CREATE = "create";
                public static final String UPDATE_COUNT = "update_count";
                public static final String UPDATE = "update";
                public static final String DELETE_COUNT = "delete_count";
                public static final String DELETE = "delete";
            }
        }
    }
}
