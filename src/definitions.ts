export interface AdyenPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
