package com.binshahab.dairyfarmpro

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : Activity() {

    private val prefsName = "dairyfarmpro_settings"
    private val themeKey = "theme"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "Settings"
        title.textSize = 28f
        title.setTextColor(Color.rgb(20, 90, 160))
        title.gravity = Gravity.CENTER

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val themeTitle = TextView(this)
        themeTitle.text = "Theme"
        themeTitle.textSize = 20f
        themeTitle.setPadding(0, 40, 0, 15)

        layout.addView(themeTitle)

        val lightButton = Button(this)
        lightButton.text = "Light"

        lightButton.setOnClickListener {
            prefs.edit()
                .putString(themeKey, "light")
                .apply()
        }

        layout.addView(lightButton)

        val darkButton = Button(this)
        darkButton.text = "Dark"

        darkButton.setOnClickListener {
            prefs.edit()
                .putString(themeKey, "dark")
                .apply()
        }

        layout.addView(darkButton)

        val systemButton = Button(this)
        systemButton.text = "System Default"

        systemButton.setOnClickListener {
            prefs.edit()
                .putString(themeKey, "system")
                .apply()
        }

        layout.addView(systemButton)

        setContentView(layout)
    }
}
