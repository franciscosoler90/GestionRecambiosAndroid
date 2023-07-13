/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.inventory

import adapters.InventarioAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ack.R
import com.example.ack.databinding.ActivityInventory2Binding
import data.Constant
import data.InventarioLineas
import db.DbInventory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Inventory2Activity : AppCompatActivity(), SearchView.OnQueryTextListener {

    //variables globales
    private lateinit var binding: ActivityInventory2Binding
    private lateinit var bindingAdapter: InventarioAdapter
    private var tmiNro = ""
    private var tmiSer = ""
    private var almCod = ""
    private var empCod = ""
    private var username = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventory2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.Inventario)

        //Obtiene la información previa del anterior Activity
        tmiNro = intent.getStringExtra(Constant.id).toString()
        tmiSer = intent.getStringExtra(Constant.tmiSer).toString()
        almCod = intent.getStringExtra(Constant.almCod).toString()
        empCod = intent.getStringExtra(Constant.companyId).toString()

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        username = sp.getString(Constant.username, "").toString()

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarInventario)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        //Ocultar botón
        binding.btnNew.hide()

        //Añadir nuevos registros
        binding.btnNew.setOnClickListener {
            val intent = Intent(this, InventoryEditarActivity::class.java)
            startActivity(intent)
        }

        readInventory()

    }

    // Ejecuta secuencialmente
    private fun readInventory() = runBlocking {
        runGetInventory()
    }

    // Ejecuta simultáneamente
    private suspend fun runGetInventory() = coroutineScope {
        launch {

            //Generar la lista
            val db = DbInventory()

            //Llama a la función initListView con la lista de Inventario
            initListView(db.readInventory(tmiNro, empCod, tmiSer, almCod))
        }
    }

    //Al finalizar la actividad
    override fun onDestroy() {
        binding.recyclerInventory.adapter = null
        super.onDestroy()
    }

    //Método que configura el buscador
    private fun configSearchView(){

        //texto
        binding.searchViewInventory.queryHint = "Buscar..."

        //texto vacio
        binding.searchViewInventory.setQuery("",false)

        //sin icono
        binding.searchViewInventory.isIconifiedByDefault = true

        //listener
        binding.searchViewInventory.setOnQueryTextListener(this)

        //invalidar
        binding.searchViewInventory.invalidate()

        //limpia
        binding.searchViewInventory.clearFocus()
    }

    //Método que se llama al clicar en un articulo
    private fun onItemSelected(inventarioLineas: InventarioLineas){
        val intent = Intent(this, InventoryEditarActivity::class.java)

        //le pasa la información al Intent, para la siguiente actividad
        intent.putExtra(Constant.lineaId, inventarioLineas.linea )
        intent.putExtra(Constant.id, tmiNro)
        intent.putExtra(Constant.tmiSer, tmiSer)
        intent.putExtra(Constant.companyId, empCod)
        intent.putExtra(Constant.almCod, almCod)

        //Iniciar la nueva actividad
        startActivity(intent)
    }

    //Método que inicia el RecycleView
    private fun initListView(inventarioLineas : List<InventarioLineas>) {

        //Inicializa el adaptador con la lista de articulos y el correspondiente listener
        bindingAdapter = InventarioAdapter(inventarioLineas) { linea -> onItemSelected(linea) }

        //Inicializa un layout de tipo LinearLayout
        binding.recyclerInventory.layoutManager = LinearLayoutManager(this)

        //Le pasa el objeto bindingAdapter al adaptador del recyclerView
        binding.recyclerInventory.adapter = bindingAdapter

        //Le pasa la lista al adaptador
        bindingAdapter.submitList(inventarioLineas)

        //Llama a la función para configurar la barra de búsqueda
        configSearchView()

        try {
            //scroll a la posición actual
            scrolltoPosition(tmiNro.toInt())
        }catch(e : Exception){
            println(e.message)
        }
    }

    private fun scrolltoPosition(position : Int){

        //Si la posicion es mayor o igual a 0 y menor a la cantidad de elementos en el adapter
        if( position>=0 && position<bindingAdapter.itemCount) {

            //realiza scroll a la posicion
            binding.recyclerInventory.scrollToPosition(position)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        //llama a la funcion filter del adaptador
        bindingAdapter.filter(newText)
        return false
    }

    //Metodo que configura la opciones del menú
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        //Cambiar el icono del menú
        menu[0].icon = ContextCompat.getDrawable(this, R.drawable.lista)
        return super.onCreateOptionsMenu(menu)
    }

    //Método que llama cuando se clica en un elemento del menú
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        //volver atrás
        android.R.id.home ->{
            //Reset Buscador
            binding.searchViewInventory.invalidate()
            binding.searchViewInventory.clearFocus()
            binding.searchViewInventory.isIconifiedByDefault = false

            //ocultar teclado
            hideKeyboard(this)

            val dbInventory = DbInventory()

            dbInventory.comprobarInventarioLineas(username,tmiNro)

            //Volver atrás
            val intent = Intent(this, InventoryFilesActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            //Si llegamos aquí, la acción del usuario no fue reconocida.
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

        //Despejar el foco del campo de texto
        view.clearFocus()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //ocultar teclado
        hideKeyboard(this)
        return super.dispatchTouchEvent(event)
    }
}