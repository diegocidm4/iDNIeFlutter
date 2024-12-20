
import 'dart:ffi';
import 'dart:typed_data';

import 'idnieflut_platform_interface.dart';
import 'beans.dart';

class Idnieflut {

    Future<EstadoLicencia?> configure(String apiKey) {
      return IdnieflutPlatform.instance.configure(apiKey);
  }

  Future<MRZKey?> getMRZKey(String passportNumber, String dateOfBirth, String dateOfExpiry) {
      return IdnieflutPlatform.instance.getMRZKey(passportNumber, dateOfBirth, dateOfExpiry);
  }

  Future<RespuestaReadPassport?> readPassport(String accessKey, int paceKeyReference, List<String> tags) {
      return IdnieflutPlatform.instance.readPassport(accessKey, paceKeyReference, tags);
  }

  Future<RespuestaFirma?> signTextDNIe(String accessKey, String pin, String datosFirma, String certToUse) {
      return IdnieflutPlatform.instance.signTextDNIe(accessKey, pin, datosFirma, certToUse);
  }

  Future<RespuestaFirma?> signDocumentDNIe(String accessKey, String pin, String document, String certToUse) {
      return IdnieflutPlatform.instance.signDocumentDNIe(accessKey, pin, document, certToUse);
  }

  Future<RespuestaFirma?> signHashDNIe(String accessKey, String pin, List<int> hash, int digest, String certToUse) {
      return IdnieflutPlatform.instance.signHashDNIe(accessKey, pin, hash, digest, certToUse);
  }

    Future<RespuestaNFC?> isNFCEnable() {
      return IdnieflutPlatform.instance.isNFCEnable();
  }

}
