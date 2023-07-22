/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import data.Empresa
import kotlinx.coroutines.DelicateCoroutinesApi
import java.sql.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbEmpresas {

    interface OnCompaniesDataReceivedListener {
        fun onCompaniesDataReceived(companies: List<Empresa>)
        fun onError(errorMessage: String)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getCompanies(username: String?, listener: OnCompaniesDataReceivedListener) {

        val lista: ArrayList<Empresa> = ArrayList()

        if (username.isNullOrEmpty()) {
            listener.onCompaniesDataReceived(lista.toList())
            return
        }

        try {
            GlobalScope.launch(Dispatchers.IO) {
                var connection: Connection? = null

                try {
                    connection = DbConnectionManager.getConnection()

                    if (connection != null) {

                        val query = DbSentences.sqlEmpresas

                        val stmt = connection.prepareStatement(query)
                        stmt.setString(1, username)

                        val rs = stmt.executeQuery()

                        while (rs.next()) {
                            val empresa = Empresa(
                                rs.getString(1),
                                rs.getString(2)
                            )
                            lista.add(empresa)
                        }

                        rs.close()
                        stmt.close()
                    }

                    withContext(Dispatchers.Main) {
                        listener.onCompaniesDataReceived(lista.toList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        listener.onError("Error en la conexión: ${e.message}")
                    }
                } finally {
                    connection?.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onError(e.toString())
        }
    }
}
