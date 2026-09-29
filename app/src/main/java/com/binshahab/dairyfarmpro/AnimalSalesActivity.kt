package com.binshahab.dairyfarmpro

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import java.util.Calendar
import java.util.Locale

class AnimalSalesActivity : Activity() {

    private lateinit var animalRepository: AnimalRepository
    private lateinit var saleRepository: AnimalSaleRepository
    private lateinit var milkRepository: MilkRepository
    private lateinit var listLayout: LinearLayout

    private val farmId = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animalRepository = AnimalRepository(this)
        saleRepository = AnimalSaleRepository(this)
        milkRepository = MilkRepository(this)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(245, 247, 250))

        val header = TextView(this)
        header.text = "Animal Sales"
        header.textSize = 24f
        header.setTextColor(Color.WHITE)
        header.setBackgroundColor(Color.rgb(13, 71, 161))
        header.setPadding(24, 30, 24, 30)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val newSaleButton = Button(this)
        newSaleButton.text = "+ New Sale"
        newSaleButton.setOnClickListener {
            showAnimalSelection()
        }

        root.addView(newSaleButton)

        val scroll = ScrollView(this)

        listLayout = LinearLayout(this)
        listLayout.orientation = LinearLayout.VERTICAL
        listLayout.setPadding(12, 12, 12, 12)

        scroll.addView(listLayout)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)

        refreshSales()
    }

    private fun showAnimalSelection() {

        val animals = animalRepository.getAnimals(farmId)
            .filter {
                !it.status.equals("Sold", ignoreCase = true)
            }

        if (animals.isEmpty()) {
            Toast.makeText(
                this,
                "No active animals available for sale",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val names = animals.map {
            "${it.id} - ${it.name}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Animal")
            .setItems(names) { _, which ->
                showSaleDialog(animals[which])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSaleDialog(animal: Animal) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 10)

        val animalInfo = TextView(this)
        animalInfo.text =
            "Animal ID: ${animal.id}\n" +
            "Animal: ${animal.name}\n" +
            "Purchase Date: ${animal.purchaseDate}\n" +
            "Purchase Price: Rs ${animal.purchasePrice}"

        animalInfo.textSize = 16f
        animalInfo.setPadding(0, 0, 0, 20)

        val saleDate = EditText(this)
        saleDate.hint = "Sale Date"
        saleDate.isFocusable = false
        saleDate.setOnClickListener {
            showDatePicker(saleDate)
        }

        val salePrice = EditText(this)
        salePrice.hint = "Sale Price"
        salePrice.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        val buyerName = EditText(this)
        buyerName.hint = "Buyer Name"

        val buyerPhone = EditText(this)
        buyerPhone.hint = "Buyer Phone"
        buyerPhone.inputType = android.text.InputType.TYPE_CLASS_PHONE

        val notes = EditText(this)
        notes.hint = "Notes (Optional)"

        layout.addView(animalInfo)
        layout.addView(saleDate)
        layout.addView(salePrice)
        layout.addView(buyerName)
        layout.addView(buyerPhone)
        layout.addView(notes)

        val dialog = AlertDialog.Builder(this)
            .setTitle("New Animal Sale")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save Sale", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val date = saleDate.text.toString().trim()
                    val price = salePrice.text.toString().toDoubleOrNull()
                    val buyer = buyerName.text.toString().trim()
                    val phone = buyerPhone.text.toString().trim()
                    val noteText = notes.text.toString().trim()

                    if (date.isEmpty() || price == null || price <= 0) {
                        Toast.makeText(
                            this,
                            "Please enter sale date and sale price",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val existingSale =
                        saleRepository.getSalesForAnimal(
                            farmId,
                            animal.id
                        )

                    if (
                        animal.status.equals("Sold", ignoreCase = true) ||
                        existingSale.isNotEmpty()
                    ) {
                        Toast.makeText(
                            this,
                            "This animal has already been sold",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                        refreshSales()
                        return@setOnClickListener
                    }

                    val sale = AnimalSale(
                        id = System.currentTimeMillis().toString(),
                        farmId = farmId,
                        animalId = animal.id,
                        saleDate = date,
                        purchaseDate = animal.purchaseDate,
                        purchasePrice = animal.purchasePrice,
                        salePrice = price,
                        buyerName = buyer,
                        buyerPhone = phone,
                        notes = noteText
                    )

                    val saved = saleRepository.addSale(
                        farmId,
                        sale
                    )

                    if (!saved) {
                        Toast.makeText(
                            this,
                            "This animal has already been sold",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }

                    val markedSold =
                        animalRepository.markSold(
                            farmId,
                            animal.id,
                            date,
                            price
                        )

                    if (!markedSold) {
                        Toast.makeText(
                            this,
                            "Sale could not be completed",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }

                    dialog.dismiss()
                    refreshSales()

                    Toast.makeText(
                        this,
                        "Animal sale saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        dialog.show()
    }

    private fun showDatePicker(target: EditText) {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                target.setText(
                    String.format(
                        Locale.US,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        day
                    )
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun refreshSales() {

        listLayout.removeAllViews()

        val sales = saleRepository.getSales(farmId)

        if (sales.isEmpty()) {
            val empty = TextView(this)
            empty.text = "No animal sales yet."
            empty.textSize = 18f
            empty.gravity = Gravity.CENTER
            empty.setPadding(20, 50, 20, 50)

            listLayout.addView(empty)
            return
        }

        sales.reversed().forEach { sale ->
            addSaleRow(sale)
        }
    }

    private fun addSaleRow(sale: AnimalSale) {

        val animal =
            animalRepository.findAnimal(
                farmId,
                sale.animalId
            )

        val milkIncome =
            milkRepository.getRecords(farmId)
                .filter {
                    it.animalId.equals(
                        sale.animalId,
                        ignoreCase = true
                    )
                }
                .sumOf {
                    it.milkIncome
                }

        val totalIncome =
            sale.salePrice + milkIncome

        val totalProfit =
            totalIncome - sale.purchasePrice

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(20, 18, 20, 18)
        card.setBackgroundColor(
            Color.argb(45, 244, 67, 54)
        )

        val title = TextView(this)
        title.text =
            "${sale.animalId} • ${animal?.name ?: "Animal"}"
        title.textSize = 19f
        title.setTextColor(Color.rgb(13, 71, 161))

        val details = TextView(this)

        details.text =
            "Purchase Date: ${sale.purchaseDate}\n" +
            "Purchase Price: Rs ${sale.purchasePrice}\n" +
            "Sale Date: ${sale.saleDate}\n" +
            "Sale Price: Rs ${sale.salePrice}\n" +
            "Milk Income: Rs $milkIncome\n" +
            "Total Income: Rs $totalIncome\n" +
            "Total Profit: Rs $totalProfit\n" +
            "Buyer: ${sale.buyerName.ifEmpty { "Not provided" }}\n" +
            "Phone: ${sale.buyerPhone.ifEmpty { "Not provided" }}\n" +
            "Status: SOLD"

        details.textSize = 15f
        details.setPadding(0, 10, 0, 0)

        card.addView(title)
        card.addView(details)

        listLayout.addView(
            card,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        )
    }
}
