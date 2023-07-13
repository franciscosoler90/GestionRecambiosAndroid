/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack.picking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.ack.R
import com.example.ack.databinding.ActivityPickingEditarBinding
import data.Constant
import db.DbPicking

class PickingEditarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickingEditarBinding

    companion object {
        private var unidadesContadas = 0F
        private var lineaId = 0
        private var id = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPickingEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TITULO
        this.title = getString(R.string.picking)

        id = intent.getStringExtra(Constant.id).toString()
        lineaId = intent.getIntExtra(Constant.lineaId,1)

        val db = DbPicking()
        val pickingLineas = db.getLinea(id,lineaId)

        //Previene que se abra el teclado al iniciar la actividad
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        //SHARED PREFERENCES
        val sp = applicationContext.getSharedPreferences(Constant.myUserPrefs, MODE_PRIVATE)
        val username = sp.getString(Constant.username, "")

        //Si pickingLineas no es nulo
        if(pickingLineas!=null){

            lineaId = pickingLineas.TMPArtLin

            //Mostrar en pantalla
            binding.txtAlmacen.text = pickingLineas.TMPAlmCod?.trim()
            binding.txtCodigo.text = pickingLineas.TMPArtCod?.trim()
            binding.txtDescripcion.text = pickingLineas.TMPArtDes?.trim()
            binding.txtUbicacion.text = pickingLineas.TMPArtUbi?.trim()

            try{

                //Obtiene una cadena de texto, y reemplaza las caracterees "," por "."
                val num = pickingLineas.TMPArtUnRe?.replace(",",".")?.toFloat()

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.round_arrow_back_24)
        supportActionBar?.subtitle = sp.getString(Constant.companyName, "")

        //Listener al hacer clic en el botón
        binding.buttonContar.setOnClickListener {

            val activado = binding.txtUnidadesRecogidas.isFocusable

            binding.txtUnidadesRecogidas.isFocusable = !activado
            binding.txtUnidadesRecogidas.isFocusableInTouchMode = !activado
            binding.txtUnidadesRecogidas.isEnabled = !activado
            binding.txtUnidadesRecogidas.isActivated = !activado
            binding.txtUnidadesRecogidas.isCursorVisible = !activado

            binding.editTextCodigo.isFocusable = activado
            binding.editTextCodigo.isFocusableInTouchMode = activado
            binding.editTextCodigo.isEnabled = activado
            binding.editTextCodigo.isActivated = activado
            binding.editTextCodigo.isCursorVisible = activado

            //Si está activado
            if(activado){
                binding.editTextCodigo.requestFocus()
                Toast.makeText(this,"Modo escáner activado", Toast.LENGTH_SHORT).show()
            }else{
                binding.txtUnidadesRecogidas.requestFocus()
                Toast.makeText(this,"Modo escáner desactivado", Toast.LENGTH_SHORT).show()
            }

        }

        binding.editTextCodigo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val text1 = s.toString()
                val text2 = binding.txtCodigo.text.toString()

                //Si esta vacio
                if (s.isEmpty()) {
                    return
                }

                if(text1 == text2){

                    try{

                        val cantidad = binding.txtUnidadesRecogidas.text.toString().toFloat() - 1

                        val unidadesRecogidas = if(cantidad < 0){
                            0.0
                        }else{
                            cantidad
                        }

                        val editableCantidad : Editable = SpannableStringBuilder(unidadesRecogidas.toString())

                        binding.txtUnidadesRecogidas.text = editableCantidad

                    }catch(e : Exception){

                        println(e)

                    }
                }else{

                    Toast.makeText(this@PickingEditarActivity, "Código incorrecto", Toast.LENGTH_LONG).show()

                }

                //Limpia el campo editable
                val editable : Editable = SpannableStringBuilder("")
                binding.editTextCodigo.text = editable

            }
        })

        //Botón Confirmar
        binding.buttonConfirmar.setOnClickListener{
            if(pickingLineas!=null) {

                try {

                    val num = binding.txtUnidadesRecogidas.text.toString()

                    val num2 = num.toFloat()
                    val num3 = pickingLineas.TMPArtUni?.toFloat()

                    //PROVISIONAL
                    db.editPickingCabeceraEstado(id, 1)

                    if (num3 != null) {

                        val num4 = num3 - num2

                        db.editPickingUnidades(id, pickingLineas.TMPArtLin, num4)

                    }

                    if(num2 == 0F){
                        //ESTADO
                        db.editPickingEstado(id, pickingLineas.TMPArtLin, 1)
                    }

                    back()

                }catch(e : Exception){

                    Toast.makeText(this, "Ha ocurrido un error: $e", Toast.LENGTH_LONG).show()

                }

            }
        }
    }

    private fun back(){
        val intent = Intent(this, Picking2Activity::class.java)
        intent.putExtra(Constant.id, id)
        startActivity(intent)
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
            back()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}