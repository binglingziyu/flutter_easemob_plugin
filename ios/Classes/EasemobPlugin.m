#import "EasemobPlugin.h"
#import <easemob_plugin/easemob_plugin-Swift.h>

@implementation EasemobPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftEasemobPlugin registerWithRegistrar:registrar];
}
@end
