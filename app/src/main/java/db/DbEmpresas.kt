/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import data.Empresa
import java.sql.Connection
import java.sql.ResultSet

class DbEmpresas {

    fun getCompanies(username : String?) : List<Empresa>{

        val lista : ArrayList<Empresa> = ArrayList()

        //Comprueba si el nombre de usuario es nulo
        if (username.isNullOrEmpty()) {
            return lista.toList()
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query =
                        "SELECT EmpCod, EmpNom FROM TRUSU1 (NOLOCK)\n" +
                                "INNER JOIN TREMP ON (TRUSU1.UsuEmpCodS = TREMP.EmpCod)\n" +
                                "WHERE UsuCod = ?\n" +
                                "ORDER BY EmpNom ASC"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, username)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecucion
                    connection.run {

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

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

        }catch(e : Exception){
            println(e)
        }

        //Devuelve la lista
        return lista.toList()

    }
}