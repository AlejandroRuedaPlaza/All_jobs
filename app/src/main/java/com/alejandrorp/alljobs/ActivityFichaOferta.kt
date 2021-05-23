package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_ficha_oferta.*

class ActivityFichaOferta : Metodos() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_oferta)
        setup()
    }

    override fun onStart() {
        super.onStart()

        bt_Edit_Ficha.visibility=View.INVISIBLE
        bt_Del_Ficha.visibility=View.INVISIBLE
    }


    override fun onResume() {
        super.onResume()
        setup()
    }

    private fun setup() {


        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombreOferta", null)

        //recoge los datos de la oferta
        if (nombre != null){
        db.collection("empresas")
            .document(nombre).get().addOnSuccessListener {
                    tV_Nombre_Ficha.text=it.get("name").toString()
                    tV_Categoria_Ficha.text=it.get("categoria").toString()
                    tV_Descrip_Ficha.text=it.get("descripcion").toString()
                    tV_Correo_Ficha.text=it.get("correo").toString()
                    tV_Telef_Ficha.text=it.get("telefono").toString()

                    val creador = it.get("creador").toString()

                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                    val email = prefs.getString("email", null)
                    val userType = prefs.getString("userType", null)
                    if (creador == email.toString() || userType == UserType.ADMIN.toString() ){
                        bt_Edit_Ficha.visibility=View.VISIBLE
                        bt_Del_Ficha.visibility=View.VISIBLE
                    }
//                showAlert(""+" "+bt_Edit_Ficha.getVisibility())
            }
        }
        //BOTON BORRAR
        bt_Del_Ficha.setOnClickListener {
            if (nombre != null){

                val builder = AlertDialog.Builder(this)
                builder.setMessage("¿Estas seguro?")
                builder.setCancelable(false)
                builder.setPositiveButton("Si"){dialog, id->

                    db.collection("empresas").document(nombre).delete()
                    onBackPressed()
                }
                builder.setNegativeButton("No"){dialog, id->
                    dialog.dismiss()
                }
                val alert: AlertDialog =builder.create()
                alert.show()

                }


        }

        //BOTON EDITAR
        bt_Edit_Ficha.setOnClickListener {
            if (nombre != null){
                val intent: Intent = Intent(this, ActivityFormularioOferta::class.java)
            var empresa: Empresa = Empresa()
            db.collection("empresas")
                    .document(nombre).get().addOnSuccessListener {
                        empresa.name=it.get("name").toString()
                        empresa.categoria=it.get("categoria").toString()
                        empresa.descripcion=it.get("descripcion").toString()
                        empresa.correo=it.get("correo").toString()
                        empresa.telefono=it.get("telefono").toString()
                        empresa.creador= it.get("creador").toString()

                        intent.putExtra("datos",empresa)
                        startActivity(intent)
                        finish()
                    }
            }
        }

        bt_Volver_Ficha.setOnClickListener{

            onBackPressed()
        }

    }

}