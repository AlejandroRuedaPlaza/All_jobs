package com.alejandrorp.alljobs

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType{
    BASIC,
    GOOGLE
}

enum class UserType{
    ADMIN,
    OFERTANTE,
    DEMANDANTE
}

open class HomeActivity : Metodos() {

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
            }
        }

        setup(email?:"",provider?:"")

        //Guardado de datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()


    }

    private fun logout(){

        //borra los datos guardados al cerrar sesion
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }

    private fun setup(email:String, provider:String) {
//        textView.text=email
//        textView2.text=provider
//
//        bt_Logout.setOnClickListener{
//            logout()
//        }


    }

    //1.-cargamos el menu de opciones que hemos creado
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")
        var userType:String?=null
        if (email != null) {
            FirebaseFirestore.getInstance().collection("users").document(email).get().addOnSuccessListener {
                userType=(it.get("userType") as String?)
                //dependiendo del tipo de usuario carga un menu u otro
                when (userType){
                    "ADMIN" -> {
                        val inflate: MenuInflater = menuInflater
                        inflate.inflate(R.menu.demand_menu, menu)

                    }
                    "OFERTANTE" -> {
                        val inflate: MenuInflater = menuInflater
                        inflate.inflate(R.menu.demand_menu, menu)

                    }
                    else -> {
                        val inflate: MenuInflater = menuInflater
                        inflate.inflate(R.menu.demand_menu, menu)

                    }


                }
            }
        }
        return true
    }
    //2.-ponemos listener a los items de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.Logout->{
                logout()
            }
//            R.id.MUsuario->{
//                val intent: Intent = Intent(this, Usuario::class.java)
//                startActivity(intent)
//            }
        }
        return super.onOptionsItemSelected(item)
    }


}