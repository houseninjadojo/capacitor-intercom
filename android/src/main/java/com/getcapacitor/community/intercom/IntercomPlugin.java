package com.getcapacitor.community.intercom;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.getcapacitor.CapConfig;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.intercom.android.sdk.*;
import io.intercom.android.sdk.push.*;
import io.intercom.android.sdk.identity.*;

@CapacitorPlugin(name = "Intercom", permissions = @Permission(strings = {}, alias = "receive"))
public class IntercomPlugin extends Plugin {
    private final IntercomPushClient intercomPushClient = new IntercomPushClient();

    @Override
    public void load() {
        // Set up Intercom
        setUpIntercom();

        // load parent
        super.load();
    }

    @Override
    public void handleOnStart() {
        super.handleOnStart();
        bridge.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //We also initialize intercom here just in case it has died. If Intercom is already set up, this won't do anything.
                setUpIntercom();
                Intercom.client().handlePushMessage();
                Intercom.client().addUnreadConversationCountListener(
                        i -> {
                            JSObject ret = new JSObject();
                            ret.put("value", i);
                            notifyListeners("onUnreadCountChange", ret);
                        }
                );
            }
        });
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void boot(PluginCall call) {
        call.unimplemented("Not implemented on Android. Use `registerIdentifiedUser` instead.");
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void loginUser(PluginCall call) throws JSONException {
        String email = call.getString("email");
        String userId = call.getData().get("userId").toString();

        Registration registration = new Registration();

        if (email != null && email.length() > 0) {
            registration = registration.withEmail(email);
        }
        if (userId != null && userId.length() > 0) {
            registration = registration.withUserId(userId);
        }
        Intercom.client().loginIdentifiedUser(
                registration,
                new IntercomStatusCallback() {
                    @Override
                    public void onSuccess() {
                        call.resolve();
                    }
                    @Override
                    public void onFailure(@NonNull IntercomError intercomError) {
                        call.reject(intercomError.toString());
                    }
                }
        );
    }

    /**
     * @deprecated use {@link IntercomPlugin#loginUser(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void registeredIdentifiedUser(PluginCall call) throws JSONException {
        this.loginUser(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void loginUnidentifiedUser(PluginCall call) {
        Intercom.client().loginUnidentifiedUser(
                new IntercomStatusCallback() {
                    @Override
                    public void onSuccess() {
                        call.resolve();
                    }
                    @Override
                    public void onFailure(@NonNull IntercomError intercomError) {
                        call.reject(intercomError.toString());
                    }
                }
        );
    }

    /**
     * @deprecated use {@link IntercomPlugin#loginUnidentifiedUser(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void registerUnidentifiedUser(PluginCall call) {
        this.loginUnidentifiedUser(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void updateUser(PluginCall call) {
        UserAttributes.Builder builder = new UserAttributes.Builder();
        String userId = call.getString("userId");
        if (userId != null && userId.length() > 0) {
            builder.withUserId(userId);
        }
        String email = call.getString("email");
        if (email != null && email.length() > 0) {
            builder.withEmail(email);
        }
        String name = call.getString("name");
        if (name != null && name.length() > 0) {
            builder.withName(name);
        }
        String phone = call.getString("phone");
        if (phone != null && phone.length() > 0) {
            builder.withPhone(phone);
        }
        String languageOverride = call.getString("languageOverride");
        if (languageOverride != null && languageOverride.length() > 0) {
            builder.withLanguageOverride(languageOverride);
        }
        Map<String, Object> customAttributes = mapFromJSON(call.getObject("customAttributes"));
        builder.withCustomAttributes(customAttributes);
        Intercom.client().updateUser(
                builder.build(),
                new IntercomStatusCallback() {
                    @Override
                    public void onSuccess() {
                        call.resolve();
                    }
                    @Override
                    public void onFailure(@NonNull IntercomError intercomError) {
                        call.reject(intercomError.toString());
                    }
                }
        );
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void logout(PluginCall call) {
        Intercom.client().logout();
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void logEvent(PluginCall call) {
        String eventName = call.getString("name");
        Map<String, Object> metaData = mapFromJSON(call.getObject("data"));
        if (metaData == null) {
            Intercom.client().logEvent(eventName);
        } else {
            Intercom.client().logEvent(eventName, metaData);
        }
        call.resolve();
    }

    /**
     * @deprecated use {@link IntercomPlugin#show(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void displayMessenger(PluginCall call) {
        this.show(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void show(PluginCall call) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Intercom.client().present(IntercomSpace.Home);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void displayInbox(PluginCall call) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Intercom.client().present(IntercomSpace.Messages);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void displayMessageComposer(PluginCall call) {
        String message = call.getString("message");
        Intercom.client().displayMessageComposer(message);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void displayHelpCenter(PluginCall call) {
        Intercom.client().present(IntercomSpace.HelpCenter);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void displayArticle(PluginCall call) {
        String articleId = call.getString("articleId");
        IntercomContent.Article content = new IntercomContent.Article(articleId);
        Intercom.client().presentContent(content);
        call.resolve();
    }

    /**
     * @deprecated use {@link IntercomPlugin#hide(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void hideMessenger(PluginCall call) {
        this.hide(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void hide(PluginCall call) {
        Intercom.client().hideIntercom();
        call.resolve();
    }

    /**
     * @deprecated use {@link IntercomPlugin#enableLauncher(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void displayLauncher(PluginCall call) {
        this.enableLauncher(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void enableLauncher(PluginCall call) {
        Intercom.client().setLauncherVisibility(Intercom.VISIBLE);
        call.resolve();
    }

    /**
     * @deprecated use {@link IntercomPlugin#disableLauncher(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void hideLauncher(PluginCall call) {
        this.disableLauncher(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void disableLauncher(PluginCall call) {
        Intercom.client().setLauncherVisibility(Intercom.GONE);
        call.resolve();
    }

    /**
     * @deprecated use {@link IntercomPlugin#enableMessengerPopups(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void displayInAppMessages(PluginCall call) {
        this.enableMessengerPopups(call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void enableMessengerPopups(PluginCall call) {
        Intercom.client().setInAppMessageVisibility(Intercom.VISIBLE);
        call.resolve();
    }

    /**
     * @deprecated use {@link IntercomPlugin#disableMessengerPopups(PluginCall)}
     */
    @Deprecated(forRemoval = true)
    public void hideInAppMessages(PluginCall call) {
        Intercom.client().setLauncherVisibility(Intercom.GONE);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void disableMessengerPopups(PluginCall call) {
        Intercom.client().setLauncherVisibility(Intercom.GONE);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void displayCarousel(PluginCall call) {
        String carouselId = call.getString("carouselId");
        IntercomContent.Carousel content = new IntercomContent.Carousel(carouselId);
        Intercom.client().presentContent(content);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void setUserHash(PluginCall call) {
        String hmac = call.getString("hmac");
        Intercom.client().setUserHash(hmac);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void setBottomPadding(PluginCall call) {
        String stringValue = call.getString("value");
        int value = Integer.parseInt(stringValue);
        Intercom.client().setBottomPadding(value);
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void sendPushTokenToIntercom(PluginCall call) {
        String token = call.getString("value");
        try {
            intercomPushClient.sendTokenToIntercom(this.getActivity().getApplication(), token);
            call.resolve();
        } catch (Exception e) {
            call.reject("Failed to send push token to Intercom", e);
        }
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void receivePush(PluginCall call) {
        try {
            JSObject notificationData = call.getData();
            Map message = mapFromJSON(notificationData);
            if (intercomPushClient.isIntercomPush(message)) {
                intercomPushClient.handlePush(this.getActivity().getApplication(), message);
                call.resolve();
            } else {
                call.reject("Notification data was not a valid Intercom push message");
            }
        } catch (Exception e) {
            call.reject("Failed to handle received Intercom push", e);
        }
    }

    @PluginMethod
    public void getUnreadConversationCount(PluginCall call) {
        int val = Intercom.client().getUnreadConversationCount();
        String stringValue = String.valueOf(val);
        JSObject ret = new JSObject();
        ret.put("value", stringValue);
        call.resolve(ret);
    }

    private void setUpIntercom() {
        try {
            // get config
            CapConfig config = this.bridge.getConfig();
            String apiKey = config.getPluginConfiguration("Intercom").getString("androidApiKey");
            String appId = config.getPluginConfiguration("Intercom").getString("androidAppId");

            // init intercom sdk
            Intercom.initialize(this.getActivity().getApplication(), apiKey, appId);
        } catch (Exception e) {
            Logger.error("Intercom", "ERROR: Something went wrong when initializing Intercom. Check your configurations", e);
        }
    }

    private static Map<String, Object> mapFromJSON(JSObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keysIter = jsonObject.keys();
        while (keysIter.hasNext()) {
            String key = keysIter.next();
            Object value = getObject(jsonObject.opt(key));
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    private static Object getObject(Object value) {
        if (value instanceof JSObject) {
            value = mapFromJSON((JSObject) value);
        } else if (value instanceof JSArray) {
            value = listFromJSON((JSArray) value);
        }
        return value;
    }

    private static List<Object> listFromJSON(JSArray jsonArray) {
        List<Object> list = new ArrayList<>();
        for (int i = 0, count = jsonArray.length(); i < count; i++) {
            Object value = getObject(jsonArray.opt(i));
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }
}
