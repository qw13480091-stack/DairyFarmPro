package com.binshahab.dairyfarmpro

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class PurchaseActivity : Activity() {

    private lateinit var animalSpinner: Spinner
    private lateinit var dateInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var sellerNameInput: EditText
    private lateinit var sellerPhoneInput: EditText
    private lateinit var notesInput: EditText

    private var animals = mutableListOf<Animal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animals = AnimalRepository.getAll(this)
            .filter { !it.status.equals("Sold", ignoreCase = true) }
            .toMutableList()

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "Purchase Record"
        title.textSize = 26f
        title.setTextColor(Color.rgb(20, 90, 160))
        title.gravity = Gravity.CENTER

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        animalSpinner = Spinner(this)

        val animalNames = animals.map {
            "${it.id} - ${it.name}"
        }

        animalSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            animalNames
        )

        layout.addView(animalSpinner)

        dateInput = createInput("Purchase Date")
        priceInput = createInput("Purchase Price")
        sellerNameInput = createInput("Seller Name")
        sellerPhoneInput = createInput("Seller Phone")
        notesInput = createInput("Notes")

        dateInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
        priceInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        sellerPhoneInput.inputType =
            android.text.InputType.TYPE_CLASS_PHONE

        notesInput.minLines = 3
        notesInput.gravity = Gravity.TOP

        layout.addView(dateInput)
        layout.addView(priceInput)
        layout.addView(sellerNameInput)
        layout.addView(sellerPhoneInput)
        layout.addView(notesInput)

        val saveButton = Button(this)
        saveButton.text = "Save Purchase"
        saveButton.textSize = 18f

        saveButton.setOnClickListener {
            savePurchase()
        }

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        buttonParams.topMargin = 25

        layout.addView(saveButton, buttonParams)

        setContentView(layout)
    }

    private fun createInput(hint: String): EditText {
        val input = EditText(this)
        input.hint = hint
        input.textSize = 16f
        input.setPadding(20, 15, 20, 15)

        input.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        return input
    }

    private fun savePurchase() {

        if (animals.isEmpty()) {
            Toast.makeText(
                this,
                "No active animals available",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val date = dateInput.text.toString().trim()
        val priceText = priceInput.text.toString().trim()

        if (date.isEmpty()) {
            dateInput.error = "Enter purchase date"
            return
        }

        if (priceText.isEmpty()) {
            priceInput.error = "Enter purchase price"
            return
        }

        val price = priceText.toDoubleOrNull()

        if (price == null || price < 0) {
            priceInput.error = "Enter valid price"
            return
        }

        val animal = animals[animalSpinner.selectedItemPosition]

        val purchase = Purchase(
            id = System.currentTimeMillis().toString(),
            farmId = "default",
            animalId = animal.id,
            purchaseDate = date,
            purchasePrice = price,
            sellerName = sellerNameInput.text.toString().trim(),
            sellerPhone = sellerPhoneInput.text.toString().trim(),
            notes = notesInput.text.toString().trim()
        )

        PurchaseRepository.add(this, purchase)

        Toast.makeText(
            this,
            "Purchase saved",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}
