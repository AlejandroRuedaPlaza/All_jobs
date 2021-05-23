package com.alejandrorp.alljobs

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter (private val listaFichas: ArrayList<User>, val c: Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_lusu, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item =listaFichas[position]
        val ivUsuario: ImageView = holder.itemView.findViewById(R.id.iV_lUsu)
        val tvCorreo: TextView = holder.itemView.findViewById(R.id.tv_Correo_LUsu)
        val tvProvider: TextView = holder.itemView.findViewById(R.id.tv_Provider_LUsu)
        val tvUserType: TextView = holder.itemView.findViewById(R.id.tv_UserType_LUsu)
        val tvBajaAlta: TextView = holder.itemView.findViewById(R.id.tv_BajaAlta_LUsu)

        if(item.imgInByte!=null){
            val img: ByteArray=item.imgInByte!!.toBytes()

            val bm = BitmapFactory.decodeByteArray(img, 0, img.size)
            ivUsuario.setImageBitmap(bm)

        }
        tvCorreo.text = item.correo
        tvProvider.text = item.provider
        tvUserType.text = item.userType
        if (item.baja.equals("true")){
            tvBajaAlta.text = "Baja"
        }else{
            tvBajaAlta.text = "Alta"
        }

    }

    override fun getItemCount(): Int {
        return listaFichas.size
    }
}