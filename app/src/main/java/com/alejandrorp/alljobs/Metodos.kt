package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class Metodos: AppCompatActivity()  {
    val db = FirebaseFirestore.getInstance()

    fun showAlert(titulo:String, texto: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(texto)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    fun showAlert(texto: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(texto)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    fun showAlert(texto: String, context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(texto)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    fun showAlert(titulo:String, texto: String,context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(texto)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
    fun showHome(email:String, provider: ProviderType, userType: UserType){

        val homeIntent= Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
            putExtra("userType", userType.name)
        }
        startActivity(homeIntent)
    }
    fun showHome(email:String, provider: ProviderType){

        val homeIntent= Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }

    fun logout(){

        //borra los datos guardados al cerrar sesion
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

//    private fun setup(email:String, provider:String) {
//        textView.text=email
//        textView2.text=provider
//
//        bt_Logout.setOnClickListener{
//            logout()
//        }
//
//
//    }



    //MENU
    //1.-cargamos el menu de opciones que hemos creado
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val bundle: Bundle?=intent.extras
//        val email:String?=bundle?.getString("email")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        var userType:String?=null
        if (email != null) {
            FirebaseFirestore.getInstance().collection("users").document(email).get().addOnSuccessListener {
                userType=(it.get("userType") as String?)
                //dependiendo del tipo de usuario carga un menu u otro
                when (userType){
                    "ADMIN" -> {
                        val inflate: MenuInflater = menuInflater
                        inflate.inflate(R.menu.admin_menu, menu)

                    }
                    "OFERTANTE" -> {
                        val inflate: MenuInflater = menuInflater
                        inflate.inflate(R.menu.ofert_menu, menu)

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
            R.id.addOfert->{
                val intent: Intent = Intent(this, ActivityFormularioOferta::class.java)
                startActivity(intent)
            }
            R.id.perfil->{
                val intent: Intent = Intent(this, ActivityPerfil::class.java)
                startActivity(intent)
            }
            R.id.home->{
                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                val email = prefs.getString("email", null)
                val provider = prefs.getString("provider", null)
                val userType = prefs.getString("userType", null)
                val intent: Intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("email",email)
                    putExtra("provider",provider)
                    putExtra("userType",provider)

                }
                startActivity(intent)
                finish()
            }
            R.id.listaUsu->{
                val intent: Intent = Intent(this, ActivityListaUsu::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}