package com.alejandrorp.alljobs

import java.io.Serializable

class Empresa: Serializable {

    constructor(categoria: String, correo: String, creador: String, descripcion: String
                , name: String, telefono: String
                , nameLoc: String, latitud: String, longitud: String) {
        this.categoria = categoria
        this.correo = correo
        this.creador = creador
        this.descripcion = descripcion
        this.name = name
        this.telefono = telefono
        this.nameLoc = nameLoc
        this.latitud = latitud
        this.longitud = longitud
    }
    constructor(name: String){
    this.name = name
    }

    constructor()

    var categoria:String = ""
        get() = field
        set(value) {
            field = value
        }
    var correo:String = ""
        get() = field
        set(value) {
            field = value
        }
    var creador:String = ""
        get() = field
        set(value) {
            field = value
        }
    var descripcion:String = ""
        get() = field
        set(value) {
            field = value
        }
    var name:String = ""
        get() = field
        set(value) {
            field = value
        }
    var telefono:String = ""
        get() = field
        set(value) {
            field = value
        }
    var nameLoc:String = ""
        get() = field
        set(value) {
            field = value
        }
    var latitud:String = ""
        get() = field
        set(value) {
            field = value
        }
    var longitud:String = ""
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        return "Empresa(categoria='$categoria', correo='$correo', creador='$creador'" +
                ", descripcion='$descripcion', name='$name', telefono='$telefono'" +
                ", nameLoc='$nameLoc', latitud='$latitud', longitud='$longitud')"
    }


}