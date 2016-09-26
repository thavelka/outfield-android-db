package com.outfieldapp.outfieldbackend.api;

import android.util.Log;

import com.outfieldapp.outfieldbackend.api.response.ContactsResponse;
import com.outfieldapp.outfieldbackend.api.response.InteractionsResponse;
import com.outfieldapp.outfieldbackend.api.response.SyncResponse;
import com.outfieldapp.outfieldbackend.models.Comment;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Form;
import com.outfieldapp.outfieldbackend.models.Interaction;
import com.outfieldapp.outfieldbackend.models.Notification;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.List;

import rx.Observable;

public final class OutfieldAPI {

    public static final String TAG = OutfieldAPI.class.getSimpleName();

    private static ApiService apiService = ApiService.Builder.createService();
    private OutfieldAPI() {}

    /**
     * Recreates HTTP client with X-User-Email and X-Auth-Token headers.
     * @param email The user's email.
     * @param token The user's auth token.
     */
    public static void setAuthHeaders(String email, String token) {
        apiService = ApiService.Builder.createService(email, token);
    }

    //#############################################################################################
    //                                      USER REQUESTS
    //#############################################################################################

    /**
     * <code>POST /api/v2/sign_in</code>
     * Sends login request to server, which responds with a {@link User} object containing the
     * user's info. If an error is encountered, the user will be null.
     * @param email The user's email address.
     * @param password The user's password
     * @return {@link Observable} containing the user object or null on failure.
     */
    public static Observable<User> signIn(String email, String password) {
        return apiService.signIn(email, password)
                .map(User.Wrapper::getUser)
                .doOnError(throwable -> Log.e(TAG, "Error during signIn", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/me/new</code>
     * <p>
     * Checks to see if account already exists for this email address. If so, user info is returned.
     * If user doesn't exist, <code>success</code> will be false.
     * @param email The user's email address.
     * @return {@link Observable} containing the user object or null if the user does not exist.
     */
    public static Observable<User> checkAccountExists(String email) {
        return apiService.checkAccountExists(email)
                .map(User.Wrapper::getUser)
                .doOnError(throwable -> Log.e(TAG, "Error during checkAccountExists", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/me</code>
     * <p>
     * Retrieves detailed information about user.
     * @return {@link Observable} containing the user object or null on failure.
     */
    public static Observable<User> getUserDetails() {
        return apiService.getUserDetails()
                .map(User.Wrapper::getUser)
                .doOnError(throwable -> Log.e(TAG, "Error during getUserDetails", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>PUT /api/v2/me</code>
     * <p>
     * Updates user's information.
     * @param user The user object to be sent to the server.
     * @return {@link Observable} containing the updated user or null on failure.
     */
    public static Observable<User> updateUser(User user) {
        return apiService.updateUser(user.wrap())
                .map(User.Wrapper::getUser)
                .doOnError(throwable -> Log.e(TAG, "Error during updateUser", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>POST /api/v2/sign_up</code>
     * <p>
     * Creates a new organization and user on the server.
     * @param email The user's email address.
     * @param name The user's full name.
     * @param password The user's password.
     * @param orgName Name of the organization to be created.
     * @return {@link Observable} containing newly created user or null on failure.
     */
    public static Observable<User> signUp(String email, String name, String password, String orgName) {
        return apiService.signUp(email, name, password, password, orgName)
                .map(User.Wrapper::getUser)
                .doOnError(throwable -> Log.e(TAG, "Error during signUp", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>POST /api/v2/password_reset</code>
     * <p>
     * Sends email to specified address with a link to reset password.
     * @param email The user's email address.
     * @return {@link Observable} containing boolean success value.
     */
    public static Observable<Boolean> resetPassword(String email) {
        return apiService.resetPassword(email)
                .map(aVoid -> true)
                .doOnError(throwable -> Log.e(TAG, "Error during resetPassword", throwable))
                .onErrorReturn(throwable -> false);
    }

    // TODO: public void createPushCredential(String token, ResponseCallback<String> callback)

    //#############################################################################################
    //                                    CONTACT REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/contacts</code>
     * <p>
     * Retrieves one page of people contacts.
     * @param page The page to retrieve. Index begins at 1, not 0. Defaults to 0 if null.
     * @param perPage The number of contacts to retrieve per page. Defauts to 25 if null.
     * @param global Retrieves all places if true, only favored places if false or null.
     * @param search Search contacts for the given query. Can be null.
     * @return {@link Observable} containing a {@link ContactsResponse} object or null on failure.
     */
    public static Observable<ContactsResponse> getPeople(Integer page, Integer perPage, Boolean global, String search) {
        // Set default parameter values
        if (page == null) page = 1;
        if (perPage == null) perPage = 25;
        if (global == null) global = false;

        return apiService.getPeople(Contact.Type.PERSON.toString(), search, perPage, page, global)
                .doOnError(throwable -> Log.e(TAG, "Error during getPeople", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/my_contacts</code>
     * <p>
     * Retrieves one page of place contacts.
     * @param page The page to retrieve. Index begins at 1, not 0. Defaults to 0 if null.
     * @param perPage The number of contacts to retrieve per page. Defauts to 25 if null.
     * @param global Retrieves all places if true, only favored places if false or null.
     * @param search Search contacts for the given query. (optional)
     * @param latitude Latitude as a double, used to get nearby places. (optional)
     * @param longitude Longitude as a double, used to get nearby places. (optional)
     * @param radius Radius in miles, used to get nearby places. (optional)
     * @return {@link Observable} containing a {@link ContactsResponse} object or null on failure.
     */
    public static Observable<ContactsResponse> getPlaces(Integer page, Integer perPage, Boolean global,
                                                     String search, Double latitude, Double longitude,
                                                     Integer radius) {

        // Set default parameter values
        if (page == null) page = 1;
        if (perPage == null) perPage = 25;
        if (global == null) global = false;

        String location = null;
        if (latitude != null && longitude != null) {
            location = latitude + "," + longitude;
        }

        return apiService.getPlaces(Contact.Type.PLACE.toString(), search, perPage, page, global,
                location, radius)
                .doOnError(throwable -> Log.e(TAG, "Error during getPlaces", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/contacts/explore</code>
     * <p>
     * Find all organization contacts and Foursquare venues near the provided coordinates.
     * @param latitude Latitude as a double.
     * @param longitude Longitude as a double.
     * @param search Search for something nearby (e.g. “GNC”). (optional)
     * @return {@link Observable} containing list of nearby locations or null on failure.
     */
    public static Observable<List<Contact>> explore(Double latitude, Double longitude, String search) {

        String location = null;
        if (latitude != null && longitude != null) {
            location = latitude + "," + longitude;
        }
        return apiService.explore(location, search)
                .map(ContactsResponse::getContacts)
                .doOnError(throwable -> Log.e(TAG, "Error during explore", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/contacts/{id}></code>
     * <p>
     * Attempts to retrieve contact with provided id.
     * @param contactId API ID of the contact to be retrieved.
     * @return {@link Observable} containing the retrieved contact or null on failure.
     */
    public static Observable<Contact> getContact(long contactId) {
        return apiService.getContact(contactId)
                .map(Contact.Wrapper::getContact)
                .doOnError(throwable -> Log.e(TAG, "Error during getContact", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>POST /api/v2/my_contacts</code>
     * <p>
     * Add an organization's contact to the current user's “my contacts” list.
     * @param contactId API ID of the contact to be favored.
     * @return {@link Observable} containing the favored contact or null on failure.
     */
    public static Observable<Contact> favorContact(long contactId) {
        return apiService.favorContact(contactId)
                .map(Contact.Wrapper::getContact)
                .doOnError(throwable -> Log.e(TAG, "Error during favorContact", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>POST /api/v2/my_contacts</code>
     * <p>
     * Add an organization's contact to the current user's “my contacts” list,
     * while simultaneously updating the contact.
     * @param contact Contact object to be favored and updated.
     * @return {@link Observable} containing the favored contact or null on failure.
     */
    public static Observable<Contact> updateAndFavorContact(Contact contact) {
        return apiService.updateAndFavorContact(contact.wrap())
                .map(Contact.Wrapper::getContact)
                .doOnError(throwable -> Log.e(TAG, "Error during updateAndFavorContact", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>DELETE /api/v2/my_contacts/{id}</code>
     * <p>
     * Remove a contact from the current user's “my contacts” list, keep the contact in the
     * organization.
     * @param contact The contact to be deleted.
     * @return {@link Observable} containing the original {@link Contact} or null on failure.
     */
    public static Observable<Contact> unfavorContact(Contact contact) {
        return apiService.unfavorContact(contact.getId())
                .map(response -> (response.isSuccessful() || response.code() == 404) ? contact : null)
                .doOnError(throwable -> Log.e(TAG, "Error during unfavorContact", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>POST /api/v2/contacts</code>
     * <p>
     * Creates a contact on the server with the properties of the attached contact.
     * This method should not be used if the contact already has an ID. If the contact has an ID,
     * it already exists on the server and should be updated using {@link #updateContact}.
     * @param contact The contact to be uploaded.
     * @return {@link Observable} containing the created contact or null on failure.
     */
    public static Observable<Contact> createContact(Contact contact) {
        return apiService.createContact(contact.wrap())
                .doOnError(throwable -> Log.e(TAG, "Error during createContact", throwable))
                .map(Contact.Wrapper::getContact);
    }

    /**
     * <code>PUT /api/v2/contacts/{id}</code>
     * <p>
     * Updates an existing contact on the server with the properties of the attached contact.
     * This method should only be used for contacts that already have an ID. If the contact does
     * not have an ID, it doesn't exist on the server yet, and needs to be created using
     * {@link #createContact}.
     * @param contact The contact to be updated.
     * @return {@link Observable} containing the updated contact or null on failure.
     */
    public static Observable<Contact> updateContact(Contact contact) {
        return apiService.updateContact(contact.getId(), contact.wrap())
                .map(Contact.Wrapper::getContact)
                .doOnError(throwable -> Log.e(TAG, "Error during updateContact", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>DELETE /api/v2/contacts/{id}</code>
     * <p>
     * Deletes an existing contact from the server. This method is only necessary for
     * contacts that already have an ID. If the contact does not have an ID, it doesn't
     * exist on the server yet and only needs to be deleted locally.
     * @param contact Contact to be deleted.
     * @return {@link Observable} containing the original contact or null on failure.
     */
    public static Observable<Contact> deleteContact(Contact contact) {
        return apiService.deleteContact(contact.getId())
                .map(response -> (response.isSuccessful() || response.code() == 404) ? contact : null)
                .doOnError(throwable -> Log.e(TAG, "Error during deleteContact", throwable))
                .onErrorReturn(throwable -> null);
    }

    // TODO: public void uploadContactImages(List<Image> images, long contactId)

    //#############################################################################################
    //                                  INTERACTION REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/interactions</code>
     * <p>
     * @param onlyMe When false, retrieves interactions by all team members.
     * @param page The page to retrieve. Index begins at 1, not 0. Defaults to 1 if null.
     * @param perPage The number of interactions to retrieve per page. Defaults to 25 if null.
     * @param interactionType Only retrieve interactions of a particular type. (optional)
     * @param search Search interactions for provided text. (optional)
     * @return {@link Observable} containing the {@link InteractionsResponse} object or null on failure.
     */
    public static Observable<InteractionsResponse> getInteractions(boolean onlyMe, Integer page,
                                                               Integer perPage,
                                                               Interaction.Type interactionType,
                                                               String search) {
        // Set default params
        if (page == null) page = 1;
        if (perPage == null) perPage = 25;
        String type = (interactionType != null) ? interactionType.toString() : null;
        return apiService.getInteractions(onlyMe, page, perPage, type, search)
                .doOnError(throwable -> Log.e(TAG, "Error during getInteractions", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/interactions/{id}</code>
     * <p>
     * Attempts to retrieve interaction with provided ID.
     * @param interactionId API ID of interaction to be retrieved.
     * @return {@link Observable} containing the retrieved {@link Interaction} or null on failure.
     */
    public static Observable<Interaction> getInteraction(long interactionId) {
        return apiService.getInteraction(interactionId)
                .map(Interaction.Wrapper::getInteraction)
                .doOnError(throwable -> Log.e(TAG, "Error during getInteraction", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>POST /api/v2/interactions</code>
     * <p>
     * Creates an interaction on the server with the properties of the attached interaction.
     * This method should not be used if the interaction already has an ID. If the interaction has
     * an ID, it already exists on the server and should be updated using
     * {@link #updateInteraction}.
     * @param interaction The interaction to be uploaded.
     * @return {@link Observable} containing the created {@link Interaction} or null on failure.
     */
    public static Observable<Interaction> createInteraction(Interaction interaction) {
        return apiService.createInteraction(interaction.wrap())
                .map(Interaction.Wrapper::getInteraction)
                .doOnError(throwable -> Log.e(TAG, "Error during createInteraction", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>PUT /api/v2/interactions/{id}</code>
     * <p>
     * Updates an existing interaction on the server with the properties of the attached
     * interaction. This method should only be used for interactions that already have an ID.
     * If the interaction does not have an ID, it doesn't exist on the server yet, and needs to be
     * created using {@link #createInteraction}.
     * @param interaction The interaction to be updated.
     * @return {@link Observable} containing the created {@link Interaction} or null on failure.
     */
    public static Observable<Interaction> updateInteraction(Interaction interaction) {
        return apiService.updateInteraction(interaction.getId(), interaction.wrap())
                .map(Interaction.Wrapper::getInteraction)
                .doOnError(throwable -> Log.e(TAG, "Error during updateInteraction", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>DELETE /api/v2/interactions/{id}</code>
     * <p>
     * Deletes an existing interaction from the server. This method is only necessary for
     * interactions that already have an ID. If the interaction does not have an ID, it doesn't
     * exist on the server yet and only needs to be deleted locally.
     * @param interaction Interaction to be deleted.
     * @return {@link Observable} containing the original {@link Interaction} or null on failure.
     */
    public static Observable<Interaction> deleteInteraction(Interaction interaction) {
        return apiService.deleteInteraction(interaction.getId())
                .map(response -> (response.isSuccessful() || response.code() == 404) ? interaction : null)
                .doOnError(throwable -> Log.e(TAG, "Error during deleteInteraction", throwable))
                .onErrorReturn(throwable -> null);
    }

    // TODO: public void uploadInteractionImages(List<Image> images, long interactionId)

    // TODO: Analytics requests

    //#############################################################################################
    //                                     COMMENT REQUESTS
    //#############################################################################################

    /**
     * <code>POST /api/v2/interactions/{id}/comments</code>
     * <p>
     * Creates a comment for an interaction on the server. This method should only be used on
     * comments that do not have an ID. If the comment has an ID, it already exists on the server
     * and should be updated using {@link #updateComment}.
     * @param comment The {@link Comment} to be uploaded.
     * @return {@link Observable} containing the created {@link Comment} or null on failure.
     */
    public static Observable<Comment> createComment(Comment comment) {
        return apiService.createComment(comment.getInteractionId(), comment.wrap())
                .map(Comment.Wrapper::getComment)
                .doOnError(throwable -> Log.e(TAG, "Error during createComment", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>PUT /api/v2/comments/{id}</code>
     * <p>
     * Updates a comment on the server. This method should only be used on comments that have an ID.
     * If the comment does not have an ID, it does not exist on the server yet and should be created
     * using {@link #createComment}.
     * @param comment The {@link Comment} to be updated.
     * @return {@link Observable} containing the updated {@link Comment} or null on failure.
     */
    public static Observable<Comment> updateComment(Comment comment) {
        return apiService.updateComment(comment.getId(), comment.wrap())
                .map(Comment.Wrapper::getComment)
                .doOnError(throwable -> Log.e(TAG, "Error during updateComment", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>DELETE /api/v2/comments/{id}</code>
     * <p>
     * Deletes a comment on the server. This method is only necessary for comments that have an ID.
     * If the comment does not have an ID, it does not exist on the server yet and only needs to be
     * deleted locally.
     * @param comment The {@link Comment} to be deleted.
     * @return {@link Observable} containing the original {@link Comment} or null on failure.
     */
    public static Observable<Comment> deleteComment(Comment comment) {
        return apiService.deleteComment(comment.getId())
                .map(response -> (response.isSuccessful() || response.code() == 404) ? comment : null)
                .doOnError(throwable -> Log.e(TAG, "Error during deleteComment", throwable))
                .onErrorReturn(throwable -> null);
    }

    //#############################################################################################
    //                                      FORMS REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/forms</code>
     * <p>
     * Retrieves all current interaction forms.
     * @return {@link Observable} containing current {@link Form Forms} or null on failure.
     */
    public static Observable<List<Form>> getLatestForms() {
        return apiService.getLatestForms()
                .map(Form.ArrayWrapper::getForms)
                .doOnError(throwable -> Log.e(TAG, "Error during getForms", throwable))
                .onErrorReturn(throwable -> null);
    }

    /**
     * <code>GET /api/v2/forms/{id}</code>
     * <p>
     * Retrieves form matching specified form ID.
     * @param formId API ID of form to be retrieved.
     * @return {@link Observable} containing the retrieved {@link Form} or null on failure.
     */
    public static Observable<Form> getForm(long formId) {
        return apiService.getForm(formId)
                .map(Form.Wrapper::getForm)
                .doOnError(throwable -> Log.e(TAG, "Error during getForm", throwable))
                .onErrorReturn(throwable -> null);
    }

    //#############################################################################################
    //                                   NOTIFICATIONS REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/notifications</code>
     * <p>
     * Retrieves latest page of notifications for user
     * @return Latest page of {@link Notification Notifications} for the user or null on failure.
     */
    public static Observable<List<Notification>> getNotifications() {
        return apiService.getNotifications()
                .map(Notification.ArrayWrapper::getNotifications)
                .doOnError(throwable -> Log.e(TAG, "Error during getNotifications", throwable))
                .onErrorReturn(throwable -> null);
    }

    //#############################################################################################
    //                                       SYNC REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/sync</code>
     * <p>
     * Gets contact and interaction changes for favored contacts from server.
     * @param onlyMe When false, gets interactions by all team members. Defaults to false if null.
     * @param perSync The number of items to retrieve per page. Defaults to 50 if null.
     * @param syncToken Where to begin syncing. If null, will sync from beginning of time.
     * @return {@link Observable} containing the {@link SyncResponse} or null on failure.
     */
    public static Observable<SyncResponse> sync(Boolean onlyMe, Integer perSync, String syncToken) {

        // Set default params
        if (onlyMe == null) onlyMe = false;
        if (perSync == null) perSync = 50;

        return apiService.sync(onlyMe, perSync, syncToken)
                .map(response -> {
                    String status = response.headers().get(Constants.Headers.SYNC_STATUS);
                    SyncResponse syncResponse = response.body();
                    syncResponse.setStatus(status);
                    return syncResponse;
                })
                .doOnError(throwable -> Log.e(TAG, "Error during sync", throwable))
                .onErrorReturn(throwable -> null);
    }
}
