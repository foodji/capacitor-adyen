package io.foodji.plugins.adyen;

import android.content.Intent;

import com.adyen.checkout.components.model.PaymentMethodsApiResponse;
import com.adyen.checkout.components.model.payments.Amount;
import com.adyen.checkout.card.CardConfiguration;
import com.adyen.checkout.core.api.Environment;
import com.adyen.checkout.dropin.DropIn;
import com.adyen.checkout.dropin.DropInConfiguration;
import com.adyen.checkout.googlepay.GooglePayConfiguration;
import com.adyen.checkout.googlepay.model.MerchantInfo;
import com.adyen.checkout.sepa.SepaConfiguration;
import com.google.android.gms.wallet.WalletConstants;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;
import org.json.JSONObject;

@CapacitorPlugin(name = "Adyen")
public class AdyenPlugin extends Plugin {

    @Override
    public void load() {
        // getContext().startService(new Intent(getContext(), AdyenService.class));
    }

    @PluginMethod
    public void presentDropIn(PluginCall call) {
        try {
            AdyenService.currentCall = call;
            AdyenService.lastPaymentResponse = null;

            JSObject options = call.getData();
            String environment = options.optString("environment", "test");
            int amount = options.getInt("amount");
            String currencyCode = options.getString("currencyCode");
            String paymentMethodsResponse = options.getString("paymentMethodsResponse");
            String clientKey = options.getString("clientKey");
            JSONObject paymentMethodsConfiguration = options.getJSONObject("paymentMethodsConfiguration");

            PaymentMethodsApiResponse paymentMethodsApiResponse = PaymentMethodsApiResponse.SERIALIZER.deserialize(new JSONObject(paymentMethodsResponse));

            Intent intent = new Intent(getContext(), getActivity().getClass());
            intent.setAction("INTENT_ACTION_PRESENTDROPIN");

            Environment env = "live".equals(environment) ? Environment.EUROPE : Environment.TEST;

            DropInConfiguration.Builder dropInConfigurationBuilder = new DropInConfiguration.Builder(getContext(), AdyenService.class, clientKey).setEnvironment(env);
            dropInConfigurationBuilder.addSepaConfiguration(new SepaConfiguration.Builder(getContext(), clientKey).setEnvironment(env).build());

            if (paymentMethodsConfiguration.has("card")) {
                JSONObject card = paymentMethodsConfiguration.getJSONObject("card");
                CardConfiguration cardConfiguration = new CardConfiguration.Builder(getContext(), clientKey).setEnvironment(env)
                        .setHolderNameRequired(card.getBoolean("holderNameRequired"))
                        .setShowStorePaymentField(card.getBoolean("showStorePaymentField"))
                        .build();
                dropInConfigurationBuilder.addCardConfiguration(cardConfiguration);
            }

            if (paymentMethodsConfiguration.has("paywithgoogle")) {
                JSONObject paywithgoogle = paymentMethodsConfiguration.getJSONObject("paywithgoogle");
                GooglePayConfiguration googlePayConfiguration = new GooglePayConfiguration.Builder(getContext(), clientKey).setEnvironment(env)
                        .setGooglePayEnvironment("live".equals(environment) ? WalletConstants.ENVIRONMENT_PRODUCTION : WalletConstants.ENVIRONMENT_TEST)
                        .setMerchantAccount(paywithgoogle.getJSONObject("configuration").getString("gatewayMerchantId"))
                        .build();
                dropInConfigurationBuilder.addGooglePayConfiguration(googlePayConfiguration);
            }

            if(amount > 0) {
                Amount dropInAmount = new Amount();
                dropInAmount.setCurrency(currencyCode);
                dropInAmount.setValue(amount);
                dropInConfigurationBuilder.setAmount(dropInAmount);
            }

            DropInConfiguration dropInConfiguration = dropInConfigurationBuilder.build();

            DropIn.startPayment(getActivity(), paymentMethodsApiResponse, dropInConfiguration, null);
        } catch (JSONException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void handleAction(PluginCall call) {
        AdyenService.currentCall = call;
        String action = call.getString("value");
        AdyenService.getInstance().callResultAction(action);
    }

    @PluginMethod
    public void dismissDropIn() {
        AdyenService.getInstance().callResultFinished();
    }
}
