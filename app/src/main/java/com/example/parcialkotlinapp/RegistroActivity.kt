package com.example.parcialkotlinapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialkotlinapp.api.ApiClient
import com.example.parcialkotlinapp.models.CrearUsuarioRequest
import com.example.parcialkotlinapp.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
class RegistroActivity : AppCompatActivity() {

    private var calculatedAge: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Habilitar la flecha de navegación superior (Atrás)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarRegistro)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)}

        // COMPONENTES
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etPasswordRegistro = findViewById<EditText>(R.id.etPasswordRegistro)

        // Nuevos componentes requeridos
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val etUniversidad = findViewById<EditText>(R.id.etUniversidad)
        val spinnerSemestre = findViewById<Spinner>(R.id.spinnerSemestre)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val tvMensaje = findViewById<TextView>(R.id.tvMensajeRegistro)

        // CONFIGURAR SPINNER DE SEMESTRES (1 a 10)
        val semestres = (1..10).toList()
        val adapterSemestre = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            semestres
        )
        adapterSemestre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSemestre.adapter = adapterSemestre

        // CONFIGURAR DATEPICKER PARA FECHA DE NACIMIENTO
        etFechaNacimiento.setOnClickListener {
            showDatePickerDialog(etFechaNacimiento)
        }
        // Evitar que escriban directamente con el teclado físico/virtual
        etFechaNacimiento.isFocusable = false
        etFechaNacimiento.isClickable = true

        // BOTÓN REGISTRAR
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPasswordRegistro.text.toString().trim()
            val fechaNacimiento = etFechaNacimiento.text.toString().trim()
            val universidad = etUniversidad.text.toString().trim()
            val semestreSeleccionado = spinnerSemestre.selectedItem.toString().toInt()

            // VALIDACIONES
            if (nombre.isBlank()) {
                etNombre.error = "Ingrese el nombre"
                return@setOnClickListener
            }

            if (apellido.isBlank()) {
                etApellido.error = "Ingrese el apellido"
                return@setOnClickListener
            }

            if (correo.isBlank()) {
                etCorreo.error = "Ingrese el correo"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Correo no válido"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                etPasswordRegistro.error = "Ingrese la contraseña"
                return@setOnClickListener
            }

            if (fechaNacimiento.isBlank()) {
                etFechaNacimiento.error = "Seleccione la fecha de nacimiento"
                return@setOnClickListener
            }

            if (calculatedAge < 0 || calculatedAge > 120) {
                Toast.makeText(this, "Fecha de nacimiento no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (universidad.isBlank()) {
                etUniversidad.error = "Ingrese la universidad"
                return@setOnClickListener
            }

            // OBJETO QUE SE ENVÍA AL BACKEND (.NET)
            val nuevoUsuario = CrearUsuarioRequest(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                password = password,

                universidad = universidad,  // Nuevo campo
                semestre = semestreSeleccionado, // Semestre del Spinner

            )

            tvMensaje.text = "Registrando usuario..."
            btnRegistrar.isEnabled = false

            // LLAMADA AL BACKEND MEDIANTE RETROFIT
            ApiClient.usuarioApi
                .crearUsuario(nuevoUsuario)
                .enqueue(object : Callback<Usuario> {

                    override fun onResponse(
                        call: Call<Usuario>,
                        response: Response<Usuario>
                    ) {
                        btnRegistrar.isEnabled = true

                        if (response.isSuccessful) {
                            val usuarioCreado = response.body()
                            tvMensaje.text = ""

                            // MOSTRAR ALERTDIALOG DE CONFIRMACIÓN Y RETORNAR AL LOGIN
                            androidx.appcompat.app.AlertDialog.Builder(this@RegistroActivity)
                                .setTitle("Registro exitoso")
                                .setMessage("El usuario fue creado exitosamente.")
                                .setPositiveButton("ACEPTAR") { dialog, _ ->
                                    dialog.dismiss()
                                    finish() // Cierra la pantalla de registro y regresa al Login
                                }
                                .setCancelable(false)
                                .show()

                        } else {
                            // Manejo de errores del backend
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = if (!errorBody.isNullOrEmpty()) {
                                try {
                                    //
                                    val jsonObject = org.json.JSONObject(errorBody)
                                    jsonObject.optString("mensaje", "Error del servidor: ${response.code()}")
                                } catch (e: Exception) {
                                    "Error del servidor: ${response.code()}"
                                }
                            } else {
                                "Error del servidor: ${response.code()}"
                            }

                            tvMensaje.text = errorMessage
                        }  }

                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                        btnRegistrar.isEnabled = true
                        tvMensaje.text = "Error de conexión: ${t.message}"
                    }
                })
        }
    }

    // MÉTODO PARA MOSTRAR EL DATEPICKER Y CALCULAR LA EDAD EXACTA
    private fun showDatePickerDialog(etDestino: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Formato de visualización
            val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            etDestino.setText(formattedDate)

            // Cálculo exacto considerando día, mes y año actual
            calculatedAge = calculateExactAge(selectedYear, selectedMonth, selectedDay)

        }, year, month, day)

        // Limitar la fecha máxima al día actual
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun calculateExactAge(birthYear: Int, birthMonth: Int, birthDay: Int): Int {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH)
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear

        // Si aún no ha cumplido años este año, restamos 1
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--
        }
        return age
    }

    // MANEJAR EL CLIC DE LA FLECHA SUPERIOR (ATRÁS)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}