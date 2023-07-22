/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.Usuario
import kotlinx.coroutines.DelicateCoroutinesApi
import java.sql.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbUsuario {

    @OptIn(DelicateCoroutinesApi::class)
    fun getUsuario(username: String, listener: OnUsuarioReceivedListener) {

        if (username.isEmpty()) {
            listener.onUsuarioReceived(null)
            return
        }

        try {
            GlobalScope.launch(Dispatchers.IO) {
                var resultado: Usuario? = null
                var connection: Connection? = null

                try {
                    connection = DbConnectionManager.getConnection()

                    if (connection != null) {
                        val query = DbSentences.sqlUsuario

                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, username)

                        val rs = stmt.executeQuery()

                        while (rs.next()) {
                            resultado = Usuario(
                                rs.getString(1).trim(),
                                rs.getString(2).trim()
                            )
                        }

                        rs.close()
                        stmt.close()
                    }

                    withContext(Dispatchers.Main) {
                        listener.onUsuarioReceived(resultado)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        listener.onUsuarioReceived(null)
                    }
                } finally {
                    connection?.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onUsuarioReceived(null)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getIVECO(empCod: String, listener: OnIVECOReceivedListener) {
        if (empCod.isEmpty()) {
            listener.onIVECOReceived(false)
            return
        }

        try {
            GlobalScope.launch(Dispatchers.IO) {
                var resultado = false
                var connection: Connection? = null

                try {
                    connection = DbConnectionManager.getConnection()

                    if (connection != null) {

                        val query = DbSentences.sqlUsuarioIveco

                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, empCod)

                        val rs = stmt.executeQuery()

                        var resultado1 = ""

                        while (rs.next()) {
                            resultado1 = rs.getString(1)
                        }

                        rs.close()
                        stmt.close()

                        resultado = resultado1 != "N"

                    }

                    withContext(Dispatchers.Main) {
                        listener.onIVECOReceived(resultado)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        listener.onIVECOReceived(false)
                    }
                } finally {
                    connection?.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onIVECOReceived(false)
        }
    }

    // Interfaz de devolución de llamada para notificar el resultado de la consulta de usuario
    interface OnUsuarioReceivedListener {
        fun onUsuarioReceived(usuario: Usuario?)
    }

    // Interfaz de devolución de llamada para notificar el resultado de la consulta de IVECO
    interface OnIVECOReceivedListener {
        fun onIVECOReceived(ivecoExists: Boolean)
    }
}
