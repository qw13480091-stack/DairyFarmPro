package com.binshahab.dairyfarmpro

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MilkSheetsActivity : Activity() {

    private lateinit var animalRepository: AnimalRepository
    private lateinit var milkRepository: MilkRepository
    private lateinit var rowsLayout: LinearLayout
    private lateinit var dateInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var givenInput: EditText

    private val farmId = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animalRepository = AnimalRepository(this)
        milkRepository = MilkRepository(this)

        buildScreen()
        loadSheet()
    }

    private fun buildScreen() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(245, 247, 250))

        val header = TextView(this)
        header.text = "Milk Sheets"
        header.textSize = 24f
        header.setTextColor(Color.WHITE)
        header.setBackgroundColor(Color.rgb(13, 71, 161))
        header.setPadding(24, 30, 24, 30)

        root.addView(header)

        dateInput = EditText(this)
        dateInput.hint = "Date (dd-MM-yyyy)"
        dateInput.setText(
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())
        )

        root.addView(dateInput)

        val loadButton = Button(this)
        loadButton.text = "Load Day"
        loadButton.setOnClickListener {
            loadSheet()
        }

        root.addView(loadButton)

        priceInput = EditText(this)
        priceInput.hint = "Milk Price per kg"
        priceInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        root.addView(priceInput)

        givenInput = EditText(this)
        givenInput.hint = "Milk Given to Labour/Others (kg)"
        givenInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        root.addView(givenInput)

        val scroll = ScrollView(this)

        rowsLayout = LinearLayout(this)
        rowsLayout.orientation = LinearLayout.VERTICAL
        rowsLayout.setPadding(12, 12, 12, 12)

        scroll.addView(rowsLayout)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val saveButton = Button(this)
        saveButton.text = "Save Milk Sheet"
        saveButton.setOnClickListener {
            saveSheet()
        }

        root.addView(saveButton)

        setContentView(root)
    }

    private fun loadSheet() {

        rowsLayout.removeAllViews()

        val date = dateInput.text.toString().trim()

        if (date.isEmpty()) {
            Toast.makeText(
                this,
                "Enter a date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val animals = animalRepository.getAnimals(farmId)

        val records = milkRepository.getRecordsForDate(
            farmId,
            date
        )

        val recordAnimalIds = records.map {
            it.animalId.lowercase(Locale.getDefault())
        }.toSet()

        val eligibleAnimals = animals.filter {
            it.status.equals("Active", ignoreCase = true) &&
                it.gender.equals("Female", ignoreCase = true) &&
                it.isMilkProducing
        }

        val historicalAnimals = animals.filter {
            recordAnimalIds.contains(
                it.id.lowercase(Locale.getDefault())
            )
        }

        val displayAnimals = (
            eligibleAnimals + historicalAnimals
        ).distinctBy {
            it.id.lowercase(Locale.getDefault())
        }

        if (displayAnimals.isEmpty()) {

            val empty = TextView(this)
            empty.text =
                "No milk-producing female animals for this day."

            empty.textSize = 18f
            empty.gravity = Gravity.CENTER
            empty.setPadding(20, 50, 20, 50)

            rowsLayout.addView(empty)
            return
        }

        val heading = TextView(this)
        heading.text = "Animal ID    Animal    Morning kg    Evening kg    Total"
        heading.textSize = 16f
        heading.setTypeface(null, android.graphics.Typeface.BOLD)
        heading.setPadding(8, 15, 8, 15)

        rowsLayout.addView(heading)

        displayAnimals.forEach { animal ->

            val existingRecord = records.firstOrNull {
                it.animalId.equals(
                    animal.id,
                    ignoreCase = true
                )
            }

            addAnimalRow(
                animal,
                existingRecord
            )

            if (existingRecord != null &&
                priceInput.text.toString().isEmpty()
            ) {
                priceInput.setText(
                    existingRecord.pricePerKg.toString()
                )

                givenInput.setText(
                    existingRecord.milkGivenKg.toString()
                )
            }
        }
    }

    private fun addAnimalRow(
        animal: Animal,
        record: MilkRecord?
    ) {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(16, 14, 16, 14)

        val isSold =
            animal.status.equals(
                "Sold",
                ignoreCase = true
            )

        card.setBackgroundColor(
            if (isSold) {
                Color.argb(45, 244, 67, 54)
            } else {
                Color.argb(45, 76, 175, 80)
            }
        )

        val title = TextView(this)

        title.text =
            if (isSold) {
                "${animal.id} • ${animal.name} • SOLD"
            } else {
                "${animal.id} • ${animal.name}"
            }

        title.textSize = 18f
        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        title.setTextColor(
            if (isSold) {
                Color.rgb(198, 40, 40)
            } else {
                Color.rgb(46, 125, 50)
            }
        )

        card.addView(title)

        val fields = LinearLayout(this)
        fields.orientation = LinearLayout.HORIZONTAL

        val morning = EditText(this)
        morning.hint = "Morning"
        morning.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        morning.setText(
            if (record == null) {
                ""
            } else {
                record.morningKg.toString()
            }
        )

        val evening = EditText(this)
        evening.hint = "Evening"
        evening.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        evening.setText(
            if (record == null) {
                ""
            } else {
                record.eveningKg.toString()
            }
        )
        morning.isEnabled = !isSold
        evening.isEnabled = !isSold

        fields.addView(
            morning,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        fields.addView(
            evening,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        card.addView(fields)

        val total = TextView(this)

        fun updateTotal() {
            val m =
                morning.text.toString().toDoubleOrNull()
                    ?: 0.0

            val e =
                evening.text.toString().toDoubleOrNull()
                    ?: 0.0

            total.text = "Total: ${m + e} kg"
        }

        updateTotal()

        morning.setOnTextChanged {
            updateTotal()
        }

        evening.setOnTextChanged {
            updateTotal()
        }

        total.textSize = 16f
        total.setPadding(0, 8, 0, 4)

        card.addView(total)

        rowsLayout.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 10
            }
        )

        card.tag = animal.id
    }

    private fun saveSheet() {

        val date = dateInput.text.toString().trim()

        if (date.isEmpty()) {
            Toast.makeText(
                this,
                "Enter a date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val price =
            priceInput.text.toString().toDoubleOrNull()
                ?: 0.0

        val given =
            givenInput.text.toString().toDoubleOrNull()
                ?: 0.0

        val animals = animalRepository.getAnimals(farmId)

        var savedCount = 0

        for (i in 1 until rowsLayout.childCount) {

            val view = rowsLayout.getChildAt(i)

            if (view !is LinearLayout) continue

            val animalId = view.tag as? String
                ?: continue

            val animal = animals.firstOrNull {
                it.id.equals(
                    animalId,
                    ignoreCase = true
                )
            } ?: continue

            val fields =
                view.getChildAt(1) as? LinearLayout
                    ?: continue

            val morning =
                (fields.getChildAt(0) as EditText)
                    .text.toString()
                    .toDoubleOrNull()
                    ?: 0.0

            val evening =
                (fields.getChildAt(1) as EditText)
                    .text.toString()
                    .toDoubleOrNull()
                    ?: 0.0

            val oldRecord =
                milkRepository.getRecordsForDate(
                    farmId,
                    date
                ).firstOrNull {
                    it.animalId.equals(
                        animal.id,
                        ignoreCase = true
                    )
                }

            val record = MilkRecord(
                id = oldRecord?.id
                    ?: "${date}_${animal.id}",
                farmId = farmId,
                animalId = animal.id,
                date = date,
                morningKg = morning,
                eveningKg = evening,
                pricePerKg = price,
                milkGivenKg = given
            )

            milkRepository.addRecord(
                farmId,
                record
            )

            savedCount++
        }

        Toast.makeText(
            this,
            "$savedCount milk records saved",
            Toast.LENGTH_SHORT
        ).show()

        loadSheet()
    }

    private fun EditText.setOnTextChanged(
        action: () -> Unit
    ) {
        addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    action()
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )
    }
}
