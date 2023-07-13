/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package data

data class InventarioCabecera(
    val EmpCod: String?,
    val TMISer: String?,
    val TMINro: String?,
    val AlmCod: String?,
    val TMIFec: String?,
    val TMIEst: String?,
    val TMIUsuGen: String?,
    val TMIUsuTer: String?
)

data class InventarioLineas(
    val EmpCod: String?,
    val TMISer: String?,
    val TMINro: String?,
    val AlmCod: String?,
    val TMIArtUbi: String?,
    val TMIArtCod: String?,
    val TMIArtDes: String?,
    val TMIArtExi: String?,
    val TMIUniCon: String?,
    val TMILinEst: String?,
    val TMIFecHor: String?,
    val linea: Int
)