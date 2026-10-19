package com.example.parcialkotlinapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.MenuItem


class ParImparActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_par_impar)

        // Conectamos Kotlin con los componentes del XML
        val etNumero = findViewById<EditText>(R.id.etNumero)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        // Configurar la Toolbar superior con la flecha de navegación
        val toolbar = findViewById<Toolbar>(R.id.toolbarParImpar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Ejercicio Par o Impar"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Activa la flecha hacia atrás
        supportActionBar?.setDisplayShowHomeEnabled(true)



        btnValidar.setOnClickListener {

            val texto = etNumero.text.toString()

            // Validar que el campo no esté vacío
            if (texto.isEmpty()) {
                etNumero.error = "Ingrese un número"
                return@setOnClickListener
            }

            // Conversión segura
            val numero = texto.toIntOrNull()

            if (numero == null) {
                etNumero.error = "Ingrese un número entero válido"
                return@setOnClickListener
            }

            // Validación par o impar
            if (numero % 2 == 0) {
                tvResultado.text = "$numero es PAR"
            } else {
                tvResultado.text = "$numero es IMPAR"
            }
        }
    }
    // VOLVER
    // =====================================
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                // El ID estándar de Android para la flecha de la Toolbar es android.R.id.home
                android.R.id.home -> {
                    finish() // Cierra esta actividad y regresa a la anterior (MainActivity)
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }





        }}
