import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'idnieflut_platform_interface.dart';
import 'beans.dart';

/// An implementation of [IdnieflutPlatform] that uses method channels.
class MethodChannelIdnieflut extends IdnieflutPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('idnieflut');

  @override
  Future<EstadoLicencia?> configure(String apiKey) async {
    final mapEstadoLicencia = await methodChannel.invokeMethod<Map>('configure', {"apiKey": apiKey});
    
    EstadoLicencia estadoLicencia = EstadoLicencia.fromJson(mapEstadoLicencia!);
    return estadoLicencia;
  }

  @override
  Future<MRZKey?> getMRZKey(String passportNumber, String dateOfBirth, String dateOfExpiry) async{
      final mapMRZKey = await methodChannel.invokeMethod<Map>('getMRZKey', {"passportNumber": passportNumber, "dateOfBirth": dateOfBirth, "dateOfExpiry": dateOfExpiry});

      MRZKey mrzKey = MRZKey.fromJson(mapMRZKey);
      return mrzKey;
  }

  @override
  Future<RespuestaReadPassport?> readPassport(String accessKey, int paceKeyReference, List<String> tags) async{
    final mapRespuestaReadPassport = await methodChannel.invokeMethod<Map>('readPassport', {"accessKey": accessKey,"paceKeyReference": paceKeyReference,"tags": tags});
    
    RespuestaReadPassport respuestaReadPassport = RespuestaReadPassport.fromJson(mapRespuestaReadPassport!);
    return respuestaReadPassport;
  }

  @override
  Future<RespuestaFirma?> signTextDNIe(String accessKey, String pin, String datosFirma, String certToUse)  async{
      final mapRespuestaFirma = await methodChannel.invokeMethod<Map>('signTextDNIe', {"accessKey": accessKey, "pin": pin, "datosFirma": datosFirma, "certToUse": certToUse});

      RespuestaFirma respuestaFirma = RespuestaFirma.fromJson(mapRespuestaFirma);
      return respuestaFirma;
  }

  @override
  Future<RespuestaFirma?> signDocumentDNIe(String accessKey, String pin, String document, String certToUse)  async{
      final mapRespuestaFirma = await methodChannel.invokeMethod<Map>('signDocumentDNIe', {"accessKey": accessKey, "pin": pin, "document": document, "certToUse": certToUse});

      RespuestaFirma respuestaFirma = RespuestaFirma.fromJson(mapRespuestaFirma);
      return respuestaFirma;
  }

  @override
  Future<RespuestaFirma?> signHashDNIe(String accessKey, String pin, List<int> hash, int digest, String certToUse)  async{
      final mapRespuestaFirma = await methodChannel.invokeMethod<Map>('signHashDNIe', {"accessKey": accessKey, "pin": pin, "hash": hash, "digest": digest, "certToUse": certToUse});

      RespuestaFirma respuestaFirma = RespuestaFirma.fromJson(mapRespuestaFirma);
      return respuestaFirma;
  }

  @override
  Future<RespuestaNFC?> isNFCEnable()  async{
      final mapRespuestaNFC = await methodChannel.invokeMethod<Map>('isNFCEnable');

      RespuestaNFC respuestaNFC = RespuestaNFC.fromJson(mapRespuestaNFC);
      return respuestaNFC;
  }
}
