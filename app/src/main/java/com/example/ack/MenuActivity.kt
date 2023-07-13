/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ack.databinding.ActivityMenuBinding
import com.example.ack.inventory.InventoryFilesActivity
import com.example.ack.mercancia.MercanciaCajasActivity
import com.example.ack.picking.PickingFilesActivity
import data.Constant
import db.DbUsuario
import androidx.gridlayout.widget.GridLayout

class MenuActivity : AppCompatActivity() {

    //Variables globales
    private lateinit var binding: ActivityMenuBinding

    @SuppressLint("WorldWriteableFiles")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.gestionRecambios)

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarMenu)

        val db = DbUsuario()
        val usuario = sp.getString(Constant.username,"")?.let { db.getUsuario(it) }
        val iveco = sp.getString(Constant.companyId,"")?.let { db.getIVECO(it) }

        //Ocultar si no IVECO
        if(iveco == false){

            val layoutParams = binding.cardMercancia.layoutParams as GridLayout.LayoutParams

            layoutParams.rowSpec = GridLayout.spec(1)
            layoutParams.columnSpec = GridLayout.spec(0)

            // Actualizar las propiedades de las columnas
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams.setMargins(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
            layoutParams.columnSpec = GridLayout.spec(0, 1f)

            // Solicitar una actualización del diseño
            binding.cardMercancia.requestLayout()

            val layoutParams2 = binding.cardReubicaciones.layoutParams as GridLayout.LayoutParams

            layoutParams2.rowSpec = GridLayout.spec(1)
            layoutParams2.columnSpec = GridLayout.spec(1)

            // Actualizar las propiedades de las columnas
            layoutParams2.width = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams2.height = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams2.setMargins(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
            layoutParams2.columnSpec = GridLayout.spec(1, 1f)

            // Solicitar una actualización del diseño
            binding.cardReubicaciones.requestLayout()

            val layoutParams3 = binding.cardPendiente2.layoutParams as GridLayout.LayoutParams

            layoutParams3.rowSpec = GridLayout.spec(2)
            layoutParams3.columnSpec = GridLayout.spec(0)

            // Actualizar las propiedades de las columnas
            layoutParams3.width = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams3.height = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams3.setMargins(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
            layoutParams3.columnSpec = GridLayout.spec(0, 1f)

            // Solicitar una actualización del diseño
            binding.cardPendiente2.requestLayout()

            binding.cardIveco.visibility = View.GONE
        }

        //Si el usuario no es nulo
        if(usuario != null){
            //Subtitulo en la action bar
            supportActionBar?.subtitle = usuario.toString()
        }

        //Salir de la aplicación
        binding.btnExit.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)

            //Inicia la actividad
            startActivity(intent)
        }

        //Inventario
        binding.cardInventary.setOnClickListener {
            val intent = Intent(this, InventoryFilesActivity::class.java)

            //Inicia la actividad
            startActivity(intent)
        }

        //Picking
        binding.cardPicking.setOnClickListener {
            val intent = Intent(this, PickingFilesActivity::class.java)

            //Inicia la actividad
            startActivity(intent)
        }

        //Mercancia IVECO
        binding.cardIveco.setOnClickListener {
            val intent = Intent(this, MercanciaCajasActivity::class.java)

            //Le pasa variable de tipo booleano al intent
            intent.putExtra(Constant.isIVECO, true)

            //Inicia la actividad
            startActivity(intent)
        }

        //Mercancia NO IVECO
        binding.cardMercancia.setOnClickListener {
            val intent = Intent(this, MercanciaCajasActivity::class.java)

            //Le pasa variable de tipo booleano al intent
            intent.putExtra(Constant.isIVECO, false)

            //Inicia la actividad
            startActivity(intent)
        }

        //Reubicaciones
        binding.cardReubicaciones.setOnClickListener {

            val intent = Intent(this, ReubicacionesActivity::class.java)

            //Inicia la actividad
            startActivity(intent)

        }

        //Pendiente
        binding.cardPendiente2.setOnClickListener {

            Toast.makeText(this,"Actualmente no disponible", Toast.LENGTH_SHORT).show()

        }

    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

}