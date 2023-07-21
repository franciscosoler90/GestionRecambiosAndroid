/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import data.Usuario
import java.sql.Connection
import java.sql.ResultSet

class DbUsuario {

    fun getUsuario(username : String) : Usuario?{

        var resultado : Usuario? = null

        //Si el nombre de usuario está vacio
        if (username.isEmpty()) {
            return null
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT UsuCod, UsuNom FROM TRUSU (NOLOCK) WHERE UsuCod = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, username)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecución
                    connection.run {

                        while(rs.next()){

                            resultado = Usuario(
                                rs.getString(1).trim(),
                                rs.getString(2).trim())

                            //cierra conexiones
                            rs.close()
                            stmt.close()
                            close()

                        }
                    }

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

            return resultado

        }catch(e : Exception){
            println(e)
        }

        return null

    }

    fun getIVECO(empCod : String) : Boolean{

        var resultado = false

        //Si el nombre de empresa está vacio
        if (empCod.isEmpty()) {
            return false
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT PchChe FROM TRPCH1 (NOLOCK) WHERE EmpCod = ? AND PchGru = 'R' AND PchAtr = 'TMCAJASIVE'"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, empCod)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecución
                    connection.run {

                        var resultado1 = ""

                        while(rs.next()) {

                            resultado1 = rs.getString(1)

                        }

                        //cierra conexiones
                        rs.close()
                        stmt.close()
                        close()

                        resultado = (resultado1.isEmpty() && resultado1 != "N")

                    }

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

            return resultado

        }catch(e : Exception){
            println(e)
        }

        return false
    }

}