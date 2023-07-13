/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.Empresa
import java.sql.ResultSet

class DbEmpresas {

    fun getCompanies(username : String?) : List<Empresa>{

        val lista : ArrayList<Empresa> = ArrayList()

        //Comprueba si el nombre de usuario es nulo
        if (username.isNullOrEmpty()) {
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
            val query =
                "SELECT EmpCod, EmpNom FROM TRUSU1 (NOLOCK)\n" +
                        "INNER JOIN TREMP ON (TRUSU1.UsuEmpCodS = TREMP.EmpCod)\n" +
                        "WHERE UsuCod = ?\n" +
                        "ORDER BY EmpNom ASC"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, username)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val empresa = Empresa(
                        rs.getString(1),
                        rs.getString(2)
                    )
                    //Añade a la lista
                    lista.add(empresa)
                }

                //cierra conexiones
                rs.close()
                stmt.close()
                close()

            }

        }catch(e : Exception){
            println(e)
        }

        //Devuelve la lista
        return lista.toList()

    }
}