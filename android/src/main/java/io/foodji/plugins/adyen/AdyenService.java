package io.foodji.plugins.adyen;

import com.adyen.checkout.components.ActionComponentData;
import com.adyen.checkout.components.PaymentComponentState;
import com.adyen.checkout.dropin.service.DropInService;
import com.adyen.checkout.dropin.service.DropInServiceResult;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import org.json.JSONObject;

public class AdyenService extends DropInService {
    public static JSONObject lastPaymentResponse;
    public static PluginCall currentCall;
    private static AdyenService INSTANCE;

    // TODO not entirely sure this is a singleton, so using this to be safe
    public static AdyenService getInstance() {
        return INSTANCE;
    }

    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    @Override
    public void onDetailsCallRequested(ActionComponentData actionComponentData, JSONObject actionComponentJson) {
        // this is called after the "action" (for additional details) completes

        JSObject result = new JSObject();
        result.put("action", "onAdditionalDetails");
        result.put("data", actionComponentJson);

        currentCall.resolve(result);
    }

    @Override
    public void onPaymentsCallRequested(PaymentComponentState paymentComponentState, JSONObject paymentComponentJson) {
        // this is called after the user picked one of the payment methods from the list
        lastPaymentResponse = paymentComponentJson;

        JSObject result = new JSObject();
        result.put("action", "onSubmit");
        result.put("data", paymentComponentJson);

        currentCall.resolve(result);
    }

    void callResultFinished() {
        // Note that the content here is send as the RESULT_KEY in the intent, so we could use that in AdyenPlugin.java,
        // however, that would require AndroidManifest.xml need this to be added o the activity: android:launchMode="singleInstance"
        // because otherwise onNewIntent in AdyenPlugin.java won't fire. So doing it here is more robust.
        sendResult(new DropInServiceResult.Finished(lastPaymentResponse.toString()));

        if (lastPaymentResponse == null) {
            currentCall.resolve();
        } else {
            JSObject result = new JSObject();
            result.put("data", lastPaymentResponse);
            currentCall.resolve(result);
        }
    }

    void callResultAction(String action) {
        sendResult(new DropInServiceResult.Action(action));
    }

}
