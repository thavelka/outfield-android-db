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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Provides methods for communicating with Outfield REST API asynchronously.
 * A {@link ResponseCallback} is provided as a parameter to any request method to capture the
 * response data and return it to the calling thread.
 *
 * @see {@link ResponseCallback}
 */
public final class OutfieldAPI {

    public static final String TAG = OutfieldAPI.class.getSimpleName();

    private ApiService apiService = ApiService.Builder.createService();
    private static OutfieldAPI instance = new OutfieldAPI();
    private OutfieldAPI() {}

    public static OutfieldAPI getInstance() {
        return instance;
    }

    public void setAuthHeaders(String email, String token) {
        apiService = ApiService.Builder.createService(email, token);
    }

    //#############################################################################################
    //                                      USER REQUESTS
    //#############################################################################################

    /**
     * Callback used to capture response data.
     * <p>
     * <code>success</code> will be false if the parameters are invalid or if the HTTP response code
     * is not between 200 and 300.
     * @param <T> The type of object to be returned from the API response.
     */
    public interface ResponseCallback<T> {
        public void onResponse(boolean success, T object);
    }

    /**
     * <code>POST /api/v2/sign_in</code>
     * <p>
     * Sends login request to server, which responds with a user object containg the user's info.
     * @param email The user's email address.
     * @param password The user's password.
     * @param callback Callback to receive boolean success value and user object.
     */
    public void signIn(String email, String password, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = apiService.signIn(email, password);
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during signIn", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/me/new</code>
     * <p>
     * Checks to see if account already exists for this email address. If so, user info is returned.
     * If user doesn't exist, <code>success</code> will be false.
     * @param email The user's email address.
     * @param callback Callback to receive boolean success value and user object.
     */
    public void getAccountExists(String email, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = apiService.getAccountExists(email);
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getAccountExists", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/me</code>
     * <p>
     * Retrieves detailed information about user.
     * @param callback Callback to receive boolean success value and user object.
     */
    public void getUserDetails(final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = apiService.getUserDetails();
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code() + "\nMessage: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getUserDetails", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>PUT /api/v2/me</code>
     * <p>
     * Updates user's information.
     * @param user The user object to be sent to the server.
     * @param callback Callback to receive boolean success value and user object.
     */
    public void updateUser(User user, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = apiService.updateUser(user.wrap());
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during updateUser", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/sign_up</code>
     * <p>
     * Creates a new organization and user on the server.
     * @param email The user's email address.
     * @param name The user's full name.
     * @param password The user's password.
     * @param orgName Name of the organization to be created.
     * @param callback Callback to receive boolean success value and user object.
     */
    public void signUp(String email, String name, String password,
                              String orgName, final ResponseCallback<User> callback) {
        Call<User.Wrapper> call = apiService
                .signUp(email, name, password, password, orgName);
        call.enqueue(new Callback<User.Wrapper>() {
            @Override
            public void onResponse(Call<User.Wrapper> call, Response<User.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getUser());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<User.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during signUp", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/password_reset</code>
     * <p>
     * Sends email to specified address with a link to reset password.
     * @param email The user's email address.
     * @param callback Callback to receive boolean success value.
     */
    public void resetPassword(String email, final ResponseCallback<Void> callback) {
        Call<Void> call = apiService.resetPassword(email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, null);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error during resetPassword", t);
                callback.onResponse(false, null);
            }
        });
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
     * @param callback Callback to receive boolean success value and response.
     */
    public void getPeople(Integer page, Integer perPage, Boolean global, String search,
                                 final ResponseCallback<ContactsResponse> callback) {
        // Set default parameter values
        if (page == null) page = 1;
        if (perPage == null) perPage = 25;
        if (global == null) global = false;

        Call<ContactsResponse> call = apiService
                .getPeople(Contact.Type.PERSON.toString(), search, perPage, page, global);
        call.enqueue(new Callback<ContactsResponse>() {
            @Override
            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ContactsResponse> call, Throwable t) {
                Log.e(TAG, "Error during getPeople", t);
                callback.onResponse(false, null);
            }
        });
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
     * @param callback Callback to receive boolean success value and response.
     */
    public void getPlaces(Integer page, Integer perPage, Boolean global, String search,
                                 Double latitude, Double longitude, Integer radius,
                                 final ResponseCallback<ContactsResponse> callback) {

        // Set default parameter values
        if (page == null) page = 1;
        if (perPage == null) perPage = 25;
        if (global == null) global = false;

        String location = null;
        if (latitude != null && longitude != null) {
            location = latitude + "," + longitude;
        }

        Call<ContactsResponse> call = apiService
                .getPlaces(Contact.Type.PLACE.toString(), search, perPage, page,
                        global, location, radius);
        call.enqueue(new Callback<ContactsResponse>() {
            @Override
            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ContactsResponse> call, Throwable t) {
                Log.e(TAG, "Error during getPeople", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/contacts/explore</code>
     * <p>
     * Find all organization contacts and Foursquare venues near the provided coordinates.
     * @param latitude Latitude as a double.
     * @param longitude Longitude as a double.
     * @param search Search for something nearby (e.g. “GNC”). (optional)
     * @param callback Callback to receive boolean success value and contacts array.
     */
    public void explore(Double latitude, Double longitude, String search,
                               final ResponseCallback<List<Contact>> callback) {

        String location = null;
        if (latitude != null && longitude != null) {
            location = latitude + "," + longitude;
        }
        Call<ContactsResponse> call = apiService
                .explore(location, search);
        call.enqueue(new Callback<ContactsResponse>() {
            @Override
            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContacts());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ContactsResponse> call, Throwable t) {
                Log.e(TAG, "Error during explore", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/contacts/{id}></code>
     * <p>
     * Attempts to retrieve contact with provided id.
     * @param contactId API ID of the contact to be retrieved.
     * @param callback Callback to received boolean success value and retrieved contact.
     */
    public void getContact(long contactId, final ResponseCallback<Contact> callback) {

        if (contactId <= 0) {
            Log.e(TAG, "Contact ID must be greater than 0.");
            callback.onResponse(false, null);
            return;
        }

        Call<Contact.Wrapper> call = apiService.getContact(contactId);
        call.enqueue(new Callback<Contact.Wrapper>() {
            @Override
            public void onResponse(Call<Contact.Wrapper> call, Response<Contact.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContact());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Contact.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getContact", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/my_contacts</code>
     * <p>
     * Add an organization's contact to the current user's “my contacts” list.
     * @param contactId API ID of the contact to be favored.
     * @param callback Callback to receive boolean success value and favored contact.
     */
    public void favorContact(long contactId, final ResponseCallback<Contact> callback) {

        if (contactId <= 0) {
            Log.e(TAG, "Contact ID must be greater than 0.");
            callback.onResponse(false, null);
            return;
        }

        Call<Contact.Wrapper> call = apiService.favorContact(contactId);
        call.enqueue(new Callback<Contact.Wrapper>() {
            @Override
            public void onResponse(Call<Contact.Wrapper> call, Response<Contact.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContact());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Contact.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during favorContact", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/my_contacts</code>
     * <p>
     * Add an organization's contact to the current user's “my contacts” list,
     * while simultaneously updating the contact.
     * @param contact Contact object to be favored and updated.
     * @param callback Callback to receive boolean success value and updated contact.
     */
    public void updateAndFavorContact(Contact contact, final ResponseCallback<Contact> callback) {

        if (contact.getId() <= 0) {
            Log.e(TAG, "Contact ID must be greater than 0.");
            callback.onResponse(false, null);
            return;
        }

        Call<Contact.Wrapper> call = apiService.updateAndFavorContact(contact.wrap());
        call.enqueue(new Callback<Contact.Wrapper>() {
            @Override
            public void onResponse(Call<Contact.Wrapper> call, Response<Contact.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContact());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Contact.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during updateAndFavorContact", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>DELETE /api/v2/my_contacts/{id}</code>
     * <p>
     * Remove a contact from the current user's “my contacts” list, keep the contact in the
     * organization.
     * @param contactId API ID of the contact to be unfavored.
     * @param callback Callback to receive boolean success value.
     */
    public void unfavorContact(long contactId, final ResponseCallback<Void> callback) {

        if (contactId <= 0) {
            Log.e(TAG, "Contact ID must be greater than 0.");
            callback.onResponse(false, null);
            return;
        }

        Call<Void> call = apiService.unfavorContact(contactId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, null);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error during unfavorContact", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/contacts</code>
     * <p>
     * Creates a contact on the server with the properties of the attached contact.
     * This method should not be used if the contact already has an ID. If the contact has an ID,
     * it already exists on the server and should be updated using {@link #updateContact}.
     * @param contact The contact to be uploaded.
     * @param callback Callback to receive the boolean success value and returned new contact.
     */
    public void createContact(Contact contact, final ResponseCallback<Contact> callback) {

        if (contact.getId() >= 0) {
            Log.e(TAG, "Contact already exists on server.");
            callback.onResponse(false, null);
            return;
        }

        if (contact.getContactType() == null) {
            Log.e(TAG, "Contact must have type.");
            callback.onResponse(false, null);
            return;
        }

        Call<Contact.Wrapper> call = apiService.createContact(contact.wrap());
        call.enqueue(new Callback<Contact.Wrapper>() {
            @Override
            public void onResponse(Call<Contact.Wrapper> call, Response<Contact.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContact());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Contact.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during createContact", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>PUT /api/v2/contacts/{id}</code>
     * <p>
     * Updates an existing contact on the server with the properties of the attached contact.
     * This method should only be used for contacts that already have an ID. If the contact does
     * not have an ID, it doesn't exist on the server yet, and needs to be created using
     * {@link #createContact}.
     * @param contact The contact to be uploaded.
     * @param callback Callback to receive the boolean success value and returned new contact.
     */
    public void updateContact(Contact contact, final ResponseCallback<Contact> callback) {

        if (contact.getId() <= 0) {
            Log.e(TAG, "Contact does not exist on server.");
            callback.onResponse(false, null);
            return;
        }

        if (contact.getContactType() == null) {
            Log.e(TAG, "Contact must have type.");
            callback.onResponse(false, null);
            return;
        }

        Call<Contact.Wrapper> call = apiService
                .updateContact(contact.getId(), contact.wrap());
        call.enqueue(new Callback<Contact.Wrapper>() {
            @Override
            public void onResponse(Call<Contact.Wrapper> call, Response<Contact.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getContact());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Contact.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during updateContact", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>DELETE /api/v2/contacts/{id}</code>
     * <p>
     * Deletes an existing contact from the server. This method is only necessary for
     * contacts that already have an ID. If the contact does not have an ID, it doesn't
     * exist on the server yet and only needs to be deleted locally.
     * @param contactId API ID of the contact to be deleted.
     * @param callback Callback to receive boolean success value.
     */
    public void deleteContact(long contactId, final ResponseCallback<Void> callback) {

        if (contactId <= 0) {
            Log.e(TAG, "Contact does not exist on server.");
            callback.onResponse(true, null);
            return;
        }

        Call<Void> call = apiService.deleteContact(contactId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, null);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error during deleteContact", t);
                callback.onResponse(false, null);
            }
        });
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
     * @param callback Callback to receive boolean success value and response.
     */
    public void getInteractions(boolean onlyMe, Integer page, Integer perPage,
                                           Interaction.Type interactionType, String search,
                                           final ResponseCallback<InteractionsResponse> callback) {

        // Set default params
        if (page == null) page = 1;
        if (perPage == null) perPage = 25;
        String type = (interactionType != null) ? interactionType.toString() : null;

        Call<InteractionsResponse> call = apiService
                .getInteractions(onlyMe, page, perPage, type, search);
        call.enqueue(new Callback<InteractionsResponse>() {
            @Override
            public void onResponse(Call<InteractionsResponse> call, Response<InteractionsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<InteractionsResponse> call, Throwable t) {
                Log.e(TAG, "Error during getInteractions", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/interactions/{id}</code>
     * <p>
     * Attempts to retrieve interaction with provided ID.
     * @param interactionId API ID of interaction to be retrieved.
     * @param callback Callback to receive boolean success value and retrieved contact.
     */
    public void getInteraction(long interactionId, final ResponseCallback<Interaction> callback) {

        if (interactionId <= 0) {
            Log.e(TAG, "Interaction does not exist on server.");
            callback.onResponse(false, null);
            return;
        }

        Call<Interaction.Wrapper> call = apiService.getInteraction(interactionId);
        call.enqueue(new Callback<Interaction.Wrapper>() {
            @Override
            public void onResponse(Call<Interaction.Wrapper> call, Response<Interaction.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getInteraction());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Interaction.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getInteraction", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>POST /api/v2/interactions</code>
     * <p>
     * Creates an interaction on the server with the properties of the attached interaction.
     * This method should not be used if the interaction already has an ID. If the interaction has
     * an ID, it already exists on the server and should be updated using
     * {@link #updateInteraction}.
     * @param interaction The interaction to be uploaded.
     * @param callback Callback to receive boolean success value and created interaction.
     */
    public void createInteraction(Interaction interaction, final ResponseCallback<Interaction> callback) {

        if (interaction.getId() > 0) {
            Log.e(TAG, "Interaction already exists on server");
            callback.onResponse(false, null);
            return;
        }

        if (interaction.getInteractionType() == null) {
            Log.e(TAG, "Interaction must have type");
            callback.onResponse(false, null);
            return;
        }

        Call<Interaction.Wrapper> call = apiService.createInteraction(interaction.wrap());
        call.enqueue(new Callback<Interaction.Wrapper>() {
            @Override
            public void onResponse(Call<Interaction.Wrapper> call, Response<Interaction.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getInteraction());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Interaction.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during createInteraction", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>PUT /api/v2/interactions/{id}</code>
     * <p>
     * Updates an existing interaction on the server with the properties of the attached
     * interaction. This method should only be used for interactions that already have an ID.
     * If the interaction does not have an ID, it doesn't exist on the server yet, and needs to be
     * created using {@link #createInteraction}.
     * @param interaction The interaction to be updated.
     * @param callback Callback to receive boolean success value and updated interaction.
     */
    public void updateInteraction(Interaction interaction, final ResponseCallback<Interaction> callback) {

        if (interaction.getId() <= 0) {
            Log.e(TAG, "Interaction does not exist on server");
            callback.onResponse(false, null);
            return;
        }

        if (interaction.getInteractionType() == null) {
            Log.e(TAG, "Interaction must have type");
            callback.onResponse(false, null);
            return;
        }

        Call<Interaction.Wrapper> call = apiService
                .updateInteraction(interaction.getId(), interaction.wrap());
        call.enqueue(new Callback<Interaction.Wrapper>() {
            @Override
            public void onResponse(Call<Interaction.Wrapper> call, Response<Interaction.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getInteraction());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Interaction.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during updateInteraction", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>DELETE /api/v2/interactions/{id}</code>
     * <p>
     * Deletes an existing interaction from the server. This method is only necessary for
     * interactions that already have an ID. If the interaction does not have an ID, it doesn't
     * exist on the server yet and only needs to be deleted locally.
     * @param interactionId API ID of the interaction to be deleted.
     * @param callback Callback to receive boolean success value.
     */
    public void deleteInteraction(long interactionId, final ResponseCallback<Void> callback) {

        if (interactionId <= 0) {
            Log.e(TAG, "Interaction does not exist on server.");
            callback.onResponse(true, null);
            return;
        }

        Call<Void> call = apiService.deleteInteraction(interactionId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, null);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error during deleteInteraction", t);
                callback.onResponse(false, null);
            }
        });
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
     * @param comment The comment to be uploaded.
     * @param callback Callback to receive boolean success value and created comment.
     */
    public void createComment(Comment comment, final ResponseCallback<Comment> callback) {

        if (comment.getId() > 0) {
            Log.e(TAG, "Comment already exists on server.");
            callback.onResponse(false, null);
            return;
        }

        if (comment.getInteractionId() <= 0) {
            Log.e(TAG, "Comment has no parent interaction on server.");
            callback.onResponse(false, null);
            return;
        }

        Call<Comment.Wrapper> call = apiService
                .createComment(comment.getInteractionId(), comment.wrap());
        call.enqueue(new Callback<Comment.Wrapper>() {
            @Override
            public void onResponse(Call<Comment.Wrapper> call, Response<Comment.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getComment());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Comment.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during createComment", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>PUT /api/v2/comments/{id}</code>
     * <p>
     * Updates a comment on the server. This method should only be used on comments that have an ID.
     * If the comment does not have an ID, it does not exist on the server yet and should be created
     * using {@link #createComment}.
     * @param comment The comment to be updated.
     * @param callback Callback to receive boolean success value and updated comment.
     */
    public void updateComment(Comment comment, final ResponseCallback<Comment> callback) {

        if (comment.getId() <= 0) {
            Log.e(TAG, "Comment does not exist on server.");
            callback.onResponse(false, null);
        }

        Call<Comment.Wrapper> call = apiService
                .updateComment(comment.getId(), comment.wrap());
        call.enqueue(new Callback<Comment.Wrapper>() {
            @Override
            public void onResponse(Call<Comment.Wrapper> call, Response<Comment.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getComment());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Comment.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during updateComment", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>DELETE /api/v2/comments/{id}</code>
     * <p>
     * Deletes a comment on the server. This method is only necessary for comments that have an ID.
     * If the comment does not have an ID, it does not exist on the server yet and only needs to be
     * deleted locally.
     * @param commentId API ID of the comment to be deleted.
     * @param callback Callback to receive boolean success value.
     */
    public void deleteComment(long commentId, final ResponseCallback<Void> callback) {

        if (commentId <= 0) {
            Log.e(TAG, "Comment does not exist on server.");
            callback.onResponse(true, null);
        }

        Call<Void> call = apiService.deleteComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, null);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error during deleteComment", t);
                callback.onResponse(false, null);
            }
        });
    }

    //#############################################################################################
    //                                      FORMS REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/forms</code>
     * <p>
     * Retrieves all current interaction forms.
     * @param callback Callback to receive boolean success value and array of forms
     */
    public void getLatestForms(final ResponseCallback<List<Form>> callback) {
        Call<Form.ArrayWrapper> call = apiService.getLatestForms();
        call.enqueue(new Callback<Form.ArrayWrapper>() {
            @Override
            public void onResponse(Call<Form.ArrayWrapper> call, Response<Form.ArrayWrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getForms());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Form.ArrayWrapper> call, Throwable t) {
                Log.e(TAG, "Error during getLatestForms", t);
                callback.onResponse(false, null);
            }
        });
    }

    /**
     * <code>GET /api/v2/forms/{id}</code>
     * <p>
     * Retrieves form matching specified form ID.
     * @param formId API ID of form to be retrieved.
     * @param callback Callback to receive boolean success value and retrieved form.
     */
    public void getForm(long formId, final ResponseCallback<Form> callback) {

        if (formId <= 0) {
            Log.e(TAG, "Form does not exist on server");
            callback.onResponse(false, null);
            return;
        }

        Call<Form.Wrapper> call = apiService.getForm(formId);
        call.enqueue(new Callback<Form.Wrapper>() {
            @Override
            public void onResponse(Call<Form.Wrapper> call, Response<Form.Wrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getForm());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Form.Wrapper> call, Throwable t) {
                Log.e(TAG, "Error during getForm", t);
                callback.onResponse(false, null);
            }
        });
    }

    //#############################################################################################
    //                                   NOTIFICATIONS REQUESTS
    //#############################################################################################

    /**
     * <code>GET /api/v2/notifications</code>
     * <p>
     * Retrieves latest page of notifications for user
     * @param callback Callback to receive boolean success value and array of notifications.
     */
    public void getNotifications(final ResponseCallback<List<Notification>> callback) {
        Call<Notification.ArrayWrapper> call = apiService.getNotifications();
        call.enqueue(new Callback<Notification.ArrayWrapper>() {
            @Override
            public void onResponse(Call<Notification.ArrayWrapper> call, Response<Notification.ArrayWrapper> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(true, response.body().getNotifications());
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Notification.ArrayWrapper> call, Throwable t) {
                Log.e(TAG, "Error during getNotifications", t);
                callback.onResponse(false, null);
            }
        });
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
     * @param callback Callback to receive boolean success value and {@link SyncResponse}.
     */
    public void sync(Boolean onlyMe, Integer perSync, String syncToken,
                            final ResponseCallback<SyncResponse> callback) {

        // Set default params
        if (onlyMe == null) onlyMe = false;
        if (perSync == null) perSync = 50;

        Call<SyncResponse> call = apiService.sync(onlyMe, perSync, syncToken);
        call.enqueue(new Callback<SyncResponse>() {
            @Override
            public void onResponse(Call<SyncResponse> call, Response<SyncResponse> response) {
                if (response.isSuccessful()) {
                    String status = response.headers().get(Constants.Headers.SYNC_STATUS);
                    SyncResponse syncResponse = response.body();
                    syncResponse.setStatus(status);
                    callback.onResponse(true, syncResponse);
                } else {
                    onFailure(call, new Exception("Status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<SyncResponse> call, Throwable t) {
                Log.e(TAG, "Error during sync", t);
                callback.onResponse(false, null);
            }
        });
    }
}
