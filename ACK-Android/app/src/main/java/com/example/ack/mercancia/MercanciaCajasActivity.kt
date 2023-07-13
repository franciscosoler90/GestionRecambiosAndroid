/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.mercancia

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.ack.MenuActivity
import com.example.ack.R
import com.example.ack.databinding.ActivityMercanciaCajasBinding
import data.Constant
import db.DbMercancia

class MercanciaCajasActivity : AppCompatActivity() {

    //Variables globales
    private lateinit var binding: ActivityMercanciaCajasBinding

    private var isIVECO : Boolean = false
    private var conformar : Boolean = false

    private var username = ""
    private var documento = ""

    //Al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMercanciaCajasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Variables Booleans
        isIVECO = intent.getBooleanExtra(Constant.isIVECO,false)
        conformar = intent.getBooleanExtra(Constant.conformar,false)

        documento = intent.getStringExtra(Constant.document).toString()

        if(documento != "null") {
            val editableDocumento: Editable = SpannableStringBuilder(documento)
            binding.txtCaja.text = editableDocumento
        }

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        //Obtiene las cadenas de texto
        username = sp.getString(Constant.username,"").toString()

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarCabeceras)

        //Establece el botón para volver atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Icono de volver atrás
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)

        //Subtitulo
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        if(!conformar){
            binding.btnConforme.hide()
        }

        //campo de texto de Unidades Recogidas
        binding.txtCaja.requestFocus()
        binding.txtCaja.isFocusable = true
        binding.txtCaja.isFocusableInTouchMode = true
        binding.txtCaja.isEnabled = true
        binding.txtCaja.isActivated = true
        binding.txtCaja.isCursorVisible = true

        //Evento del listener del botón nuevo
        binding.btnConforme.setOnClickListener {

            //Generar la lista
            val db = DbMercancia()

            val cajaTexto = binding.txtCaja.text.toString()

            val codigo: String = if (cajaTexto.length == 10) {
                cajaTexto.substring(cajaTexto.length - 9)
            }else{
                cajaTexto
            }

            val caja = db.getCaja(cajaTexto)

            if( caja != null ){

                if( db.comprobarLineas(cajaTexto) ){

                    db.updateEstadoCaja(cajaTexto, sp.getString(Constant.username, ""),3)

                }else{

                    db.updateEstadoCaja(cajaTexto, sp.getString(Constant.username, ""),2)

                }

                db.ejecutarProcedimiento(sp.getString(Constant.companyId, ""), caja.TMCPrvCod, caja.TMCAriNum, codigo, sp.getString(Constant.username, ""))

                val editableDocumento: Editable = SpannableStringBuilder("")

                binding.txtCaja.text = editableDocumento

                binding.btnConforme.hide()

                binding.txtCaja.requestFocus()

                Toast.makeText(this,"Caja $codigo conformada", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this,"Caja $codigo no recibida en los archivos de IVECO",Toast.LENGTH_SHORT).show()
            }

        }

        binding.buttonConfirmar.setOnClickListener {

            //Generar la lista
            val db = DbMercancia()

            val cajaTexto = binding.txtCaja.text.toString()

            val codigo: String = if (cajaTexto.length == 10) {
                cajaTexto.substring(cajaTexto.length - 9)
            }else{
                cajaTexto
            }

            val caja = db.getCaja( codigo )

            if( caja != null ){

                val estado = db.estadoCaja( codigo )

                if(estado == 0 || estado == 1){

                    val intent = Intent(this, MercanciaCajas2Activity::class.java)
                    intent.putExtra(Constant.document, codigo)
                    intent.putExtra(Constant.isIVECO, isIVECO)
                    startActivity(intent)

                }else{
                    Toast.makeText(this, "Caja $codigo ya actualizada en XAUTO. No es posible consultar",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this,"Caja $codigo no recibida en los archivos de IVECO",Toast.LENGTH_SHORT).show()
            }
        }

    }

    //Método que llama cuando se clica en un elemento del menú
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        //Volver atrás
        android.R.id.home ->{

            //Ocultar el teclado
            hideKeyboard(this)

            //Declara el intent
            val intent = Intent(this, MenuActivity::class.java)

            //Inicia la actividad
            startActivity(intent)
            true
        }

        else -> {
            // Si llegamos aquí, la acción del usuario no fue reconocida.
            // Invoca a la superclase para manejarlo.
            super.onOptionsItemSelected(item)
        }
    }

    //Metodo que configura la opciones del menú
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)

        //Cambiar el icono del menú
        if(isIVECO){

            //Icono del menú
            menu[0].icon = ContextCompat.getDrawable(this, R.drawable.carpeta)

            //Titulo
            this.title = getString(R.string.mercanciaIVECO)
        }else{

            //Icono del menú
            menu[0].icon = ContextCompat.getDrawable(this, R.drawable.proceso)

            //Titulo
            this.title = getString(R.string.mercanciaNoIVECO)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Encuentra la vista actualmente enfocada, para que podamos obtener el token de ventana correcto de ella.
        var view: View? = activity.currentFocus
        //Si ninguna vista tiene actualmente el foco, crea una nueva, sólo para que podamos obtener un token de ventana de ella
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        //Limpia
        view.clearFocus()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        hideKeyboard(this)
        return super.dispatchTouchEvent(event)
    }

}