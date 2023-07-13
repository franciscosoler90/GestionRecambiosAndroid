/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package data

data class PickingCabecera(
    val EmpCod: String?,
    val TMPSer: String?,
    val TMPNro: String?,
    val TMPFaCo: String?,
    val TMPCliCod: String?,
    val TMPCliNom: String?,
    val TMPUltNroL: String?,
    val TMPFec: String?,
    val TMPEst: String?,
    val linea: Int
)

data class PickingLineas(
    val EmpCod: String?,
    val TMPSer: String?,
    val TMPNro: String?,
    val TMPArtLin: Int,
    val TMPAlmCod: String?,
    val TMPArtCod: String?,
    val TMPArtDes: String?,
    val TMPArtUbi: String?,
    val TMPArtUni: String?,
    val TMPArtUnRe: String?,
    val TMPLinEst: String?,
    val linea: Int
)
