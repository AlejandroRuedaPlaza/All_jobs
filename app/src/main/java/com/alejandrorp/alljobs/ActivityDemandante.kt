package com.alejandrorp.alljobs

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_demandante.*

class ActivityDemandante : Metodos() {
    //variable para comprobar si la sesion esta iniciada
    private var open= false

    private val GOOGLE_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AllJobs)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demandante)
//        //esconde la barra
//        if (getSupportActionBar() != null) {
//            getSupportActionBar()?.hide();
//        }
        //Llamada a la funcion setup
        setup()
    }

    override fun onStart() {
        super.onStart()

        open=false
    }

    private fun setup() {
        title="Registro como Demandante"
        bt_Reg_Demand.setOnClickListener{
            if (eT_Email_Reg_Demand.text.isNotEmpty() && eT_Password_Reg_Demand.text.isNotEmpty()){
                if (eT_Password_Reg_Demand.text.length>=5){
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(eT_Email_Reg_Demand.text.toString(),
                        eT_Password_Reg_Demand.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful){
                            if(open!=true){
                                showHome(it.result?.user?.email?:"", ProviderType.BASIC, UserType.DEMANDANTE)
                                open=true
                            }

//                            //añade a la BD de users el provider y el tipo de usuario usando como PK el email
                            FirebaseFirestore.getInstance().collection("users").document(eT_Email_Reg_Demand.text.toString()).set(
                                    hashMapOf("correo" to eT_Email_Reg_Demand.text.toString(),
                                        "provider" to ProviderType.BASIC,"userType" to UserType.DEMANDANTE,
                                        "baja" to false)
                            )
                        }else{
                            showAlert("Se ha producido un error de autenticando al usuario, compruebe que el correo este bien escrito o ya existe")
                        }
                    }
                }else{
                    showAlert("La contraseña debe de tener un minimo de 6 caracteres")
                }
            }
        }
        //BOTON DE CUENTA GOOGLE
        bt_RegGoogle_Demand.setOnClickListener{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient =  GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        //boton volver
                bt_Volver_Reg_Demand.setOnClickListener{

                    onBackPressed()
                }

    }//fin setup()

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


//                                    comprueba que no existe la cuenta y la registra
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(email).get().addOnSuccessListener {
                                                try {
                                                    val provider=(it.get("provider") as String)
                                                    var userType=(it.get("userType") as String)

                                                    showAlert("La cuenta seleccionada ya esta registrada en la aplicacion pruebe con otra")

                                                }catch (e: Exception){
                                                    //crea un usuario de tipo google y demandante
                                                    FirebaseFirestore.getInstance().collection("users").document(email).set(
                                                            hashMapOf("correo" to email,
                                                    "provider" to ProviderType.GOOGLE,"userType" to UserType.DEMANDANTE,
                                                    "baja" to false)
                                                    )

                                                    showHome(email?:"", ProviderType.GOOGLE, UserType.DEMANDANTE)

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