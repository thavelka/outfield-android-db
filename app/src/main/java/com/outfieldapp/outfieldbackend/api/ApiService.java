package com.outfieldapp.outfieldbackend.api;

import com.outfieldapp.outfieldbackend.api.Constants.Endpoints;
import com.outfieldapp.outfieldbackend.api.Constants.Headers;
import com.outfieldapp.outfieldbackend.api.Constants.Params;
import com.outfieldapp.outfieldbackend.api.response.ContactsResponse;
import com.outfieldapp.outfieldbackend.api.response.InteractionsResponse;
import com.outfieldapp.outfieldbackend.api.response.SyncResponse;
import com.outfieldapp.outfieldbackend.models.Comment;
import com.outfieldapp.outfieldbackend.models.Contact;
import com.outfieldapp.outfieldbackend.models.Form;
import com.outfieldapp.outfieldbackend.models.Interaction;
import com.outfieldapp.outfieldbackend.models.Notification;
import com.outfieldapp.outfieldbackend.models.User;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Provides type-safe interface for building HTTP requests.
 * Calls defined below will be accessible from an ApiService instance.
 *
 * Example call:
 * <pre>
 * {@code
 * @literal @PUT("contacts/{id}"                      // Specifies method and endpoint to use
 *     Call<Contact.Wrapper> updateContact(      // Response type and name
 *        @literal @Path("id") long id,                  // What to replace "id" in the endpoint with
 *        @literal @Body Contact.Wrapper contact         // Type of object used as the request body
 *     );
 * }
 *</pre>
 */
public interface ApiService {

    class Builder {

        public static ApiService createService() {
            return createService(null, null);
        }

        public static ApiService createService(final String email, final String token) {
            OkHttpClient.Builder httpClient =
                    new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS);

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                if (email != null && token != null) {
                    requestBuilder.header(Headers.EMAIL, email)
                            .header(Headers.AUTH_TOKEN, token);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClient.addInterceptor(loggingInterceptor);

            OkHttpClient client = httpClient.build();

            Retrofit.Builder builder =
                    new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.client(client).build();
            return retrofit.create(ApiService.class);
        }
    }

    //#############################################################################################
    //                                      USER REQUESTS
    //#############################################################################################

    @GET(Endpoints.ME)
    Observable<User.Wrapper> getUserDetails();

    @GET(Constants.Endpoints.ACCOUNT_EXISTS)
    Observable<User.Wrapper> checkAccountExists(@Query(Params.AccountExists.EMAIL) String email);

    @POST(Endpoints.SIGN_IN)
    Observable<User.Wrapper> signIn(
            @Query(Params.SignIn.EMAIL) String email,
            @Query(Params.SignIn.PASSWORD) String password);

    @POST(Endpoints.SIGN_UP)
    Observable<User.Wrapper> signUp(
            @Query(Params.SignUp.EMAIL) String email,
            @Query(Params.SignUp.NAME) String name,
            @Query(Params.SignUp.PASSWORD) String password,
            @Query(Params.SignUp.PASSWORD_CONFIRMATION) String passwordConfirmation,
            @Query(Params.SignUp.ORG_NAME) String organizationName
    );

    @PUT(Endpoints.ME)
    Observable<User.Wrapper> updateUser(@Body User.Wrapper user);

    @POST(Endpoints.PASSWORD_RESET)
    Observable<Void> resetPassword(@Query(Params.PasswordReset.EMAIL) String email);

    // TODO: Upload user image


    //#############################################################################################
    //                                    CONTACT REQUESTS
    //#############################################################################################

    @GET(Endpoints.MY_CONTACTS)
    Observable<ContactsResponse> getPlaces(
            @Query(Params.Contacts.CONTACT_TYPE) String contactType,
            @Query(Params.Contacts.SEARCH) String search,
            @Query(Params.Contacts.PER_PAGE) Integer perPage,
            @Query(Params.Contacts.PAGE) Integer page,
            @Query(Params.Contacts.GLOBAL) Boolean global,
            @Query(Params.Contacts.LOCATION) String location,
            @Query(Params.Contacts.RADIUS) Integer radius
    );

    @GET(Endpoints.CONTACTS)
    Observable<ContactsResponse> getPeople(
            @Query(Params.Contacts.CONTACT_TYPE) String contactType,
            @Query(Params.Contacts.SEARCH) String search,
            @Query(Params.Contacts.PER_PAGE) Integer perPage,
            @Query(Params.Contacts.PAGE) Integer page,
            @Query(Params.Contacts.GLOBAL) Boolean global
    );

    @GET(Endpoints.CONTACTS + "/{id}")
    Observable<Contact.Wrapper> getContact(
            @Path("id") Long id
    );

    @GET(Endpoints.EXPLORE)
    Observable<ContactsResponse> explore(
            @Query(Params.Explore.LOCATION) String location,
            @Query(Params.Explore.QUERY) String search
    );

    @POST(Endpoints.MY_CONTACTS)
    Observable<Contact.Wrapper> favorContact(
            @Query(Params.Contacts.CONTACT_ID) long contactId
    );

    @POST(Endpoints.MY_CONTACTS)
    Observable<Contact.Wrapper> updateAndFavorContact(
            @Body Contact.Wrapper contact
    );

    @POST(Endpoints.CONTACTS)
    Observable<Contact.Wrapper> createContact(
            @Body Contact.Wrapper contact
    );

    @PUT(Endpoints.CONTACTS + "/{id}")
    Observable<Contact.Wrapper> updateContact(
            @Path("id") long id,
            @Body Contact.Wrapper contact
    );

    @DELETE(Endpoints.MY_CONTACTS + "/{id}")
    Observable<Response<Void>> unfavorContact(
            @Path("id") long id
    );

    @DELETE(Endpoints.CONTACTS + "/{id}")
    Observable<Response<Void>> deleteContact(
            @Path("id") long id
    );

    // TODO: upload contact image

    //#############################################################################################
    //                                  INTERACTION REQUESTS
    //#############################################################################################

    @GET(Endpoints.INTERACTIONS)
    Observable<InteractionsResponse> getInteractions(
            @Query(Params.Interactions.ONLY_ME) boolean onlyMe,
            @Query(Params.Interactions.PAGE) Integer page,
            @Query(Params.Interactions.PER_PAGE) Integer perPage,
            @Query(Params.Interactions.INTERACTION_TYPE) String interactionType,
            @Query(Params.Interactions.SEARCH) String search
    );

    @GET(Endpoints.INTERACTIONS + "/{id}")
    Observable<Interaction.Wrapper> getInteraction(
            @Path("id") long id
    );

    @POST(Endpoints.INTERACTIONS)
    Observable<Interaction.Wrapper> createInteraction(
            @Body Interaction.Wrapper interaction
    );

    @PUT(Endpoints.INTERACTIONS + "/{id}")
    Observable<Interaction.Wrapper> updateInteraction(
            @Path("id") long id,
            @Body Interaction.Wrapper interaction
    );

    @DELETE(Endpoints.INTERACTIONS + "/{id}")
    Observable<Response<Void>> deleteInteraction(
            @Path("id") long id
    );

    // TODO: Upload interaction images

    // TODO: Analytics requests

    //#############################################################################################
    //                                     COMMENT REQUESTS
    //#############################################################################################

    @POST(Endpoints.INTERACTIONS + "/{id}/comments")
    Observable<Comment.Wrapper> createComment(
            @Path("id") long interactionId,
            @Body Comment.Wrapper comment
    );

    @PUT(Endpoints.COMMENTS + "/{id}")
    Observable<Comment.Wrapper> updateComment(
            @Path("id") long commentId,
            @Body Comment.Wrapper comment
    );

    @DELETE(Endpoints.COMMENTS + "/{id}")
    Observable<Response<Void>> deleteComment(
            @Path("id") long commentId
    );

    //#############################################################################################
    //                                      FORMS REQUESTS
    //#############################################################################################

    @GET(Endpoints.FORMS)
    Observable<Form.ArrayWrapper> getLatestForms();

    @GET(Endpoints.FORMS + "/{id}")
    Observable<Form.Wrapper> getForm(
            @Path("id") long formId
    );

    //#############################################################################################
    //                                   NOTIFICATIONS REQUESTS
    //#############################################################################################

    @GET(Endpoints.NOTIFICATIONS)
    Observable<Notification.ArrayWrapper> getNotifications();

    //#############################################################################################
    //                                       SYNC REQUESTS
    //#############################################################################################

    @GET(Endpoints.SYNC)
    Observable<Response<SyncResponse>> sync(
            @Query(Params.Sync.ONLY_ME) Boolean onlyMe,
            @Query(Params.Sync.PER_SYNC) Integer perSync,
            @Query(Params.Sync.SYNC_TOKEN) String syncToken
    );
}
