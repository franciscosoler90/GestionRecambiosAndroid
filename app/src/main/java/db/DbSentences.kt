/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package db

object DbSentences {

    //PASSWORD -----------------------------------------------------------------------------------------------------------------

    const val sqlPassword =
        "SELECT UsuPas FROM TRUSU (NOLOCK) WHERE UsuCod = ?"

    //USUARIO -----------------------------------------------------------------------------------------------------------------

    const val sqlUsuario =
        "SELECT UsuCod, UsuNom FROM TRUSU (NOLOCK) WHERE UsuCod = ?"

    const val sqlUsuarioIveco =
        "SELECT PchChe FROM TRPCH1 (NOLOCK) WHERE EmpCod = ? AND PchGru = 'R' AND PchAtr = 'TMCAJASIVE'"

    //EMPRESAS -----------------------------------------------------------------------------------------------------------------

    const val sqlEmpresas =
        "SELECT EmpCod, EmpNom FROM TRUSU1 (NOLOCK) INNER JOIN TREMP ON (TRUSU1.UsuEmpCodS = TREMP.EmpCod) WHERE UsuCod = ? ORDER BY EmpNom ASC"

    //ARTICULO -----------------------------------------------------------------------------------------------------------------

    const val sqlArticulo =
        "SELECT ArtDes FROM TRART (NOLOCK) WHERE ArtCod = ? AND EmpCod = ?"

    //ALMACEN -----------------------------------------------------------------------------------------------------------------

    const val sqlAlmacen =
        "SELECT TOP(1) AlmCod FROM TREXI1 (NOLOCK) WHERE ArtCod = ? AND EmpCod = ?"

    const val sqlAlmacenes =
        "SELECT T1.SedAlmCodS, T2.AlmDes FROM TRSED2 T1 (NOLOCK) INNER JOIN TRALM T2 ON T1.SedAlmCodS=T2.AlmCod WHERE T1.EmpCod = ?"

    //UBICACIONES -----------------------------------------------------------------------------------------------------------------

    const val sqlUbicacion =
        "SELECT TOP(1) ExiUbi FROM TREXI1 (NOLOCK) WHERE EmpCod = ? AND AlmCod = ? AND ArtCod = ?"
    const val sqlUbicaciones =
        "SELECT ExiUbi, ExiExi FROM TREXI1 (NOLOCK) WHERE EmpCod = ? AND AlmCod = ? AND ArtCod = ?"

    //REUBICACIONES -----------------------------------------------------------------------------------------------------------------

    const val procedureReubicaciones =
        "{call SP_TM_Reubica(?, ?, ?, ?, ?, ?, ?)}"

    //INVENTARIO -----------------------------------------------------------------------------------------------------------------

    const val sqlInventario =
        "SELECT * FROM TRTMINV (NOLOCK) WHERE TMIEst < 3"

    const val sqlInventarioLineas =
        "SELECT * FROM TRTMINVL (NOLOCK) WHERE TMINro = ? AND EmpCod = ? AND TMISer = ? AND AlmCod = ? AND TMILinEst < 3 ORDER BY TMILinEst"

    const val sqlInventarioLinea =
        "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS RowNum FROM TRTMINVL WHERE TMINro = ?) AS TablaTemporal WHERE TablaTemporal.RowNum = ?"

    const val sqlInventarioUpdateLinea =
        "UPDATE TablaTemporal SET TMIUniCon = ?, TMILinEst = 1 FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY (SELECT 1)) AS RowNum FROM TRTMINVL WHERE TMINro = ?) AS TablaTemporal WHERE TablaTemporal.RowNum = ?"

    const val stringTodasLineas =
        "todas_las_lineas_con_TMINLinEst_igual_a_1"

    const val sqlInventarioCheckLineas =
        "SELECT CASE WHEN COUNT(*) = SUM(CASE WHEN TMILinEst = 1 THEN 1 ELSE 0 END) THEN 'True' ELSE 'False' END AS $stringTodasLineas FROM TRTMINVL WHERE TMINro = ?"

    const val sqlInventarioUpdate =
        "UPDATE TRTMINV SET TMIEst = ?, TMIUsuTer = ? WHERE TMINro = ?"

    //PICKING -----------------------------------------------------------------------------------------------------------------

    const val sqlPicking =
        "SELECT * FROM TRTMPIC (NOLOCK)"

    const val sqlPickingLineas =
        "SELECT * FROM TRTMPICL (NOLOCK) WHERE TMPNro = ?"

    const val sqlPickingLinea =
        "SELECT * FROM TRTMPICL (NOLOCK) WHERE TMPNro = ? AND TMPArtLin = ?"

    const val sqlPickingUpdateUnidades =
        "UPDATE TRTMPICL SET TMPArtUnRe = ? WHERE TMPNro = ? AND TMPArtLin = ?"

    const val sqlPickingUpdateEstado =
        "UPDATE TRTMPICL SET TMPLinEst = ? WHERE TMPNro = ? AND TMPArtLin = ?"

    const val sqlPickingUpdate =
        "UPDATE TRTMPIC SET TMPEst = ? WHERE TMPNro = ?"

    //MERCANCIA -----------------------------------------------------------------------------------------------------------------

    const val sqlMercanciaCaja =
        "SELECT * FROM TRTMCAJ (NOLOCK) WHERE TMCAriNumP = ?"

    const val sqlMercanciaLinea =
        "SELECT * FROM TRTMCAJL (NOLOCK) WHERE TMCAriNumP = ? AND TMCAriArtL = ?"

    const val sqlMercanciaLineas =
        "SELECT * FROM TRTMCAJL (NOLOCK) WHERE TMCAriNumP = ? ORDER BY TMCEstLin"

    const val sqlMercanciaEstado =
        "SELECT TMCEst FROM TRTMCAJ (NOLOCK) WHERE TMCAriNumP = ?"

    const val sqlMercanciaUpdateLineas =
        "UPDATE TRTMCAJL SET TMCEstLin = ?, TMCUniCont = ? WHERE TMCAriNumP = ? AND TMCAriArtL = ?"

    const val sqlMercanciaUpdateCaja =
        "UPDATE TRTMCAJ SET TMCEst = ?, TMCUsuTer = ?, TMCFecVal = ?, TMCHorVal = ? WHERE TMCAriNumP = ?"

    const val sqlMercanciaUpdateComprobados =
        "UPDATE TRTMCAJL SET TMCEstLin = ? WHERE TMCAriNumP = ?"

    const val sqlMercanciaLineasComprobados =
        "SELECT TMCAriUni, TMCUniCont FROM TRTMCAJL (NOLOCK) WHERE TMCAriNumP = ?"

    const val procedureMercancia =
        "{call SP_TM_Actualiza_Caja(?, ?, ?, ?, ?)}"

    const val sqlMercanciaPedido =
        "SELECT TOP(1) ARIPedPrv FROM TRARCIN1 (NOLOCK) WHERE EmpCod = ? AND ARIPrvCodS = ? AND ARINum = ? AND ARINumPaq = ?"

    const val sqlMercanciaNumeroPedidos =
        "SELECT COUNT(DISTINCT AriPedPrv) FROM TRARCin1 (NOLOCK) WHERE EmpCod = ? AND AriPrvCodS = ? AND AriNum = ? AND AriNumPaq = ?"

    const val sqlMercanciaPosicionamiento =
        "SELECT TOP(1) TMCAriArtL FROM TRTMCAJL (NOLOCK) where EmpCod = ? AND TMCPrvCod = ? AND TMCArinum = ? AND TMCAriNumP = ? ORDER BY TMCEstLin, TMCAriArtL"


}