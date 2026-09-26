package com.binshahab.dairyfarmpro

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(40, 40, 40, 40)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "DairyFarmPro"
        title.textSize = 30f
        title.setTextColor(Color.rgb(20, 90, 160))
        title.gravity = Gravity.CENTER

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val animalsButton = Button(this)
        animalsButton.text = "Animals"
        animalsButton.textSize = 18f

        animalsButton.setOnClickListener {
            startActivity(
                android.content.Intent(
                    this,
                    AnimalsActivity::class.java
                )
            )
        }

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        buttonParams.topMargin = 40

        layout.addView(animalsButton, buttonParams)
        val milkSheetsButton = Button(this)
milkSheetsButton.text = "Milk Sheets"
milkSheetsButton.textSize = 18f

milkSheetsButton.setOnClickListener {
    startActivity(
        android.content.Intent(
            this,
            MilkSheetsActivity::class.java
        )
    )
}

val milkButtonParams = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.MATCH_PARENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
)

milkButtonParams.topMargin = 20

layout.addView(
    milkSheetsButton,
    milkButtonParams
)
        setContentView(layout)
    }
}
