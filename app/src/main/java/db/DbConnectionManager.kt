/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

import java.sql.Connection
import java.sql.DriverManager

object DbConnectionManager {

    private val connectURL = "jdbc:jtds:sqlserver://${DbValues.ip}:${DbValues.port}/${DbValues.database}"

    fun getConnection(): Connection? {
        return try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            DriverManager.getConnection(connectURL, DbValues.username.trim(), DbValues.password.trim())
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            null
        }
    }
}