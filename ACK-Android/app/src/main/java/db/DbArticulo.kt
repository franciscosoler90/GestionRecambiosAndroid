/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

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

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return ""
            }

            //Cadena de texto Query SQL
            val query = "SELECT ArtDes FROM TRART (NOLOCK) WHERE ArtCod = ? AND EmpCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, code)
            stmt.setString(2, empCod)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){
                    resultado = rs.getString(1)
                }

                //cierra conexiones
                rs.close()
                stmt.close()
                close()
            }

        }catch(e : Exception){
            println(e)
        }

        return resultado

    }
}