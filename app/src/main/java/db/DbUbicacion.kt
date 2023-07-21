/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import data.Existencia
import java.sql.Connection
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

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT TOP(1) ExiUbi FROM TREXI1 (NOLOCK) WHERE ArtCod = ? AND EmpCod = ? AND AlmCod = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, code)
                    stmt.setString(2, empCod)
                    stmt.setString(3, almCod)

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

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT ExiUbi, ExiExi FROM TREXI1 (NOLOCK) WHERE EmpCod = ? AND AlmCod = ? AND ArtCod = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, empCod)
                    stmt.setString(2, almCod)
                    stmt.setString(3, code)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecucion
                    connection.run {

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

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

        }catch(e : Exception){
            println(e)
        }

        return lista

    }

}