import { WebPlugin } from '@capacitor/core';
import type { IntercomPlugin, IntercomPushNotificationData, IntercomSettings, IntercomUserUpdateOptions, UnreadConversationCount } from './definitions';
declare global {
    interface Window {
        Intercom: any;
    }
}
export declare class IntercomWeb extends WebPlugin implements IntercomPlugin {
    private _unreadConversationCount;
    boot(options: IntercomSettings): Promise<void>;
    /**
     * @deprecated
     */
    registerIdentifiedUser(options: {
        userId?: string;
        email?: string;
    }): Promise<void>;
    loginUser(options: {
        userId?: string;
        email?: string;
    }): Promise<void>;
    /**
     * @deprecated
     */
    registerUnidentifiedUser(): Promise<void>;
    loginUnidentifiedUser(): Promise<void>;
    updateUser(options: IntercomUserUpdateOptions): Promise<void>;
    logout(): Promise<void>;
    logEvent(options: {
        name: string;
        data?: any;
    }): Promise<void>;
    displayMessenger(): Promise<void>;
    displayMessageComposer(options: {
        message: string;
    }): Promise<void>;
    displayHelpCenter(): Promise<void>;
    displayArticle(options: {
        articleId: string;
    }): Promise<void>;
    hideMessenger(): Promise<void>;
    displayLauncher(): Promise<void>;
    hideLauncher(): Promise<void>;
    displayInAppMessages(): Promise<void>;
    hideInAppMessages(): Promise<void>;
    displayCarousel(options: {
        carouselId: string;
    }): Promise<void>;
    setUserHash(options: {
        hmac: string;
    }): Promise<void>;
    setBottomPadding(options: {
        value: string;
    }): Promise<void>;
    receivePush(notification: IntercomPushNotificationData): Promise<void>;
    sendPushTokenToIntercom(options: {
        value: string;
    }): Promise<void>;
    unreadConversationCount(): Promise<UnreadConversationCount>;
    private setupListeners;
}
declare const Intercom: IntercomWeb;
export { Intercom };
