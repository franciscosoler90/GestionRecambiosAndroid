/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.mercancia

import adapters.CabecerasLineasAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ack.R
import com.example.ack.databinding.ActivityMercanciaCajas2Binding
import data.Constant
import data.MercanciaLineas
import db.DbMercancia
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MercanciaCajas2Activity : AppCompatActivity(), SearchView.OnQueryTextListener {

    //Variables globales
    private lateinit var binding: ActivityMercanciaCajas2Binding
    private lateinit var bindingAdapter: CabecerasLineasAdapter
    private var isIVECO : Boolean = false
    private var documento : String = ""

    //Al iniciar la actividad
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMercanciaCajas2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get Intent
        isIVECO = intent.getBooleanExtra(Constant.isIVECO,false)
        documento = intent.getStringExtra(Constant.document).toString()

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarCabeceras)

        //Habilita la opción de volver atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Establece el icono de volver atrás
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)

        //Subtitulo en la action bar
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        //Número de caja
        binding.textCodigo.text = documento

        readMercancia()

        //Generar la lista
        val db = DbMercancia()

        val caja = db.getCaja(documento)

        if( caja != null ){

            val linea = db.posicionamiento(sp.getString(Constant.companyId,""),caja.TMCPrvCod,caja.TMCAriNum,caja.TMCAriNumP)

            scrolltoPosition(linea)

            println(linea)

            //Número de pedidos
            val numPedidos = db.numeroPedidos(sp.getString(Constant.companyId,""),caja.TMCPrvCod,caja.TMCAriNum,caja.TMCAriNumP)

            //varios pedidos
            if(numPedidos){
                //Número de pedido
                binding.textPedido.text = "${db.verPedido(sp.getString(Constant.companyId, ""), caja.TMCPrvCod, caja.TMCAriNum, caja.TMCAriNumP)} (Varios pedidos)"
            }else{
                //Número de pedido
                binding.textPedido.text = db.verPedido(sp.getString(Constant.companyId, ""),caja.TMCPrvCod,caja.TMCAriNum,caja.TMCAriNumP)
            }

        }

        //Listener al hacer click en el botón nuevo
        binding.btnComprobados.setOnClickListener {

            db.updateEstadoCaja(documento, sp.getString(Constant.username, ""),1)
            db.todosLineasComprobadas(documento, 1)

            val intent = Intent(this, MercanciaCajasActivity::class.java)

            //Le pasa la información al Intent
            intent.putExtra(Constant.isIVECO, isIVECO)
            intent.putExtra(Constant.document, documento)
            intent.putExtra(Constant.conformar, true)

            //Inicia la actividad
            startActivity(intent)
        }

    }

    private fun scrolltoPosition(position : Int){

        //Si la posicion es mayor o igual a 0 y menor a la cantidad de elementos en el adapter
        if( position>=0 && position<bindingAdapter.itemCount) {

            //realiza scroll a la posicion
            binding.listaCabeceras.scrollToPosition(position)
        }
    }

    // Ejecuta secuencialmente
    private fun readMercancia() = runBlocking {
        runGetMercancia()
    }

    // Ejecuta simultáneamente
    private suspend fun runGetMercancia() = coroutineScope {
        launch {

            val db = DbMercancia()

            //llama a la función readMercanciaLineas para rellenar la lista con elementos
            initListView(db.readMercanciaLineas(documento))

        }

    }

    //Al finalizar la actividad
    override fun onDestroy() {
        binding.listaCabeceras.adapter = null
        super.onDestroy()
    }

    //Metodo que configura la opciones del menú
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)

        //Cambiar el icono del menú
        if(isIVECO){
            menu[0].icon = ContextCompat.getDrawable(this, R.drawable.carpeta)
            this.title = "Recepción Cajas IVECO"
        }else{
            menu[0].icon = ContextCompat.getDrawable(this, R.drawable.proceso)
            this.title = "Recepción Mercancía No IVECO"
        }

        return super.onCreateOptionsMenu(menu)
    }

    //Método que inicia el RecycleView
    private fun initListView(lineas : List<MercanciaLineas>) {

        //Inicializa el adapter
        bindingAdapter = CabecerasLineasAdapter(lineas) { cabecera -> onItemSelected(cabecera) }

        //Establece un layout de tipo LinearLayout
        binding.listaCabeceras.layoutManager = LinearLayoutManager(this)

        //Le pasa el objeto bindingAdapter al adapter
        binding.listaCabeceras.adapter = bindingAdapter

        //Envia la lista
        bindingAdapter.submitList(lineas)

        //Llama a la funcion para configurar la barra de menú
        //configSearchView()
    }

    /*
    //Método que configura el buscador
    private fun configSearchView(){
        binding.searchViewCabeceras.queryHint = "Buscar..."
        binding.searchViewCabeceras.setQuery("",false)
        binding.searchViewCabeceras.isIconifiedByDefault = true
        binding.searchViewCabeceras.setOnQueryTextListener(this)
        binding.searchViewCabeceras.invalidate()
        binding.searchViewCabeceras.clearFocus()
    }
    */

    //Método que se llama al clicar en un item
    private fun onItemSelected(linea: MercanciaLineas){
        val intent = Intent(this, MercanciaLineasActivity::class.java)

        intent.putExtra(Constant.isIVECO, isIVECO )
        intent.putExtra(Constant.document,documento)
        intent.putExtra(Constant.lineaId, linea.TMCAriArtL )

        startActivity(intent)
    }

    //Método que llama cuando se clica en un elemento del menú
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home ->{
            //Volver atrás
            val intent = Intent(this, MercanciaCajasActivity::class.java)
            intent.putExtra(Constant.isIVECO, isIVECO)
            intent.putExtra(Constant.document,documento)
            startActivity(intent)
            true
        }

        else -> {
            // Si llegamos aquí, la acción del usuario no fue reconocida.
            // Invoca a la superclase para manejarlo.
            super.onOptionsItemSelected(item)
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Encuentra la vista actualmente enfocada, para que podamos obtener el token de ventana correcto de ella.

        var view: View? = activity.currentFocus
        //Si ninguna vista tiene actualmente el foco, crea una nueva, sólo para que podamos obtener un token de ventana de ella

        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        view.clearFocus()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        hideKeyboard(this)
        return super.dispatchTouchEvent(event)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        bindingAdapter.filter(newText)
        return false
    }

}