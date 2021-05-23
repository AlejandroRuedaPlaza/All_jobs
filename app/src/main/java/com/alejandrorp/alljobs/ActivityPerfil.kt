package com.alejandrorp.alljobs

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_perfil.*
import java.io.ByteArrayOutputStream

class ActivityPerfil : Metodos() {
//    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
//    val email = prefs.getString("email", null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        setup()
    }

    override fun onResume() {
        super.onResume()
        setup()
    }

    private fun setup() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val userType = prefs.getString("userType", null)
        tv_Correo_MyAc.setText(email)
        tv_UserType_MyAc.setText(userType)

        bt_Volver_MyAc.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            val email = prefs.getString("email", null)
            val provider = prefs.getString("provider", null)
            val intent: Intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email",email)
                putExtra("provider",provider)

            }
            startActivity(intent)
            finish()
        }
        bt_Logout.setOnClickListener {
            logout()
        }

        bt_Imagen.setOnClickListener {
            cargarImagen()
        }

        //CARGA LA IMAGEN DEL PERFIL SI TIENE UNA
        if (email!=null)
        db.collection("users").document(email).get().addOnSuccessListener {
//                                textView3.text=(it.get("userType") as String?)

            if (it.exists()) {//EXISTE EL USUARIO
                try {
                    val imgBlob = it.getBlob("imgInByte")
//                    val img = img0 as ByteArray
                    if (imgBlob != null) {
                        val img: ByteArray=imgBlob.toBytes()

                        val bm = BitmapFactory.decodeByteArray(img, 0, img.size)
                        iV_FotoPerfil.setImageBitmap(bm)


                    }
                }catch (e:Exception){

                }
            }
        }


        bt_BajaUsu.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            val email = prefs.getString("email", null)
            val provider = prefs.getString("provider", null)
            val userType = prefs.getString("userType", null)


            if (email!=null) {

                val builder = AlertDialog.Builder(this)
                builder.setMessage("¿Estas seguro?")
                builder.setCancelable(false)
                builder.setPositiveButton("Si"){dialog, id->

                    //REALIZA UNA COPIA DEL USUARIO
                    FirebaseFirestore.getInstance().collection("users")
                        .document(email).get().addOnSuccessListener {
                            var time = System.currentTimeMillis()
                            var imgInByte=it.get("imgInByte")
                            if (imgInByte != null){
                            FirebaseFirestore.getInstance().collection("users").document(time.toString()).set(
                                hashMapOf("correo" to email, "imgInByte" to imgInByte,
                                    "provider" to provider,"userType" to userType,
                                    "baja" to true)
                            )}else{
                            FirebaseFirestore.getInstance().collection("users").document(time.toString()).set(
                                hashMapOf("correo" to email,
                                    "provider" to provider,"userType" to userType,
                                    "baja" to true)
                            )}

                        }

                    FirebaseAuth.getInstance().currentUser.delete()
                    db.collection("users").document(email).delete()
                    logout()
                }
                builder.setNegativeButton("No"){dialog, id->
                    dialog.dismiss()
                }
                val alert: AlertDialog =builder.create()
                alert.show()

            }
//                db.collection("users").document(email).update("baja", true)

//                //añade a la BD de users el provider y el tipo de usuario usando como PK el email
//                FirebaseFirestore.getInstance().collection("users").document().set(
//                    hashMapOf("email" to eT_Email_Reg_Demand.text.toString(),
//                        "provider" to ProviderType.BASIC,"userType" to UserType.DEMANDANTE,
//                        "baja" to false)
//                )

        }

    }//fin setup()

    fun countUsers(): Int {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        var v=0
        if (email!=null) {
            FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener{
                v=v++
            }
        }

        return 0
    }

    private fun cargarImagen() {
        val intent: Intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/")
        startActivityForResult(intent,REQ.REQ1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)


        if (resultCode== RESULT_OK){
            if (data != null){
                val path: Uri? = data.data
                iV_FotoPerfil.setImageURI(path)

                var bitmap=(iV_FotoPerfil.drawable as BitmapDrawable).bitmap
                val stream= ByteArrayOutputStream()
                bitmap=scaleDownBitmap(bitmap,90)//REDIMENSIONA LA IMAGEN PARA COMPRIMIRLA
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imgInByte= stream.toByteArray()

                if (email!=null) {
                    db.collection("users").document(email).update("imgInByte", Blob.fromBytes(imgInByte))
                }

                showAlert("","Imagen cambiada con exito")
            }

        }

    }
    private fun scaleDownBitmap(photo: Bitmap, newHeight: Int): Bitmap? {
        var photo = photo
        val densityMultiplier = resources.displayMetrics.density
        val height = (newHeight * densityMultiplier).toInt()
        val width = (height * photo.width / photo.height.toDouble()).toInt()
        photo = Bitmap.createScaledBitmap(photo, width, height, true)
        return photo
    }

}