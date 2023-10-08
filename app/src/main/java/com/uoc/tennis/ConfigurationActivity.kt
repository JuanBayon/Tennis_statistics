package com.uoc.tennis

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.uoc.tennis.databinding.ActivityConfigurationBinding

class ConfigurationActivity : Activity() {
    private var playerTypeConfiguration: String = MainActivity.NONE
    private var hitTypeConfiguration: String = MainActivity.NONE
    private var genderConfiguration: String = MainActivity.NONE
    private var playerText: Button? = null
    private var hitText: Button? = null
    private var genderText: Button? = null
    private var exitButton: Button? = null
    private var _binding: ActivityConfigurationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        playerText = findViewById(R.id.playerType)
        hitText = findViewById(R.id.hitType)
        genderText = findViewById(R.id.genre)
        exitButton = findViewById(R.id.exitConfiguration)

        playerText!!.setOnClickListener {
            if (playerText!!.text.equals(getString(R.string.playerType))) {
                playerText!!.text = getString(R.string.casual)
                hitText!!.text = getString(R.string.amateur)
                genderText!!.text = getString(R.string.professional)
            } else {
                if (playerText!!.text.equals(getString(R.string.casual))) {
                    playerTypeConfiguration = getString(R.string.casual)
                } else if (playerText!!.text.equals(getString(R.string.drive))) {
                    hitTypeConfiguration = getString(R.string.drive)
                } else if (playerText!!.text.equals(getString(R.string.male))) {
                    genderConfiguration = getString(R.string.male)
                }
                playerText!!.text = getString(R.string.playerType)
                hitText!!.text = getString(R.string.hitType)
                genderText!!.text = getString(R.string.gender)
            }
        }

        hitText!!.setOnClickListener {
            if (hitText!!.text.equals(getString(R.string.hitType))) {
                playerText!!.text = getString(R.string.drive)
                hitText!!.text = getString(R.string.backhand)
                genderText!!.text = getString(R.string.ball)
            } else {
                if (hitText!!.text.equals(getString(R.string.amateur))) {
                    playerTypeConfiguration = getString(R.string.amateur)
                } else if (hitText!!.text.equals(getString(R.string.backhand))) {
                    hitTypeConfiguration = getString(R.string.backhand)
                } else if (hitText!!.text.equals(getString(R.string.female))) {
                    genderConfiguration = getString(R.string.female)
                }
                playerText!!.text = getString(R.string.playerType)
                hitText!!.text = getString(R.string.hitType)
                genderText!!.text = getString(R.string.gender)
            }
        }

        genderText!!.setOnClickListener {
            if (genderText!!.text.equals(getString(R.string.gender))) {
                playerText!!.text = getString(R.string.male)
                hitText!!.text = getString(R.string.female)
                genderText!!.text = getString(R.string.other)
            } else {
                if (playerText!!.text.equals(getString(R.string.professional))) {
                    playerTypeConfiguration = getString(R.string.professional)
                } else if (playerText!!.text.equals(getString(R.string.ball))) {
                    hitTypeConfiguration = getString(R.string.ball)
                } else if (playerText!!.text.equals(getString(R.string.other))) {
                    genderConfiguration = getString(R.string.other)
                }
                playerText!!.text = getString(R.string.playerType)
                hitText!!.text = getString(R.string.hitType)
                genderText!!.text = getString(R.string.gender)
            }
        }

        exitButton!!.setOnClickListener {
            val intent = Intent().apply {
                putExtra(MainActivity.PLAYER_TYPE, playerTypeConfiguration)
                putExtra(MainActivity.HIT_TYPE, hitTypeConfiguration)
                putExtra(MainActivity.GENDER, genderConfiguration)
            }
            this.setResult(RESULT_OK, intent)
            finish()
        }
    }
}