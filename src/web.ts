import { WebPlugin } from '@capacitor/core';

import type { AdyenPlugin } from './definitions';

export class AdyenWeb extends WebPlugin implements AdyenPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
