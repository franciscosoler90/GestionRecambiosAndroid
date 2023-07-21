/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import utilidades.EncryptTool
import java.sql.Connection
import java.sql.ResultSet

@Suppress("SameReturnValue")
class DbPassword {

    fun getPassword(username : String, password : String) : Boolean{

        var resultado = false

        //Si el nombre de usuario está vacio
        if (username.isEmpty()) {
            return false
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT UsuPas FROM TRUSU (NOLOCK) WHERE UsuCod = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, username)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecución
                    connection.run {

                        //Cadena de texto vacia
                        var pass = ""

                        //Bucle para recorrer la tabla SQL
                        while(rs.next()) {
                            //obtiene el primer resultado
                            pass = rs.getString(1)
                        }

                        //cierra conexiones
                        rs.close()
                        stmt.close()
                        close()

                        //Encriptacion de contraseña
                        val encryptTool = EncryptTool()
                        val password2 = encryptTool.encryptString(true,password)

                        resultado = password2 == pass

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