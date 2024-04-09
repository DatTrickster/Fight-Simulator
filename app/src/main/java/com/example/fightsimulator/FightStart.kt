package com.example.fightsimulator

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FightStart : AppCompatActivity() {
    private lateinit var fightTextView: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight_start)

        fightTextView = findViewById(R.id.fightDescriptionTextView)

        val fighterNames = intent.getStringArrayExtra("fighterNames") ?: arrayOf("User", "Fighter 2")
        val userHealth = intent.getStringExtra("health")?.toIntOrNull() ?: 100
        val userArmorBonus = intent.getStringExtra("armorBonus")?.toIntOrNull() ?: 0

        val userName = fighterNames.getOrNull(0) ?: "User"
        val fighter2Name = "Fighter 2"
        val fighter2Health = (50..150).random()
        val fighter2ArmorBonus = (5..15).random()

        val userDetails = buildFighterDetails(userName, userHealth, userArmorBonus)
        val fighter2Details = buildFighterDetails(fighter2Name, fighter2Health, fighter2ArmorBonus)

        fightTextView.text = buildFightDescription(userName, fighter2Name, userDetails, fighter2Details)

        fight(userName, userHealth, userArmorBonus, fighter2Name, fighter2Health, fighter2ArmorBonus)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun buildFighterDetails(name: String, health: Int, armor: Int): String {
        return "Name: $name\nHealth: $health\nArmor: $armor"
    }

    private fun buildFightDescription(
        userName: String,
        fighter2Name: String,
        userDetails: String,
        fighter2Details: String
    ): String {
        return "$userName vs $fighter2Name\n\n$userDetails\n\n$fighter2Details\n\n"
    }

    private fun fight(
        user: String,
        userHealth: Int,
        userArmor: Int,
        opponent: String,
        opponentHealth: Int,
        opponentArmor: Int
    ) {
        var currentUserHealth = userHealth
        var currentOpponentHealth = opponentHealth

        // Continuously fight until one of the fighters' health reaches zero
        while (currentUserHealth > 0 && currentOpponentHealth > 0) {
            val userDamage = (1..20).random()
            val opponentDamage = (1..20).random()

            val actualUserDamage = userDamage - opponentArmor
            val actualOpponentDamage = opponentDamage - userArmor

            currentOpponentHealth -= actualUserDamage.coerceAtLeast(0)
            currentUserHealth -= actualOpponentDamage.coerceAtLeast(0)

            val userAction = if (actualUserDamage > 0) {
                "$user deals ${actualUserDamage.coerceAtLeast(0)} damage to $opponent\n"
            } else {
                "The attack of $user is blocked by $opponent's armor!\n"
            }

            val opponentAction = if (actualOpponentDamage > 0) {
                "$opponent deals ${actualOpponentDamage.coerceAtLeast(0)} damage to $user\n"
            } else {
                "The attack of $opponent is blocked by $user's armor!\n"
            }

            if (currentOpponentHealth < 0) currentOpponentHealth = 0
            if (currentUserHealth < 0) currentUserHealth = 0

            val roundText = buildRoundText(userAction, opponentAction, currentUserHealth, currentOpponentHealth, user)

            // Append the fight progress without delay
            fightTextView.append(roundText)
            fightTextView.post { fightTextView.scrollTo(0, fightTextView.height) }
        }

        // Display the winner once the fight is over
        val winner = if (currentUserHealth > 0) user else opponent
        val winText = SpannableString("$winner wins!\n")
        winText.setSpan(ForegroundColorSpan(Color.RED), 0, winText.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        fightTextView.append(winText)
    }


    private fun buildRoundText(
        userAction: String,
        opponentAction: String,
        userHealth: Int,
        opponentHealth: Int,
        user: String
    ): SpannableStringBuilder {
        val roundText = SpannableStringBuilder("$userAction$opponentAction$user Health: $userHealth\nFighter 2 Health: $opponentHealth\n\n")

        // Apply green color to user action if found
        val userActionIndex = roundText.indexOf(userAction)
        if (userActionIndex != -1) {
            roundText.setSpan(
                ForegroundColorSpan(Color.GREEN),
                userActionIndex,
                userActionIndex + userAction.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Apply green color to opponent action if found
        val opponentActionIndex = roundText.indexOf(opponentAction)
        if (opponentActionIndex != -1) {
            roundText.setSpan(
                ForegroundColorSpan(Color.GREEN),
                opponentActionIndex,
                opponentActionIndex + opponentAction.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Apply red color to health indicators
        roundText.setSpan(
            ForegroundColorSpan(Color.RED),
            roundText.indexOf("$user Health:"),
            roundText.indexOf("$user Health:") + "$user Health: $userHealth".length,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        roundText.setSpan(
            ForegroundColorSpan(Color.RED),
            roundText.indexOf("Fighter 2 Health:"),
            roundText.indexOf("Fighter 2 Health:") + "Fighter 2 Health: $opponentHealth".length,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return roundText
    }

}