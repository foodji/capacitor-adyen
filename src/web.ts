import { WebPlugin } from '@capacitor/core';

import type { AdyenPlugin, DropInOptions } from './definitions';

export class AdyenWeb extends WebPlugin implements AdyenPlugin {
  presentDropIn(_options: DropInOptions): Promise<{ action: 'onSubmit' | 'onDetails'; data: unknown; }> {
    throw new Error('Method not implemented.');
  }

  dismissDropIn(): Promise<void> {
    throw new Error('Method not implemented.');
  }

  handleAction(_action: { value: string }): Promise<any> {
    throw new Error('Method not implemented.');
  }
}
