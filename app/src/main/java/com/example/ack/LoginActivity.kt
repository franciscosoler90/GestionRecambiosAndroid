/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package com.example.ack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ack.databinding.ActivityLoginActivityBinding
import data.Constant
import data.Empresa
import db.DbEmpresas
import db.DbPassword
import db.DbValues
import utilidades.Conectividad

class LoginActivity : AppCompatActivity() {

    //Binding Late initialization
    private lateinit var binding: ActivityLoginActivityBinding

    private var empresa : Empresa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Llama a la función SharedPreferencesBD()
        sharedPreferencesBD()

        binding.buttonAcceder.setOnClickListener {

            if (!Conectividad().isConnected(this)) {
                Toast.makeText(this, "No tiene conexión a red wifi", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val selectedItem = binding.spinnerCompany.selectedItem
            val nombreEmpresa = selectedItem?.toString()?.trim() ?: ""

            if (nombreEmpresa.isEmpty()) {
                Toast.makeText(this,"No hay opciones disponibles",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val username = binding.username.text.toString().uppercase()
            val password = binding.password.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this,"Rellena el campo de Usuario",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (empresa == null) {
                Toast.makeText(this,"Empresa no válida",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dbPassword = DbPassword()

            dbPassword.getPassword(username, password, object : DbPassword.OnPasswordReceivedListener {
                override fun onPasswordReceived(passwordMatches: Boolean) {
                    if (passwordMatches) {
                        toMenu(username)
                        println("Contraseña correcta. Iniciar sesión exitoso.")
                    } else {
                        println("Contraseña incorrecta. Iniciar sesión fallido.")
                        mostrarToast("Contraseña incorrecta. Iniciar sesión fallido.")
                        return
                    }
                }
            })

        }

        //Establece un TextWatcher en el campo username, que detecta cambios cuando se introduce texto
        binding.username.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            //Llama a esta función cuando el texto ha cambiado
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                //Eliminar la advertencia de error
                if (binding.editTextUsername.error != null) {
                    binding.editTextUsername.isErrorEnabled = true
                    binding.editTextUsername.error = null
                    binding.editTextUsername.isErrorEnabled = false
                }

                //Llama a la función updateCompanies, para actualizar la lista de Empresas
                //updateCompanies()

                val username = binding.username.text.toString()

                val dbEmpresas = DbEmpresas()

                dbEmpresas.getCompanies(username, object : DbEmpresas.OnCompaniesDataReceivedListener {
                    override fun onCompaniesDataReceived(companies: List<Empresa>) {
                        setupSpinner(companies)
                    }

                    override fun onError(errorMessage: String) {
                        // Manejo de errores si es necesario
                        println("Error: $errorMessage")
                        mostrarToast("Error: $errorMessage")
                        setupSpinner(ArrayList())
                    }
                })

            }
        })

    } //Fin onCreate


    private fun toMenu(username: String){

        val sharedPref = applicationContext.getSharedPreferences(Constant.myUserPrefs, Context.MODE_PRIVATE)

        with (sharedPref.edit()) {
            putString(Constant.companyName, empresa.toString())
            putString(Constant.companyId, empresa!!.id)
            putString(Constant.username, username)
            apply()
        }

        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)


    }

    private fun mostrarToast(message : String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    //Función que rellena el spinner con lista de opciones de empresas
    private fun setupSpinner(listaOpciones : List<Empresa>){

        //Declara un objeto de tipo ArrayAdapter, con un estilo definido en color_spinner_layout
        val arrayAdapter : ArrayAdapter<Empresa> = ArrayAdapter(this,R.layout.color_spinner_layout,listaOpciones)

        //Se declara un estilo de tipo DropDown
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Le pasa el objeto arrayAdapter al adaptador del spinner
        binding.spinnerCompany.adapter = arrayAdapter

        //Le pasa un item selected listener al spinner
        binding.spinnerCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                empresa = parent?.getItemAtPosition(position) as Empresa
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("No hay nada seleccionado")
            }
        }
    }


    //Función de sharedPreferencesBD en relación a la configuración de bases de datos
    private fun sharedPreferencesBD(){

        //Se declara un objeto sharedPrefBD
        val sharedPrefBD = applicationContext.getSharedPreferences(Constant.BdPrefs, Context.MODE_PRIVATE)

        //Introduce los valores en el objeto DbValues
        DbValues.ip = sharedPrefBD.getString(Constant.DBip,"").toString()
        DbValues.port = sharedPrefBD.getString(Constant.DBport,"").toString()
        DbValues.database = sharedPrefBD.getString(Constant.DBdatabase,"").toString()
        DbValues.username = sharedPrefBD.getString(Constant.DBusername,"").toString()
        DbValues.password = sharedPrefBD.getString(Constant.DBpassword,"").toString()

        //Comprueba si la cadena ip esta vacia
        if(DbValues.ip.isEmpty()){
            Toast.makeText(this,"(BD) No se ha establecido una dirección ip válida", Toast.LENGTH_LONG).show()

            //Introduce valor vacio
            with (sharedPrefBD.edit()) {
                putString(Constant.DBip, "")
                apply()
            }

        }

        //Comprueba si la cadena port esta vacia
        if(DbValues.port.isEmpty()){
            Toast.makeText(this,"(BD) No se ha establecido un puerto válido", Toast.LENGTH_LONG).show()

            //Introduce valor vacio
            with (sharedPrefBD.edit()) {
                putString(Constant.DBport, "")
                apply()
            }
        }

        //Comprueba si la cadena database esta vacia
        if(DbValues.database.isEmpty()){
            Toast.makeText(this,"(BD) No se ha establecido un nombre de la base de datos", Toast.LENGTH_LONG).show()

            //Introduce valor vacio
            with (sharedPrefBD.edit()) {
                putString(Constant.DBdatabase, "")
                apply()
            }
        }

        //Comprueba si la cadena username esta vacia
        if(DbValues.username.isEmpty()){
            Toast.makeText(this,"(BD) No se ha establecido un nombre de usuario válido", Toast.LENGTH_LONG).show()

            //Introduce valor vacio
            with (sharedPrefBD.edit()) {
                putString(Constant.DBusername, "")
                apply()
            }
        }

        //Comprueba si la cadena password esta vacia
        if(DbValues.password.isEmpty()){
            Toast.makeText(this,"(BD) No se ha establecido una contraseña", Toast.LENGTH_LONG).show()

            //Introduce valor vacio
            with (sharedPrefBD.edit()) {
                putString(Constant.DBpassword, "")
                apply()
            }
        }

    }

}