import type { IntercomSettings } from './definitions';

declare global {
  interface Window {
    Intercom: any;
    intercomSettings: IntercomSettings;
    attachEvent: any;
  }
}

/**
 * Snippet to initialize the Intercom instance
 * 
 * @see {@link https://github.com/devrnt/react-use-intercom/blob/master/src/initialize.ts}
 * @see {@link https://developers.intercom.com/installing-intercom/docs/basic-javascript}
 *
 * @param appId - Intercom app id
 * @param [timeout=0] - Amount of milliseconds that the initialization should be delayed, defaults to 0
 */
const initialize = (appId: string, timeout = 0): void => {
  const w = window;
  const ic = w.Intercom;
  if (typeof ic === 'function') {
    ic('reattach_activator');
    ic('update', w.intercomSettings);
  } else {
    const d = document;
    const i = function(...args: any[]) {
      i.c(args);
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    i.q = [];
    i.c = function(...args: any[]) {
      i.q.push(args);
    };
    w.Intercom = i;
    const l = function() {
      setTimeout(function() {
        const s = d.createElement('script');
        s.type = 'text/javascript';
        s.async = true;
        s.src = 'https://widget.intercom.io/widget/' + appId;
        const x = d.getElementsByTagName('script')[0];
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        x.parentNode.insertBefore(s, x);
      }, timeout);
    };
    if (document.readyState === 'complete') {
      l();
    } else if (w.attachEvent) {
      w.attachEvent('onload', l);
    } else {
      w.addEventListener('load', l, false);
    }
  }
};

export { initialize };