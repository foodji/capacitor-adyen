export interface AdyenPlugin {
  presentDropIn(options: DropInOptions): Promise<{ action: 'onSubmit' | 'onDetails', data: unknown }>;
  dismissDropIn(): Promise<void>;

  handleAction(action: { value: string }): Promise<any>;
}

export interface DropInOptions {
  paymentMethodsResponse: string, // the paymentMethods response from the server
  environment: AdyenEnvironment, // or "live", default "test"
  clientKey: string, // the public key linked to your API credential, used for client-side authentication.
  currencyCode: string,
  countryCode: string,
  amount: number, // in minor units (cents), so 123 in this case is "EUR 1,23"
  paymentMethodsConfiguration: Record<string, unknown>// configuration for payment methods (like card, applepay, googlepay)
}

export enum AdyenEnvironment {
  test = 'test',
  live = 'live'
}
