/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.Looper
import data.PickingCabecera
import data.PickingLineas
import java.sql.Connection
import java.sql.ResultSet

class DbPicking {

    fun readPickingFiles(username : String?) : List<PickingCabecera>{

        val lista : ArrayList<PickingCabecera> = ArrayList()

        //Comprueba si está vacio o es nulo
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
                    val query = "SELECT * FROM TRTMPIC (NOLOCK)"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    val rs : ResultSet = stmt.executeQuery()

                    var linea = 0

                    //Ejecucion
                    connection.run {

                        //Bucle para recorrer la tabla SQL
                        while(rs.next()){

                            val lineas = PickingCabecera(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5),
                                rs.getString(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9),
                                linea
                            )
                            //Añade a la lista
                            lista.add(lineas)

                            linea++
                        }

                        //cierra conexiones
                        rs.close()
                        stmt.close()
                        connection.close()

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


    fun readPicking(id : String?) : List<PickingLineas>{

        val lista : ArrayList<PickingLineas> = ArrayList()

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return lista.toList()
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT * FROM TRTMPICL (NOLOCK) WHERE TMPNro = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, id)

                    val rs : ResultSet = stmt.executeQuery()

                    var linea = 0

                    //Ejecucion
                    connection.run {

                        //Bucle para recorrer la tabla SQL
                        while (rs.next()) {

                            val lineas = PickingLineas(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getInt(4),
                                rs.getString(5),
                                rs.getString(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9),
                                rs.getString(10),
                                rs.getString(11),
                                linea
                            )
                            //Añade a la lista
                            lista.add(lineas)

                            linea++

                        }

                        //cierra conexiones
                        rs.close()
                        stmt.close()
                        connection.close()
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

    fun editPickingUnidades(id: String?, linea: Int, unidades: Float){

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return
        }

        //Comprueba si es 0
        if (linea < 0) {
            return
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "UPDATE TRTMPICL\n" +
                            "SET TMPArtUnRe = ?\n" +
                            "WHERE TMPNro = ? AND TMPArtLin = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer los parámetros en el PreparedStatement
                    unidades.let { stmt.setFloat(1, it) }
                    stmt.setString(2, id)
                    stmt.setInt(3, linea)

                    // Ejecutar la actualizacion
                    stmt.executeUpdate()

                    //Ejecucion
                    connection.run {

                        //cierra conexiones
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

    }

    fun editPickingEstado(id: String?, linea : Int, estado : Int){

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return
        }

        //Comprueba si es 0
        if (linea < 0) {
            return
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "UPDATE TRTMPICL\n" +
                            "SET TMPLinEst = ?\n" +
                            "WHERE TMPNro = ? AND TMPArtLin = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer los parámetros en el PreparedStatement
                    stmt.setInt(1, estado)
                    stmt.setString(2, id)
                    stmt.setInt(3, linea)

                    // Ejecutar la actualizacion
                    stmt.executeUpdate()

                    //cierra conexiones
                    stmt.close()
                    connection.close()

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

        }catch(e : Exception){
            println(e)
        }

    }

    fun editPickingCabeceraEstado(id: String?, estado : Int){

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "UPDATE TRTMPIC\n" +
                            "SET TMPEst = ?\n" +
                            "WHERE TMPNro = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer los parámetros en el PreparedStatement
                    stmt.setInt(1, estado)
                    stmt.setString(2, id)

                    // Ejecutar la actualizacion
                    stmt.executeUpdate()

                    //cierra conexiones
                    stmt.close()
                    connection.close()

                } else {
                    println("Error en la conexión: $errorMessage")
                }
            }

        }catch(e : Exception){
            println(e)
        }

    }

    fun getLinea(id : String?, linea: Int) : PickingLineas? {

        var resultado : PickingLineas? = null

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return null
        }

        //Comprueba si es 0
        if (linea < 0) {
            return null
        }

        try{

            val handler = Handler(Looper.getMainLooper())

            DbConnect.connectDB(handler) { connectionResult ->
                val connection: Connection? = connectionResult.connection
                val errorMessage: String? = connectionResult.errorMessage

                if (connection != null) {

                    //Cadena de texto Query SQL
                    val query = "SELECT * FROM TRTMPICL (NOLOCK) WHERE TMPNro = ? AND TMPArtLin = ?"

                    // Crear una instancia de PreparedStatement
                    val stmt = connection.prepareStatement(query)

                    // Establecer el parámetro en el PreparedStatement
                    stmt.setString(1, id)
                    stmt.setInt(2, linea)

                    val rs : ResultSet = stmt.executeQuery()

                    //Ejecucion
                    connection.run {

                        //Bucle para recorrer la tabla SQL
                        while(rs.next()){

                            resultado = PickingLineas(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getInt(4),
                                rs.getString(5),
                                rs.getString(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9),
                                rs.getString(10),
                                rs.getString(11),
                                0
                            )

                            //cierra conexiones
                            rs.close()
                            stmt.close()
                            close()

                        }

                    }

                    //cierra conexiones
                    rs.close()
                    stmt.close()
                    connection.close()

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

}