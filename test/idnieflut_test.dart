import 'dart:ffi';
import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:idnieflut/beans.dart';
import 'package:idnieflut/idnieflut.dart';
import 'package:idnieflut/idnieflut_platform_interface.dart';
import 'package:idnieflut/idnieflut_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockIdnieflutPlatform
    with MockPlatformInterfaceMixin
    implements IdnieflutPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<EstadoLicencia?> configure(String apiKey) {
    // TODO: implement configure
    throw UnimplementedError();
  }
  
  @override
  Future<RespuestaReadPassport?> readPassport(String accessKey, int paceKeyReference, List<String> tags) {
    // TODO: implement readPassport
    throw UnimplementedError();
  }
  
  @override
  Future<MRZKey?> getMRZKey(String passportNumber, String dateOfBirth, String dateOfExpiry) {
    // TODO: implement getMRZKey
    throw UnimplementedError();
  }
  
  @override
  Future<RespuestaNFC?> isNFCEnable() {
    // TODO: implement isNFCEnable
    throw UnimplementedError();
  }
  
  @override
  Future<RespuestaFirma?> signDocumentDNIe(String accessKey, String pin, String document, String certToUse) {
    // TODO: implement signDocumentDNIe
    throw UnimplementedError();
  }
  
  @override
  Future<RespuestaFirma?> signHashDNIe(String accessKey, String pin, List<int> hash, int digest, String certToUse) {
    // TODO: implement signHashDNIe
    throw UnimplementedError();
  }
  
  @override
  Future<RespuestaFirma?> signTextDNIe(String accessKey, String pin, String datosFirma, String certToUse) {
    // TODO: implement signTextDNIe
    throw UnimplementedError();
  }
}

void main() {
  final IdnieflutPlatform initialPlatform = IdnieflutPlatform.instance;

  test('$MethodChannelIdnieflut is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelIdnieflut>());
  });

  test('getPlatformVersion', () async {
    Idnieflut idnieflutPlugin = Idnieflut();
    MockIdnieflutPlatform fakePlatform = MockIdnieflutPlatform();
    IdnieflutPlatform.instance = fakePlatform;

  });
}
