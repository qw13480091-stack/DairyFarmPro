package com.binshahab.dairyfarmpro

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class AnimalsActivity : Activity() {

    private lateinit var repository: AnimalRepository
    private lateinit var listLayout: LinearLayout

    private val farmId = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = AnimalRepository(this)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(245, 247, 250))

        val header = TextView(this)
        header.text = "Animals"
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

        val addButton = Button(this)
        addButton.text = "+ Add Animal"
        addButton.setOnClickListener {
            showAnimalDialog(null)
        }

        root.addView(addButton)

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

        refreshAnimals()
    }

    private fun refreshAnimals() {
        listLayout.removeAllViews()

        val animals = repository.getAnimals(farmId)

        if (animals.isEmpty()) {
            val empty = TextView(this)
            empty.text = "No animals added yet."
            empty.textSize = 18f
            empty.gravity = Gravity.CENTER
            empty.setPadding(20, 50, 20, 50)
            listLayout.addView(empty)
            return
        }

        animals.forEach { animal ->
            addAnimalRow(animal)
        }
    }

    private fun addAnimalRow(animal: Animal) {

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(20, 18, 20, 18)
        card.setBackgroundColor(
    when {
        animal.status.equals("Sold", ignoreCase = true) ->
            Color.argb(45, 244, 67, 54)

        animal.isMilkProducing &&
            animal.gender.equals("Female", ignoreCase = true) &&
            !animal.status.equals("Sold", ignoreCase = true) ->
            Color.argb(45, 76, 175, 80)

        else ->
            Color.WHITE
    }
)
        val title = TextView(this)
        title.text = "${animal.id}  •  ${animal.name}"
        title.textSize = 19f
        title.setTextColor(Color.rgb(13, 71, 161))

        val details = TextView(this)
        details.text =
            "Type: ${animal.type}\n" +
            "Breed: ${animal.breed}\n" +
            "Gender: ${animal.gender}\n" +
            "Birth: ${animal.birthDate}\n" +
            "Purchase: ${animal.purchaseDate}\n" +
            "Purchase Price: Rs ${animal.purchasePrice}\n" +
            "Status: ${animal.status}"

        details.textSize = 15f
        details.setPadding(0, 10, 0, 10)

        card.addView(title)
        card.addView(details)

        val buttons = LinearLayout(this)
        buttons.orientation = LinearLayout.HORIZONTAL

        val edit = Button(this)
        edit.text = "Edit"
        edit.setOnClickListener {
            showAnimalDialog(animal)
        }

        val delete = Button(this)
        delete.text = "Delete"
        delete.setOnClickListener {
            confirmDelete(animal)
        }

        buttons.addView(
            edit,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        buttons.addView(
            delete,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        card.addView(buttons)

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

    private fun showAnimalDialog(existing: Animal?) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 10)

        val id = EditText(this)
        id.hint = "Animal ID"
        id.setText(existing?.id ?: "")

        val name = EditText(this)
        name.hint = "Animal Name"
        name.setText(existing?.name ?: "")

        val tag = EditText(this)
        tag.hint = "Tag"
        tag.setText(existing?.tag ?: "")

        val type = EditText(this)
        type.hint = "Type (Cow/Buffalo/Bull/Calf/Goat)"
        type.setText(existing?.type ?: "")

        val breed = EditText(this)
        breed.hint = "Breed"
        breed.setText(existing?.breed ?: "")

        val gender = EditText(this)
        gender.hint = "Gender"
        gender.setText(existing?.gender ?: "")

        val birthDate = EditText(this)
        birthDate.hint = "Birth Date"
        birthDate.setText(existing?.birthDate ?: "")

        val purchaseDate = EditText(this)
        purchaseDate.hint = "Purchase Date"
        purchaseDate.setText(existing?.purchaseDate ?: "")

        val purchasePrice = EditText(this)
        purchasePrice.hint = "Purchase Price"
        purchasePrice.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        purchasePrice.setText(
            if (existing == null) "" else existing.purchasePrice.toString()
        )

        layout.addView(id)
        layout.addView(name)
        layout.addView(tag)
        layout.addView(type)
        layout.addView(breed)
        layout.addView(gender)
        layout.addView(birthDate)
        layout.addView(purchaseDate)
        layout.addView(purchasePrice)

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Add Animal" else "Edit Animal")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                val animalId = id.text.toString().trim()
                val animalName = name.text.toString().trim()
                val animalTag = tag.text.toString().trim()
                val animalType = type.text.toString().trim()
                val animalBreed = breed.text.toString().trim()
                val animalGender = gender.text.toString().trim()
                val animalBirth = birthDate.text.toString().trim()
                val animalPurchaseDate = purchaseDate.text.toString().trim()
                val price = purchasePrice.text.toString().toDoubleOrNull()

                if (
                    animalId.isEmpty() ||
                    animalName.isEmpty() ||
                    animalTag.isEmpty() ||
                    animalType.isEmpty() ||
                    animalBreed.isEmpty() ||
                    animalGender.isEmpty() ||
                    animalPurchaseDate.isEmpty() ||
                    price == null
                ) {
                    Toast.makeText(
                        this,
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val newAnimal = Animal(
                    id = animalId,
                    name = animalName,
                    tag = animalTag,
                    type = animalType,
                    breed = animalBreed,
                    gender = animalGender,
                    birthDate = animalBirth,
                    purchaseDate = animalPurchaseDate,
                    purchasePrice = price,
                    saleDate = existing?.saleDate ?: "",
                    salePrice = existing?.salePrice ?: 0.0,
                    status = existing?.status ?: "Active"
                )

                val success = if (existing == null) {
                    repository.addAnimal(farmId, newAnimal)
                } else {
                    repository.updateAnimal(
                        farmId,
                        existing.id,
                        newAnimal
                    )
                }

                if (success) {
                    dialog.dismiss()
                    refreshAnimals()
                    Toast.makeText(
                        this,
                        "Animal saved",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "ID, name, or tag already exists",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        dialog.show()
    }

    private fun confirmDelete(animal: Animal) {

        AlertDialog.Builder(this)
            .setTitle("Delete Animal?")
            .setMessage(
                "Delete ${animal.id} - ${animal.name}? This cannot be undone."
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->

                if (repository.deleteAnimal(farmId, animal.id)) {
                    refreshAnimals()

                    Toast.makeText(
                        this,
                        "Animal deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}
