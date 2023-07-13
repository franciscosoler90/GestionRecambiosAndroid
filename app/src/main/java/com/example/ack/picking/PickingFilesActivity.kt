/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.picking

import adapters.PickingFilesAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ack.MenuActivity
import com.example.ack.R
import com.example.ack.databinding.ActivityInventoryFilesBinding
import data.Constant
import data.PickingCabecera
import db.DbPicking
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PickingFilesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryFilesBinding
    private lateinit var bindingAdapter: PickingFilesAdapter
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.picking)

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)
        username = sp.getString(Constant.username, "").toString()

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarInventario)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        //Ocultar botón
        binding.btnNewFile.hide()

        readPicking()

        val position = intent.getIntExtra(Constant.position,0)

        scrolltoPosition(position)

    }

    private fun scrolltoPosition(position : Int){
        if( position>=0 && position<bindingAdapter.itemCount) {
            binding.recyclerFiles.scrollToPosition(position)
        }
    }

    // Ejecuta secuencialmente
    private fun readPicking() = runBlocking {
        runGetPicking()
    }

    // Ejecuta simultáneamente
    private suspend fun runGetPicking() = coroutineScope {
        launch {
            val db = DbPicking()
            initListView(db.readPickingFiles(username))
        }
    }

    //Método que inicia el RecycleView
    private fun initListView(pickingCabeceraFiles : List<PickingCabecera>) {
        bindingAdapter = PickingFilesAdapter { file -> onItemSelected(file) }
        binding.recyclerFiles.layoutManager = LinearLayoutManager(this)
        binding.recyclerFiles.adapter = bindingAdapter
        bindingAdapter.submitList(pickingCabeceraFiles)
    }

    //Método que se llama al clicar en un item
    private fun onItemSelected(cabecera: PickingCabecera){
        val intent = Intent(this, Picking2Activity::class.java)
        intent.putExtra(Constant.id, cabecera.TMPNro )
        intent.putExtra(Constant.position, cabecera.linea )
        startActivity(intent)
    }

    override fun onDestroy() {
        binding.recyclerFiles.adapter = null
        super.onDestroy()
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
            //Volver atrás
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}