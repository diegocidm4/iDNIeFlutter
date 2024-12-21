# idnieflut
Plugin para el uso del DNIe en Flutter. Disponible para android e iOS.

## Funcionalidades
Esta librería ofrece las siguientes funcionalidades:
### Lectura de datos públicos del DNIe o cualquier documento electrónico de identidad:

#### Utilizando can o mrz para establecer canal seguro.
```Dart
  /**
   * Lee el eID utilizando la conexión NFC.
   * @param accessKey (Indica el can o mrz utilizado para establecer la comunicación)
   * @param paceKeyReference (indica el tipo de clave usada en la conexión, se puede utilizar CAN o MRZ)
   * @param tags (indica los dataGroups a leer del documento. [] para leer todos. En android si no se especifica DG2 no se recupera la foto y si no se especifica DG7 no se recupera la firma, el resto de DGs se recuperan siempre)
   */
  Future<RespuestaReadPassport?> readPassport(String accessKey, int paceKeyReference, List<String> tags) {
      return IdnieflutPlatform.instance.readPassport(accessKey, paceKeyReference, tags);
  }
```


### Firma de un texto en formato String con el certificado del DNIe que se le indique en certToUse:
```Dart
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
```

### Firma el hash del documento pasado como parámetro en document con el certificado del DNIe que se le indique en certToUse:
```Dart
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
```

### Firma el hash y el digest pasados como parámetros con el certificado del DNIe que se le indique en certToUse:
```Dart
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
```

## Install


## Autor

iDNIe ha sido creada y mantenida por [Diego Cid]

Puede seguirme en Twitter en [@diegocidm4](https://twitter.com/diegocidm4).

## Licencia
La librería se distribuye con una licencia anual asociada a un app bundle.