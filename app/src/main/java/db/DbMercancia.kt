/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.MercanciaCaja
import data.MercanciaLineas
import java.sql.ResultSet
import java.sql.Timestamp

class DbMercancia {

    fun getCaja(caja: String?) : MercanciaCaja?{

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
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
            val query = "SELECT * FROM TRTMCAJ (NOLOCK) WHERE TMCAriNumP = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, caja)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val resultado = MercanciaCaja(
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

    fun getLinea(caja: String?, linea: String?) : MercanciaLineas? {

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
            return null
        }

        //Comprueba si está vacio o es nulo
        if (linea.isNullOrEmpty()) {
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
            val query = "SELECT * FROM TRTMCAJL (NOLOCK) WHERE TMCAriNumP = ? AND TMCAriArtL = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, caja)
            stmt.setString(2, linea)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val resultado = MercanciaLineas(
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
                        rs.getString(12)
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

    fun updateEstadoLinea(caja : String?, linea: String?, unidadesContadas: Float, estado : Int){

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
            return
        }

        if(unidadesContadas < 0){
            return
        }

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

            val unidadesContadasFormat = "%.2f".format(unidadesContadas).replace(",",".")

            //Cadena de texto Query SQL
            val query = "UPDATE TRTMCAJL SET TMCEstLin = ?, TMCUniCont = ? WHERE TMCAriNumP = ? AND TMCAriArtL = ?"
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros de la consulta
            stmt.setInt(1, estado)
            stmt.setString(2, unidadesContadasFormat)
            stmt.setString(3, caja)
            stmt.setString(4, linea)

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

    fun updateEstadoCaja(caja : String?, usuario : String?, estado : Int){

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
            return
        }

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
            val query = "UPDATE TRTMCAJ SET TMCEst = ?, TMCUsuTer = ?, TMCFecVal = ?, TMCHorVal = ? WHERE TMCAriNumP = ?"

            // Crear un PreparedStatement con la consulta SQL
            val stmt = connect2.prepareStatement(query)

            val fechaHora = Timestamp(System.currentTimeMillis())

            // Establecer los parámetros de la consulta
            stmt.setInt(1, estado)
            stmt.setString(2, usuario)
            stmt.setTimestamp(3, fechaHora)
            stmt.setTimestamp(4, fechaHora)
            stmt.setString(5, caja)

            // Ejecutar la consulta
            stmt.executeUpdate()

            // Cerrar la conexión y el PreparedStatement
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return

    }

    fun todosLineasComprobadas(caja: String?, estado: Int){

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
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
            val query = "UPDATE TRTMCAJL SET TMCEstLin = ? WHERE TMCAriNumP = ?"

            // Crear un PreparedStatement con la consulta SQL
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros de la consulta
            stmt.setInt(1, estado)
            stmt.setString(2, caja)

            // Ejecutar la consulta
            stmt.executeUpdate()

            // Cerrar la conexión y el PreparedStatement
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return

    }

    fun readMercanciaLineas(caja: String?) : List<MercanciaLineas>{

        val lista : ArrayList<MercanciaLineas> = ArrayList()

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
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

            // Cadena de texto Query SQL
            val query = "SELECT * FROM TRTMCAJL (NOLOCK) WHERE TMCAriNumP = ? ORDER BY TMCEstLin"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, caja)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val lineas = MercanciaLineas(
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
                        rs.getString(12)
                    )

                    //Añade a la lista
                    lista.add(lineas)

                }

                //cierra conexiones
                rs.close()
                stmt.close()
                connect2.close()

            }

        }catch(e : Exception){
            println(e)
        }

        //Devuelve la lista
        return lista.toList()
    }

    fun estadoCaja(caja: String?): Int{

        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
            return -1
        }

        try{

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return -1
            }

            //Cadena de texto Query SQL
            val query = "SELECT TMCEst FROM TRTMCAJ (NOLOCK) WHERE TMCAriNumP = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, caja)

            val rs : ResultSet = stmt.executeQuery()

            //Ejecucion
            connect2.run {

                //Bucle para recorrer la tabla SQL
                while(rs.next()){

                    val resultado = rs.getString("TMCEst").toInt()

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

        return -1

    }

    fun comprobarLineas(caja: String?): Boolean {
        //Comprueba si está vacio o es nulo
        if (caja.isNullOrEmpty()) {
            return false
        }

        try {

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if (connect2 == null) {
                println("Conexión nula")
                return false
            }

            //Cadena de texto Query SQL
            val query = "SELECT TMCAriUni, TMCUniCont FROM TRTMCAJL (NOLOCK) WHERE TMCAriNumP = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer el parámetro en el PreparedStatement
            stmt.setString(1, caja)

            val rs: ResultSet = stmt.executeQuery()

            //Verifica si los valores son iguales en ambas columnas para todos los registros
            while (rs.next()) {
                if (rs.getString("TMCAriUni") != rs.getString("TMCUniCont")) {
                    return false
                }
            }

            //cierra conexiones
            rs.close()
            stmt.close()
        } catch (e: Exception) {
            println(e)
        }

        return true
    }

    fun ejecutarProcedimiento(empCod: String?, tmcPrvCod: String?, tmcAriNum: String?, tmcAriNumPaq: String?, usuCod: String?) {

        val tmcPrvCod2 = tmcPrvCod?.toInt()

        try {

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return
            }

            // Preparar la llamada al procedimiento almacenado
            val procedimiento = connect2.prepareCall("{call SP_TM_Actualiza_Caja(?, ?, ?, ?, ?)}")

            // Establecer los parámetros del procedimiento almacenado
            if (empCod != null) {
                procedimiento.setInt(1, empCod.toInt())
            }
            if (tmcPrvCod2 != null) {
                procedimiento.setInt(2, tmcPrvCod2)
            }
            procedimiento.setString(3, tmcAriNum)
            procedimiento.setString(4, tmcAriNumPaq)
            procedimiento.setString(5, usuCod)

            // Ejecutar el procedimiento almacenado
            procedimiento.execute()

            // Cerrar la conexión y liberar recursos
            procedimiento.close()

            connect2.close()

        }catch(e : Exception){
            println(e)
        }

    }

    fun verPedido(empCod: String?, tmcPrvCod: String?, tmcAriNum: String?, tmcAriNumPaq: String?) : String {

        try {

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return ""
            }

            //Cadena de texto Query SQL
            val query = "SELECT TOP(1) ARIPedPrv FROM TRARCIN1 (NOLOCK) WHERE EmpCod = ? AND ARIPrvCodS = ? AND ARINum = ? AND ARINumPaq = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros del procedimiento almacenado
            stmt.setString(1, empCod)
            stmt.setString(2, tmcPrvCod)
            stmt.setString(3, tmcAriNum)
            stmt.setString(4, tmcAriNumPaq)

            val rs: ResultSet = stmt.executeQuery()

            //Verifica si los valores son iguales en ambas columnas para todos los registros
            while (rs.next()) {

                val resultado = rs.getString("ARIPedPrv")

                //cierra conexiones
                rs.close()
                stmt.close()
                connect2.close()

                return resultado
            }

            //cierra conexiones
            rs.close()
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return ""
    }

    fun numeroPedidos(empCod: String?, ariPrvCodS: String?, ariNum: String?, ariNumPaq: String?) : Boolean{

        try {

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return false
            }

            //Cadena de texto Query SQL
            val query = "SELECT COUNT(DISTINCT AriPedPrv) FROM TRARCin1 (NOLOCK) WHERE EmpCod = ? AND AriPrvCodS = ? AND AriNum = ? AND AriNumPaq = ?"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros del procedimiento almacenado
            stmt.setString(1, empCod)
            stmt.setString(2, ariPrvCodS)
            stmt.setString(3, ariNum)
            stmt.setString(4, ariNumPaq)

            val rs: ResultSet = stmt.executeQuery()

            //Verifica si los valores son iguales en ambas columnas para todos los registros
            while (rs.next()) {

                val resultado = rs.getString(1).toInt()

                //cierra conexiones
                rs.close()
                stmt.close()
                connect2.close()

                return resultado > 1

            }

            //cierra conexiones
            rs.close()
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return false

    }


    fun posicionamiento(empCod: String?, ariPrvCodS: String?, ariNum: String?, ariNumPaq: String?) : Int{

        try {

            val connect = DbConnect.connectDB()
            val connect2 = connect.first

            //si es nulo, finaliza
            if(connect2 == null) {
                println("Conexión nula")
                return 0
            }

            //Cadena de texto Query SQL
            val query = "SELECT TOP(1) TMCAriArtL FROM TRTMCAJL (NOLOCK) where EmpCod = ? AND TMCPrvCod = ? AND TMCArinum = ? AND TMCAriNumP = ? ORDER BY TMCEstLin, TMCAriArtL"

            // Crear una instancia de PreparedStatement
            val stmt = connect2.prepareStatement(query)

            // Establecer los parámetros del procedimiento almacenado
            stmt.setString(1, empCod)
            stmt.setString(2, ariPrvCodS)
            stmt.setString(3, ariNum)
            stmt.setString(4, ariNumPaq)

            val rs: ResultSet = stmt.executeQuery()

            while (rs.next()) {

                val resultado = rs.getString(1).toInt() - 1

                //cierra conexiones
                rs.close()
                stmt.close()
                connect2.close()

                return resultado

            }

            //cierra conexiones
            rs.close()
            stmt.close()
            connect2.close()

        }catch(e : Exception){
            println(e)
        }

        return 0

    }

}