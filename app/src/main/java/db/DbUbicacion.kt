/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.Existencia
import java.sql.ResultSet

class DbUbicacion {

    //Obtener Ubicacion
    fun getUbicacion(code : String?, empCod : String?, almCod : String?) : String {

        var resultado = ""

        //si el texto es nulo o esta vacio
        if (code.isNullOrEmpty()) {
            return resultado
        }

        //si el texto es nulo o esta vacio
        if (empCod.isNullOrEmpty()) {
            return resultado
        }

        //si el texto es nulo o esta vacio
        if (almCod.isNullOrEmpty()) {
            return resultado
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return ""
            }

            //Cadena de texto Query SQL
            val query = "SELECT TOP(1) ExiUbi FROM TREXI1 (NOLOCK) WHERE ArtCod = ? AND EmpCod = ? AND AlmCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, code)
            stmt.setString(2, empCod)
            stmt.setString(3, almCod)

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

    //Obtener Ubicaciones
    fun getUbicaciones(empCod : String?, almCod : String?, code : String?) : List<Existencia> {

        val lista : ArrayList<Existencia> = ArrayList()

        //si el texto es nulo o esta vacio
        if (code.isNullOrEmpty()) {
            return lista
        }

        //si el texto es nulo o esta vacio
        if (empCod.isNullOrEmpty()) {
            return lista
        }

        //si el texto es nulo o esta vacio
        if (almCod.isNullOrEmpty()) {
            return lista
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return lista
            }

            //Cadena de texto Query SQL
            val query = "SELECT ExiUbi, ExiExi FROM TREXI1 (NOLOCK) WHERE EmpCod = ? AND AlmCod = ? AND ArtCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, empCod)
            stmt.setString(2, almCod)
            stmt.setString(3, code)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val existencia = Existencia(
                        rs.getString(1),
                        rs.getString(2)
                    )
                    //Añade a la lista
                    lista.add(existencia)

                }

                //cierra conexiones
                rs.close()
                stmt.close()
                close()
            }

        }catch(e : Exception){
            println(e)
        }

        return lista

    }

}