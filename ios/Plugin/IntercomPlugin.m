#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(IntercomPlugin, "Intercom",
          CAP_PLUGIN_METHOD(boot, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(registerIdentifiedUser, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(loginUser, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(loginUnidentifiedUser, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(registerUnidentifiedUser, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(updateUser, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(logout, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(logEvent, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayMessenger, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(show, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayInbox, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayMessageComposer, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(hideMessenger, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(hide, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayHelpCenter, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayArticle, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayLauncher, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(enableLauncher, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(hideLauncher, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(disableLauncher, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayInAppMessages, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(enableMessengerPopups, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(hideInAppMessages, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(disableMessengerPopups, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(displayCarousel, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(setUserHash, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(setBottomPadding, CAPPluginReturnNone);
          CAP_PLUGIN_METHOD(unreadConversationCount, CAPPluginReturnPromise);
          CAP_PLUGIN_METHOD(removeAllListeners, CAPPluginReturnPromise);
          CAP_PLUGIN_METHOD(removeListener, CAPPluginReturnNone);
)
