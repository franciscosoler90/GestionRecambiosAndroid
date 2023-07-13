/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.InventarioCabecera
import data.InventarioLineas
import java.sql.ResultSet

class DbInventory {

    fun readInventoryFiles(username : String?) : List<InventarioCabecera>{

        val lista : ArrayList<InventarioCabecera> = ArrayList()

        //Comprueba si está vacio o es nulo
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

            //Cadena de texto Query SQL, sacar las lineas no estan importadas con el estado menor que 3
            val query = "SELECT * FROM TRTMINV (NOLOCK) WHERE TMIEst < 3"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val inventarioCabecera = InventarioCabecera(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8))

                    //Añade a la lista
                    lista.add(inventarioCabecera)
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

    fun readInventory(id : String?, empCod : String?, tmiSer : String?, almCod : String?) : List<InventarioLineas>{

        val lista : ArrayList<InventarioLineas> = ArrayList()

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
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
            val query = "SELECT * FROM TRTMINVL (NOLOCK) WHERE TMINro = ? AND EmpCod = ?  AND TMISer = ? AND AlmCod = ? AND TMILinEst < 3"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, id)
            stmt.setString(2, empCod)
            stmt.setString(3, tmiSer)
            stmt.setString(4, almCod)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                var linea = 1

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val lineas = InventarioLineas(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
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
                close()

            }

        }catch(e : Exception){
            println(e)
        }

        //Devuelve la lista
        return lista.toList()
    }

    fun updateLinea(id: String?, linea : Int, unidades: String?){

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return
        }

        //Comprueba si es 0
        if (linea < 0) {
            return
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return
            }

            // Cadena de texto Query SQL
            val query = "UPDATE TablaTemporal\n" +
                    "SET TMIUniCon = ?, TMILinEst = 1\n" +
                    "FROM (\n" +
                    "    SELECT *, ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS RowNum\n" +
                    "    FROM TRTMINVL\n" +
                    "\tWHERE TMINro = ?\n" +
                    ") AS TablaTemporal\n" +
                    "WHERE TablaTemporal.RowNum = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros en el PreparedStatement
            unidades?.toFloat()?.let { stmt.setFloat(1, it) }
            stmt.setString(2, id)
            stmt.setInt(3, linea)

            // Ejecutar la actualizacion
            stmt.executeUpdate()

            //cierra conexiones
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return

    }

    fun getLinea(id : String?, linea: Int) : InventarioLineas?{

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return null
        }

        //Comprueba si es 0
        if (linea < 0) {
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
            val query = "SELECT * \n" +
                    "FROM (\n" +
                    "    SELECT *, ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS RowNum\n" +
                    "    FROM TRTMINVL\n" +
                    "\tWHERE TMINro = ?\n" +
                    ") AS TablaTemporal\n" +
                    "WHERE TablaTemporal.RowNum = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, id)
            stmt.setInt(2, linea)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                        val resultado = InventarioLineas(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9),
                            rs.getString(10),
                            rs.getString(11),
                            rs.getInt(12)
                        )

                        //cierra conexiones
                        rs.close()
                        stmt.close()
                        close()

                        return resultado

                    }

                }

                //cierra conexiones
                rs.close()
                stmt.close()
                connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return null

    }


    fun comprobarInventarioLineas(username: String?, id : String?){

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return
        }

        //Comprueba si está vacio o es nulo
        if (username.isNullOrEmpty()) {
            return
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return
            }

            // Cadena de texto Query SQL
            val query = "SELECT \n" +
                    "    CASE WHEN COUNT(*) = SUM(CASE WHEN TMILinEst = 1 THEN 1 ELSE 0 END) \n" +
                    "         THEN 'True' \n" +
                    "         ELSE 'False' \n" +
                    "    END AS todas_las_lineas_con_TMINLinEst_igual_a_1\n" +
                    "FROM TRTMINVL \n" +
                    "WHERE TMINro = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, id)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val resultado = rs.getString("todas_las_lineas_con_TMINLinEst_igual_a_1").toBoolean()

                    if(resultado){
                        updateInventario(id, username,2)
                    }

                    //cierra conexiones
                    rs.close()
                    stmt.close()
                    close()

                    return
                }
            }

            //cierra conexiones
            rs.close()
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return

    }

    fun updateInventario(id: String?, username: String?, estado : Int ){

        //Comprueba si está vacio o es nulo
        if (id.isNullOrEmpty()) {
            return
        }

        //Comprueba si está vacio o es nulo
        if (username.isNullOrEmpty()) {
            return
        }

        //Comprueba si estado es menor a 0 o mayor a 3
        if(estado < 0 || estado > 3){
            return
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return
            }

            // Cadena de texto Query SQL
            val query = "UPDATE TRTMINV\n" +
                    "SET TMIEst = ?, TMIUsuTer = ?\n" +
                    "WHERE TMINro = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros en el PreparedStatement
            stmt.setInt(1, estado)
            stmt.setString(2, username)
            stmt.setString(3, id)

            // Ejecutar la actualizacion
            stmt.executeUpdate()

            //cierra conexiones
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }
    }
}