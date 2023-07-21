/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import java.sql.Connection
import java.sql.ResultSet

class DbArticulo {

    //Obtener descripcion
    fun getDescription(empCod : String?, code : String?) : String{

        //si el texto es nulo o esta vacio
        if (code.isNullOrEmpty()) {
            return ""
        }

        //si el texto es nulo o esta vacio
        if (empCod.isNullOrEmpty()) {
            return ""
        }

        var resultado = ""

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT ArtDes FROM TRART (NOLOCK) WHERE ArtCod = ? AND EmpCod = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, code)
                    stmt.setString(2, empCod)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecucion
                    connection.run {

                        //Bucle para recorrer la tabla SQL
                        while(rs.next()){
                            resultado = rs.getString(1)
                        }

                        //cierra conexiones
                        rs.close()
                        stmt.close()
                        close()
                    }

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

        }catch(e : Exception){
            println(e)
        }

        return resultado

    }
}