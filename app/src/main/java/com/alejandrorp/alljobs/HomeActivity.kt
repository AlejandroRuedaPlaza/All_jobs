package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_formulario_oferta.*
import kotlinx.android.synthetic.main.activity_home.*

object REQ{
    val REQ0= 9000
    val REQ1= 9001
}

enum class ProviderType{
    BASIC,
    GOOGLE
}

enum class UserType{
    ADMIN,
    OFERTANTE,
    DEMANDANTE
}
// data class Empresas(
//    val name: String = "",
//    val categoria: String = "",
//    val descripcion: String = ""
//)
class EmpresaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

open class HomeActivity : Metodos() {
    override fun onBackPressed() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AllJobs)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")
        val provider:String?=bundle?.getString("provider")
        var userType:String?=null
        if (email != null) {
            FirebaseFirestore.getInstance().collection("users").document(email).get().addOnSuccessListener {
                userType=(it.get("userType") as String?)
//                textView3.text=userType

                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("userType", userType)
                prefs.apply()
            }
        }

//        setup(email?:"",provider?:"")

        //Guardado de datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()


        //SPINNER
        //1.-Creamos el adapter desde el recurso y se lo asociamos
        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,R.array.sectorTrabajo,R.layout.spinner_layout)
        sp_FiltroSector.adapter = adapter2
        //2.- Creamos el listener
        sp_FiltroSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                //cambia el recyclerView
//                showAlert(parent?.getItemAtPosition(position).toString())
//                tV_Paises02.text = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        onItemSelect()
        ponerListener()

    }

    override fun onResume() {
        super.onResume()
        traerDatos()
    }

    //LISTENER AL DESLIZAR EL RECVIEW
    val listaFicha: ArrayList<Empresa> = ArrayList<Empresa>()
    private fun ponerListener() {
        val itemTouchCallBack= object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT->{
                        //si he arrastrado a la derecha

                        val intent: Intent = Intent(this@HomeActivity, ActivityFichaOferta::class.java)

//                        var datos:Bundle = Bundle()
//                        datos.putString("nombre", )
//                        intent.putExtras(datos)

                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("nombreOferta", listaFicha[viewHolder.adapterPosition].name)
                        prefs.apply()

                        startActivity(intent)
                    }
                }
                return
            }

        }
        val ith= ItemTouchHelper(itemTouchCallBack)
        ith.attachToRecyclerView(recView)
    }

    //CAMBIA EL RECYCLERVIEW AL SELECIONAR EN EL SPINNER
    private fun onItemSelect(){
        sp_FiltroSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                traerDatos()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }


    //RECOGE DATOS DEPENDIENDO DEL SPINNER
    private fun traerDatos() {
        listaFicha.clear()
        val array: Array<String> = resources.getStringArray(R.array.sectorTrabajo)
        var query = db.collection("empresas").whereEqualTo("categoria", array.get(sp_FiltroSector.selectedItemId.toInt()))
        if (sp_FiltroSector.selectedItem == array.get(0)){
         query = db.collection("empresas")
        }else{
             query = db.collection("empresas").whereEqualTo("categoria", array.get(sp_FiltroSector.selectedItemId.toInt()))

        }
        val options = FirestoreRecyclerOptions.Builder<Empresa>().setQuery(query, Empresa::class.java)
                .setLifecycleOwner(this).build()
        val adapter = object: FirestoreRecyclerAdapter<Empresa, EmpresaViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
                val view = LayoutInflater.from(this@HomeActivity).inflate(R.layout.card_layout, parent, false)
                return EmpresaViewHolder(view)
            }

            override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int, model: Empresa) {
                val tvName: TextView = holder.itemView.findViewById(R.id.tv_Name)
                val tvCat: TextView = holder.itemView.findViewById(R.id.tv_Categoria)
                val tvDescrip: TextView = holder.itemView.findViewById(R.id.tv_Descrip)
                tvName.text = model.name
                tvCat.text = model.categoria
                tvDescrip.text = model.descripcion

                var ficha= Empresa(model.name)
                listaFicha.add(ficha)
            }
        }
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(this)

    }



}