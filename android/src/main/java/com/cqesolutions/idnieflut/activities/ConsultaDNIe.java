package com.cqesolutions.idnieflut.activities;

import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cqesolutions.idnieflut.R;
import com.cqesolutions.idnieflut.bean.DatosCertificado;
import com.cqesolutions.idnieflut.bean.DatosDNIe;
import com.cqesolutions.idnieflut.utils.CertificateUtils;
import com.cqesolutions.idnieflut.utils.DnieKeyStoreUtils;
import com.cqesolutions.idnieflut.utils.dniedroid.Common;
import com.cqesolutions.idnieflut.utils.pki.Tool;

import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLSocketFactory;

import de.tsenger.androsmex.data.CANSpecDO;
import es.gob.fnmt.dniedroid.gui.PasswordUI;
import es.gob.fnmt.dniedroid.gui.SignatureNotification;
import es.gob.fnmt.dniedroid.help.Loader;
import es.gob.jmulticard.card.baseCard.mrtd.MrtdCard;
import es.gob.jmulticard.jse.provider.DnieLoadParameter;
import es.gob.jmulticard.jse.provider.DnieProvider;

public class ConsultaDNIe extends AppCompatActivity implements NfcAdapter.ReaderCallback, SignatureNotification {
    private DatosDNIe datosDnie = null;
    private X509Certificate x509CertificadoAutenticacion = null;
    private X509Certificate x509CertificadoFirma = null;
    private KeyStore keyStoreDNIe = null;
    //private MyPasswordDialog myPasswordDialog = null;

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

    private static boolean recuperaFoto = false;
    private static boolean recuperaFirma = false;
    //Acceso a FNMT
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quitamos la barra del título
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_consulta_dnie);

        _executor = Executors.newSingleThreadExecutor();
        _handler = new Handler(Looper.getMainLooper());

        _baseInfo = this.findViewById(R.id.base_info);
        _resultInfo = this.findViewById(R.id.result_info);

        _ui_image = findViewById(R.id.dnieImg);
        _ui_dnieanimation = AnimationUtils.loadAnimation(this, R.anim.dnie30_grey);
        _progressBar = findViewById(R.id.progressBar);

        _can = (String) getIntent().getExtras().get("CAN");
        recuperaFoto = (boolean) getIntent().getExtras().get("recuperaFoto");
        recuperaFirma = (boolean) getIntent().getExtras().get("recuperaFirma");

        Common.EnableReaderMode(this);
        getRead();

    }

    //Nuevos métodos
    @Override
    public void onTagDiscovered(Tag tag) {

        reading();

        try {
            boolean existeKeyStore = false;
            if(keyStoreDNIe!=null)
            {
                existeKeyStore = true;
            }
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

            if(!existeKeyStore) {
                recuperaCertificados();

                //Leyendo datos públicos
                updateInfo("Leyendo datos", "Obteniendo datos del DNIe...");
                MrtdCard mrtdCardInfo = initInfo.getMrtdCardInfo();
                DnieKeyStoreUtils dnieKeyStoreUtils = new DnieKeyStoreUtils(mrtdCardInfo);
                datosDnie = dnieKeyStoreUtils.obtenerDatosDNIe(true, recuperaFoto, recuperaFirma);

                //Clase 'Initializer' que nos permite actualizar la BBDD de CAN de la App
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyStore.aliases().nextElement());
                CANSpecDO canSpecDO;
                if (initInfo.getKeyStoreType().equalsIgnoreCase(DnieProvider.KEYSTORE_TYPE_AVAILABLE.get(1))) {
                    canSpecDO = new CANSpecDO(_can, Tool.getCN(certificate), Tool.getNIF(certificate));
                } else {
                    canSpecDO = new CANSpecDO(_can, Tool.getCN(certificate), "");
                }
                Loader.saveCan2DB(canSpecDO, this);

                updateInfo("Leyendo datos", "Mostrando datos...", true, false);
            }
            else {
                updateInfo("Leyendo datos", "Firmando datos...", false, true);
            }

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
            if(muestraDatos)
            {
                DatosCertificado certificadoAutenticacion = CertificateUtils.obtenerTodosDatosCertificado(x509CertificadoAutenticacion);
                DatosCertificado certificadoFirma = CertificateUtils.obtenerTodosDatosCertificado(x509CertificadoFirma);
                Intent data = new Intent();
                data.putExtra("datosDNIe", datosDnie);
                data.putExtra("certificadoAutenticacion", certificadoAutenticacion);
                data.putExtra("certificadoFirma", certificadoFirma);
                data.putExtra("can", _can);

                setResult(RESULT_OK, data);
                finish();
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

}