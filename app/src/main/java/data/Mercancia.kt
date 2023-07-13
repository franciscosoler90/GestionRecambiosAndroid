/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package data


data class MercanciaCaja(
    val EmpCod : String?,
    val TMCPrvCod : String?,
    val TMCAriNum : String?,
    val TMCAriNumP: String?,
    val TMCAriFec: String?,
    val TMCEst: String?,
    val TMCUsuTer: String?,
    val TMCFecVal: String?,
    val TMCHorVal: String?,
    val TMCObs: String?,
    val TMCAct: String?,
)

data class MercanciaLineas(
    val EmpCod : String?,
    val TMCPrvCod : String?,
    val TMCAriNum : String?,
    val TMCAriNumP: String?,
    val TMCAriArtL: String?,
    val TMCAriArtC: String?,
    val TMCAriArtD: String?,
    val TMCAriAlmC: String?,
    val TMCAriUbi: String?,
    val TMCAriUni: String?,
    val TMCUniCont: String?,
    val TMCEstLin: String?,
)