/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import kotlinx.coroutines.DelicateCoroutinesApi
import utilidades.EncryptTool
import java.sql.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbPassword {

    // Interfaz de devolución de llamada para notificar el resultado de la consulta de contraseña
    interface OnPasswordReceivedListener {
        fun onPasswordReceived(passwordMatches: Boolean)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getPassword(username: String, password: String, listener: OnPasswordReceivedListener) {

        if (username.isEmpty()) {
            listener.onPasswordReceived(false)
            return
        }

        try {
            GlobalScope.launch(Dispatchers.IO) {
                var resultado = false
                var connection: Connection? = null

                try {
                    connection = DbConnectionManager.getConnection()

                    if (connection != null) {

                        val query = DbSentences.sqlPassword

                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, username)

                        val rs = stmt.executeQuery()

                        // Cadena de texto vacía
                        var pass = ""

                        // Bucle para recorrer la tabla SQL
                        while (rs.next()) {
                            // Obtiene el primer resultado
                            pass = rs.getString(1)
                        }

                        rs.close()
                        stmt.close()

                        // Encriptación de contraseña
                        val encryptTool = EncryptTool()
                        val password2 = encryptTool.encryptString(true, password)

                        resultado = password2 == pass
                    }

                    // Notificar el resultado en el hilo principal
                    withContext(Dispatchers.Main) {
                        listener.onPasswordReceived(resultado)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Notificar el error en el hilo principal
                    withContext(Dispatchers.Main) {
                        listener.onPasswordReceived(false)
                    }
                } finally {
                    connection?.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onPasswordReceived(false)
        }
    }


}