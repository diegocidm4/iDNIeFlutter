package com.cqesolutions.idnieflut.activities;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cqesolutions.idnieflut.R;
import com.cqesolutions.idnieflut.bean.DatosCertificadoFirma;
import com.cqesolutions.idnieflut.bean.DatosDNIe;
import com.cqesolutions.idnieflut.bean.DatosICAO;
import com.cqesolutions.idnieflut.utils.CertificateUtils;
import com.cqesolutions.idnieflut.utils.DateUtils;
import com.cqesolutions.idnieflut.utils.dniedroid.Common;

import net.sf.scuba.data.Gender;
import net.sf.scuba.smartcards.CardFileInputStream;
import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;

import org.jmrtd.BACKey;
import org.jmrtd.PACEKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardAccessFile;
import org.jmrtd.lds.DisplayedImageInfo;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SODFile;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.DG7File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tsenger.androsmex.mrtd.DG13;

public class ConsultaPasaporteCAN extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;

    private PendingIntent pendingIntent;
    private TextView _baseInfo = null;
    private TextView _resultInfo = null;
    private ImageView _ui_image = null;
    private Animation _ui_dnieanimation = null;
    private ExecutorService _executor;
    private Handler _handler;

    DatosDNIe datosDnie = new DatosDNIe();
    DatosCertificadoFirma datosCertificado = new DatosCertificadoFirma();
    boolean passiveAuthResult = false;
    private String _can = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quitamos la barra del título
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_consulta_pasaporte);

        //Mantenemos la pantalla encendida para evitar que se desconecte durante la conexión
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        _executor = Executors.newSingleThreadExecutor();
        _handler = new Handler(Looper.getMainLooper());

        _baseInfo = this.findViewById(R.id.base_info);
        _resultInfo = this.findViewById(R.id.result_info);

        _ui_image = findViewById(R.id.dnieImg);
        _ui_dnieanimation = AnimationUtils.loadAnimation(this, R.anim.dnie30_grey);
        _can = (String) getIntent().getExtras().get("CAN");

        Common.EnableReaderMode(this);

        getRead();

    }

    public void onTagDiscovered(Tag tag) {
        //Iniciamos la lectura
        reading();
        datosDnie = new DatosDNIe();
        _executor.execute(() ->{
//            Tag tag = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            if(existeElemento(techList, "android.nfc.tech.IsoDep"))
            {
                if(_can != null
                        && !_can.isEmpty())
                {
                    PACEKeySpec canKey = PACEKeySpec.createCANKey(_can);
                    ReadTask(IsoDep.get(tag), canKey);
                }
                else {
                    getReadError("Los datos introducidos no son correctos para establecer el canal seguro con el documento.");
                }
            }
        });
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    private void ReadTask(IsoDep isoDep, PACEKeySpec canKey)
    {
        reading();
        DG1File dg1File;
        DG2File dg2File;
        DG7File dg7File = null;
        SODFile sodFile;
        String imageBase64 = null;
        Bitmap bitmap = null;

        try {
            isoDep.setTimeout(10000);

            CardService cardService = CardService.getInstance(isoDep);
            cardService.open();

            PassportService service = new PassportService(
                    cardService,
                    PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                    PassportService.DEFAULT_MAX_BLOCKSIZE,
                    false,
                    false);
            service.open();

            boolean paceSucceeded = false;
            try {
                CardAccessFile cardAccessFile = new CardAccessFile(service.getInputStream(PassportService.EF_CARD_ACCESS));
                Collection<SecurityInfo> securityInfoCollection = cardAccessFile.getSecurityInfos();
                for (SecurityInfo securityInfo: securityInfoCollection) {
                    if(securityInfo instanceof PACEInfo)
                    {
                        service.doPACE(canKey, securityInfo.getObjectIdentifier(), PACEInfo.toParameterSpec(((PACEInfo) securityInfo).getParameterId()), null);
                        paceSucceeded = true;
                    }
                }
            }
            catch (Exception ex)
            {
                Log.println(Log.ERROR, "READNFC",ex.getLocalizedMessage());
            }

            service.sendSelectApplet(paceSucceeded);
            if(!paceSucceeded)
            {
                try {
                    service.getInputStream(PassportService.EF_COM).read();
                }
                catch (Exception ex)
                {
                    service.doBAC(canKey);
                }
            }

            CardFileInputStream dg1In = service.getInputStream(PassportService.EF_DG1);
            dg1File = new DG1File(dg1In);

            String nombre = dg1File.getMRZInfo().getSecondaryIdentifier();
            String apellidos = dg1File.getMRZInfo().getPrimaryIdentifier();
            apellidos = apellidos.replaceAll(" ", "<");
            int pos = apellidos.indexOf("<");
            String apellido1 = "";
            String apellido2 = "";
            if(pos>0)
            {
                apellido1 = apellidos.substring(0, pos);
                apellido2 = apellidos.substring(pos+1);
            }
            else
            {
                apellido1 = apellidos;
            }
            nombre = nombre.replaceAll("<", " ");

            datosDnie.setNif(dg1File.getMRZInfo().getPersonalNumber());
            datosDnie.setNombre(nombre);
            datosDnie.setApellido1(apellido1);
            datosDnie.setApellido2(apellido2);
            datosDnie.setNombreCompleto(apellido1+" "+apellido2+" "+nombre);
            datosDnie.setFechaNacimiento(dg1File.getMRZInfo().getDateOfBirth());
            datosDnie.setFechaValidez(dg1File.getMRZInfo().getDateOfExpiry());
            Gender gender = dg1File.getMRZInfo().getGender();

            String sexo = "";
            switch (gender)
            {
                case MALE:
                    sexo = "M";
                    break;
                case FEMALE:
                    sexo = "F";
                    break;
                case UNKNOWN:
                    sexo = "U";
                    break;
                case UNSPECIFIED:
                    sexo = "U";
                    break;

            }
            datosDnie.setSexo(sexo);
            datosDnie.setNacionalidad(dg1File.getMRZInfo().getNationality());
            //datosDNIe.setEmisor(m_dg1.getIssuer());

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    CardFileInputStream dg13In = service.getInputStream(PassportService.EF_DG13);
                    DG13 dg13 = null;
                    dg13 = new DG13(dg13In.readAllBytes());
                    String name = dg13.getName();
                    String surname1 = dg13.getSurName1();
                    String surname2 = dg13.getSurName2();
                    datosDnie.setNombreCompleto(surname1 + " " + surname2 + " " + name);
                    datosDnie.setMunicipioNacimiento(dg13.getBirthPopulation());
                    datosDnie.setProvinciaNacimiento(dg13.getBirthProvince());
                    datosDnie.setApellido1(dg13.getSurName1());
                    datosDnie.setApellido2(dg13.getSurName2());
                    datosDnie.setNombrePadre(dg13.getFatherName());
                    datosDnie.setNombreMadre(dg13.getMotherName());
                    datosDnie.setFechaValidez(dg13.getExpirationDate().replaceAll(" ", "/"));
                    datosDnie.setDireccion(dg13.getActualAddress());
                    datosDnie.setProvinciaActual(dg13.getActualProvince());
                    datosDnie.setMunicipioActual(dg13.getActualPopulation());
                }
            }
            catch (Exception ex)
            {
                //No hacemos nada si hay una excepción
            }

            //Foto
            CardFileInputStream dg2In = service.getInputStream(PassportService.EF_DG2);
            dg2File = new DG2File(dg2In);
            List<FaceImageInfo> allFaceImageInfo = new ArrayList<>();
            List<FaceInfo> faceInfos = dg2File.getFaceInfos();
            for (FaceInfo faceInfo: faceInfos) {
                allFaceImageInfo.addAll(faceInfo.getFaceImageInfos());
            }
            if(!allFaceImageInfo.isEmpty())
            {
                FaceImageInfo faceImageInfo = allFaceImageInfo.get(0);
                int imageLength = faceImageInfo.getImageLength();
                DataInputStream dataInputStream = new DataInputStream(faceImageInfo.getImageInputStream());
                byte[] buffer = new byte[imageLength];
                dataInputStream.readFully(buffer, 0, imageLength);
//                InputStream inputStream = new ByteArrayInputStream(buffer, 0, imageLength);
//                bitmap = decodeImage(this@MainActivity, faceImageInfo.mimeType, inputStream)
                datosDnie.setImagen(buffer);
                imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT);
            }

            //Firma
            try {
                CardFileInputStream dg7In = service.getInputStream(PassportService.EF_DG7);
                dg7File = new DG7File(dg7In);
                List<DisplayedImageInfo> allDisplayedImageInfo = dg7File.getImages();
                if(!allDisplayedImageInfo.isEmpty())
                {
                    DisplayedImageInfo displayedImageInfo = allDisplayedImageInfo.get(0);
                    int imageLength = displayedImageInfo.getImageLength();
                    DataInputStream dataInputStream = new DataInputStream(displayedImageInfo.getImageInputStream());
                    byte[] buffer = new byte[imageLength];
                    dataInputStream.readFully(buffer, 0, imageLength);
                    datosDnie.setFirma(buffer);
                    //imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT);
                }

            }
            catch (Exception ex)
            {
                //No hacemos nada si hay una excepción
            }

            CardFileInputStream sodIn = service.getInputStream(PassportService.EF_SOD);
            sodFile = new SODFile(sodIn);

            //Recuperamos los datos del certificado con el que se firman los datos del documento
            X509Certificate certificadoPublico = sodFile.getDocSigningCertificate();
            datosCertificado = CertificateUtils.obtenerDatosCertificadoFirma(certificadoPublico);
            //Comprobamos la autenticación pasiva
            passiveAuthResult = doPassiveAuth(sodFile, dg1File, dg2File);


            //Recuperamos datos ICAO en base64
            DatosICAO datosICAO = new DatosICAO();
            datosICAO.setDG1(Base64.encodeToString(dg1File.getEncoded(), Base64.DEFAULT));
            datosICAO.setDG2(Base64.encodeToString(dg2File.getEncoded(), Base64.DEFAULT));
            datosICAO.setSOD(Base64.encodeToString(sodFile.getEncoded(), Base64.DEFAULT));
            datosDnie.setDatosICAO(datosICAO);

            respuestaPlugin(datosDnie, datosCertificado, passiveAuthResult);


        } catch (CardServiceException | IOException e) {
            getReadError(e.getLocalizedMessage());
            return;
        }


    }

    private boolean doPassiveAuth(SODFile sodFile, DG1File dg1File, DG2File dg2File)
    {
        boolean resultado = false;
        try {
            MessageDigest digest = MessageDigest.getInstance(sodFile.getDigestAlgorithm());
            Map<Integer, byte[]> dataHashes = sodFile.getDataGroupHashes();

            byte[] dg1Hash = digest.digest(dg1File.getEncoded());
            byte[] dg2Hash = digest.digest(dg2File.getEncoded());

            if(Arrays.equals(dg1Hash, dataHashes.get(1)) && Arrays.equals(dg2Hash, dataHashes.get(2)))
            {
                List<X509Certificate> certChain =  sodFile.getDocSigningCertificates();
                X509Certificate certificadoPublico = sodFile.getDocSigningCertificate();
                String sodDigestEncryptionAlgorithm = certificadoPublico.getSigAlgName();

                byte[] contenido =  sodFile.getEContent();
                byte[] contenidoEncriptado =  sodFile.getEncryptedDigest();

                boolean isSSA = false;
                if ((sodDigestEncryptionAlgorithm == "SSAwithRSA/PSS")) {
                    sodDigestEncryptionAlgorithm = "SHA256withRSA/PSS";
                    isSSA = true;
                }
                Signature sign = Signature.getInstance(sodDigestEncryptionAlgorithm);
                if (isSSA) {
                    sign.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
                }
                sign.initVerify(certificadoPublico);
                sign.update(contenido);
                resultado = sign.verify(contenidoEncriptado);

            }
            else
            {
                //Si no coinciden los hash de los datagroups con los hash del datagroup SOD devolvemos error.
                resultado = false;
            }
        }
        catch (Exception ex)
        {

        }

        return resultado;
    }

    private void getRead(){
        _handler.post(() -> {
            updateInfo("Aproxime el eID al dispositivo", null);
            _ui_image.setImageResource(R.drawable.dni30_grey_peq);
            _ui_image.setVisibility(View.VISIBLE);
            _ui_image.startAnimation(_ui_dnieanimation);
        });
    }

    /**
     *
     */
    private void getReadError(String extra){
        _handler.post(() -> {
            /*
            updateInfo("Aproxime el eID al dispositivo", extra);
            _ui_image.setImageResource(R.drawable.dni30_grey_peq);
            _ui_image.setVisibility(View.VISIBLE);
            _ui_image.startAnimation(_ui_dnieanimation);
            */
            Intent data = new Intent();
            data.putExtra("errorText", extra);

            setResult(RESULT_CANCELED, data);
            finish();

        });
    }

    private void reading(){
        _handler.post(() -> {
            updateInfo("Documento electrónico de identificación", "Leyendo eID…");
            _ui_image.clearAnimation();
            _ui_image.setImageResource(R.drawable.dni30_peq);
            _ui_image.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Actualización de la información que se muestra en la interfaz de usuario.
     */
    private void updateInfo(final String info){
        updateInfo(info, null, false);
    }

    public void updateInfo(final String info, final String extra){
        updateInfo(info, extra, false);
    }
    /**
     *
     * @param info
     * @param extra
     */
    public void updateInfo(final String info, final String extra, final boolean muestraDatos){
        runOnUiThread(() -> {
            if(info!=null){
                _baseInfo.setText(info);
            }
            if(extra!=null){
                _resultInfo.setVisibility(VISIBLE);
                _resultInfo.setText(extra);
            }else
                _resultInfo.setVisibility(View.GONE);
        });
    }

    private static boolean existeElemento(String[] arr, String toCheckValue)
    {
        // check if the specified element
        // is present in the array or not
        // using Linear Search method
        boolean result = false;
        for (String element : arr) {
            if (element.equals(toCheckValue)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private void respuestaPlugin(DatosDNIe datosDnie, DatosCertificadoFirma datosCertificado, boolean passiveAuthResult) {
        Intent data = new Intent();
        data.putExtra("datosDNIe", datosDnie);
        data.putExtra("certificadoCA", datosCertificado);
        data.putExtra("integridadDocumento", passiveAuthResult);
        setResult(RESULT_OK, data);
        finish();

    }
}