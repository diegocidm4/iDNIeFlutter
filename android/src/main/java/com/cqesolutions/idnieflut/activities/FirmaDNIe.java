package com.cqesolutions.idnieflut.activities;

import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cqesolutions.idnieflut.R;
import com.cqesolutions.idnieflut.bean.DatosDNIe;
import com.cqesolutions.idnieflut.gui.MyPasswordDialog;
import com.cqesolutions.idnieflut.utils.dniedroid.Common;

import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.gob.fnmt.dniedroid.gui.PasswordUI;
import es.gob.fnmt.dniedroid.gui.SignatureNotification;
import es.gob.jmulticard.jse.provider.DnieLoadParameter;
import es.gob.jmulticard.jse.provider.DnieProvider;

public class FirmaDNIe extends AppCompatActivity implements NfcAdapter.ReaderCallback, SignatureNotification {

    private DatosDNIe datosDnie = null;
    private X509Certificate x509CertificadoAutenticacion = null;
    private X509Certificate x509CertificadoFirma = null;
    private KeyStore keyStoreDNIe = null;

    private static final int STEP = 10;
    private TextView _baseInfo = null;
    private TextView _resultInfo = null;
    private ImageView _ui_image = null;
    private Animation _ui_dnieanimation = null;
    private ExecutorService _executor;
    private String _can = null;
    private Handler _handler;
    private ProgressBar _progressBar = null;

    private static final DnieProvider dnieProv = new DnieProvider();

    private String pin = null;
    private String datosFirma = null;
    private String certToUse = null;

    private byte[] hashFirma = null;
    private int digest = 256;
    //Acceso a FNMT
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quitamos la barra del título
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_firma_dnie);

        _executor = Executors.newSingleThreadExecutor();
        _handler = new Handler(Looper.getMainLooper());

        _baseInfo = this.findViewById(R.id.base_info);
        _resultInfo = this.findViewById(R.id.result_info);

        _ui_image = findViewById(R.id.dnieImg);
        _ui_dnieanimation = AnimationUtils.loadAnimation(this, R.anim.dnie30_grey);
        _progressBar = findViewById(R.id.progressBar);

        _can = (String) getIntent().getExtras().get("CAN");
        pin = (String) getIntent().getExtras().get("pin");
        certToUse = (String) getIntent().getExtras().get("certToUse");

        if(getIntent().getExtras().containsKey("datosFirma")) {
            datosFirma = (String) getIntent().getExtras().get("datosFirma");
        }
        else
        {
            datosFirma = null;
        }

        if(getIntent().getExtras().containsKey("hashFirma")) {
            hashFirma = (byte[]) getIntent().getExtras().get("hashFirma");
        }
        else
        {
            hashFirma = null;
        }

        if(getIntent().getExtras().containsKey("digest")) {
            digest = (int) getIntent().getExtras().get("digest");
        }
        else
        {
            digest = 256;
        }


        MyPasswordDialog myPasswordDialog = new MyPasswordDialog(this, true, pin.toCharArray());
        PasswordUI.setPasswordDialog(myPasswordDialog); //Establecemos nuestro propio diálogo de petición de PIN.
        PasswordUI.setAppContext(this);

        Common.EnableReaderMode(this);
        getRead();

    }

    //Nuevos métodos
    @Override
    public void onTagDiscovered(Tag tag) {

        reading();

        try {
            //Clase 'Initializer' que nos devuelve directamente el keystore
            //Loader.InitInfo initInfo= Loader.init(new String[]{_can}, tag, this);

            // Atención (mayo 2022):
            //      setCipherState(true)     --> para conexiones realizadas con HTTPClient
            //      setCipherState(false)    --> para conexiones con okHttpClient
            dnieProv.setCipherState(true);

            // Versión DNIeDroid v2.03.109++
            Security.insertProviderAt(dnieProv, 1);

            updateInfo("Leyendo datos", "Obteniendo datos del certificado...");
            DnieLoadParameter initInfo = DnieLoadParameter.getBuilder(new String[]{_can}, tag).build();
            KeyStore keyStore = KeyStore.getInstance(DnieProvider.KEYSTORE_PROVIDER_NAME);
            keyStore.load(initInfo);
            keyStoreDNIe = keyStore;

            recuperaCertificados();
/*
            //Leyendo datos públicos
            updateInfo("Leyendo datos", "Obteniendo datos del DNIe...");
            MrtdCard mrtdCardInfo = initInfo.getMrtdCardInfo();
            DnieKeyStoreUtils dnieKeyStoreUtils = new DnieKeyStoreUtils(mrtdCardInfo);
            datosDnie = dnieKeyStoreUtils.obtenerDatosDNIe(true, false, false);
 */
            updateInfo("Leyendo datos", "Firmando datos...", false, true);

        } catch (Exception e) {
            getReadError("Error leyendo DNIe: "+e.getMessage());
        }


    }

    //Callback para la interfaz es.gob.fnmt.gui.SignatureNotification
    @Override
    public void doNotify(sign_callback_notify notify) {
        final String message;
        switch(notify){
            case SIGNATURE_INIT:
                message = "Iniciando firma, no retire el DNIe del dispositivo NFC.";
                break;
            case SIGNATURE_UPDATE:
                message = "Actualizando datos a firmar.";
                break;
            case SIGNATURE_START:
                message = "Firmando los datos.";
                break;
            case SIGNATURE_DONE:
                message = "Firma realizada, puede retirar el DNIe. Continuando con descarga de datos...";
                break;
            default:
                message = null;
        }
        runOnUiThread(() -> {
            _baseInfo.setText("Proceso de firma");
            if (message != null) {
                _resultInfo.setText(message);
                _resultInfo.setVisibility(VISIBLE);
                _progressBar.incrementProgressBy(STEP);
            }
        });
    }

    /**
     *
     */
    private void getRead(){
        _handler.post(() -> {
            updateInfo("Aproxime el DNIe al dispositivo", null);
            _ui_image.setImageResource(R.drawable.dni30_grey_peq);
            _ui_image.setVisibility(View.VISIBLE);
            _ui_image.startAnimation(_ui_dnieanimation);
            _progressBar.setProgress(0);
            _progressBar.setVisibility(View.GONE);
        });
    }

    /**
     *
     */
    private void getReadError(String extra){
        _handler.post(() -> {
            /*
            updateInfo("Aproxime el DNIe al dispositivo", extra);
            _ui_image.setImageResource(R.drawable.dni30_grey_peq);
            _ui_image.setVisibility(View.VISIBLE);
            _ui_image.startAnimation(_ui_dnieanimation);
            _progressBar.setProgress(0);
            _progressBar.setVisibility(View.GONE);
            */
            Intent data = new Intent();
            data.putExtra("errorText", extra);

            setResult(RESULT_CANCELED, data);
            finish();

        });
    }

    /**
     *
     */
    private void reading(){
        _handler.post(() -> {
            updateInfo("DNIe", "Leyendo DNIe...");
            _ui_image.clearAnimation();
            _ui_image.setImageResource(R.drawable.dni30_peq);
            _ui_image.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Actualización de la información que se muestra en la interfaz de usuario.
     */
    private void updateInfo(final String info){
        updateInfo(info, null, false, false);
    }

    public void updateInfo(final String info, final String extra){
        updateInfo(info, extra, false, false);
    }
    /**
     *
     * @param info
     * @param extra
     */
    public void updateInfo(final String info, final String extra, final boolean muestraDatos, final boolean firmaDatos){
        runOnUiThread(() -> {
            if(info!=null){
                _baseInfo.setText(info);
            }
            if(extra!=null){
                _resultInfo.setVisibility(VISIBLE);
                _resultInfo.setText(extra);
            }else
                _resultInfo.setVisibility(View.GONE);

            if(firmaDatos)
            {
                firmaDatos();
            }
        });
    }

    private void updateProgressDlg(final String title, final String msg){
        updateProgressDlg(title, msg, STEP);
    }
    private void updateProgressDlg(final String title, final String msg, final int step){
        runOnUiThread(() -> {
            if(title != null)_baseInfo.setText(title);
            _resultInfo.setText(msg);
            _progressBar.incrementProgressBy(step);
        });
    }

    private void recuperaCertificados()
    {
        //Limpiamos los datos por si acaso.
        x509CertificadoAutenticacion = null;
        x509CertificadoFirma = null;

        try {
            x509CertificadoAutenticacion = ((X509Certificate) keyStoreDNIe.getCertificate(DnieProvider.AUTH_CERT_ALIAS));
        }catch (Exception ex)
        {

        }

        try{
            x509CertificadoFirma = ((X509Certificate) keyStoreDNIe.getCertificate(DnieProvider.SIGN_CERT_ALIAS));
        }catch (Exception ex)
        {

        }

    }

    public void firmaDatos()
    {
        _executor.execute(() -> {
            byte[] resultado = doInBackground();
            _handler.post(() -> {
                Intent data = new Intent();
                if(resultado != null)
                {
                    data.putExtra("firma", Base64.encodeToString(resultado, Base64.DEFAULT));
                    data.putExtra("error", "");
                    setResult(RESULT_OK, data);
                }
                else
                {

                    data.putExtra("firma", "");
                    data.putExtra("error", "No ha sido posible realizar la firma");
                    setResult(RESULT_CANCELED, data);
                }
                finish();
            });
        });
    }

    public byte[] doInBackground(){
        byte[] resultado = null;
        try {
            String certAlias = DnieProvider.SIGN_CERT_ALIAS;
            if(certToUse.equals("AUTENTICACION"))
            {
                certAlias = DnieProvider.AUTH_CERT_ALIAS;
            }
            final PrivateKey privateKey = (PrivateKey) keyStoreDNIe.getKey(certAlias, pin.toCharArray());

            if(datosFirma != null) {
                resultado = Common.getSignature(privateKey, datosFirma);
            }
            else if(hashFirma != null)
            {
                resultado = Common.getSignature(privateKey, hashFirma, digest);
            }

        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException |
                 UnrecoverableKeyException | KeyStoreException | NoSuchPaddingException |
                IllegalBlockSizeException | BadPaddingException | DigestException e) {
            resultado = null;
        }

        return resultado;
    }

}