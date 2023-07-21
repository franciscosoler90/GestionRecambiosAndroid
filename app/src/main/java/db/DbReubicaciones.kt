/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import java.sql.Connection

class DbReubicaciones {

    fun reubicar(empCod: String?, usuCod: String?, almCod: String?, artCod: String?, ubicacionOrigen: String?, ubicacionDestino: String?, unidades : Float){

        try {

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    // Preparar la llamada al procedimiento almacenado
                    val procedimiento = connection.prepareCall("{call SP_TM_Reubica(?, ?, ?, ?, ?, ?, ?)}")

                    // Establecer los parámetros del procedimiento almacenado
                    procedimiento.setString(1, empCod)
                    procedimiento.setString(2, usuCod)
                    procedimiento.setString(3, almCod)
                    procedimiento.setString(4, artCod)
                    procedimiento.setString(5, ubicacionOrigen)
                    procedimiento.setString(6, ubicacionDestino)
                    procedimiento.setFloat(7, unidades)

                    // Ejecutar el procedimiento almacenado
                    procedimiento.execute()

                    // Cerrar la conexión y liberar recursos
                    procedimiento.close()

                    connection.close()

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

        }catch(e : Exception){
            println(e)
        }

    }
}