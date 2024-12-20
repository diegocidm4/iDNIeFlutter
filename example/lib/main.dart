import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:idnieflut/beans.dart';
import 'package:idnieflut/idnieflut.dart';
import 'package:crypto/crypto.dart';
import 'package:file_picker/file_picker.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Plugin example app')),
        body: const MyWidget(),
      ),
    );
  }
  
}

class MyWidget extends StatefulWidget {
  const MyWidget({super.key});

  @override
  State<MyWidget> createState() => _MyAppState();
}

class _MyAppState extends State<MyWidget> {
  final _idnieflutPlugin = Idnieflut();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final canController = TextEditingController();
  final pinController = TextEditingController();

  @override
  void dispose() {
    // Clean up the controller when the widget is disposed.
    canController.dispose();
    pinController.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    initiDNIe();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initiDNIe() async {
    var estadoLicencia = await _idnieflutPlugin.configure("");
    

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

  }

  void _leeDNIe(BuildContext ctx, String can) async
  {
    List<String> tags =[];

    var respuestaReadPassport = await _idnieflutPlugin.readPassport(can, PACEHandler().CAN_PACE_KEY_REFERENCE, tags);

    var texto = "";
    
    if(respuestaReadPassport?.datosDNIe != null)
    {
      texto = respuestaReadPassport!.datosDNIe!.nif + " " +respuestaReadPassport!.datosDNIe!.nombreCompleto;
    }
    else
    {
      texto = respuestaReadPassport?.error ?? "Error no especificado";
    }

    showMyDialog(ctx, "Aviso", texto);
  }

  void _firmaTexto(BuildContext ctx, String can, String pin) async
  {
      List<String> tags =[];
      tags.add(DataGroupId().DG1);
      tags.add(DataGroupId().DG11);
      tags.add(DataGroupId().DG13);

    var respuestaReadPassport = await _idnieflutPlugin.readPassport(can, PACEHandler().CAN_PACE_KEY_REFERENCE, tags);

    
    if(respuestaReadPassport?.datosDNIe != null)
    {
      sleep(Duration(seconds:5));

      var respuestaFirma = await _idnieflutPlugin.signTextDNIe(can, pin, "Texto a firmar", DNIeCertificates().FIRMA); 

      if(respuestaFirma?.firma != null)
      {
        showMyDialog(ctx, "Firma realizada", respuestaFirma?.firma ?? "");  
      }
      else{
          var texto = respuestaFirma?.error ?? "Error no especificado";
          showMyDialog(ctx, "Error", texto);
      }
    }
    else
    {
      var texto = respuestaReadPassport?.error ?? "Error no especificado";
      showMyDialog(ctx, "Error", texto);
    }
    
  }

  void _firmaHash(BuildContext ctx, String can, String pin) async
  {
      List<String> tags =[];
      tags.add(DataGroupId().DG1);
      tags.add(DataGroupId().DG11);
      tags.add(DataGroupId().DG13);

    var respuestaReadPassport = await _idnieflutPlugin.readPassport(can, PACEHandler().CAN_PACE_KEY_REFERENCE, tags);

    
    if(respuestaReadPassport?.datosDNIe != null)
    {
      sleep(Duration(seconds:5));

      var textoFirma = "Texto a firmar";

      var textoInBytes = utf8.encode(textoFirma);  
      var digest = sha256.convert(textoInBytes);      

      List<int> lista = [];
      for (var byte in digest.bytes) {
        lista.add(byte);
      }

      var respuestaFirma = await _idnieflutPlugin.signHashDNIe(can, pin, lista, DigestType().SHA256, DNIeCertificates().FIRMA); 

      if(respuestaFirma?.firma != null)
      {
        showMyDialog(ctx, "Firma realizada", respuestaFirma?.firma ?? "");  
      }
      else{
          var texto = respuestaFirma?.error ?? "Error no especificado";
          showMyDialog(ctx, "Error", texto);
      }

    }
    else
    {
      var texto = respuestaReadPassport?.error ?? "Error no especificado";
      showMyDialog(ctx, "Error", texto);
    }
    
  }

  void _firmaDocumento(BuildContext ctx, String can, String pin) async
  {
    List<String> tags =[];
    tags.add(DataGroupId().DG1);
    tags.add(DataGroupId().DG11);
    tags.add(DataGroupId().DG13);


    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['pdf'],
      );
 
    if (result != null) {
      String documento = result.files.single.path ?? "";

      var respuestaReadPassport = await _idnieflutPlugin.readPassport(can, PACEHandler().CAN_PACE_KEY_REFERENCE, tags); 
      
      if(respuestaReadPassport?.datosDNIe != null)
      {
        sleep(Duration(seconds:5));

        var respuestaFirma = await _idnieflutPlugin.signDocumentDNIe(can, pin, documento, DNIeCertificates().FIRMA); 

        if(respuestaFirma?.firma != null)
        {
          showMyDialog(ctx, "Firma realizada", respuestaFirma?.firma ?? "");  
        }
        else{
            var texto = respuestaFirma?.error ?? "Error no especificado";
            showMyDialog(ctx, "Error", texto);
        }        
      }
      else
      {
        var texto = respuestaReadPassport?.error ?? "Error no especificado";
        showMyDialog(ctx, "Error", texto);
      }     
    } 
    else 
    {
      showMyDialog(ctx, "Aviso", "Fichero no seleccionado");
    }

  }

  @override
  Widget build(BuildContext context) {
    return Form(
          key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 5.0),
            child: TextFormField(
              controller: canController,
              decoration: const InputDecoration(
                hintText: 'Introduzca el código CAN',
              ),
              validator: (String? value) {
                if (value == null || value.isEmpty || value.length != 6) {
                  return 'Introduzca un número de 6 dígitos';
                }
                return null;
              },
            )
          ),          
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 5.0),
            child: TextFormField(
              controller: pinController,
              decoration: const InputDecoration(
                hintText: 'Introduzca el PIN del DNIe',
              ),
            )
          ),          
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 5.0),
            child: ElevatedButton(
              onPressed: ()  
              {
                // Validate will return true if the form is valid, or false if
                // the form is invalid.
                if (_formKey.currentState!.validate()) {
                    _leeDNIe(context, canController.text);
                }                
              },
              child: const Text('Leer DNIe'),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 5.0),
            child: ElevatedButton(
              onPressed: ()  
              {
                 _firmaTexto(context, canController.text, pinController.text);
              },
              child: const Text('Firma Texto'),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 5.0),
            child: ElevatedButton(
              onPressed: ()  
              {
                 _firmaHash(context, canController.text, pinController.text);
              },
              child: const Text('Firma Hash'),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 5.0),
            child: ElevatedButton(
              onPressed: ()  
              {                 
                 _firmaDocumento(context, canController.text, pinController.text);
              },
              child: const Text('Firma Documento'),
            ),
          ),

        ],
      ),
      );
  }

  void showMyDialog(BuildContext context, String titulo, String contenido) async{
    Widget aceptarButton = TextButton(
      child: Text("Aceptar"),
      onPressed: () {
        Navigator.of(context).pop();
      },
    );

    AlertDialog alert = AlertDialog(
      title: Text(titulo),
      content: Text(contenido),
      actions: [
        aceptarButton,
      ],
    );

    showDialog(
      context: context, 
      builder: (context) {
        return alert;
      }
    );
  }

}

