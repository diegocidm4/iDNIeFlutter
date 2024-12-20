import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:idnieflut/idnieflut_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelIdnieflut platform = MethodChannelIdnieflut();
  const MethodChannel channel = MethodChannel('idnieflut');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

}
