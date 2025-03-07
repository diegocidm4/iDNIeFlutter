package com.cqesolutions.idnieflut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cqesolutions.idnieflut.activities.ConsultaDNIe;
import com.cqesolutions.idnieflut.activities.ConsultaPasaporte;
import com.cqesolutions.idnieflut.activities.ConsultaPasaporteCAN;
import com.cqesolutions.idnieflut.activities.FirmaDNIe;
import com.cqesolutions.idnieflut.bean.DatosCertificado;
import com.cqesolutions.idnieflut.bean.DatosCertificadoFirma;
import com.cqesolutions.idnieflut.bean.DatosDNIe;
import com.cqesolutions.idnieflut.utils.DateUtils;
import com.cqesolutions.idnieflut.utils.ImageUtils;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** IdnieflutPlugin */
public class IdnieflutPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  private Activity activity;
  private Result pluginResult = null;
  private final int READ_PASSPORT_CODE = 1;
  private final int SIGN_TEXT_CODE = 2;
  private final int SIGN_HASH_CODE = 3;
  private final int SIGN_DOCUMENT_CODE = 4;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "idnieflut");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    try {
      pluginResult = result;
      switch (call.method)
      {
        case "configure":
          result = configure(call, result);
          break;
        case "getMRZKey":
          result = getMRZKey(call, result);
          break;
        case "readPassport":
          readPassport(call, result);
          break;
        case "signTextDNIe":
          signDNIe(call, result);
          break;
        case "signDocumentDNIe":
          signDocumentDNIe(call, result);
          break;
        case "signHashDNIe":
          signHashDNIe(call, result);
          break;
        case "isNFCEnable":
          result = isNFCEnable(call, result);
          break;
        default:
          result.notImplemented();
          break;
      }
    } catch (JSONException e) {
      result.error("99", "Se ha producido un error inesperado", e.getLocalizedMessage());
    }

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  public Result configure(@NonNull MethodCall call, @NonNull Result result) throws JSONException {
    if (!call.hasArgument("apiKey")) {
      result.error("01", "No se ha recibido un parámetro obligatorio", "");
      return result;
    }
    String apiKey = call.argument("apiKey");

    configure(apiKey);

    Map<Object, Object> ret = new HashMap<>();
    ret.put("descripcion","Licencia válida de forma indefinida.");
    ret.put("APIKeyValida",true);
    ret.put("lecturaDGHabilitada",true);
    ret.put("autenticacionHabilitada",true);
    ret.put("firmaHabilitada",true);

    result.success(ret);

    return result;
  }

  public Result getMRZKey(@NonNull MethodCall call, @NonNull Result result) throws JSONException {
    if (!call.hasArgument("passportNumber")
            || !call.hasArgument("dateOfBirth")
            || !call.hasArgument("dateOfExpiry")) {
      result.error("01", "No se ha recibido un parámetro obligatorio", "");
      return result;
    }

    String passportNumber = call.argument("passportNumber");
    String dateOfBirth = call.argument("dateOfBirth");
    String dateOfExpiry = call.argument("dateOfExpiry");

    String mrzKey = getMRZKey(passportNumber, dateOfBirth, dateOfExpiry);

    Map<Object, Object> ret = new HashMap<>();
    ret.put("mrzKey",mrzKey);
    result.success(ret);

    return result;
  }

  public void readPassport(@NonNull MethodCall call, @NonNull Result result) throws JSONException {
    if (!call.hasArgument("accessKey")
            || !call.hasArgument("paceKeyReference")
            || !call.hasArgument("tags")) {

      finreadPassport(null, null, null, null, null, false, "No se ha recibido un parámetro obligatorio");
      return;
    }

    String accessKey = call.argument("accessKey");
    int paceKeyReference = call.argument("paceKeyReference");
    ArrayList<String> jtags = call.argument("tags");
    Boolean esDNIe = call.argument("esDNIe");

    String[] tags = new String[jtags.size()];

    for(int i=0; i<jtags.size();i++)
    {
      tags[i]=jtags.get(i);
    }

    boolean esperaRespuesta = readPassport(accessKey, paceKeyReference, tags, esDNIe);

    if (!esperaRespuesta) {
      result.notImplemented();
      //return result;
    }
  }

  public void finreadPassport(DatosDNIe datosDNIe, String can, DatosCertificado certificadoAutenticacion, DatosCertificado certificadoFirma,
                              DatosCertificadoFirma certificadoCA, boolean integridadDocumento, String errorText)
  {
    Map<Object, Object> jsonDatosDNIe = null;
    if(datosDNIe!=null)
    {
      jsonDatosDNIe = new HashMap<>();

      Map<Object, Object> jsonDatosCertAut = null;
      if(certificadoAutenticacion != null) {
        jsonDatosCertAut = new HashMap<>();
        jsonDatosCertAut.put("nif", certificadoAutenticacion.getNif());
        jsonDatosCertAut.put("nombre", certificadoAutenticacion.getNombre());
        jsonDatosCertAut.put("apellidos", certificadoAutenticacion.getApellidos());
        jsonDatosCertAut.put("fechaNacimiento", certificadoAutenticacion.getFechaNacimiento());
        jsonDatosCertAut.put("tipo", certificadoAutenticacion.getTipo());
        jsonDatosCertAut.put("nifRepresentante", certificadoAutenticacion.getNifRepresentante());
        jsonDatosCertAut.put("nombreRepresentante", certificadoAutenticacion.getNombreRepresentante());
        jsonDatosCertAut.put("apellidosRepresentante", certificadoAutenticacion.getApellidosRepresentante());
        jsonDatosCertAut.put("fechaInicioValidez", certificadoAutenticacion.getFechaInicioValidez());
        jsonDatosCertAut.put("fechaFinValidez", certificadoAutenticacion.getFechaFinValidez());
        jsonDatosCertAut.put("estado", certificadoAutenticacion.getEstado());
        jsonDatosCertAut.put("email", certificadoAutenticacion.getEmail());
      }

      Map<Object, Object> jsonDatosCertFirma = null;
      if(certificadoFirma != null) {
        jsonDatosCertFirma = new HashMap<>();
        jsonDatosCertFirma.put("nif", certificadoFirma.getNif());
        jsonDatosCertFirma.put("nombre", certificadoFirma.getNombre());
        jsonDatosCertFirma.put("apellidos", certificadoFirma.getApellidos());
        jsonDatosCertFirma.put("fechaNacimiento", certificadoFirma.getFechaNacimiento());
        jsonDatosCertFirma.put("tipo", certificadoFirma.getTipo());
        jsonDatosCertFirma.put("nifRepresentante", certificadoFirma.getNifRepresentante());
        jsonDatosCertFirma.put("nombreRepresentante", certificadoFirma.getNombreRepresentante());
        jsonDatosCertFirma.put("apellidosRepresentante", certificadoFirma.getApellidosRepresentante());
        jsonDatosCertFirma.put("fechaInicioValidez", certificadoFirma.getFechaInicioValidez());
        jsonDatosCertFirma.put("fechaFinValidez", certificadoFirma.getFechaFinValidez());
        jsonDatosCertFirma.put("estado", certificadoFirma.getEstado());
        jsonDatosCertFirma.put("email", certificadoFirma.getEmail());

      }

      Map<Object, Object> jsonDatosCertCA = null;
      if(certificadoCA != null) {
        jsonDatosCertCA = new HashMap<>();
        jsonDatosCertCA.put("nif", certificadoCA.getNumeroSerie());
        jsonDatosCertCA.put("nombre", certificadoCA.getSujeto());
        jsonDatosCertCA.put("fechaInicioValidez", certificadoCA.getFechaInicioValidez());
        jsonDatosCertCA.put("fechaFinValidez", certificadoCA.getFechaFinValidez());
        jsonDatosCertCA.put("estado", certificadoCA.getEstado());
      }

      Map<Object, Object> jsonDatosDatosICAO = null;
      if(datosDNIe.getDatosICAO() != null) {
        jsonDatosDatosICAO = new HashMap<>();
        jsonDatosDatosICAO.put("DG1", datosDNIe.getDatosICAO().getDG1());
        jsonDatosDatosICAO.put("DG2", datosDNIe.getDatosICAO().getDG2());
        jsonDatosDatosICAO.put("DG13", datosDNIe.getDatosICAO().getDG13());
        jsonDatosDatosICAO.put("SOD", datosDNIe.getDatosICAO().getSOD());
      }

      String imagen = null;
      if(datosDNIe.getImagen() != null)
      {
        imagen = ImageUtils.encodeToBase64(datosDNIe.getImagen());
      }

      String firma = null;
      if(datosDNIe.getFirma() != null)
      {
        firma = ImageUtils.encodeToBase64(datosDNIe.getFirma());
      }

      jsonDatosDNIe.put("nif", datosDNIe.getNif());
      jsonDatosDNIe.put("nombreCompleto", datosDNIe.getNombreCompleto());
      jsonDatosDNIe.put("nombre", datosDNIe.getNombre());
      jsonDatosDNIe.put("apellido1", datosDNIe.getApellido1());
      jsonDatosDNIe.put("apellido2", datosDNIe.getApellido2());
      jsonDatosDNIe.put("imagen", imagen);
      jsonDatosDNIe.put("firma", firma);
      jsonDatosDNIe.put("fechaNacimiento", DateUtils.formateaFechaDNIe(datosDNIe.getFechaNacimiento(), "dd/MM/yyyy", activity.getApplicationContext()));
      jsonDatosDNIe.put("provinciaNacimiento", datosDNIe.getProvinciaNacimiento());
      jsonDatosDNIe.put("municipioNacimiento", datosDNIe.getMunicipioNacimiento());
      jsonDatosDNIe.put("nombrePadre", datosDNIe.getNombrePadre());
      jsonDatosDNIe.put("nombreMadre", datosDNIe.getNombreMadre());
      jsonDatosDNIe.put("fechaValidez", DateUtils.formateaFechaDNIe(datosDNIe.getFechaValidez(), "dd/MM/yyyy", activity.getApplicationContext()));
      jsonDatosDNIe.put("emisor", datosDNIe.getEmisor());
      jsonDatosDNIe.put("nacionalidad", datosDNIe.getNacionalidad());
      jsonDatosDNIe.put("sexo", datosDNIe.getSexo());
      jsonDatosDNIe.put("direccion", datosDNIe.getDireccion());
      jsonDatosDNIe.put("provinciaActual", datosDNIe.getProvinciaActual());
      jsonDatosDNIe.put("municipioActual", datosDNIe.getMunicipioActual());
      jsonDatosDNIe.put("numSoporte", datosDNIe.getNumSoporte());
      jsonDatosDNIe.put("certificadoAutenticacion", jsonDatosCertAut);
      jsonDatosDNIe.put("certificadoFirma", jsonDatosCertFirma);
      jsonDatosDNIe.put("certificadoCA", jsonDatosCertCA);
      jsonDatosDNIe.put("integridadDocumento", integridadDocumento);
      jsonDatosDNIe.put("pemCertificadoFirmaSOD", null);
      jsonDatosDNIe.put("datosICAO", jsonDatosDatosICAO);
      jsonDatosDNIe.put("can", can);
    }

    Map<Object, Object> ret = new HashMap<>();
    ret.put("datosDNIe",jsonDatosDNIe);
    ret.put("error", errorText);

    pluginResult.success(ret);
  }

  public void signDNIe(@NonNull MethodCall call, @NonNull Result result) {
    if (!call.hasArgument("accessKey")
            || !call.hasArgument("pin")
            || !call.hasArgument("datosFirma")
            || !call.hasArgument("certToUse")) {
      finsignDNIe(null, "No se ha recibido un parámetro obligatorio");
      return;
    }

    String accessKey = call.argument("accessKey");
    String pin = call.argument("pin");
    String datosFirma = call.argument("datosFirma");
    String certToUse = call.argument("certToUse");

    signDNIe(accessKey, pin, datosFirma, certToUse);
  }

  public void finsignDNIe(String firma, String errorText) {
    Map<Object, Object> ret = new HashMap<>();
    ret.put("firma",firma);
    ret.put("error", errorText);
    pluginResult.success(ret);
  }

  public void signDocumentDNIe(@NonNull MethodCall call, @NonNull Result result) throws JSONException {
    if (!call.hasArgument("accessKey")
            || !call.hasArgument("pin")
            || !call.hasArgument("document")
            || !call.hasArgument("certToUse")) {
        finsignDNIe(null, "No se ha recibido un parámetro obligatorio");
        return;
    }

    String accessKey = call.argument("accessKey");
    String pin = call.argument("pin");
    String document = call.argument("document");
    String certToUse = call.argument("certToUse");


    int digest = 256;
    byte[] hash = new byte[0];
    try {
      hash = calculaHashDocumento(document, digest, activity);
    } catch (IOException | NoSuchAlgorithmException e) {
        finsignDNIe(null, "Se ha producido un error al procesar los datos");
        return;
    }

    signHashDNIe(accessKey, pin, hash, digest, certToUse);

  }

  public void signHashDNIe(@NonNull MethodCall call, @NonNull Result result) throws JSONException {
    if (!call.hasArgument("accessKey")
            || !call.hasArgument("pin")
            || !call.hasArgument("hash")
            || !call.hasArgument("digest")
            || !call.hasArgument("certToUse")) {
        finsignDNIe(null, "No se ha recibido un parámetro obligatorio");
        return;
    }

    String accessKey = call.argument("accessKey");
    String pin = call.argument("pin");
    ArrayList<Integer> jhash = call.argument("hash");
    Integer digest = call.argument("digest");
    String certToUse = call.argument("certToUse");


    int[] ihash = new int[jhash.size()];
    byte[] hash = null;
    for(int i=0; i<jhash.size();i++)
    {
      ihash[i]=jhash.get(i);
    }

    try {
      hash = toByte(ihash);
    } catch (IOException e) {
      finsignDNIe(null, "Se ha producido un error al procesar los datos");
      return;
    }

    signHashDNIe(accessKey, pin, hash, digest, certToUse);
  }

  public Result isNFCEnable(@NonNull MethodCall call, @NonNull Result result) throws JSONException {
    Boolean disponible = true;
    Boolean activo = true;


    NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
    NfcAdapter adapter = manager.getDefaultAdapter();

    if(adapter==null) {
      try{
        disponible = false;
      }
      catch (IllegalStateException ignored) {
        // There's no way to avoid getting this if saveInstanceState has already been called.
      }
    }
    else if (!adapter.isEnabled()) {
      try{
        activo = false;
      }
      catch (IllegalStateException ignored) {
        // There's no way to avoid getting this if saveInstanceState has already been called.
      }
    }

    Map<Object, Object> ret = new HashMap<>();
    ret.put("disponible",disponible);
    ret.put("activo",activo);

    result.success(ret);

    return result;
  }

  public void configure(String apiKey){
    //Not implemented on android
  }

  public String getMRZKey(String passportNumber,String dateOfBirth, String dateOfExpiry){
    return passportNumber+"#"+dateOfBirth+"#"+dateOfExpiry;
  }

  public boolean readPassport(String accessKey, int paceKeyReference, String[] tags, boolean esDNIe){
      boolean recuperaFoto = false;
      boolean recuperaFirma = false;
      if(tags.length==0)
      {
          recuperaFoto = true;
          recuperaFirma = true;
      }
      else {
          for (int i = 0; i < tags.length; i++) {
              if (tags[i].equals("DG2")) {
                  recuperaFoto = true;
              } else if (tags[i].equals("DG7")) {
                  recuperaFirma = true;
              }

          }
      }
      boolean esperaRespuesta = false;
      switch (paceKeyReference) {
          case 1:
              //Open activity to read DNIe
              Intent intentPassport = new Intent(activity, ConsultaPasaporte.class);
              intentPassport.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
              String[] accesKeyArray = accessKey.split("#");
              String passportNumber = accesKeyArray[0];
              String dateOfBirth = accesKeyArray[1];
              String dateOfExpiry = accesKeyArray[2];

              intentPassport.putExtra("passportNumber", passportNumber);
              intentPassport.putExtra("dateOfBirth", dateOfBirth);
              intentPassport.putExtra("dateOfExpiry", dateOfExpiry);
              intentPassport.putExtra("recuperaFoto", recuperaFoto);
              intentPassport.putExtra("recuperaFirma", recuperaFirma);
              activity.startActivityForResult(intentPassport, READ_PASSPORT_CODE);
              esperaRespuesta = true;
              break;
          case 2:

              //Open activity to read DNIe
              Intent intent = new Intent(activity, ConsultaDNIe.class);
              if (!esDNIe)
              {
                  intent = new Intent(activity, ConsultaPasaporteCAN.class);
              }
              intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
              intent.putExtra("CAN", accessKey);
              intent.putExtra("recuperaFoto", recuperaFoto);
              intent.putExtra("recuperaFirma", recuperaFirma);
              activity.startActivityForResult(intent, READ_PASSPORT_CODE);
              esperaRespuesta = true;
            break;
        default:
              //Not implemented
              esperaRespuesta = false;
              break;
      }

      return esperaRespuesta;
  }

    public void signDNIe(String accessKey, String pin, String datosFirma, String certToUse){

        //Open activity to read DNIe
        Intent intent = new Intent(activity, FirmaDNIe.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("CAN", accessKey);
        intent.putExtra("pin", pin);
        intent.putExtra("datosFirma", datosFirma);
        intent.putExtra("certToUse", certToUse);
        activity.startActivityForResult(intent, SIGN_TEXT_CODE);


    }

    public void signHashDNIe(String accessKey, String pin,byte[] hash, int digest, String certToUse){
        //Open activity to read DNIe
        Intent intent = new Intent(activity, FirmaDNIe.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("CAN", accessKey);
        intent.putExtra("pin", pin);
        intent.putExtra("hashFirma", hash);
        intent.putExtra("digest", digest);
        intent.putExtra("certToUse", certToUse);
        activity.startActivityForResult(intent, SIGN_HASH_CODE);

    }

    public byte[] toByte(int[] data) throws IOException {

        byte[] bytes = new byte[data.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) data[i];
        }
        return bytes;
    }

    public byte[] calculaHashDocumento(String documento, int digest, Activity activity) throws IOException, NoSuchAlgorithmException {
        String hashAlgo = "SHA-256";
        switch (digest)
        {
            case 1: hashAlgo = "SHA-1";
                break;
            case 224: hashAlgo = "SHA-224";
                break;
            case 256: hashAlgo = "SHA-256";
                break;
            case 384: hashAlgo = "SHA-384";
                break;
            case 512: hashAlgo = "SHA-512";
                break;

        }

        MessageDigest md = MessageDigest.getInstance(hashAlgo);
        FileInputStream is = new FileInputStream(documento);

        byte[] buffer = new byte[4 * 1024]; // optimal size is 4K
        long available = is.available();
        long processed = 0;

        int read = 0;
        do {
            read = is.read(buffer);
            if (read > 0) {
                md.update(buffer, 0, read);
                processed += read;
                //cb.onProgress(file, processed, available);
            }
        } while (read > 0);

        is.close();

        return md.digest();
    }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
      activity = binding.getActivity();
      binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
      activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    switch (requestCode)
    {
      case READ_PASSPORT_CODE:
        readPassport(resultCode, data);
        break;
      case SIGN_TEXT_CODE:
        signDNIe(resultCode, data);
        break;
      case SIGN_HASH_CODE:
        signDNIe(resultCode, data);
        break;

    }
    return false;
  }

  private void readPassport(int resultCode, Intent data) {
    if(resultCode == Activity.RESULT_OK)
    {
      DatosDNIe datosDNIe = (DatosDNIe) data.getExtras().get("datosDNIe");
      String can = (String) data.getExtras().get("can");
      DatosCertificado certificadoAutenticacion = (DatosCertificado) data.getExtras().get("certificadoAutenticacion");
      DatosCertificado certificadoFirma = (DatosCertificado) data.getExtras().get("certificadoFirma");
      DatosCertificadoFirma certificadoCA = null;
      if(data.getExtras().containsKey("certificadoCA")) {
        certificadoCA = (DatosCertificadoFirma) data.getExtras().get("certificadoCA");
      }
      boolean integridadDocumento = false;
      if(data.getExtras().containsKey("integridadDocumento")) {
        integridadDocumento = (boolean) data.getExtras().get("integridadDocumento");
      }

      finreadPassport(datosDNIe, can, certificadoAutenticacion, certificadoFirma, certificadoCA, integridadDocumento, null);
    }
    else
    {
      String errorText ="Se ha producido un error desconocido";
      if(data != null && data.hasExtra("errorText")) {
        errorText = (String) data.getExtras().get("errorText");
      }

      finreadPassport(null, null, null, null, null, false, errorText);
    }
  }

  private void signDNIe(int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      String firma = (String) data.getExtras().get("firma");
      finsignDNIe(firma, null);
    }
    else
    {
      String errorText ="Se ha producido un error desconocido";
      if(data != null && data.hasExtra("error")) {
        errorText = (String) data.getExtras().get("error");
      }

      finsignDNIe(null, errorText);
    }
  }
}
