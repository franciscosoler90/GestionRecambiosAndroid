/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package data

data class Usuario(
    val id : String,
    val nombre : String
)

//Sobreescribe el método toString() para mostrar solo la cadena de texto "nombre"
{
    override fun toString(): String {
        return (nombre.trim())
    }
}