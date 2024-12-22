package com.cqesolutions.io.idniecap.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cqesolutions.io.idniecap.R;

public class MyPasswordDialog implements es.gob.jmulticard.ui.passwordcallback.DialogUIHandler {

    static private AlertDialog.Builder alertDialogBuilder;
    private final Activity activity;

    /**
     * Flag que indica si se cachea el PIN.
     */
    private final boolean cachePIN;

    /**
     * El password introducido. Si está activado el cacheo se reutilizará.
     */
    private char[] password = null;

    public MyPasswordDialog(final Context context, final boolean cachePIN, char[] password) {

        // Guardamos el contexto para poder mostrar el diálogo
        activity = ((Activity) context);
        this.cachePIN = cachePIN;
        this.password = password;

        // Cuadro de diálogo para confirmación de firmas
        alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setIcon(R.drawable.alert_dialog_icon);

    }

    @Override
    public int showConfirmDialog(String message) {
        return doShowConfirmDialog(message);
    }

    public int doShowConfirmDialog(String message) {
        final AlertDialog.Builder dialog 	= new AlertDialog.Builder(activity);
        final MyPasswordDialog instance 	= this;
        final StringBuilder resultBuilder 	= new StringBuilder();
        resultBuilder.append(message);

        synchronized (instance)
        {
            activity.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    try {
                        dialog.setTitle("Proceso de firma con el DNI electrónico");
                        dialog.setMessage(resultBuilder);
                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                synchronized (instance) {
                                    resultBuilder.delete(0, resultBuilder.length());
                                    resultBuilder.append("0");
                                    instance.notifyAll();
                                }
                            }
                        });
                        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                synchronized (instance) {
                                    resultBuilder.delete(0, resultBuilder.length());
                                    resultBuilder.append("1");
                                    instance.notifyAll();
                                }
                            }
                        });
                        dialog.setCancelable(false);
                        AlertDialog alertDialog = dialog.create();
//                        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
                        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                        alertDialog.show();
                    } catch (es.gob.jmulticard.ui.passwordcallback.CancelledOperationException ex) {
                        android.util.Log.e("Tr@mite", "Excepción en diálogo de confirmación" + ex.getMessage());
                    } catch (Error err) {
                        android.util.Log.e("Tr@mite", "Error en diálogo de confirmación" + err.getMessage()!=null?err.getMessage():"");
                    }
                }
            });
            try
            {
                instance.wait();
                return Integer.parseInt(resultBuilder.toString());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception ex) {
                throw new es.gob.jmulticard.ui.passwordcallback.CancelledOperationException();
            }
        }
    }

    //    @android.annotation.SuppressLint("InflateParams")
    private char[] doShowPasswordDialog(final int retries) {
        final AlertDialog.Builder dialog 	= new AlertDialog.Builder(activity);
        final LayoutInflater inflater 		= activity.getLayoutInflater();
        final StringBuilder passwordBuilder = new StringBuilder();
        final MyPasswordDialog instance 	= this;
        dialog.setTitle(getTriesMessage(retries));

        synchronized (instance)
        {
            activity.runOnUiThread( new Runnable() {

                @Override
                public void run() {
                    try {
//                        final View passwordView = inflater.inflate(R.layout.lib_password_entry, null);
                        final View passwordView = inflater.inflate(R.layout.passwordentry, null);

                        final EditText passwordEdit = (EditText) passwordView.findViewById(R.id.password_edit);
                        final CheckBox passwordShow = (CheckBox) passwordView.findViewById(R.id.checkBoxShow);

                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                            /**
                             * @param dialog El diálogo que genera el evento.
                             * @see DialogInterface.OnClickListener#onClick(DialogInterface,
                             *      int)
                             */
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                synchronized (instance) {
                                    passwordBuilder.delete(0, passwordBuilder.length());
                                    passwordBuilder.append(passwordEdit.getText().toString());
                                    instance.notifyAll();
                                }
                            }
                        });
                        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                            /**
                             * @param dialog El diálogo que genera el evento.
                             * @see DialogInterface.OnClickListener#onClick(DialogInterface,
                             *      int)
                             */
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                synchronized (instance) {
                                    passwordBuilder.delete(0, passwordBuilder.length());
                                    instance.notifyAll();
                                }
                            }
                        });
                        passwordShow.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(final android.widget.CompoundButton buttonView, final boolean isChecked) {
                                if (isChecked) {
                                    passwordEdit.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                                    passwordShow.setText("Mostrar contraseña");
                                } else {
                                    passwordEdit.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                                    passwordShow.setText("Ocultar contraseña");
                                }
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.setView(passwordView);
                        AlertDialog alertDialog = dialog.create();
//                        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
                        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                        alertDialog.show();
                    } catch (Exception ex) {
                        android.util.Log.e("MyPasswordFragment", "Excepción en diálogo de contraseña" + ex.getMessage());
                    } catch (Error err) {
                        android.util.Log.e("MyPasswordFragment", "Error en diálogo de contraseña" + err.getMessage());
                    }
                }
            });
            try
            {
                instance.wait();
                return passwordBuilder.toString().toCharArray();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public char[] showPasswordDialog(final int retries) {
        char[] returning;

        if (retries < 0 && cachePIN && password != null && password.length > 0)
            returning = password.clone();
        else
            returning = doShowPasswordDialog(retries);

        if (cachePIN && returning != null && returning.length > 0)
            password = returning.clone();

        return returning;
    }

    public boolean existePassword() {
        return password!=null;
    }

    /**
     * Genera el mensaje de reintentos del diálogo de contraseña.
     *
     * @param retries El número de reintentos pendientes. Si es negativo, se considera que no se conocen los intentos.
     * @return El mensaje a mostrar.
     */
    private String getTriesMessage(final int retries) {
        String text;
        if (retries < 0) {
            text = "Introduzca PIN.";
        } else if (retries == 1) {
            text = "Introduzca PIN. Queda 1 reintento.";
        } else {
            text = "Introduzca PIN. Quedan " +retries+" reintentos.";
        }
        return text;
    }
}

