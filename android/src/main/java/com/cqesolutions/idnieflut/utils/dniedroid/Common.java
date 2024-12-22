package com.cqesolutions.io.idniecap.utils.dniedroid;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;

import java.io.IOException;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.gob.jmulticard.jse.provider.DnieProvider;

public class Common {
    /**
     *
     * @param activity
     * @return
     */
    public static NfcAdapter EnableReaderMode (Activity activity)
    {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        Bundle options = new Bundle();
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000);
        nfcAdapter.enableReaderMode(activity,
                (NfcAdapter.ReaderCallback) activity,
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK 	|
                        NfcAdapter.FLAG_READER_NFC_A 	|
                        NfcAdapter.FLAG_READER_NFC_B,
                options);

        return nfcAdapter;
    }

    /**
     *
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     */

    public static byte[] getSignature(PrivateKey privateKey, String datosFirmar)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String algorithm = "SHA256withRSA";
        Signature signatureEngine = Signature.getInstance(algorithm,new DnieProvider());
        signatureEngine.initSign(privateKey);
        signatureEngine.update(datosFirmar.getBytes());
        return signatureEngine.sign();
    }

    public static byte[] getSignature(PrivateKey privateKey, byte[] datosFirmar, int digest) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException, DigestException {
        String algorithm = "NONEwithRSA";
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
        Signature signatureEngine = Signature.getInstance(algorithm,new DnieProvider());
        signatureEngine.initSign(privateKey);
        signatureEngine.update(wrapForRsaSign(datosFirmar, hashAlgo));
        //signatureEngine.update(datosFirmar);
        return signatureEngine.sign();
    }

    private static byte[] wrapForRsaSign(byte[] dig, String hashAlgo) throws DigestException {
        ASN1ObjectIdentifier oid = new DefaultDigestAlgorithmIdentifierFinder().find(hashAlgo).getAlgorithm();
        ASN1Sequence oidSeq = new DERSequence(new ASN1Encodable[] { oid, DERNull.INSTANCE });
        ASN1Sequence seq = new DERSequence(new ASN1Encodable[] { oidSeq, new DEROctetString(dig) });
        try {
            return seq.getEncoded();
        } catch (IOException e) {
            throw new DigestException(e);
        }
    }

    /**
     *
     * @param title
     * @param message
     */
    /*
    public static void showDialog(Context context, String title, String message){
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
     */
}
