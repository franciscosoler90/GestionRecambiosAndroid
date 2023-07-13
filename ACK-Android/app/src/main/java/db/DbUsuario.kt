/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.Usuario
import java.sql.ResultSet

class DbUsuario {

    fun getUsuario(username : String) : Usuario?{

        //Si el nombre de usuario está vacio
        if (username.isEmpty()) {
            return null
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return null
            }

            //Cadena de texto Query SQL
            val query = "SELECT UsuCod, UsuNom FROM TRUSU (NOLOCK) WHERE UsuCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, username)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecución
            connect2.run {

                while(rs.next()){

                    val usuario = Usuario(
                        rs.getString(1).trim(),
                        rs.getString(2).trim())

                    //cierra conexiones
                    rs.close()
                    stmt.close()
                    close()

                    return usuario

                }
            }

        }catch(e : Exception){
            println(e)
        }

        return null

    }

    fun getIVECO(empCod : String) : Boolean{

        //Si el nombre de empresa está vacio
        if (empCod.isEmpty()) {
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
            val query = "SELECT PchChe FROM TRPCH1 (NOLOCK) WHERE EmpCod = ? AND PchGru = 'R' AND PchAtr = 'TMCAJASIVE'"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, empCod)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecución
            connect2.run {

                //Cadena de texto vacia
                var resultado = ""

                while(rs.next()) {

                    resultado = rs.getString(1)

                }

                //cierra conexiones
                rs.close()
                stmt.close()
                close()

                return resultado != null && resultado != "N"

            }

        }catch(e : Exception){
            println(e)
        }

        return false
    }

}