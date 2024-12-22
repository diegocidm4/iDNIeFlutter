class PACEHandler  { 
  final NO_PACE_KEY_REFERENCE = 0;
  final MRZ_PACE_KEY_REFERENCE = 1;
  final CAN_PACE_KEY_REFERENCE = 2;

 }

class DataGroupId  {
  final COM = 'COM';
  final DG1 = 'DG1';
  final DG2 = 'DG2';
  final DG3 = 'DG3';
  final DG4 = 'DG4';
  final DG5 = 'DG5';
  final DG6 = 'DG6';
  final DG7 = 'DG7';
  final DG8 = 'DG8';
  final DG9 = 'DG9';
  final DG10 = 'DG10';
  final DG11 = 'DG11';
  final DG12 = 'DG12';
  final DG13 = 'DG13';
  final DG14 = 'DG14';
  final DG15 = 'DG15';
  final DG16 = 'DG16';
  final SOD = 'SOD';
}

class DNIeCertificates {
  final AUTENTICACION = 'AUTENTICACION';
  final FIRMA = 'FIRMA';
}

class DigestType  { 
  final SHA1 = 0;
  final SHA224 = 1;
  final SHA256 = 2;
  final SHA384 = 2;
  final SHA512 = 2;

 }

class DatosDNIe {
    String nif = "";
    String nombreCompleto = "";    
    String nombre = "";
    String apellido1 = "";
    String apellido2 = "";
    String firma = "";
    String imagen = "";
    String fechaNacimiento = "";
    String provinciaNacimiento = "";
    String municipioNacimiento = "";
    String nombrePadre = "";
    String nombreMadre = "";
    String fechaValidez = "";
    String emisor = "";
    String nacionalidad = "";
    String sexo = "";
    String direccion = "";
    String provinciaActual = "";
    String municipioActual = "";
    String numSoporte = "";
    DatosCertificado? certificadoAutenticacion;
    DatosCertificado? certificadoFirma;
    DatosCertificado? certificadoCA;
    bool integridadDocumento = false;
    String pemCertificadoFirmaSOD = "";
    DatosICAO? datosICAO;
    String can = "";
    List<String> erroresVerificacion = [];

    DatosDNIe(this.nif, this.nombreCompleto, this.nombre, this.apellido1, this.apellido2, this.firma, this.imagen, this.fechaNacimiento, this.provinciaNacimiento, 
     this.municipioNacimiento, this.nombrePadre, this.nombreMadre, this.fechaValidez, this.emisor, this.nacionalidad, this.sexo, this.direccion, this.provinciaActual, 
     this.municipioActual, this.numSoporte, this.certificadoAutenticacion, this.certificadoFirma, this.certificadoCA, this.integridadDocumento, this.pemCertificadoFirmaSOD, 
     this.datosICAO, this.can, this.erroresVerificacion);
     
    DatosDNIe.fromJson(Map? json)
    {
     this.nif = json?["nif"]; 
     this.nombreCompleto = json?["nombreCompleto"]; 
     this.nombre = json?["nombre"]; 
     this.apellido1 = json?["apellido1"]; 
     this.apellido2 = json?["apellido2"]; 
     this.firma = json?["firma"]; 
     this.imagen = json?["imagen"]; 
     this.fechaNacimiento = json?["fechaNacimiento"]; 
     this.provinciaNacimiento = json?["provinciaNacimiento"];
     this.municipioNacimiento = json?["municipioNacimiento"]; 
     this.nombrePadre = json?["nombrePadre"]; 
     this.nombreMadre = json?["nombreMadre"]; 
     this.fechaValidez = json?["fechaValidez"]; 
     this.emisor = json?["emisor"]; 
     this.nacionalidad = json?["nacionalidad"]; 
     this.sexo = json?["sexo"]; 
     this.direccion = json?["direccion"]; 
     this.provinciaActual = json?["provinciaActual"];
     this.municipioActual = json?["municipioActual"]; 
     this.numSoporte = json?["numSoporte"] ?? ""; 
     this.certificadoAutenticacion = DatosCertificado.fromJson(json?["certificadoAutenticacion"]); 
     this.certificadoFirma = DatosCertificado.fromJson(json?["certificadoFirma"]); 
     this.certificadoCA = DatosCertificado.fromJson(json?["certificadoCA"]); 
     this.integridadDocumento = json?["integridadDocumento"] ?? false;  
     this.pemCertificadoFirmaSOD = json?["pemCertificadoFirmaSOD"] ?? ""; 
     this.datosICAO  = DatosICAO.fromJson(json?["datosICAO"]); 
     this.can = json?["can"] ?? "";   

     if(json?["erroresVerificacion"] != null && !json?["erroresVerificacion"].isEmpty)
     {
        List<Object?> lista = json?["erroresVerificacion"];
        for (var element in lista) {
          this.erroresVerificacion.add(element as String);  
        }
     }
    }
 }

 class DatosCertificado {
    String nif = "";
    String nombre = "";
    String apellidos = "";
    String fechaNacimiento = "";
    String tipo = "";
    String nifRepresentante = "";
    String nombreRepresentante = "";
    String apellidosRepresentante = "";
    String fechaInicioValidez = "";
    String fechaFinValidez = "";
    int estado = 0;
    String email = "";

    DatosCertificado(this.nif, this.nombre, this.apellidos, this.fechaNacimiento, this.tipo, this.nifRepresentante, this.nombreRepresentante, this.apellidosRepresentante, 
    this.fechaInicioValidez, this.fechaFinValidez, this.estado, this.email);

    DatosCertificado.fromJson(Map? json)
    {
      this.nif = json?["nif"] ?? "";
      this.nombre = json?["nombre"] ?? "";
      this.apellidos = json?["apellidos"] ?? "";
      this.fechaNacimiento = json?["fechaNacimiento"] ?? "";
      this.tipo = json?["tipo"] ?? "";
      this.nifRepresentante = json?["nifRepresentante"] ?? "";
      this.nombreRepresentante = json?["nombreRepresentante"] ?? "";
      this.apellidosRepresentante = json?["apellidosRepresentante"] ?? "";
      this.fechaInicioValidez = json?["fechaInicioValidez"] ?? "";
      this.fechaFinValidez = json?["fechaFinValidez"] ?? "";
      this.estado = json?["estado"] ?? -1;
      this.email = json?["email"] ?? "";
    }
 }

 class DatosICAO {
    String DG1 = "";
    String DG2 = "";
    String DG13 = "";
    String SOD = "";

    DatosICAO(this.DG1, this.DG2, this.DG13, this.SOD);

    DatosICAO.fromJson(Map? json)
    {
      this.DG1 = json?["DG1"] ?? "";
      this.DG2 = json?["DG2"] ?? "";
      this.DG13 = json?["DG13"] ?? "";
      this.SOD = json?["SOD"] ?? "";
    }
 }

 class EstadoLicencia {
    String descripcion = "";
    bool APIKeyValida = false;
    bool lecturaDGHabilitada = false;
    bool autenticacionHabilitada = false;
    bool firmaHabilitada = false;

    EstadoLicencia(this.descripcion, this.APIKeyValida, this.lecturaDGHabilitada, this.autenticacionHabilitada, this.firmaHabilitada);

    EstadoLicencia.fromJson(Map? json)
    {
      this.descripcion = json?["descripcion"];
      this.APIKeyValida = json?["APIKeyValida"];
      this.lecturaDGHabilitada = json?["lecturaDGHabilitada"];
      this.autenticacionHabilitada = json?["autenticacionHabilitada"];
      this.firmaHabilitada = json?["firmaHabilitada"];

    }
 }

  class RespuestaReadPassport {
    DatosDNIe? datosDNIe;
    String? error;

    RespuestaReadPassport(this.datosDNIe, this.error);

    RespuestaReadPassport.fromJson(Map? json)
    {
      if(json?["datosDNIe"] != null)
      {
        datosDNIe = DatosDNIe.fromJson(json?["datosDNIe"]); 
      }
      
      if(json?["error"] != null)
      {
        error = json?["error"];
      }
    }
 }

   class MRZKey {
      String? mrzKey;

      MRZKey(this.mrzKey);

      MRZKey.fromJson(Map? json)
      {
        if(json?["mrzKey"] != null)
        {
          mrzKey = json?["mrzKey"]; 
        }
      }
  }


  class RespuestaFirma {
    String? firma;
    String? error;

    RespuestaFirma(this.firma, this.error);

    RespuestaFirma.fromJson(Map? json)
    {
      if(json?["firma"] != null)
      {
        firma = json?["firma"]; 
      }
      
      if(json?["error"] != null)
      {
        error = json?["error"];
      }
    }
 }

  class RespuestaNFC {
    bool disponible = false;
    bool activo = false;

    RespuestaNFC(this.disponible, this.activo);

    RespuestaNFC.fromJson(Map? json)
    {
      disponible = json?["disponible"] ?? false; 
      activo = json?["activo"] ?? false;
    }
 }
