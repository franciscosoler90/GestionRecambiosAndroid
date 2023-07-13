/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

class DbReubicaciones {

    fun reubicar(empCod: String?, usuCod: String?, almCod: String?, artCod: String?, ubicacionOrigen: String?, ubicacionDestino: String?, unidades : Float){

        try {

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return
            }

            // Preparar la llamada al procedimiento almacenado
            val procedimiento = connect2.prepareCall("{call SP_TM_Reubica(?, ?, ?, ?, ?, ?, ?)}")

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

            connect2.close()

        }catch(e : Exception){
            println(e)
        }

    }
}