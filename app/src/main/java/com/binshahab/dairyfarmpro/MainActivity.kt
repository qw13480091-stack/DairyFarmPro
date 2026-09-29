package com.binshahab.dairyfarmpro

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var drawer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.WHITE)

        val topBar = LinearLayout(this)
        topBar.orientation = LinearLayout.HORIZONTAL
        topBar.gravity = Gravity.CENTER_VERTICAL
        topBar.setPadding(20, 20, 20, 20)
        topBar.setBackgroundColor(Color.rgb(13, 71, 161))

        val menuButton = Button(this)
        menuButton.text = "☰"
        menuButton.textSize = 24f
        menuButton.setTextColor(Color.WHITE)
        menuButton.setBackgroundColor(Color.TRANSPARENT)

        topBar.addView(
            menuButton,
            LinearLayout.LayoutParams(
                70,
                70
            )
        )

        val title = TextView(this)
        title.text = "DairyFarmPro"
        title.textSize = 22f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER_VERTICAL

        topBar.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(topBar)

        drawer = LinearLayout(this)
        drawer.orientation = LinearLayout.VERTICAL
        drawer.setPadding(20, 20, 20, 20)
        drawer.setBackgroundColor(Color.WHITE)
        drawer.visibility = View.GONE

        addMenuItem("Dashboard") {
            drawer.visibility = View.GONE
        }

        addMenuItem("Animals") {
            startActivity(
                Intent(this, AnimalsActivity::class.java)
            )
        }

        addMenuItem("Milk Sheets") {
            startActivity(
                Intent(this, MilkSheetsActivity::class.java)
            )
        }

        addMenuItem("Animal Sales") {
            startActivity(
                Intent(this, AnimalSalesActivity::class.java)
            )
        }

        addMenuItem("Purchases") {
            startActivity(
                Intent(this, PurchaseActivity::class.java)
            )
        }

        addMenuItem("Feed") {
            drawer.visibility = View.GONE
        }

        addMenuItem("Employees") {
            drawer.visibility = View.GONE
        }

        addMenuItem("Expenses") {
            drawer.visibility = View.GONE
        }

        addMenuItem("Income & Profit") {
            drawer.visibility = View.GONE
        }

        addMenuItem("Reports") {
            drawer.visibility = View.GONE
        }

        addMenuItem("Settings") {
    startActivity(
        Intent(this, SettingsActivity::class.java)
    )
}
        root.addView(
            drawer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.gravity = Gravity.CENTER
        content.setPadding(40, 40, 40, 40)

        val welcome = TextView(this)
        welcome.text = "Welcome to DairyFarmPro"
        welcome.textSize = 26f
        welcome.setTextColor(Color.rgb(20, 90, 160))
        welcome.gravity = Gravity.CENTER

        content.addView(welcome)

        root.addView(
            content,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        menuButton.setOnClickListener {
            drawer.visibility =
                if (drawer.visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }

        setContentView(root)
    }

    private fun addMenuItem(
        text: String,
        action: () -> Unit
    ) {
        val button = Button(this)
        button.text = text
        button.textSize = 17f
        button.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        button.setOnClickListener {
            action()
        }

        drawer.addView(
            button,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }
}
