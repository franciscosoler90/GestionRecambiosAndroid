/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.picking

import adapters.PickingLineasAdapter
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ack.R
import com.example.ack.databinding.ActivityPicking2Binding
import data.Constant
import data.PickingLineas
import db.DbPicking
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Picking2Activity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityPicking2Binding
    private lateinit var bindingAdapter: PickingLineasAdapter

    companion object {
        private var positionCabecera = 0
        private var positionLinea = 0
        private var id = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPicking2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.picking)

        positionCabecera = intent.getIntExtra(Constant.position,0)

        positionLinea = intent.getIntExtra(Constant.lineaId,0)

        id = intent.getStringExtra(Constant.id).toString()

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarPicking)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        readPicking()

    }

    // Ejecuta secuencialmente
    private fun readPicking() = runBlocking {
        runGetPicking()
    }

    // Ejecuta simultáneamente
    private suspend fun runGetPicking() = coroutineScope {
        launch {
            //Generar la lista
            val db = DbPicking()
            initListView( db.readPicking(id) )
        }
    }

    //Al finalizar la activity
    override fun onDestroy() {
        binding.recyclerPicking.adapter = null
        super.onDestroy()
    }

    //Método que configura el buscador
    private fun configSearchView(){
        binding.searchViewPicking.queryHint = "Buscar..."
        binding.searchViewPicking.setQuery("",false)
        binding.searchViewPicking.isIconifiedByDefault = true
        binding.searchViewPicking.setOnQueryTextListener(this)
        binding.searchViewPicking.invalidate()
        binding.searchViewPicking.clearFocus()
    }

    //Método que inicia el RecycleView
    private fun initListView(articulosList : List<PickingLineas>) {

        bindingAdapter = PickingLineasAdapter(articulosList) { articulo -> onItemSelected(articulo) }
        binding.recyclerPicking.layoutManager = LinearLayoutManager(this)
        binding.recyclerPicking.adapter = bindingAdapter

        bindingAdapter.submitList(articulosList)
        configSearchView()
        scrolltoPosition(positionLinea)
    }

    private fun scrolltoPosition(position : Int){
        if( position>=0 && position<bindingAdapter.itemCount) {
            binding.recyclerPicking.scrollToPosition(position)
        }
    }

    //Método que se llama al clicar en un articulo
    private fun onItemSelected(picking: PickingLineas){
        val intent = Intent(this, PickingEditarActivity::class.java)
        intent.putExtra(Constant.position, positionCabecera)
        intent.putExtra(Constant.lineaId, picking.TMPArtLin )
        intent.putExtra(Constant.id, id)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        bindingAdapter.filter(newText)
        return false
    }

    //Metodo que configura la opciones del menú
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)

        //Cambiar el icono del menú
        menu[0].icon = ContextCompat.getDrawable(this, R.drawable.pos)

        return super.onCreateOptionsMenu(menu)
    }

    //Método que llama cuando se clica en un elemento del menú
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home ->{
            //Reset Buscador
            binding.searchViewPicking.invalidate()
            binding.searchViewPicking.clearFocus()
            binding.searchViewPicking.isIconifiedByDefault = false
            hideKeyboard(this)

            //Volver atrás
            val intent = Intent(this, PickingFilesActivity::class.java)
            intent.putExtra(Constant.position, positionCabecera)
            startActivity(intent)
            true
        }

        else -> {
            // Si llegamos aquí, la acción del usuario no fue reconocida.
            // Invoca a la superclase para manejarlo.
            super.onOptionsItemSelected(item)
        }
    }

    //Función para esconder el teclado
    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Encuentra la vista actualmente enfocada, para que podamos obtener el token de ventana correcto de ella.
        var view: View? = activity.currentFocus
        //Si ninguna vista tiene actualmente el foco, crea una nueva, sólo para que podamos obtener un token de ventana de ella
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        //Despeja el foco
        view.clearFocus()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        hideKeyboard(this)
        return super.dispatchTouchEvent(event)
    }

}