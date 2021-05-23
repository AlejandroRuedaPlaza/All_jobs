package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Metodos() {
    private var open= false
    private val GOOGLE_SIGN_IN = 1000

    override fun onCreate(savedInstanceState: Bundle?) {

//        Thread.sleep(2000)
        //aplica el theme
    setTheme(R.style.Theme_AllJobs)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

//        if (getSupportActionBar() != null) {
//            getSupportActionBar()?.hide();
//        }

        //Llamada a la funcion setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()

        open=false
        authLayout.visibility = View.VISIBLE
    }

//DATOS DE SESION
    private fun session() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if(email!=null && provider != null){
            authLayout.visibility = View.INVISIBLE
                        showHome(email, ProviderType.valueOf(provider))


        }

    }

    private fun setup() {
        title="Inicio de sesion"
        bt_LoginEmail.setOnClickListener{
            bt_LoginEmail.isEnabled=false
            var handler: Handler = Handler()

            if (eT_Email_Log.text.isNotEmpty() && eT_Password_Log.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(eT_Email_Log.text.toString(),
                        eT_Password_Log.text.toString()).addOnCompleteListener(){
                        if (it.isSuccessful){
//                            showHome(it.result?.user?.email?:"", ProviderType.BASIC, UserType.DEMANDANTE)
                            val email=it.result?.user?.email?:""
                            FirebaseFirestore.getInstance().collection("users")
                                .document(email).get().addOnSuccessListener {
//                                textView3.text=(it.get("userType") as String?)
                                    if(open!=true){
                                    showHome(email, ProviderType.BASIC, UserType.valueOf((it.get("userType") as String)))
                                    open=true
                                    }
                                }
                        }else{
                            showAlert("Email o contraseña incorrectos")
                            handler.postDelayed( Runnable{
                                bt_LoginEmail.isEnabled=true
                            }, 500)
                        }
                    }
            }else{
                showAlert("Rellene los campos")
                handler.postDelayed( Runnable{
                    bt_LoginEmail.isEnabled=true
                }, 500)

            }


        }//fin on click

        //BOTON DE CUENTA GOOGLE
        bt_LoginGoogle.setOnClickListener{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient =  GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }



    fun onClick_Demand(v: View){
        val intent: Intent = Intent(this, ActivityDemandante::class.java)
        startActivity(intent)
    }

    fun onClick_Ofert(v: View){
        val intent: Intent = Intent(this, ActivityOfertante::class.java)
        startActivity(intent)
    }


    //ACCION DEL BOTON GOOGLE EN FIREBASE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener() {
                            if (it.isSuccessful) {
                                val email = account.email ?: ""

                                //crea un usuario de tipo google y admin
//                                FirebaseFirestore.getInstance().collection("users").document(email).set(
//                                    hashMapOf("provider" to ProviderType.GOOGLE,"userType" to UserType.ADMIN))

                                //recoge los datos del usuario
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(email).get().addOnSuccessListener {
                                        try {
                                            val provider=(it.get("provider") as String)
                                            var userType=(it.get("userType") as String)
                                            showHome(
                                                email,
    //                                            ProviderType.GOOGLE,
                                                ProviderType.valueOf(provider),
                                                UserType.valueOf(userType)
                                            )
                                        }catch (e: Exception){
                                            showAlert("La cuenta no esta registrada en la aplicacion")
                                        }
                                    }
                            } else {
                                showAlert("Email o contraseña incorrectos")
                            }
                        }
                }
            }catch (e: ApiException){
                showAlert("Error al autenticar")
            }
        }
    }
}