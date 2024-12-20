import 'dart:ffi';
import 'dart:typed_data';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'idnieflut_method_channel.dart';
import 'beans.dart';

abstract class IdnieflutPlatform extends PlatformInterface {
  /// Constructs a IdnieflutPlatform.
  IdnieflutPlatform() : super(token: _token);

  static final Object _token = Object();

  static IdnieflutPlatform _instance = MethodChannelIdnieflut();

  /// The default instance of [IdnieflutPlatform] to use.
  ///
  /// Defaults to [MethodChannelIdnieflut].
  static IdnieflutPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [IdnieflutPlatform] when
  /// they register themselves.
  static set instance(IdnieflutPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<EstadoLicencia?> configure(String apiKey) {
    throw UnimplementedError('configure() has not been implemented.');
  }

  Future<MRZKey?> getMRZKey(String passportNumber, String dateOfBirth, String dateOfExpiry) {
      throw UnimplementedError('getMRZKey() has not been implemented.');
  }

  Future<RespuestaReadPassport?> readPassport(String accessKey, int paceKeyReference, List<String> tags) {
    throw UnimplementedError('readPassport() has not been implemented.');
  }

  Future<RespuestaFirma?> signTextDNIe(String accessKey, String pin, String datosFirma, String certToUse) {
      throw UnimplementedError('signTextDNIe() has not been implemented.');
  }

  Future<RespuestaFirma?> signDocumentDNIe(String accessKey, String pin, String document, String certToUse) {
      throw UnimplementedError('signDocumentDNIe() has not been implemented.');
  }

  Future<RespuestaFirma?> signHashDNIe(String accessKey, String pin, List<int> hash, int digest, String certToUse) {
      throw UnimplementedError('signHashDNIe() has not been implemented.');
  }

  Future<RespuestaNFC?> isNFCEnable() {
      throw UnimplementedError('isNFCEnable() has not been implemented.');
  }

}
