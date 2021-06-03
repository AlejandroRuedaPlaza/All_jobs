package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_ficha_oferta.*

class ActivityFichaOferta : Metodos(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    var v: Metodos= Metodos()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ficha_oferta)
        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map_vista) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        //ESCONDE EL MAPA
        val layout: LinearLayout = findViewById(R.id.linearLayout5)
        val params: ViewGroup.LayoutParams = layout.layoutParams
        params.height = 0
        params.width = 0

        layout.layoutParams = params


        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombreOferta", null)

        //recoge los datos de la oferta
        if (nombre != null){
            v.db.collection("empresas")
            .document(nombre).get().addOnSuccessListener {
                    tV_Nombre_Ficha.text=it.get("name").toString()
                    tV_Categoria_Ficha.text=it.get("categoria").toString()
                    tV_Descrip_Ficha.text=it.get("descripcion").toString()
                    tV_Correo_Ficha.text=it.get("correo").toString()
                    tV_Telef_Ficha.text=it.get("telefono").toString()

                    val creador = it.get("creador").toString()

                    //MUESTRA EL LINEARLAYOUT DEL MAPA
                    if(it.get("nameLoc")!=null){
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        params.width = 0

                        layout.layoutParams = params
                        val nameLoc=it.get("nameLoc").toString()
//                        showAlert(nameLoc)

                        tv_UbiName.text= this.getString(R.string.ubiName, nameLoc)

//                        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map_vista) as SupportMapFragment
//                        mapFragment.getMapAsync(this)

                    }


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

                    v.db.collection("empresas").document(nombre).delete()
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
                v.db.collection("empresas")
                    .document(nombre).get().addOnSuccessListener {
                        empresa.name=it.get("name").toString()
                        empresa.categoria=it.get("categoria").toString()
                        empresa.descripcion=it.get("descripcion").toString()
                        empresa.correo=it.get("correo").toString()
                        empresa.telefono=it.get("telefono").toString()
                        empresa.creador= it.get("creador").toString()
                        if (it.get("nameLoc")!=null){
                            empresa.nameLoc=it.get("nameLoc").toString()
                            empresa.latitud=it.get("latitud").toString()
                            empresa.longitud=it.get("longitud").toString()

                            intent.putExtra("datos",empresa)
                            startActivity(intent)
                            finish()
                        }else{
                            intent.putExtra("datos",empresa)
                            startActivity(intent)
                            finish()

                        }


                    }
            }
        }

        bt_Volver_Ficha.setOnClickListener{

            onBackPressed()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
//        map.mapType=MapStyleOptions.loadRawResourceStyle(this, R.style.)
        googleMap.uiSettings.isScrollGesturesEnabled=false
        map = googleMap

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombreOferta", null)

        //recoge los datos de la oferta
        if (nombre != null) {
            v.db.collection("empresas")
                    .document(nombre).get().addOnSuccessListener {


                        if (it.get("nameLoc") != null) {
                            var latitud=(it.get("latitud") as String).toDouble()
                            var longitud=(it.get("longitud") as String).toDouble()
//                           // Add a marker in Sydney and move the camera
                            val location = LatLng(latitud, longitud)
                            map.addMarker(MarkerOptions().position(location).title(it.get("nameLoc").toString()))
//                            map.moveCamera(CameraUpdateFactory.newLatLng(location))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15F))


                        }
                    }
        }
    }

}