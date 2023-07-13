/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.ack.databinding.ActivityReubicacionesBinding
import data.Almacen
import data.Constant
import db.DbAlmacen
import db.DbArticulo
import db.DbReubicaciones
import db.DbUbicacion

class ReubicacionesActivity : AppCompatActivity() {

    //Variables globales
    private lateinit var binding: ActivityReubicacionesBinding

    private var almacen : Almacen? = null
    private var unidadesOrigen = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReubicacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonReubicar.setBackgroundResource(R.drawable.boton_selector)

        binding.buttonReubicar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green2)

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        //Configura la barra de menú
        setSupportActionBar(binding.toolbarEditar)

        //Establece el botón para volver atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Icono de volver atrás
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)

        //Subtitulo
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        setupSpinner()

        binding.buttonReubicar.isEnabled = false

        binding.articulo.requestFocus()
        binding.articulo.isFocusable = true
        binding.articulo.isFocusableInTouchMode = true
        binding.articulo.isEnabled = true
        binding.articulo.isActivated = true
        binding.articulo.isCursorVisible = true

        binding.buttonLimpiar.setOnClickListener {
            limpiar()
        }

        binding.buttonReubicar.setOnClickListener {

            if(almacen == null){
                Toast.makeText(this,"Almacén no válido",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {

                val unidades = binding.unidades.text.toString()

                if(unidades.isEmpty()){
                    Toast.makeText(this,"Introduce una cantidad de unidades válida.",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val unidadesFlotante = unidades.toFloat()

                if(unidadesFlotante.isNaN()){
                    Toast.makeText(this,"Introduce una cantidad de unidades válida.",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if(unidadesFlotante > unidadesOrigen){
                    Toast.makeText(this,"Introduce un cantidad de unidades menor o igual que la ubicación de origen",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if(unidadesFlotante <= 0){
                    Toast.makeText(this,"Introduce un cantidad de unidades mayor que 0",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val artCod = binding.articulo.text.toString()

                if(artCod.isEmpty()){
                    Toast.makeText(this,"Introduce un código de articulo",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val ubicacionOrigen = binding.ubicacionOrigen.text.toString()

                if(ubicacionOrigen.isEmpty()){
                    Toast.makeText(this,"Introduce una ubicación de origen",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val ubicacionDestino = binding.ubicacionDestino.text.toString()

                if(ubicacionDestino.isEmpty()){
                    Toast.makeText(this,"Introduce una ubicación de destino",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val empCod = sp.getString(Constant.companyId,"")
                val usuCod = sp.getString(Constant.username,"")
                val almCod = almacen!!.id

                val db = DbReubicaciones()

                db.reubicar(empCod,usuCod,almCod,artCod,ubicacionOrigen,ubicacionDestino, unidadesFlotante)

                Toast.makeText(this,"Unidades reubicadas",Toast.LENGTH_LONG).show()

                limpiar()

            }catch (e : Exception){
                Toast.makeText(this,"Se ha producido un error.",Toast.LENGTH_LONG).show()
            }

        }

        //Establece un TextWatcher en el campo articulo, que detecta cambios cuando se introduce texto
        binding.articulo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            //Llama a esta función cuando el texto ha cambiado
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                updateArticulo(s.toString())
                updateTabla(s.toString())

            }
        })

    }

    private fun updateArticulo(articulo : String){

        val db2 = DbArticulo()

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        val articulo2 = db2.getDescription(sp.getString(Constant.companyId, ""), articulo)

        binding.descripcion.text = articulo2

    }

    private fun updateTabla(articulo : String){

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        val db = DbUbicacion()

        val db2 = DbAlmacen()

        val almacen = db2.getAlmacen(sp.getString(Constant.companyId, ""), articulo)

        val lista2 = db.getUbicaciones(sp.getString(Constant.companyId, ""),almacen,articulo)

        // Remover todas las filas excepto la cabecera
        val rowCount = binding.tabla.childCount
        if (rowCount > 1) {
            binding.tabla.removeViews(1, rowCount - 1)
        }

        lista2.forEach { existencia ->
            val row = TableRow(this)

            row.setBackgroundColor(Color.parseColor("#FF70A7BF")) // Establecer color de fondo de la fila

            val ubicacionTextView = TextView(this)
            ubicacionTextView.text = existencia.exiUbi
            ubicacionTextView.setPadding(convertDpToPx(5), convertDpToPx(5), convertDpToPx(5), convertDpToPx(5)) // Añadir padding de 5dp
            ubicacionTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Establecer layout weight de 1
            ubicacionTextView.setTextColor(Color.WHITE) // Establecer color de texto en blanco
            row.addView(ubicacionTextView)

            val existenciaTextView = TextView(this)
            existenciaTextView.text = existencia.exiExi
            existenciaTextView.setPadding(convertDpToPx(5), convertDpToPx(5), convertDpToPx(5), convertDpToPx(5)) // Añadir padding de 5dp
            existenciaTextView.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f) // Establecer layout weight de 1
            existenciaTextView.setTextColor(Color.WHITE) // Establecer color de texto en blanco
            row.addView(existenciaTextView)

            val clickedColor = Color.parseColor("#689F38") // Color de fondo al hacer clic
            val originalColor = Color.parseColor("#FF70A7BF") // Color de fondo original

            // Agregar OnClickListener a la fila
            row.setOnClickListener {

                val clickAnimation = ValueAnimator.ofArgb(originalColor, clickedColor)
                clickAnimation.duration = 300 // Duración de la animación en milisegundos

                clickAnimation.addUpdateListener { animator ->
                    val color = animator.animatedValue as Int
                    row.setBackgroundColor(color)
                }

                // Animación para restaurar el color original
                val restoreAnimation = ValueAnimator.ofArgb(clickedColor, originalColor)
                restoreAnimation.duration = 300 // Duración de la animación en milisegundos

                restoreAnimation.addUpdateListener { animator ->
                    val color = animator.animatedValue as Int
                    row.setBackgroundColor(color)
                }

                // Configurar la secuencia de animación
                clickAnimation.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        restoreAnimation.start() // Iniciar la animación de restauración después de la animación de clic
                    }
                })

                clickAnimation.start()

                binding.buttonReubicar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green1)

                binding.buttonReubicar.isEnabled = true

                // Establecer el valor de ubicacionOrigen con existencia.exiUbi
                binding.ubicacionOrigen.setText(existencia.exiUbi.trim())

                val unidades = existencia.exiExi.toFloat()

                binding.unidades.setText(unidades.toString())

                try {
                    unidadesOrigen = existencia.exiExi.toFloat()
                }catch (e : Exception){
                    println(e)
                }

            }
            binding.tabla.addView(row)
        }

    }//Update Tabla

    private fun limpiar(){

        binding.articulo.text.clear()
        binding.descripcion.text = ""

        // Remover todas las filas excepto la cabecera
        val rowCount = binding.tabla.childCount
        if (rowCount > 1) {
            binding.tabla.removeViews(1, rowCount - 1)
        }

        binding.ubicacionOrigen.text.clear()
        binding.ubicacionDestino.text.clear()
        binding.unidades.text.clear()

        binding.articulo.requestFocus()

        binding.buttonReubicar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green2)

        binding.buttonReubicar.isEnabled = false

    }

    private fun convertDpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    //Función que rellena el spinner con lista de opciones de almacenes
    private fun setupSpinner(){

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        val db = DbAlmacen()

        val listaOpciones = db.getAlmacenes(sp.getString(Constant.companyId, ""))

        //Declara un objeto de tipo ArrayAdapter, con un estilo definido en color_spinner_layout
        val arrayAdapter : ArrayAdapter<Almacen> = ArrayAdapter(this,R.layout.color_spinner_layout,listaOpciones)

        //Se declara un estilo de tipo DropDown
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Le pasa el objeto arrayAdapter al adaptador del spinner
        binding.spinnerAlmacen.adapter = arrayAdapter

        //Le pasa un item selected listener al spinner
        binding.spinnerAlmacen.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                almacen = parent?.getItemAtPosition(position) as Almacen
                binding.articulo.requestFocus()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("No hay nada seleccionado")
                almacen = null
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

        //Icono del menú
        menu[0].icon = ContextCompat.getDrawable(this, R.drawable.busqueda)

        //Titulo
        this.title = getString(R.string.reubicaciones)

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