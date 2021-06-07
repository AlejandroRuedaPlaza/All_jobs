package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_formulario_oferta.*

class ActivityFormularioOferta : Metodos() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_oferta)

        //SPINNER
        //1.-Creamos el adapter desde el recurso y se lo asociamos
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,R.array.sectorTrabajo,R.layout.spinner_layout)
        sp_FormuEMP.adapter = adapter2
        sp_FormuEMP.setSelection(0)
        //2.- Creamos el listener
        sp_FormuEMP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //cambia el recyclerView
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        borrarPrefs()
        setup()
    }

    override fun onResume() {
        super.onResume()
        setup()
    }

    fun borrarPrefs() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("nameLoc", null)
        prefs.putString("latitud", null)
        prefs.putString("longitud", null)
        prefs.apply()
    }

    private fun setup() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val nameLoc = prefs.getString("nameLoc", null)
        val latitud = prefs.getString("latitud", null)
        val longitud = prefs.getString("longitud", null)

        if(nameLoc!=null && latitud != null && longitud != null){
            tv_Ubicacion.setText(nameLoc.toString())
//            showAlert(nameLoc+" "+latitud+" "+longitud,this)


        }

        //==================================================================================
        //EDITAR EMPRESA
        val empresa=intent.getSerializableExtra("datos") as? Empresa
        if(empresa != null){
//            showAlert(empresa.toString()+"afaa")
            eT_Name_Form.setText(empresa.name)
            eT_Name_Form.isEnabled=false
            eT_TelefCont_Form.setText(empresa.telefono)
            eT_Correo_Form.setText(empresa.correo)
            eT_Descripcion_Form.setText(empresa.descripcion)
            val array: Array<String> = resources.getStringArray(R.array.sectorTrabajo)
            sp_FormuEMP.setSelection(getIndex(array,empresa.categoria))

            if (empresa.nameLoc.isNotEmpty()){
//                tv_Ubicacion.setText(empresa.nameLoc)

                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("nameLoc", empresa.nameLoc)
                prefs.putString("latitud", empresa.latitud)
                prefs.putString("longitud", empresa.longitud)
                prefs.apply()
            }
            bt_CrearEMP.setText(R.string.editar)


        }

        //==================================================================================

        bt_VolverEMP.setOnClickListener {
            borrarPrefs()
            onBackPressed()
        }

        bt_SelecUbi.setOnClickListener {
            val intent: Intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)

        }

        bt_CrearEMP.setOnClickListener {
//            showAlert(nameLoc+"")
//            Thread.sleep(4000)
            //PARTE DE EDICION
            if (empresa != null) {
//                db.collection("users").document(email).update("baja", true)
                if(nameLoc!=null && latitud != null && longitud != null){
                    db.collection("empresas").document(empresa.name).update(
                            mapOf("telefono" to eT_TelefCont_Form.text.toString()
                                    ,"descripcion" to eT_Descripcion_Form.text.toString()
                                    ,"categoria" to sp_FormuEMP.selectedItem.toString()
                                    ,"correo" to eT_Correo_Form.text.toString()
                                    ,"nameLoc" to nameLoc.toString()
                                    ,"latitud" to latitud.toString()
                                    ,"longitud" to longitud.toString())
                    )

                }else{
                    db.collection("empresas").document(empresa.name).update(
                            mapOf("telefono" to eT_TelefCont_Form.text.toString()
                                    ,"descripcion" to eT_Descripcion_Form.text.toString()
                                    ,"categoria" to sp_FormuEMP.selectedItem.toString()
                                    ,"correo" to eT_Correo_Form.text.toString())
                    )
                }


                finish()
            }
            //PARTE DE CREACION
            else{
            //COMPRUEBA QUE NO HAYA CAMPOS VACIOS
                if (eT_Name_Form.text.isNotEmpty() && eT_Correo_Form.text.isNotEmpty() &&
                        eT_TelefCont_Form.text.isNotEmpty() && eT_Descripcion_Form.text.isNotEmpty()) {

                    //COMPRUEBA QUE NO EXISTA
                    db.collection("empresas")
                            .document(eT_Name_Form.text.toString()).get().addOnSuccessListener {


                                if (it.exists()) {//EXISTE LA EMPRESA
                                    showAlert("Ya existe una oferta de esta empresa, introduzca otra empresa")

                                } else {//NO EXISTE LA EMPRESA
                                    val array: Array<String> = resources.getStringArray(R.array.sectorTrabajo)
                                    if (sp_FormuEMP.selectedItem == array.get(0))
                                        showAlert("Seleccione una categoria")
                                    else {
                                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                                        val email = prefs.getString("email", null)

                                        if(nameLoc!=null && latitud != null && longitud != null){
                                            //añade a la BD de users el provider y el tipo de usuario usando como PK el email
                                            FirebaseFirestore.getInstance().collection("empresas").document(eT_Name_Form.text.toString()).set(
                                                    hashMapOf("name" to eT_Name_Form.text.toString()
                                                            ,"correo" to eT_Correo_Form.text.toString()
                                                            , "telefono" to eT_TelefCont_Form.text.toString()
                                                            , "categoria" to sp_FormuEMP.selectedItem
                                                            , "descripcion" to eT_Descripcion_Form.text.toString()
                                                            , "creador" to email
                                                            , "nameLoc" to nameLoc.toString()
                                                            , "latitud" to latitud.toString()
                                                            , "longitud" to longitud.toString())
                                            )

                                        }else{
                                            //añade a la BD de users el provider y el tipo de usuario usando como PK el email
                                            FirebaseFirestore.getInstance().collection("empresas").document(eT_Name_Form.text.toString()).set(
                                                    hashMapOf("name" to eT_Name_Form.text.toString(),
                                                            "correo" to eT_Correo_Form.text.toString(), "telefono" to eT_TelefCont_Form.text.toString(),
                                                            "categoria" to sp_FormuEMP.selectedItem, "descripcion" to eT_Descripcion_Form.text.toString(),
                                                            "creador" to email)
                                            )
                                        }



                                        onBackPressed()
                                    }
                                }


                            }

                }else{
                    showAlert("Atencion","Rellene todos los campos")
                }


        }
        }
    }

    //OBTIENE LA POSICION DE LA CADENA DE UN ARRAY
    private fun getIndex(array: Array<String>, get: String): Int {
        val n:Int=array.size
        for (i in 0..n){
            if (array.get(i).toString().equals(get)){
                return i

            }
        }

        return 0

    }


}