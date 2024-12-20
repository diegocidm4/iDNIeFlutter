
import 'idnieflut_platform_interface.dart';
import 'beans.dart';

class Idnieflut {

    /**
    * Método utilizado para configurar el plugin.
    * @param apiKey (código de licencia generado que permite el uso del plugin)
    */
    Future<EstadoLicencia?> configure(String apiKey) {
      return IdnieflutPlatform.instance.configure(apiKey);
  }

  /**
   * Genera el código mrz en función de los parámetros introducidos
   * @param passportNumber (número de pasaporte o numero de soporte en el caso del DNIe)
   * @param dateOfBirth (fecha de nacimiento en formato yymmdd)
   * @param dateOfExpiry (fecha de validez del documento en formato yymmdd)
   */
  Future<MRZKey?> getMRZKey(String passportNumber, String dateOfBirth, String dateOfExpiry) {
      return IdnieflutPlatform.instance.getMRZKey(passportNumber, dateOfBirth, dateOfExpiry);
  }

  /**
   * Lee el eID utilizando la conexión NFC.
   * @param accessKey (Indica el can o mrz utilizado para establecer la comunicación)
   * @param paceKeyReference (indica el tipo de clave usada en la conexión, se puede utilizar CAN o MRZ)
   * @param tags (indica los dataGroups a leer del documento. [] para leer todos. En android si no se especifica DG2 no se recupera la foto y si no se especifica DG7 no se recupera la firma, el resto de DGs se recuperan siempre)
   */
  Future<RespuestaReadPassport?> readPassport(String accessKey, int paceKeyReference, List<String> tags) {
      return IdnieflutPlatform.instance.readPassport(accessKey, paceKeyReference, tags);
  }

  /**
   * Firma un texto con el certificado del DNIe pasado como parámetro.
   * @param accessKey (Indica el can utilizado para establecer la comunicación)
   * @param pin (indica pin del DNIe)
   * @param datosFirma (texto a firmar)
   * @param certToUse (certificado a usar. Se indica uno de los valores del tipo DNIeCertificates)
   */
  Future<RespuestaFirma?> signTextDNIe(String accessKey, String pin, String datosFirma, String certToUse) {
      return IdnieflutPlatform.instance.signTextDNIe(accessKey, pin, datosFirma, certToUse);
  }

  /**
   * Firma el hash de un documento pasado como parámetro con el certificado del DNIe pasado como parámetro.
   * @param accessKey (Indica el can utilizado para establecer la comunicación)
   * @param pin (indica pin del DNIe)
   * @param document (url del documento a firmar)
   * @param certToUse (certificado a usar. Se indica uno de los valores del tipo DNIeCertificates)
   */
  Future<RespuestaFirma?> signDocumentDNIe(String accessKey, String pin, String document, String certToUse) {
      return IdnieflutPlatform.instance.signDocumentDNIe(accessKey, pin, document, certToUse);
  }

  /**
   * Firma el hash pasado como parámetro con el certificado del DNIe pasado como parámetro.
   * @param accessKey (Indica el can utilizado para establecer la comunicación)
   * @param pin (indica pin del DNIe), hash (hash a firmar)
   * @param digest (digest del algoritmo utilizado para generar el hash. Se indica uno de los valores del tipo DigestType)
   * @param certToUse (certificado a usar. Se indica uno de los valores del tipo DNIeCertificates)
   */
  Future<RespuestaFirma?> signHashDNIe(String accessKey, String pin, List<int> hash, int digest, String certToUse) {
      return IdnieflutPlatform.instance.signHashDNIe(accessKey, pin, hash, digest, certToUse);
  }

  /**
   * Indica si el dispositivo móvil dispone de la tecnología NFC y si esta opción está activada.
   */
    Future<RespuestaNFC?> isNFCEnable() {
      return IdnieflutPlatform.instance.isNFCEnable();
  }

}
