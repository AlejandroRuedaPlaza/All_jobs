package com.alejandrorp.alljobs

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class Metodos: AppCompatActivity()  {

    fun showAlert(texto: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
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

}