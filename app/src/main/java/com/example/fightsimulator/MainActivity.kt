package com.example.fightsimulator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var fighterNameEditText: TextInputEditText
    private lateinit var healthEditText: TextInputEditText
    private lateinit var armorBonusEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fighterNameEditText = findViewById(R.id.figtherName)
        healthEditText = findViewById(R.id.Health)
        armorBonusEditText = findViewById(R.id.ArmorBonus)

        findViewById<Button>(R.id.btnAddChar).setOnClickListener {
            startFight()
        }
    }

    private fun startFight() {
        val fighterName = fighterNameEditText.text.toString().trim()
        var health = healthEditText.text.toString().toIntOrNull()
        var armor = armorBonusEditText.text.toString().toIntOrNull()


        if (fighterName.isEmpty()) {
            Toast.makeText(this, "Please enter a fighter name", Toast.LENGTH_SHORT).show()
            return
        }

        if (armor == null || armor !in 0..15) {
            Toast.makeText(this, "Armor bonus must be a number between 0 and 15", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the selected radio button
        val selectedRadioButtonId = RadioGroup.FOCUSABLES_TOUCH_MODE
        var buffText = ""
        var healthBuff = 0 // Additional health from buff
        var armorBuff = 0 // Additional armor from buff

        // Check which radio button is selected and apply the corresponding buff
        when (selectedRadioButtonId) {
            R.id.radioButton2 -> {
                // Selected "Touch grass (health +10)" buff
                // Apply the buff logic here
                buffText = "Touch grass (health +10)"
                healthBuff = 10
            }
            R.id.radioButton3 -> {
                // Selected "Iron Scales (Armor+20)" buff
                // Apply the buff logic here
                buffText = "Iron Scales (Armor+20)"
                armorBuff = 20
            }
        }

        // Apply the health buff (if any)
        health = (health ?: 0) + healthBuff

        // Apply the armor buff (if any)
        armor = (armor ?: 0) + armorBuff

        // Check if health exceeds the cap of 150
        if (health != null && health > 150) {
            health = 150 // Cap health to 150
        }

        // Update the TextInputEditText fields with the modified values
        healthEditText.setText(health.toString())
        armorBonusEditText.setText(armor.toString())

        // Create an intent to start the fight activity
        val intent = Intent(this, FightStart::class.java).apply {
            putExtra("fighterNames", arrayOf(fighterName))
            putExtra("health", health.toString())
            putExtra("armorBonus", armor.toString())
            putExtra("buff", buffText) // Pass the selected buff to the next activity
        }
        startActivity(intent)
    }


}
