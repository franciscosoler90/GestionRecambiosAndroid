/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.mercancia

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.ack.R
import com.example.ack.databinding.ActivityMercanciaLineasBinding
import data.Constant
import db.DbMercancia

class MercanciaLineasActivity : AppCompatActivity() {

    //Variables globales
    private lateinit var binding: ActivityMercanciaLineasBinding
    private var isIVECO : Boolean = false
    private var documento : String = ""
    private var linea : String = ""

    //Al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMercanciaLineasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtiene los valores desde el intent
        isIVECO = intent.getBooleanExtra(Constant.isIVECO,false)
        documento = intent.getStringExtra(Constant.document).toString()
        linea = intent.getStringExtra(Constant.lineaId).toString()

        val db = DbMercancia()

        val mercanciaLinea = db.getLinea(documento,linea)

        //Si la linea no es nula
        if(mercanciaLinea != null){

            binding.txtDescripcion.text = mercanciaLinea.TMCAriArtD!!.trim()

            binding.txtCodigo.text = mercanciaLinea.TMCAriArtC

            binding.txtAlmacen.text = mercanciaLinea.TMCAriAlmC

            binding.txtUbicacion.text = mercanciaLinea.TMCAriUbi

            val num1: Float? = mercanciaLinea.TMCAriUni?.toFloat()

            if (num1 != null) {
                if (num1.rem(1) == 0F) {
                    val entero = num1.toInt()
                    binding.txtUnidades.text = entero.toString()
                } else {
                    val formattedNum1 = "%.2f".format(num1)
                    binding.txtUnidades.text = formattedNum1
                }
            }

            val num2: Float? = mercanciaLinea.TMCUniCont?.toFloat()

            if (num2 != null) {
                if (num2.rem(1) == 0F) {
                    val entero = num2.toInt()
                    val editableCantidad : Editable = SpannableStringBuilder(entero.toString())
                    binding.txtUnidadesRecogidas.text = editableCantidad
                } else {
                    val formattedNum2 = "%.2f".format(num2)
                    val editableCantidad : Editable = SpannableStringBuilder(formattedNum2)
                    binding.txtUnidadesRecogidas.text = editableCantidad
                }
            }
        }

        binding.txtUnidadesRecogidas.requestFocus()

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        //Obtiene las cadenas de texto desde Shared Preferences
        val username = sp.getString(Constant.username, "")

        //Configura la barra de menú
        setSupportActionBar(binding.toolbar)

        //Habilita el botón para volver atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Establece el icono para volver atrás
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)

        //Subtitulo
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        //Listener del botón Contar al hacer clic
        binding.buttonContar.setOnClickListener {

            //Obtiene si esta foculizable en ese momento
            val activado = binding.txtUnidadesRecogidas.isFocusable

            //Deshabilita el actual
            binding.txtUnidadesRecogidas.isFocusable = !activado
            binding.txtUnidadesRecogidas.isFocusableInTouchMode = !activado
            binding.txtUnidadesRecogidas.isEnabled = !activado
            binding.txtUnidadesRecogidas.isActivated = !activado
            binding.txtUnidadesRecogidas.isCursorVisible = !activado

            //Habilita el siguiente
            binding.editTextCodigo.isFocusable = activado
            binding.editTextCodigo.isFocusableInTouchMode = activado
            binding.editTextCodigo.isEnabled = activado
            binding.editTextCodigo.isActivated = activado
            binding.editTextCodigo.isCursorVisible = activado

            //Si esta activado
            if(activado){

                //Mantiene el foco
                binding.editTextCodigo.requestFocus()

                //Mantiene el foco
                Toast.makeText(this,"Modo escáner activado", Toast.LENGTH_SHORT).show()
            }else{
                binding.txtUnidadesRecogidas.requestFocus()

                //Mantiene el foco
                Toast.makeText(this,"Modo escáner desactivado", Toast.LENGTH_SHORT).show()
            }

        }

        //Listener que se activa cuando el campo de texto cambia
        binding.editTextCodigo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                //Si esta vacio
                if (s.isEmpty()) {
                    return
                }

                //Si las dos cadenas de texto son iguales
                if(s.toString().trim() == binding.txtCodigo.text.toString().trim()){

                    try{

                        //Incrementa en 1 la cantidad
                        val cantidad = binding.txtUnidadesRecogidas.text.toString().replace(",",".").toFloat() + 1

                        //Declara un texto editable con la cantidad
                        val editableCantidad : Editable = SpannableStringBuilder(cantidad.toString())

                        //Le pasa el editable al campo de texto
                        binding.txtUnidadesRecogidas.text = editableCantidad

                    }catch(e : Exception){

                        println(e)

                    }

                }

                //Declara un texto editable con el texto vacio
                val editable : Editable = SpannableStringBuilder("")

                //Le pasa el editable al campo de texto
                binding.editTextCodigo.text = editable

            }
        })

        //Listener que se activa cuando le da clic al botón de Confirmar
        binding.buttonConfirmar.setOnClickListener{

            try {

                val codigoString = binding.txtCodigo.text.toString()

                if (codigoString.isEmpty()) {
                    binding.txtCodigo.error = "Rellena el campo de Código"
                    return@setOnClickListener
                }

                val almacenString = binding.txtAlmacen.text.toString()

                if (almacenString.isEmpty()) {
                    binding.txtAlmacen.error = "Rellena el campo de Almacén"
                    return@setOnClickListener
                }

                val ubicacionString = binding.txtUbicacion.text.toString()

                if (ubicacionString.isEmpty()) {
                    binding.txtUbicacion.error = "Rellena el campo de Ubicación"
                    return@setOnClickListener
                }

                val unidadesString = binding.txtUnidadesRecogidas.text.toString()

                if (unidadesString.isEmpty()) {
                    binding.txtUnidadesRecogidas.error = "Rellena el campo de Unidades contadas"
                    return@setOnClickListener
                }

                //Declara una número con decimales, cambiando "," por "."
                val numeroUnidades = unidadesString.replace(",",".").toFloat()

                //Declara una cadena de texto
                val descripcionString = binding.txtDescripcion.text.toString().trim()

                //Si la cadena de texto está vacia
                if (descripcionString.isEmpty()) {

                    //Abre una barra en la parte inferior de la pantalla
                    Toast.makeText(this, "No existe una descripción del articulo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //Llama a la función
                db.updateEstadoLinea(documento, linea, numeroUnidades,1)
                db.updateEstadoCaja(documento, username,1)

                //Declara un intent
                val intent = Intent(this, MercanciaCajas2Activity::class.java)

                //Obtiene cadenas de texto desde el intent
                intent.putExtra(Constant.isIVECO, isIVECO)
                intent.putExtra(Constant.document, documento)

                //Limpiar pantalla
                //finish()

                //Desactivar animaciones
                //overridePendingTransition(0, 0)

                //Inicia la actividad
                startActivity(intent)

                //Activar animaciones
                //overridePendingTransition(0, 0)

            }catch(e1 : java.lang.NumberFormatException){

                Toast.makeText(this,"Valor de unidades no válido", Toast.LENGTH_LONG).show()

            }catch(e : Exception){

                Toast.makeText(this, "Ha ocurrido un error: $e", Toast.LENGTH_LONG).show()

            }

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
            this.title = "Recepción Cajas IVECO"
        }else{

            //Icono del menú
            menu[0].icon = ContextCompat.getDrawable(this, R.drawable.proceso)

            //Titulo
            this.title = "Recepción Mercancía No IVECO"
        }

        return super.onCreateOptionsMenu(menu)
    }

    //Método que llama cuando se clica en un elemento del menú
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home ->{
            //Volver atrás
            val intent = Intent(this, MercanciaCajas2Activity::class.java)

            //Le pasa la información al intent
            intent.putExtra(Constant.isIVECO, isIVECO)
            intent.putExtra(Constant.document,documento)

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

}