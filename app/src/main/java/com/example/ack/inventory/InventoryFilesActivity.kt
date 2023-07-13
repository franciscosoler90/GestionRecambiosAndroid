/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.inventory

import adapters.InventarioCabeceraAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ack.MenuActivity
import com.example.ack.R
import com.example.ack.databinding.ActivityInventoryFilesBinding
import data.Constant
import data.InventarioCabecera
import db.DbInventory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class InventoryFilesActivity : AppCompatActivity() {

    //Variables globales
    private lateinit var binding: ActivityInventoryFilesBinding
    private lateinit var bindingAdapter: InventarioCabeceraAdapter
    private var username = ""

    //Al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.Inventario)

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

        readInventory()

    }

    // Ejecuta secuencialmente
    private fun readInventory() = runBlocking {
        runGetInventory()
    }

    // Ejecuta simultáneamente
    private suspend fun runGetInventory() = coroutineScope {
        launch {
            val db = DbInventory()

            //LLama a la función de readInventory para rellenar la lista con elementos
            initListView(db.readInventoryFiles(username))
        }
    }

    //Método que inicia el RecycleView
    private fun initListView(items : List<InventarioCabecera>) {
        bindingAdapter = InventarioCabeceraAdapter { item -> onItemSelected(item) }
        binding.recyclerFiles.layoutManager = LinearLayoutManager(this)
        binding.recyclerFiles.adapter = bindingAdapter
        bindingAdapter.submitList(items)
    }

    //Método que se llama al clicar en un item
    private fun onItemSelected(cabecera: InventarioCabecera){
        val intent = Intent(this, Inventory2Activity::class.java)
        intent.putExtra(Constant.id, cabecera.TMINro)
        intent.putExtra(Constant.tmiSer, cabecera.TMISer)
        intent.putExtra(Constant.companyId, cabecera.EmpCod)
        intent.putExtra(Constant.almCod, cabecera.AlmCod)
        startActivity(intent)
    }

    //Al finalizar la actividad
    override fun onDestroy() {
        binding.recyclerFiles.adapter = null
        super.onDestroy()
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

        //Volver atrás
        android.R.id.home ->{

            //Declarar la Intent
            val intent = Intent(this, MenuActivity::class.java)

            //Iniciar la actividad
            startActivity(intent)
            true
        }

        else -> {
            // Si llegamos aquí, la acción del usuario no fue reconocida.
            // Invoca a la superclase para manejarlo.
            super.onOptionsItemSelected(item)
        }
    }
}