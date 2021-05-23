package com.alejandrorp.alljobs

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.activity_lista_usu.*


data class User(
    val correo: String = "",
    val provider: String = "",
    val userType: String = "",
    val imgInByte: Blob? = null,
    val baja: String = "null"

    )

//class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

open class ActivityListaUsu : Metodos() {

    lateinit var miAdapter: UserAdapter
    val listaFicha: ArrayList<User> = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usu)
        setup()
        traerDatos()
        inicializarAdapter()
    }

    private fun setup() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val userType = prefs.getString("userType", null)

        bt_Buscar_LUsu.setOnClickListener {
            if (et_Buscar_LUsu!=null&&et_Buscar_LUsu.text.isNotEmpty()){
                busqueda(et_Buscar_LUsu)
            }
            else{
                traerDatos()
                inicializarAdapter()
            }
        }

    }

    private fun busqueda(et: EditText){

        listaFicha.clear()
        db.collection("users").get().addOnSuccessListener {
            it.documents.map {
                val correo = it.get("correo") as String
                val provider= it.get("provider") as String
                val userType = it.get("userType") as String
                val baja:String =""+ it.get("baja") as Boolean

                var texto=""

                when (et.text.toString().toLowerCase()){
                    "baja" -> {texto="true"
                        cargarfiltro(correo,provider,userType,baja,texto,et,it)}
                    "alta" ->{ texto="false"
                        cargarfiltro(correo,provider,userType,baja,texto,et,it)}
                    else ->{
                        cargarfiltro(correo,provider,userType,baja,texto,et,it)}

                }


            }
        }

    }
    //CARGA LOS DATOS EN EL RECYCLER MEDIANTE EL FILTRO
    private fun cargarfiltro(correo: String, provider: String, userType: String, baja: String,texto:String, et: EditText, it:DocumentSnapshot){

            if(correo.toLowerCase().contains(et.text.toString())
                    ||provider.toLowerCase().contains(et.text.toString())
                    ||userType.toLowerCase().contains(et.text.toString())
                    ||baja.toLowerCase()==(texto)
            ){
                val correo = it.get("correo") as String
                val provider= it.get("provider") as String
                val userType = it.get("userType") as String
                val imgInByte= it.getBlob("imgInByte")
                val baja:String =""+ it.get("baja") as Boolean

                var ficha= User(correo,provider,userType,imgInByte,baja)
                listaFicha.add(ficha)
                inicializarAdapter()
            }
            if (listaFicha.size==0){
                listaFicha.clear()
                inicializarAdapter()
            }

    }



    //RECOGE DATOS DEPENDIENDO DEL SPINNER
    private fun traerDatos() {

        listaFicha.clear()
        db.collection("users").get().addOnSuccessListener {
            it.documents.map {

                    val correo = it.get("correo") as String
                    val provider= it.get("provider") as String
                    val userType = it.get("userType") as String
                    val imgInByte= it.getBlob("imgInByte")
                    val baja:String =""+ it.get("baja") as Boolean

                    var ficha= User(correo,provider,userType,imgInByte,baja)
                    listaFicha.add(ficha)
                    miAdapter.notifyDataSetChanged()

            }



        }

    }

    private fun inicializarAdapter() {

        recView_LUsu.setHasFixedSize(true)
        miAdapter=UserAdapter(listaFicha,this)
        recView_LUsu.adapter=miAdapter
        recView_LUsu.layoutManager=LinearLayoutManager(this)
    }

}