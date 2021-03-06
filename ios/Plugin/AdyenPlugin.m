#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(AdyenPlugin, "Adyen",
           CAP_PLUGIN_METHOD(presentDropIn, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(dismissDropIn, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(handleAction, CAPPluginReturnPromise);
)
