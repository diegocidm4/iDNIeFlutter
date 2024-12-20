import Flutter
import UIKit
import iDNIe
import CoreNFC

@available(iOS 13, *)
public class IdnieflutPlugin: NSObject, FlutterPlugin {
    
    //Funciones de acceso a la librería iDNIe
    private var passportReader: PassportReader? = nil
    private var passportSelected: NFCPassportModel? = nil
    private var nfcActivo: Bool = false
    private var datosDNIe: DatosDNIe? = nil

    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "idnieflut", binaryMessenger: registrar.messenger())
    let instance = IdnieflutPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
    
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "configure":
        configure(call: call, result: result)
    case "getMRZKey":
        getMRZKey(call: call, result: result)
    case "readPassport":
        readPassport(call: call, result: result)
    case "signTextDNIe":
        signTextDNIe(call: call, result: result)
    case "signDocumentDNIe":
        signDocumentDNIe(call: call, result: result)
    case "signHashDNIe":
        signHashDNIe(call: call, result: result)
    case "isNFCEnable":
        isNFCEnable(call: call, result: result)

    default:
      result(FlutterMethodNotImplemented)
    }
  }
    
    private func configure(call: FlutterMethodCall, result: @escaping FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>,
           let apiKey = args["apiKey"] as? String {

            let resultado: EstadoLicencia = configure(apiKey)
            
            var json: [String: Any] = [:]
            json["descripcion"] = resultado.descripcion
            json["APIKeyValida"] = resultado.APIKeyValida
            json["lecturaDGHabilitada"] = resultado.lecturaDGHabilitada
            json["autenticacionHabilitada"] = resultado.autenticacionHabilitada
            json["firmaHabilitada"] = resultado.firmaHabilitada
            
            result(json)

            } else {
                result(FlutterError.init(code: "errorParams", message: "No se han recibido los parámetros esperados", details: nil))
            }
        
    }

    private func getMRZKey(call: FlutterMethodCall, result: @escaping FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>,
           let passportNumber = args["passportNumber"] as? String,
           let dateOfBirth = args["dateOfBirth"] as? String,
           let dateOfExpiry = args["dateOfExpiry"] as? String{
            
            let mrzKey = getMRZKey(passportNumber, dateOfBirth, dateOfExpiry)
            
            var json: [String: Any] = [:]
            json["mrzKey"] = mrzKey

            result(json)

            } else {
                result(FlutterError.init(code: "errorParams", message: "No se han recibido los parámetros esperados", details: nil))
            }
        
    }

    
    private func readPassport(call: FlutterMethodCall, result: @escaping FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>,
           let accessKey = args["accessKey"] as? String,
           let paceKeyReference = args["paceKeyReference"] as? Int,
           let tags = args["tags"] as? [String]
        {

            let json = readPassport(accessKey, paceKeyReference, tags)
            
            result(json)

            } else {
                result(FlutterError.init(code: "errorParams", message: "No se han recibido los parámetros esperados", details: nil))
            }
        
    }
    
    private func signTextDNIe(call: FlutterMethodCall, result: @escaping FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>,
           let accessKey = args["accessKey"] as? String,
           let pin = args["pin"] as? String,
           let datosFirma = args["datosFirma"] as? String,
           let certToUse = args["certToUse"] as? String{

            
            var json = signTextDNIe(accessKey, pin, datosFirma, certToUse)
            
            result(json)

            } else {
                result(FlutterError.init(code: "errorParams", message: "No se han recibido los parámetros esperados", details: nil))
            }
        
    }

    private func signDocumentDNIe(call: FlutterMethodCall, result: @escaping FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>,
           let accessKey = args["accessKey"] as? String,
           let pin = args["pin"] as? String,
           let document = args["document"] as? String,
           let certToUse = args["certToUse"] as? String{


            
            var json = signDocumentDNIe(accessKey, pin, document, certToUse)
            
            result(json)

            } else {
                result(FlutterError.init(code: "errorParams", message: "No se han recibido los parámetros esperados", details: nil))
            }
        
    }

    private func signHashDNIe(call: FlutterMethodCall, result: @escaping FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>,
           let accessKey = args["accessKey"] as? String,
           let pin = args["pin"] as? String,
           let hash = args["hash"] as? [UInt8],
           let digest = args["digest"] as? Int,
           let certToUse = args["certToUse"] as? String{


            
            var json = signHashDNIe(accessKey, pin, hash, digest, certToUse)
            
            result(json)

            } else {
                result(FlutterError.init(code: "errorParams", message: "No se han recibido los parámetros esperados", details: nil))
            }
        
    }

    private func isNFCEnable(call: FlutterMethodCall, result: @escaping FlutterResult) {
            let json = isNFCEnable()
            result(json)

    }


    
    public func configure(_ apiKey: String)  -> EstadoLicencia {
        return iDNIe.configure(apiKey:apiKey)
    }

    @objc public func getMRZKey(_ passportNumber: String,_ dateOfBirth: String,_ dateOfExpiry: String) -> String {
        let passportUtils = PassportUtils()
        return passportUtils.getMRZKey( passportNumber: passportNumber, dateOfBirth: dateOfBirth, dateOfExpiry: dateOfExpiry)
    }

    @objc public func readPassport(_ accessKey: String, _ paceKeyReference: Int,_ tags: [String]) -> [String: Any] {
        var paceReference = PACEHandler.CAN_PACE_KEY_REFERENCE
        
        switch paceKeyReference
        {
            case 0: paceReference = PACEHandler.NO_PACE_KEY_REFERENCE
                    break;
            case 1: paceReference = PACEHandler.MRZ_PACE_KEY_REFERENCE
                    break;
            case 2: paceReference = PACEHandler.CAN_PACE_KEY_REFERENCE
                    break;
            default:paceReference = PACEHandler.NO_PACE_KEY_REFERENCE
                    break;

        }
        
        var selectedTags: [DataGroupId] = []
        
        for tag in tags{
            guard let strTag = tag as! String?
            else {continue}
            
            switch (strTag)
            {
            case "COM":
                selectedTags.append(.COM)
                break
            case "DG1":
                selectedTags.append(.DG1)
                break
            case "DG2":
                selectedTags.append(.DG2)
                break
            case "DG3":
                selectedTags.append(.DG3)
                break
            case "DG4":
                selectedTags.append(.DG4)
                break
            case "DG5":
                selectedTags.append(.DG5)
                break
            case "DG6":
                selectedTags.append(.DG6)
                break
            case "DG7":
                selectedTags.append(.DG7)
                break
            case "DG8":
                selectedTags.append(.DG8)
                break
            case "DG9":
                selectedTags.append(.DG9)
                break
            case "DG10":
                selectedTags.append(.DG10)
                break
            case "DG11":
                selectedTags.append(.DG11)
                break
            case "DG12":
                selectedTags.append(.DG12)
                break
            case "DG13":
                selectedTags.append(.DG13)
                break
            case "DG14":
                selectedTags.append(.DG14)
                break
            case "DG15":
                selectedTags.append(.DG15)
                break
            case "DG16":
                selectedTags.append(.DG16)
                break
            case "SOD":
                selectedTags.append(.SOD)
                break
            default:
                break
            }
        }
        
        passportReader = PassportReader()
        nfcActivo = false
        
        let passportUtils = PassportUtils()
        passportReader?.passiveAuthenticationUsesOpenSSL = true

        var lecturaCompleta = false;

        var datosDNIe: DatosDNIe? = nil
        var errorText: String? = nil
        var jsonDatosDNIe: [String: Any]? = nil
        
        if(passportReader == nil)
        {
            errorText = "Error. No se ha leído previamente un DNIe"
        }
        else
        {
            
            passportReader?.readPassport(accessKey: accessKey, paceKeyReference: paceReference, tags: selectedTags, skipSecureElements: true, customDisplayMessage: { (displayMessage) in  return NFCUtils.customDisplayMessage(displayMessage: displayMessage)
            }, completed: { (passport, error) in
                if let passport = passport {
                    datosDNIe = DNIeUtils.obtenerTodosDatosDNIe(dnie: passport)
                    self.datosDNIe = datosDNIe
                    self.passportSelected = datosDNIe?.getPassport()
                    if(paceReference == PACEHandler.CAN_PACE_KEY_REFERENCE)
                    {
                        self.datosDNIe?.setCan(can: accessKey)
                    }
                } else {
                    errorText = error?.localizedDescription
                    print("[NFC] - Error\(error?.localizedDescription)")
                }
                lecturaCompleta = true
            })
            
            var txt = ""
            while(!lecturaCompleta)
            {
                txt = txt + "-"
            }
            
            
            if(datosDNIe != nil)
            {
                var base64Foto = ""
                if(datosDNIe?.getImagen() != nil)
                {
                    base64Foto = DNIeUtils.convertImageToBase64(image: (datosDNIe?.getImagen())!)
                }
                
                var base64Firma = ""
                if(datosDNIe?.getFirma() != nil)
                {
                    base64Firma = DNIeUtils.convertImageToBase64(image: (datosDNIe?.getFirma())!)
                }
                
                var jsonDatosCertAut: [String: Any] = [:]
                if(datosDNIe?.getcertificadoAutenticacion() != nil)
                {
                    if(datosDNIe?.getcertificadoAutenticacion()?.nif != nil)
                    {
                        jsonDatosCertAut["nif"] = datosDNIe?.getcertificadoAutenticacion()?.nif
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.nombre != nil)
                    {
                        jsonDatosCertAut["nombre"] = datosDNIe?.getcertificadoAutenticacion()?.nombre
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.apellidos != nil)
                    {
                        jsonDatosCertAut["apellidos"] = datosDNIe?.getcertificadoAutenticacion()?.apellidos
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.fechaNacimiento != nil)
                    {
                        jsonDatosCertAut["fechaNacimiento"] = datosDNIe?.getcertificadoAutenticacion()?.fechaNacimiento
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.tipo != nil)
                    {
                        jsonDatosCertAut["tipo"] = datosDNIe?.getcertificadoAutenticacion()?.tipo
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.nifRepresentante != nil)
                    {
                        jsonDatosCertAut["nifRepresentante"] = datosDNIe?.getcertificadoAutenticacion()?.nifRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.nombreRepresentante != nil)
                    {
                        jsonDatosCertAut["nombreRepresentante"] = datosDNIe?.getcertificadoAutenticacion()?.nombreRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.apellidosRepresentante != nil)
                    {
                        jsonDatosCertAut["apellidosRepresentante"] = datosDNIe?.getcertificadoAutenticacion()?.apellidosRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.fechaInicioValidez != nil)
                    {
                        jsonDatosCertAut["fechaInicioValidez"] = datosDNIe?.getcertificadoAutenticacion()?.fechaInicioValidez
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.fechaFinValidez != nil)
                    {
                        jsonDatosCertAut["fechaFinValidez"] = datosDNIe?.getcertificadoAutenticacion()?.fechaFinValidez
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.estado != nil)
                    {
                        jsonDatosCertAut["estado"] = datosDNIe?.getcertificadoAutenticacion()?.estado
                    }
                    
                    if(datosDNIe?.getcertificadoAutenticacion()?.email != nil)
                    {
                        jsonDatosCertAut["email"] = datosDNIe?.getcertificadoAutenticacion()?.email
                    }
                }
                
                var jsonDatosCertFirma: [String: Any] = [:]
                if(datosDNIe?.getcertificadoFirma() != nil)
                {
                    if(datosDNIe?.getcertificadoFirma()?.nif != nil)
                    {
                        jsonDatosCertFirma["nif"] = datosDNIe?.getcertificadoFirma()?.nif
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.nombre != nil)
                    {
                        jsonDatosCertFirma["nombre"] = datosDNIe?.getcertificadoFirma()?.nombre
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.apellidos != nil)
                    {
                        jsonDatosCertFirma["apellidos"] = datosDNIe?.getcertificadoFirma()?.apellidos
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.fechaNacimiento != nil)
                    {
                        jsonDatosCertFirma["fechaNacimiento"] = datosDNIe?.getcertificadoFirma()?.fechaNacimiento
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.tipo != nil)
                    {
                        jsonDatosCertFirma["tipo"] = datosDNIe?.getcertificadoFirma()?.tipo
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.nifRepresentante != nil)
                    {
                        jsonDatosCertFirma["nifRepresentante"] = datosDNIe?.getcertificadoFirma()?.nifRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.nombreRepresentante != nil)
                    {
                        jsonDatosCertFirma["nombreRepresentante"] = datosDNIe?.getcertificadoFirma()?.nombreRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.apellidosRepresentante != nil)
                    {
                        jsonDatosCertFirma["apellidosRepresentante"] = datosDNIe?.getcertificadoFirma()?.apellidosRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.fechaInicioValidez != nil)
                    {
                        jsonDatosCertFirma["fechaInicioValidez"] = datosDNIe?.getcertificadoFirma()?.fechaInicioValidez
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.fechaFinValidez != nil)
                    {
                        jsonDatosCertFirma["fechaFinValidez"] = datosDNIe?.getcertificadoFirma()?.fechaFinValidez
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.estado != nil)
                    {
                        jsonDatosCertFirma["estado"] = datosDNIe?.getcertificadoFirma()?.estado
                    }
                    
                    if(datosDNIe?.getcertificadoFirma()?.email != nil)
                    {
                        jsonDatosCertFirma["email"] = datosDNIe?.getcertificadoFirma()?.email
                    }
                }
                var jsonDatosCertCA: [String: Any] = [:]
                if(datosDNIe?.getcertificadoCA() != nil)
                {
                    if(datosDNIe?.getcertificadoCA()?.nif != nil)
                    {
                        jsonDatosCertCA["nif"] = datosDNIe?.getcertificadoCA()?.nif
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.nombre != nil)
                    {
                        jsonDatosCertCA["nombre"] = datosDNIe?.getcertificadoCA()?.nombre
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.apellidos != nil)
                    {
                        jsonDatosCertCA["apellidos"] = datosDNIe?.getcertificadoCA()?.apellidos
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.fechaNacimiento != nil)
                    {
                        jsonDatosCertCA["fechaNacimiento"] = datosDNIe?.getcertificadoCA()?.fechaNacimiento
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.tipo != nil)
                    {
                        jsonDatosCertCA["tipo"] = datosDNIe?.getcertificadoCA()?.tipo
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.nifRepresentante != nil)
                    {
                        jsonDatosCertCA["nifRepresentante"] = datosDNIe?.getcertificadoCA()?.nifRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.nombreRepresentante != nil)
                    {
                        jsonDatosCertCA["nombreRepresentante"] = datosDNIe?.getcertificadoCA()?.nombreRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.apellidosRepresentante != nil)
                    {
                        jsonDatosCertCA["apellidosRepresentante"] = datosDNIe?.getcertificadoCA()?.apellidosRepresentante
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.fechaInicioValidez != nil)
                    {
                        jsonDatosCertCA["fechaInicioValidez"] = datosDNIe?.getcertificadoCA()?.fechaInicioValidez
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.fechaFinValidez != nil)
                    {
                        jsonDatosCertCA["fechaFinValidez"] = datosDNIe?.getcertificadoCA()?.fechaFinValidez
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.estado != nil)
                    {
                        jsonDatosCertCA["estado"] = datosDNIe?.getcertificadoCA()?.estado
                    }
                    
                    if(datosDNIe?.getcertificadoCA()?.email != nil)
                    {
                        jsonDatosCertCA["email"] = datosDNIe?.getcertificadoCA()?.email
                    }
                }
                
                var jsonDatosDatosICAO: [String: Any] = [:]
                if(datosDNIe?.getdatosICAO() != nil)
                {
                    if(datosDNIe?.getdatosICAO()?.DG1 != nil)
                    {
                        jsonDatosDatosICAO["DG1"] = datosDNIe?.getdatosICAO()?.DG1
                    }
                    
                    if(datosDNIe?.getdatosICAO()?.DG2 != nil)
                    {
                        jsonDatosDatosICAO["DG2"] = datosDNIe?.getdatosICAO()?.DG2
                    }
                    
                    if(datosDNIe?.getdatosICAO()?.DG13 != nil)
                    {
                        jsonDatosDatosICAO["DG13"] = datosDNIe?.getdatosICAO()?.DG13
                    }
                    
                    if(datosDNIe?.getdatosICAO()?.SOD != nil)
                    {
                        jsonDatosDatosICAO["SOD"] = datosDNIe?.getdatosICAO()?.SOD
                    }
                }
                
                jsonDatosDNIe = [:]
                jsonDatosDNIe?["nif"] = datosDNIe?.getNif()
                jsonDatosDNIe?["nombreCompleto"] = datosDNIe?.getNombreCompleto()
                jsonDatosDNIe?["nombre"] = datosDNIe?.getNombre()
                jsonDatosDNIe?["apellido1"] = datosDNIe?.getApellido1()
                jsonDatosDNIe?["apellido2"] = datosDNIe?.getApellido2()
                jsonDatosDNIe?["imagen"] = base64Foto
                jsonDatosDNIe?["firma"] = base64Firma
                jsonDatosDNIe?["fechaNacimiento"] = datosDNIe?.getFechaNacimiento()
                jsonDatosDNIe?["provinciaNacimiento"] = datosDNIe?.getProvinciaNacimiento()
                jsonDatosDNIe?["municipioNacimiento"] = datosDNIe?.getMunicipioNacimiento()
                jsonDatosDNIe?["nombrePadre"] = datosDNIe?.getNombrePadre()
                jsonDatosDNIe?["nombreMadre"] = datosDNIe?.getNombreMadre()
                jsonDatosDNIe?["fechaValidez"] = datosDNIe?.getfechaValidez()
                jsonDatosDNIe?["emisor"] = datosDNIe?.getemisor()
                jsonDatosDNIe?["nacionalidad"] = datosDNIe?.getnacionalidad()
                jsonDatosDNIe?["sexo"] = datosDNIe?.getsexo()
                jsonDatosDNIe?["direccion"] = datosDNIe?.getdireccion()
                jsonDatosDNIe?["provinciaActual"] = datosDNIe?.getprovinciaActual()
                jsonDatosDNIe?["municipioActual"] = datosDNIe?.getmunicipioActual()
                jsonDatosDNIe?["numSoporte"] = datosDNIe?.getnumSoporte()
                jsonDatosDNIe?["certificadoAutenticacion"] = jsonDatosCertAut
                jsonDatosDNIe?["certificadoFirma"] = jsonDatosCertFirma
                jsonDatosDNIe?["certificadoCA"] = jsonDatosCertCA
                jsonDatosDNIe?["integridadDocumento"] = datosDNIe?.getIntegridadDocumento()
                jsonDatosDNIe?["pemCertificadoFirmaSOD"] = datosDNIe?.getpemCertificadoFirmaSOD()
                jsonDatosDNIe?["datosICAO"] = jsonDatosDatosICAO
                jsonDatosDNIe?["can"] = datosDNIe?.getCan()
                
                var jsonErrores = [String]()
                if(datosDNIe?.geterroresVerificacion() != nil && (datosDNIe?.geterroresVerificacion()!.count ?? 0) > 0)
                {
                    let errores = datosDNIe?.geterroresVerificacion()
                    for error in errores!
                    {
                        jsonErrores.append(error.localizedDescription)
                    }
                }
                jsonDatosDNIe?["erroresVerificacion"] = jsonErrores
            }
        }
                
        var json: [String: Any] = [:]
        json["datosDNIe"] = jsonDatosDNIe
        json["error"] = errorText

        return json

    
    }

    @objc public func signTextDNIe(_ accessKey: String, _ pin: String,_ datosFirma: String,_ certToUse: String) -> [String: Any] {
        var errorText: String? = nil
        var firma: String? = nil
        var certificado: String? = nil
        
        var certificate: DNIeCertificates = DNIeCertificates.FIRMA
        
        if(certToUse == "AUTENTICACION")
        {
            certificate = DNIeCertificates.AUTENTICACION
        }
            
        var lecturaCompleta = false;
        nfcActivo = false
        // Set whether to use the new Passive Authentication verification method (default true) or the old OpenSSL CMS verifiction
        if(passportReader == nil)
        {
            errorText = "Error. No se ha leído previamente un DNIe"
        }
        else
        {
            passportReader?.passiveAuthenticationUsesOpenSSL = true
            
            passportReader?.signTextDNIe(accessKey: accessKey, pin: pin, datosFirma: datosFirma, certToUse: certificate, passport: passportSelected, paceKeyReference: PACEHandler.CAN_PACE_KEY_REFERENCE, tags: [], skipSecureElements: true, customDisplayMessage: { (displayMessage) in
                return NFCUtils.customDisplayMessage(displayMessage: displayMessage)
            }, completed: { (passport, error) in
                if let passport = passport {
                    let datosFirmados: String = binToHexRep(passport.signedMessage!)
                    firma = datosFirmados
                } else {
                    errorText = "Error. No se ha podido realizar la firma"
                }
                lecturaCompleta = true
            })
        }
        
        var txt = ""
        while(!lecturaCompleta)
        {
            txt = txt + "-"
        }

        var json: [String: Any] = [:]
        json["firma"] = firma
        json["error"] = errorText

        return json
    }

    @objc public func signDocumentDNIe(_ accessKey: String, _ pin: String,_ document: String,_ certToUse: String) -> [String: Any] {
        var paceReference = PACEHandler.CAN_PACE_KEY_REFERENCE
        var lecturaCompleta = false;

        var errorText: String? = nil
        var firma: String? = nil
        var certificado: String? = nil

        var certificate: DNIeCertificates = DNIeCertificates.FIRMA
        if(certToUse == "AUTENTICACION")
        {
            certificate = DNIeCertificates.AUTENTICACION
        }
        
        let documentoUrl: URL = URL(string: encodeParamenters(parametro: document))!
        
        nfcActivo = false
        if(passportReader == nil)
        {
            errorText = "Error. No se ha leído previamente un DNIe"
        }
        else
        {
            passportReader?.signDocumentDNIe(accessKey: accessKey, pin: pin, document: documentoUrl, certToUse: DNIeCertificates.FIRMA, passport: passportSelected, paceKeyReference: paceReference, tags: [], skipSecureElements: true, customDisplayMessage: { (displayMessage) in
                return NFCUtils.customDisplayMessage(displayMessage: displayMessage)
            }, completed: { (passport, error) in
                if let passport = passport {
                    let datosFirmados: String = binToHexRep(passport.signedMessage!)
                    firma = datosFirmados
                } else {
                    errorText = error?.localizedDescription
                }
                lecturaCompleta = true
            })
        }
        var txt = ""
        while(!lecturaCompleta)
        {
            txt = txt + "-"
        }

        var json: [String: Any] = [:]
        json["firma"] = firma
        json["error"] = errorText

        return json

    }

    @objc public func signHashDNIe(_ accessKey: String, _ pin: String,_ hash: [UInt8],_ digest: Int,_ certToUse: String) -> [String: Any]  {
        var errorText: String? = nil
        var firma: String? = nil
        var certificado: String? = nil
        
        
        var hashUInt: [UInt8] = []
        for h in hash
        {
            hashUInt.append(h as! UInt8)
        }
        
        var certificate: DNIeCertificates = DNIeCertificates.FIRMA
        if(certToUse == "AUTENTICACION")
        {
            certificate = DNIeCertificates.AUTENTICACION
        }
                    
        var digestHeader = NFCUtils.SHA256_DIGESTINFO_HEADER
        switch digest
        {
        case 1:
            digestHeader = NFCUtils.SHA1_DIGESTINFO_HEADER
            break
        case 224:
            digestHeader = NFCUtils.SHA224_DIGESTINFO_HEADER
            break
        case 256:
            digestHeader = NFCUtils.SHA256_DIGESTINFO_HEADER
            break
        case 384:
            digestHeader = NFCUtils.SHA384_DIGESTINFO_HEADER
            break
        case 512:
            digestHeader = NFCUtils.SHA512_DIGESTINFO_HEADER
            break
        default:
            digestHeader = NFCUtils.SHA256_DIGESTINFO_HEADER
            break
        }
        
        var lecturaCompleta = false
        nfcActivo = false
        if(passportReader == nil)
        {
            errorText = "Error. No se ha leído previamente un DNIe"
        }
        else
        {
            
            passportReader?.signHashDNIe(accessKey: accessKey, pin: pin, hash: hashUInt, digest: digestHeader, certToUse: certificate, passport: passportSelected, paceKeyReference: PACEHandler.CAN_PACE_KEY_REFERENCE, tags: [], skipSecureElements: true, customDisplayMessage: { (displayMessage) in
                return NFCUtils.customDisplayMessage(displayMessage: displayMessage)
            }, operacion: .FIRMA_DOCUMENTO, completed: { (passport, error) in
                if let passport = passport {
                    let datosFirmados: String = binToHexRep(passport.signedMessage!)
                    firma = datosFirmados
                } else {
                    errorText = error?.localizedDescription ?? "Error no especificado"
                }
                lecturaCompleta = true
            })
            
        }
        var txt = ""
        while(!lecturaCompleta)
        {
            txt = txt + "-"
        }

        var json: [String: Any] = [:]
        json["firma"] = firma
        json["error"] = errorText

        return json

    }

    @objc public func isNFCEnable() -> [String: Any]  {

        guard NFCNDEFReaderSession.readingAvailable else {
            var json: [String: Any] = [:]
            json["disponible"] = false
            json["activo"] = false

            return json
        }


        var json: [String: Any] = [:]
        json["disponible"] = true
        json["activo"] = true

        return json

    }

    func encodeParamenters(parametro: String) -> String {
        let allowedCharacterSet = (CharacterSet(charactersIn: "!*'():@&=+$,/?%#[] ").inverted)
        let escapedString = parametro.addingPercentEncoding(withAllowedCharacters: allowedCharacterSet) ?? ""
        return escapedString
    }
}
