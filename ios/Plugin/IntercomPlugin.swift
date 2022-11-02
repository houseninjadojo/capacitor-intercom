import Foundation
import Capacitor
import Intercom

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(IntercomPlugin)
public class IntercomPlugin: CAPPlugin {
  public override func load() {
    let apiKey = getConfig().getString("iosApiKey") ?? "ADD_IN_CAPACITOR_CONFIG_JSON"
    let appId = getConfig().getString("iosAppId") ?? "ADD_IN_CAPACITOR_CONFIG_JSON"
    Intercom.setApiKey(apiKey, forAppId: appId)

    NotificationCenter.default.addObserver(self,
      selector: #selector(self.didRegisterWithToken(notification:)),
          name: .capacitorDidRegisterForRemoteNotifications,
        object: nil
    )

    NotificationCenter.default.addObserver(self,
      selector: #selector(self.onUnreadConversationCountChange),
          name: .IntercomUnreadConversationCountDidChange,
        object: nil
    )

    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomWindowWillShow,
        object: nil
    )
    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomWindowDidShow,
        object: nil
    )
    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomWindowWillHide,
        object: nil
    )
    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomWindowDidHide,
        object: nil
    )

    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomDidStartNewConversation,
        object: nil
    )

    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomHelpCenterWillShow,
        object: nil
    )
    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomHelpCenterDidShow,
        object: nil
    )
    NotificationCenter.default.addObserver(self,
      selector: #selector(intercomNotifier(notification:)),
          name: NSNotification.Name.IntercomHelpCenterWillHide,
        object: nil
    )
    NotificationCenter.default.addObserver(self,
      selector: #selector(self.intercomNotifier(notification:)),
          name: .IntercomHelpCenterDidHide,
        object: nil
    )
  }

  @objc func didRegisterWithToken(notification: NSNotification) {
    guard let deviceToken = notification.object as? Data else {
      return
    }
    Intercom.setDeviceToken(deviceToken) { error in
      guard let error = error else { return }
      print("Error setting device token: \(error.localizedDescription)")
    }
  }

  @objc func onUnreadConversationCountChange() {
    DispatchQueue.global(qos: .default).async {
      let unreadCount = Intercom.unreadConversationCount()
      self.notifyListeners("onUnreadCountChange", data: ["value":unreadCount])
    }
  }

  @objc func intercomNotifier(notification: NSNotification) {
    let name: String
    switch notification.name {
    case .IntercomWindowWillShow:
      name = "windowWillShow"
    case .IntercomWindowDidShow:
      name = "windowDidShow"
    case .IntercomWindowWillHide:
      name = "windowWillHide"
    case .IntercomWindowDidHide:
      name = "windowDidHide"
    case .IntercomDidStartNewConversation:
      name = "didStartNewConversation"
    case .IntercomHelpCenterWillShow:
      name = "helpCenterWillShow"
    case .IntercomHelpCenterDidShow:
      name = "helpCenterDidShow"
    case .IntercomHelpCenterWillHide:
      name = "helpCenterWillHide"
    case .IntercomHelpCenterDidHide:
      name = "helpCenterDidHide"
    default:
      return
    }
    DispatchQueue.global(qos: .default).async {
      self.notifyListeners(name, data: nil)
    }
  }

  @objc func boot(_ call: CAPPluginCall) {
    call.unimplemented("Not implemented on iOS. Use `registerIdentifiedUser` instead.")
  }

  @available(*, deprecated, message: "Use `loginUser` instead")
  @objc func registerIdentifiedUser(_ call: CAPPluginCall) {
    self.loginUser(call)
  }

  @objc func loginUser(_ call: CAPPluginCall) {
    let attributes = ICMUserAttributes()
    if let userId = call.getString("userId") {
      attributes.userId = userId
    }
    if let email = call.getString("email") {
      attributes.email = email
    }
    Intercom.loginUser(with: attributes) { result in
      switch result {
      case .success:
        call.resolve()
      case .failure(let error):
        call.reject(error.localizedDescription, String((error as NSError).code), error)
      }
    }
  }

  @objc func loginUnidentifiedUser(_ call: CAPPluginCall) {
    Intercom.loginUnidentifiedUser() { result in
      switch result {
      case .success:
        call.resolve()
      case .failure(let error):
        call.reject(error.localizedDescription, String((error as NSError).code), error)
      }
    }
  }

  @available(*, deprecated, message: "Use `loginUnidentifiedUser` instead")
  @objc func registerUnidentifiedUser(_ call: CAPPluginCall) {
    self.loginUnidentifiedUser(call)
  }

  @objc func updateUser(_ call: CAPPluginCall) {
    let attributes = ICMUserAttributes()
    if let userId = call.getString("userId") {
      attributes.userId = userId
    }
    if let email = call.getString("email") {
      attributes.email = email
    }
    if let name = call.getString("name") {
      attributes.name = name
    }
    if let phone = call.getString("phone") {
      attributes.phone = phone
    }
    if let languageOverride = call.getString("languageOverride") {
      attributes.languageOverride = languageOverride
    }
    if let customAttributes = call.getObject("customAttributes") {
      attributes.customAttributes = customAttributes
    }
    Intercom.updateUser(with: attributes) { result in
      switch result {
      case .success:
        call.resolve()
      case .failure(let error):
        call.reject(error.localizedDescription, String((error as NSError).code), error)
      }
    }
  }

  @objc func logout(_ call: CAPPluginCall) {
    Intercom.logout()
    call.resolve()
  }

  @objc func logEvent(_ call: CAPPluginCall) {
    if let eventName = call.getString("name") {
      if let metaData = call.getObject("data") {
        Intercom.logEvent(withName: eventName, metaData: metaData)
      } else {
        Intercom.logEvent(withName: eventName)
      }
      call.resolve()
    }
  }

  @objc func displayMessenger(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.presentMessenger()
      call.resolve()
    }
  }

  @objc func displayMessageComposer(_ call: CAPPluginCall) {
    if let initialMessage = call.getString("message") {
      Intercom.presentMessageComposer(initialMessage);
      call.resolve()
    } else {
      call.reject("Enter an initial message")
    }
  }

  @objc func hideMessenger(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.hide()
      call.resolve()
    }
  }

  @objc func displayHelpCenter(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.presentHelpCenter()
      call.resolve()
    }
  }

  @objc func displayArticle(_ call: CAPPluginCall) {
    if let articleId = call.getString("articleId") {
      DispatchQueue.main.async {
        Intercom.presentArticle(articleId)
        call.resolve()
      }
    } else {
      call.reject("articleId not provided to presentArticle.")
    }
  }

  @objc func displayLauncher(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.setLauncherVisible(true)
      call.resolve()
    }
  }

  @objc func hideLauncher(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.setLauncherVisible(false)
      call.resolve()
    }
  }

  @objc func displayInAppMessages(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.setInAppMessagesVisible(true)
      call.resolve()
    }
  }

  @objc func hideInAppMessages(_ call: CAPPluginCall) {
    DispatchQueue.main.async {
      Intercom.setInAppMessagesVisible(false)
      call.resolve()
    }
  }

  @objc func displayCarousel(_ call: CAPPluginCall) {
    if let carouselId = call.getString("carouselId") {
      DispatchQueue.main.async {
        Intercom.presentCarousel(carouselId)
        call.resolve()
      }
    } else {
      call.reject("carouselId not provided to displayCarousel.")
    }
  }

  @objc func setUserHash(_ call: CAPPluginCall) {
    if let userHash = call.getString("hmac") {
      Intercom.setUserHash(userHash)
      call.resolve()
    } else {
      call.reject("No hmac found. Read intercom docs and generate it.")
    }
  }

  @objc func setBottomPadding(_ call: CAPPluginCall) {
    if let value = call.getString("value"),
       let number = NumberFormatter().number(from: value) {
        Intercom.setBottomPadding(CGFloat(truncating: number))
        call.resolve()
      } else {
        call.reject("Enter a value for padding bottom")
      }
  }

  @objc func unreadConversationCount(_ call: CAPPluginCall) {
    let value = Intercom.unreadConversationCount()
    call.resolve([
      "value": value
    ])
  }
}
