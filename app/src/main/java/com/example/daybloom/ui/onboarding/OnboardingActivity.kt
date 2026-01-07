package com.example.daybloom.ui.onboarding

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.daybloom.AppDatabase
import com.example.daybloom.MainActivity
import com.example.daybloom.R
import com.example.daybloom.data.entity.UserEntity
import kotlinx.coroutines.launch
import android.widget.EditText
import android.widget.Spinner
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import java.util.Calendar

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val nameEt = findViewById<EditText>(R.id.nameEt)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val birthdayEt = findViewById<TextView>(R.id.birthdayEt)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val calendar = Calendar.getInstance()
        val genders = listOf("Select Gender", "Female", "Male", "Other")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            genders
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        genderSpinner.adapter = adapter



        val userDao = AppDatabase.getDatabase(this).userDao()

        saveBtn.setOnClickListener {

            val name = nameEt.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()
            val birthday = birthdayEt.text.toString().trim()

            if (name.isEmpty() || gender == "Select Gender" || birthday.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill all details",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    AppDatabase.getDatabase(this@OnboardingActivity)
                        .userDao()
                        .insertUser(
                            UserEntity(
                                name = name,
                                gender = gender,
                                birthday = birthday
                            )
                        )

                    startActivity(
                        Intent(
                            this@OnboardingActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@OnboardingActivity,
                        "Something went wrong. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        birthdayEt.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    birthdayEt.text = "%02d/%02d/%04d"
                        .format(day, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    }

