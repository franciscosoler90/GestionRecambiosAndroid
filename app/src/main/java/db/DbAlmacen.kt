/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.Almacen
import java.sql.ResultSet

class DbAlmacen {

    //Obtener Almacen
    fun getAlmacen(empCod : String?, artCod : String?) : String {

        var resultado = ""

        //si el texto es nulo o esta vacio
        if (artCod.isNullOrEmpty()) {
            return resultado
        }

        //si el texto es nulo o esta vacio
        if (empCod.isNullOrEmpty()) {
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
            val query = "SELECT TOP(1) AlmCod FROM TREXI1 (NOLOCK) WHERE ArtCod = ? AND EmpCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, artCod)
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

    //Obtener Almacen
    fun getAlmacenes(empCod : String?) : List<Almacen> {

        val lista : ArrayList<Almacen> = ArrayList()

        //si el texto es nulo o esta vacio
        if (empCod.isNullOrEmpty()) {
            return lista.toList()
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return lista.toList()
            }

            //Cadena de texto Query SQL
            val query = "SELECT T1.SedAlmCodS, T2.AlmDes FROM TRSED2 T1 (NOLOCK) INNER JOIN TRALM T2 ON T1.SedAlmCodS=T2.AlmCod WHERE T1.EmpCod = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, empCod)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val almacen = Almacen(
                        rs.getString(1),
                        rs.getString(2)
                    )
                    //Añade a la lista
                    lista.add(almacen)

                }

                //cierra conexiones
                rs.close()
                stmt.close()
                close()
            }

        }catch(e : Exception){
            println(e)
        }

        return lista.toList()

    }
}