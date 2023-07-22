/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.StrictMode
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DbConnect {

    companion object {
        fun connectDB(): Pair<Connection?, String> {

            // Variables para almacenar la conexión y el mensaje de error
            var connection: Connection? = null
            var errorMessage: String

            // Intentar establecer la conexión
            try {

                // Cadena de texto con en enlace de conexión
                val connectURL = "jdbc:jtds:sqlserver://${DbValues.ip.trim()}:${DbValues.port.trim()}/${DbValues.database.trim()}"

                // Política StrictMode aplicada a un determinado hilo, permitir todos
                val politics = StrictMode.ThreadPolicy.Builder().permitAll().build()

                // Aplicar los cambios
                StrictMode.setThreadPolicy(politics)

                // Nombre de la clase
                Class.forName("net.sourceforge.jtds.jdbc.Driver")

                // Como parte de su inicialización, la clase DriverManager intentará cargar las clases de controladores referenciadas en la propiedad del sistema
                connection = DriverManager.getConnection(connectURL, DbValues.username.trim(), DbValues.password.trim())

                errorMessage = "Conexión a la base de datos satisfactoria"

            } catch (e: SQLException) {
                // Error específico de SQL
                errorMessage = "Ha ocurrido un error de SQL en la conexión con la base de datos: ${e.message}"
            } catch (e: ClassNotFoundException) {
                // Error de clase no encontrada
                errorMessage = "No se ha encontrado la clase del controlador de la base de datos: ${e.message}"
            } catch (e: Exception) {
                // Otros errores
                errorMessage = "Ha ocurrido un error en la conexión con la base de datos: ${e.message}"
            }

            // Retornar la conexión y el mensaje de error
            return Pair(connection, errorMessage)
        }
    }
}