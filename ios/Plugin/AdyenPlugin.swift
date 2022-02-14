import Foundation
import Capacitor
import Adyen
import PassKit

@objc(AdyenPlugin)
public class AdyenPlugin: CAPPlugin {
    var currentCall: CAPPluginCall!
    var dropInComponent: DropInComponent!
    
    @objc func presentDropIn(_ call: CAPPluginCall) {
        let environment = call.getString("environment") ?? "test"
        let paymentMethodsResponse = call.getString("paymentMethodsResponse") ?? ""
        let currencyCode = call.getString("currencyCode")!
        let amount = call.getInt("amount")!
        let clientKey = call.getString("clientKey")!
        var countryCode = call.getString("countryCode")!;
        
        let paymentMethods = try! JSONDecoder().decode(PaymentMethods.self, from: Data(paymentMethodsResponse.utf8))
        let paymentMethodsConfiguration = call.getObject("paymentMethodsConfiguration") ?? [:]
        
        let apiContext = APIContext(environment: "live".elementsEqual(environment) ? Environment.live : Environment.test, clientKey: clientKey)
        let configuration = DropInComponent.Configuration(apiContext: apiContext)
        
        let card = paymentMethodsConfiguration["card"] as? JSObject
        if (card != nil) {
            configuration.card = CardComponent.Configuration(
                showsHolderNameField: card!["holderNameRequired"] as? Bool ?? false,
                showsStorePaymentMethodField: card!["showStorePaymentField"] as? Bool ?? true
            )
        }
        
        let applePay = paymentMethodsConfiguration["applepay"] as? JSObject
        if (applePay != nil) {
            countryCode = applePay!["countryCode"] as! String
            let applePayConfig = applePay!["configuration"] as? JSObject
            if (applePayConfig != nil) {
                configuration.applePay = ApplePayComponent.Configuration(
                    summaryItems: [
                        PKPaymentSummaryItem(label: applePayConfig!["merchantName"] as! String, amount: NSDecimalNumber(string: "0.0"), type: .pending)
                    ],
                    merchantIdentifier: (applePayConfig!["merchantIdentifier"] as? String)!
                )
            }
        }
        
        configuration.payment = Payment(amount: Amount(value: amount, currencyCode: currencyCode), countryCode: countryCode)
        
        let dropInComponent = DropInComponent(paymentMethods: paymentMethods, configuration: configuration)
        self.dropInComponent = dropInComponent
        dropInComponent.delegate = self
        call.keepAlive = true
        self.currentCall = call
        
        DispatchQueue.main.async {
            self.bridge?.viewController!.present(dropInComponent.viewController, animated: true)
        }
    }
    
    @objc func dismissDropIn(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.bridge?.viewController!.dismiss(animated: true)
        }
        call.resolve()
    }
    
    @objc func handleAction(_ call: CAPPluginCall) {
        let actionStr = call.getString("value") ?? ""
        let action = try! JSONDecoder().decode(Action.self, from: Data(actionStr.utf8))
        self.dropInComponent.handle(action)
        call.resolve()
    }
}

extension AdyenPlugin: DropInComponentDelegate {
    public func didProvide(_ data: ActionComponentData, from component: DropInComponent) {
        DispatchQueue.main.async {
            self.bridge?.viewController!.dismiss(animated: true)
        }
        
        self.currentCall.resolve([
            "action": "onAdditionalDetails",
            "data": ["paymentData": data.paymentData!, "details": data.details]
        ])
    }
    
    public func didFail(with error: Error, from component: DropInComponent) {
        DispatchQueue.main.async {
            self.bridge?.viewController!.dismiss(animated: true)
        }
        
        self.currentCall.reject(error.localizedDescription, nil, error)
    }
    
    public func didSubmit(_ data: PaymentComponentData, for paymentMethod: PaymentMethod, from component: DropInComponent) {
        var paymentDetails: NSDictionary = [:]
        if (data.paymentMethod is ApplePayDetails) {
            let apd = data.paymentMethod as! ApplePayDetails
            paymentDetails = [
                "type": apd.type,
                "token": apd.token,
                "network": apd.network
            ]
        }
        if (data.paymentMethod is CardDetails) {
            let cd = data.paymentMethod as! CardDetails
            paymentDetails = [
                "type": cd.type,
                "holderName": cd.holderName!,
                "encryptedCardNumber": cd.encryptedCardNumber!,
                "encryptedSecurityCode": cd.encryptedSecurityCode!,
                "encryptedExpiryMonth": cd.encryptedExpiryMonth!,
                "encryptedExpiryYear": cd.encryptedExpiryYear!
            ]
        }
        if (data.paymentMethod is SEPADirectDebitDetails) {
            let sd = data.paymentMethod as! SEPADirectDebitDetails
            paymentDetails = [
                "type": sd.type,
                "iband": sd.iban,
                "ownerName": sd.ownerName
            ]
        }

        self.currentCall.resolve([
            "action": "onSubmit",
            "data": [
                "paymentMethod": paymentDetails,
                "storePaymentMethod": data.storePaymentMethod,
                "browserInfo": ["userAgent": data.browserInfo?.userAgent]
            ]
        ])
    }
    
    public func didComplete(from component: DropInComponent) {
        return
    }
}
