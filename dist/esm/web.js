import { WebPlugin } from '@capacitor/core';
import { objectKeysCamelToSnakeCase } from './util';
import { initialize as initializeWidget } from './widget';
export class IntercomWeb extends WebPlugin {
    async boot(options) {
        options = objectKeysCamelToSnakeCase(options);
        initializeWidget(options.app_id);
        window.Intercom('boot', options);
        this.setupListeners();
        return Promise.resolve();
    }
    /**
     * @deprecated
     */
    async registerIdentifiedUser(options) {
        return this.loginUser(options);
    }
    async loginUser(options) {
        options = objectKeysCamelToSnakeCase(options);
        window.Intercom('update', options);
        return Promise.resolve();
    }
    /**
     * @deprecated
     */
    async registerUnidentifiedUser() {
        return this.loginUnidentifiedUser();
    }
    async loginUnidentifiedUser() {
        throw this.unimplemented('Not implemented on web.');
    }
    async updateUser(options) {
        options = objectKeysCamelToSnakeCase(options);
        window.Intercom('update', options);
        return Promise.resolve();
    }
    async logout() {
        window.Intercom('shutdown');
        return Promise.resolve();
    }
    async logEvent(options) {
        window.Intercom('trackEvent', options.name, options.data);
        return Promise.resolve();
    }
    async displayMessenger() {
        window.Intercom('show');
        return Promise.resolve();
    }
    async displayMessageComposer(options) {
        window.Intercom('showNewMessage', options.message);
        return Promise.resolve();
    }
    async displayHelpCenter() {
        throw this.unimplemented('Not implemented on web.');
    }
    async displayArticle(options) {
        window.Intercom('showArticle', options.articleId);
        return Promise.resolve();
    }
    async hideMessenger() {
        window.Intercom('hide');
        return Promise.resolve();
    }
    async displayLauncher() {
        throw this.unimplemented('Not implemented on web.');
    }
    async hideLauncher() {
        throw this.unimplemented('Not implemented on web.');
    }
    async displayInAppMessages() {
        throw this.unimplemented('Not implemented on web.');
    }
    async hideInAppMessages() {
        throw this.unimplemented('Not implemented on web.');
    }
    async displayCarousel(options) {
        options;
        throw this.unimplemented('Not implemented on web.');
    }
    async setUserHash(options) {
        options;
        throw this.unimplemented('Not implemented on web.');
    }
    async setBottomPadding(options) {
        options;
        throw this.unimplemented('Not implemented on web.');
    }
    async receivePush(notification) {
        notification;
        throw this.unimplemented('Not implemented on web.');
    }
    async sendPushTokenToIntercom(options) {
        options;
        throw this.unimplemented('Not implemented on web.');
    }
    async unreadConversationCount() {
        const payload = { value: this._unreadConversationCount };
        return Promise.resolve(payload);
    }
    setupListeners() {
        window.Intercom('onHide', this.notifyListeners('onHide', null));
        window.Intercom('onShow', this.notifyListeners('onHide', null));
        window.Intercom('onUnreadCountChange', (unreadCount) => {
            this._unreadConversationCount = unreadCount;
            this.notifyListeners('onUnreadCountChange', unreadCount);
        });
    }
}
const Intercom = new IntercomWeb();
export { Intercom };
//# sourceMappingURL=web.js.map