# capacitor-adyen

Provides API for the Adyen SDK

## Install

```bash
npm install capacitor-adyen
npx cap sync
```

## API

<docgen-index>

* [`presentDropIn(...)`](#presentdropin)
* [`dismissDropIn()`](#dismissdropin)
* [`handleAction(...)`](#handleaction)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### presentDropIn(...)

```typescript
presentDropIn(options: DropInOptions) => Promise<{ action: 'onSubmit' | 'onDetails'; data: unknown; }>
```

| Param         | Type                                                    |
| ------------- | ------------------------------------------------------- |
| **`options`** | <code><a href="#dropinoptions">DropInOptions</a></code> |

**Returns:** <code>Promise&lt;{ action: 'onSubmit' | 'onDetails'; data: unknown; }&gt;</code>

--------------------


### dismissDropIn()

```typescript
dismissDropIn() => Promise<void>
```

--------------------


### handleAction(...)

```typescript
handleAction(action: { value: string; }) => Promise<any>
```

| Param        | Type                            |
| ------------ | ------------------------------- |
| **`action`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### Interfaces


#### DropInOptions

| Prop                              | Type                                                             |
| --------------------------------- | ---------------------------------------------------------------- |
| **`paymentMethodsResponse`**      | <code>string</code>                                              |
| **`environment`**                 | <code><a href="#adyenenvironment">AdyenEnvironment</a></code>    |
| **`clientKey`**                   | <code>string</code>                                              |
| **`currencyCode`**                | <code>string</code>                                              |
| **`countryCode`**                 | <code>string</code>                                              |
| **`amount`**                      | <code>number</code>                                              |
| **`paymentMethodsConfiguration`** | <code><a href="#record">Record</a>&lt;string, unknown&gt;</code> |


### Type Aliases


#### Record

Construct a type with a set of properties K of type T

<code>{ [P in K]: T; }</code>


### Enums


#### AdyenEnvironment

| Members    | Value               |
| ---------- | ------------------- |
| **`test`** | <code>'test'</code> |
| **`live`** | <code>'live'</code> |

</docgen-api>
