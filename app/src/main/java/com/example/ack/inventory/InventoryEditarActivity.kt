/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.inventory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.ack.R
import com.example.ack.databinding.ActivityInventoryEditarBinding
import data.Constant
import db.DbInventory

class InventoryEditarActivity : AppCompatActivity() {

    //Variables globales
    private lateinit var binding: ActivityInventoryEditarBinding

    companion object {
        private var unidadesContadas = 0F
        private var linea = 0
        private var tmiNro = ""
        private var tmiSer = ""
        private var almCod = ""
        private var empCod = ""
    }

    //Al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityInventoryEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.Inventario)

        //Obtiene la información previa del anterior Activity
        tmiNro = intent.getStringExtra(Constant.id).toString()
        tmiSer = intent.getStringExtra(Constant.tmiSer).toString()
        almCod = intent.getStringExtra(Constant.almCod).toString()
        empCod = intent.getStringExtra(Constant.companyId).toString()

        linea = intent.getIntExtra(Constant.lineaId,1)

        //Previene que se abra el teclado al iniciar la actividad
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)

        val db = DbInventory()

        val inventarioLineas = db.getLinea(tmiNro, linea)

        //Rellena los campos con la información del Repuesto
        if (inventarioLineas != null) {

            //Obtiene el número de linea
            linea = inventarioLineas.linea

            //Mostrar en pantalla
            binding.txtAlmacen.text = inventarioLineas.AlmCod?.trim()
            binding.txtCodigo.text = inventarioLineas.TMIArtCod?.trim()
            binding.txtDescripcion.text = inventarioLineas.TMIArtDes?.trim()
            binding.txtUbicacion.text = inventarioLineas.TMIArtUbi?.trim()

            try{

                //Obtiene una cadena de texto, y reemplaza las caracterees "," por "."
                val num = inventarioLineas.TMIUniCon?.replace(",",".")?.toFloat()

                //Si el valor de num no es nulo
                if (num != null) {

                    unidadesContadas = num

                    if (num.rem(1) == 0F) {
                        val entero = num.toInt()
                        val editableCantidad : Editable = SpannableStringBuilder(entero.toString())
                        binding.txtUnidadesRecogidas.text = editableCantidad
                    } else {
                        val formattedNum2 = "%.2f".format(num)
                        val editableCantidad : Editable = SpannableStringBuilder(formattedNum2)
                        binding.txtUnidadesRecogidas.text = editableCantidad
                    }

                }

            }catch (e : Exception){
                    println(e)
            }

        }

        //Configura el menú
        setSupportActionBar(binding.toolbarEditar)

        //Habilita el botón de volver atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Establece el icono de volver atrás
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)

        //Subtitulo
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        //Foco
        binding.txtUnidadesRecogidas.requestFocus()

        //Listener del botón Contar al hacer clic
        binding.buttonContar.setOnClickListener {

            //Obtiene el valor booleano si esta focalizable en ese momento
            val activado = binding.txtUnidadesRecogidas.isFocusable

            //Desahibilita el campo de texto de Unidades Recogidas
            binding.txtUnidadesRecogidas.isFocusable = !activado
            binding.txtUnidadesRecogidas.isFocusableInTouchMode = !activado
            binding.txtUnidadesRecogidas.isEnabled = !activado
            binding.txtUnidadesRecogidas.isActivated = !activado
            binding.txtUnidadesRecogidas.isCursorVisible = !activado

            //Habilita el campo de texto de Código
            binding.editTextCodigo.isFocusable = activado
            binding.editTextCodigo.isFocusableInTouchMode = activado
            binding.editTextCodigo.isEnabled = activado
            binding.editTextCodigo.isActivated = activado
            binding.editTextCodigo.isCursorVisible = activado

            //Si está activado
            if(activado){

                //Focaliza en el campo de texto
                binding.editTextCodigo.requestFocus()
                Toast.makeText(this,"Modo escáner activado", Toast.LENGTH_SHORT).show()
            }else{

                //Focaliza en el campo de texto
                binding.txtUnidadesRecogidas.requestFocus()
                Toast.makeText(this,"Modo escáner desactivado", Toast.LENGTH_SHORT).show()
            }
        }

        //Listener del campo de texto Código que se activa cuando el texto cambia
        binding.editTextCodigo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                //Declara dos cadenas de texto
                val text1 = s.toString()
                val text2 = binding.txtCodigo.text.toString()

                //Si esta vacio
                if (text1.isEmpty()) {
                    //Finaliza
                    return
                }

                //Si las dos cadenas de texto son iguales
                if( text1 == text2 ){

                    try{

                        //Declara un número flotante con el valor de cantidad
                        val cantidad = binding.txtUnidadesRecogidas.text.toString().toFloat() + 1

                        //Declara un objeto editable con el valor de cantidad
                        val editableCantidad : Editable = SpannableStringBuilder(cantidad.toString())

                        //Escribe en el campo de unidades recogidas
                        binding.txtUnidadesRecogidas.text = editableCantidad

                    }catch(e : Exception){

                        println(e)

                    }

                }else{

                    Toast.makeText(this@InventoryEditarActivity,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()

                }

                //Declara un editable con una cadena de texto vacia
                val editable : Editable = SpannableStringBuilder("")

                //Escribe en el campo de código
                binding.editTextCodigo.text = editable

            }
        })

        //Listener del botón Confirmar al hacer clic
        binding.buttonConfirmar.setOnClickListener {

            try{

                //Declara una cadena de texto
                val unidades = binding.txtUnidadesRecogidas.text.toString()

                //Si la cadena de texto esta vacia
                if(unidades.isEmpty()){

                    //finaliza
                    return@setOnClickListener
                }

                //Si el inventarioLineas es nulo
                if(inventarioLineas == null){
                    //finaliza
                    return@setOnClickListener
                }

                //SHARED PREFERENCES
                val username = sp.getString(Constant.username, "")

                //LLama a la función inventarioLineas con los valores correspondientes
                db.updateLinea(tmiNro,inventarioLineas.linea, unidades)

                db.updateInventario(tmiNro, username,1)

                //Vuelve atrás
                back()

            }catch(e : Exception){

                Toast.makeText(this, "Ha ocurrido un error: $e", Toast.LENGTH_LONG).show()

            }
        }

    }

    //Vuelve atrás
    private fun back(){

        //Declara un intent
        val intent = Intent(this, Inventory2Activity::class.java)

        //Le pasa la información al intent
        intent.putExtra(Constant.id, tmiNro)
        intent.putExtra(Constant.tmiSer, tmiSer)
        intent.putExtra(Constant.companyId, empCod)
        intent.putExtra(Constant.almCod, almCod)

        //Inicia la actividad
        startActivity(intent)
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

            //Llama a la función back para volver atrás
            back()
            true
        }

        else -> {
            // Si llegamos aquí, la acción del usuario no fue reconocida.
            // Invoca a la superclase para manejarlo.
            super.onOptionsItemSelected(item)
        }
    }
}