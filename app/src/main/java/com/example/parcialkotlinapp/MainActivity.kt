package com.example.parcialkotlinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        val tvSaludo =findViewById<TextView>(R.id.tvSaludo)

        // Nombre recibido desde LoginActivity
        val  nombreUsuario = intent.getStringExtra(
            "nombreUsuario"
        ) ?: "Usuario"

        //Hora Actual del dispositivo
        val horaActual =
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        //Determinar saludo
        var saludo = when(horaActual){

            in 5..11 ->
                "Buenos días"

            in 12..17 ->
                "Buenas tardes"

            else ->
                "Buenas noches"
        }

        //Mostrar saludo personalizado

        tvSaludo.text = "$saludo, $nombreUsuario"

        // Manejo de los bordes de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // =====================================
        // BOTÓN EJERCICIO PAR O IMPAR
        // =====================================

        val btnParImpar =
            findViewById<Button>(R.id.btnParImpar)

        btnParImpar.setOnClickListener {

            val intent = Intent(
                this,
                ParImparActivity::class.java
            )

            startActivity(intent)
        }

        // =====================================
        // CERRAR SESION
        // =====================================

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarMain)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.title = "Inicio"
        }




        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mostrarDialogoCerrarSesion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Está seguro de que desea cerrar sesión?")
            .setNegativeButton("CANCELAR") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("SALIR") { dialog, _ ->
                dialog.dismiss()
                cerrarSesionYRegresarLogin()
            }
            .setCancelable(false)
            .show()
    }

    private fun cerrarSesionYRegresarLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    }

