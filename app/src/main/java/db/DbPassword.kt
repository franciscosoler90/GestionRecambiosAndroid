/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import utilidades.EncryptTool
import java.sql.ResultSet

@Suppress("SameReturnValue")
class DbPassword {

    fun getPassword(username : String, password : String) : Boolean{

        //Si el nombre de usuario está vacio
        if (username.isEmpty()) {
            return false
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return false
            }

            //Cadena de texto Query SQL
            val query =
                "SELECT UsuPas FROM TRUSU (NOLOCK) WHERE UsuCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, username)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecución
            connect2.run {

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

                return password2 == pass

            }

        }catch(e : Exception){
            println(e)
        }

        return false

    }
}