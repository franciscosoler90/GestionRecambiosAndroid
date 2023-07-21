/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import android.os.Handler
import android.os.StrictMode
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DbConnect {

    companion object {
        data class ConnectionResult(val connection: Connection?, val errorMessage: String?)

        fun connectDB(handler: Handler, onConnectionResult: (ConnectionResult) -> Unit) {
            var connection: Connection? = null
            var errorMessage: String?

            try {
                val connectURL = "jdbc:jtds:sqlserver://${DbValues.ip.trim()}:${DbValues.port.trim()}/${DbValues.database.trim()}"

                val politics = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(politics)

                Class.forName("net.sourceforge.jtds.jdbc.Driver")

                // Intentar conectarse a la base de datos y obtener la conexión
                val connectionRunnable = Runnable {
                    connection = try {
                        DriverManager.getConnection(connectURL, DbValues.username.trim(), DbValues.password.trim())
                    } catch (e: SQLException) {
                        null
                    } catch (e: ClassNotFoundException) {
                        null
                    } catch (e: Exception) {
                        null
                    }

                    errorMessage = if (connection != null) {
                        "Conexión a la base de datos satisfactoria"
                    } else {
                        "Ha ocurrido un error en la conexión con la base de datos"
                    }

                    // Cerrar la conexión si se estableció
                    connection?.close()

                    // Enviar los resultados al Handler
                    handler.post {
                        // Aquí puedes manejar los resultados en el hilo principal
                        // Por ejemplo, mostrar un Toast con el mensaje de error, etc.
                        onConnectionResult(ConnectionResult(connection, errorMessage))
                    }
                }

                // Establecer un tiempo límite para la conexión
                val timeoutMillis = 3000L

                // Ejecutar el Runnable después del tiempo límite especificado
                handler.postDelayed(connectionRunnable, timeoutMillis)

            } catch (e: Exception) {
                errorMessage = "Ha ocurrido un error en la conexión con la base de datos:\n${e.message}"

                // Enviar los resultados al Handler (incluso si hubo una excepción)
                handler.post {
                    onConnectionResult(ConnectionResult(connection, errorMessage))
                }
            }
        }
    }
}
