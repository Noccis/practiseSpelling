package com.example.practisespelling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CreateTextCardActivity : AppCompatActivity(), CoroutineScope {


    private lateinit var job : Job
    private lateinit var db : AppDatabase
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var inputName: EditText
    lateinit var saveButton: Button
    lateinit var createPersonCat: ImageView
    lateinit var createVehicleCat: ImageView
    lateinit var createAnimalCat: ImageView
    lateinit var createColorCat: ImageView
    lateinit var createOtherCat: ImageView
     //lateinit var db2 : AppDatabase

    var chosenCategory = ""
    var newCardName = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_text_card)
        job = Job()

        db = Room.databaseBuilder(applicationContext,
            AppDatabase::class.java,
            "text-cards")
            .fallbackToDestructiveMigration()
            .build()// David, måste jag ha corutiner här?

        inputName = findViewById(R.id.createCardName)
        saveButton = findViewById(R.id.createSaveButton)
        createPersonCat = findViewById(R.id.createPersonImage)
        createVehicleCat = findViewById(R.id.createVehicleImage)
        createAnimalCat = findViewById(R.id.createAnimalsImage)
        createColorCat = findViewById(R.id.createColorImage)
        createOtherCat = findViewById(R.id.createOtherImage)

        db = Room.databaseBuilder(applicationContext,
            AppDatabase::class.java,
            "text-cards")
            .fallbackToDestructiveMigration()
            .build()

        chosenCategory = "Other"

        createPersonCat.setOnClickListener {
            chosenCategory = "Person"

            Log.d("ffs", "Kategori vald: Person")
        }

        createVehicleCat.setOnClickListener {
            chosenCategory = "Fordon"
        }

        createAnimalCat.setOnClickListener {
            chosenCategory = "Djur"
        }

        createColorCat.setOnClickListener {
            chosenCategory = "Färger"
        }

        createOtherCat.setOnClickListener {
            chosenCategory = "Other"
        }


        saveButton.setOnClickListener {
            newCardName = inputName.text.toString()

            val newCard = TextCard(0, newCardName, chosenCategory)

            if (newCard != null) {

                Log.d("ffs", "Sparar, textCard är inte null.")
               if (newCard.category != null) {
                    saveTextCard(newCard)
                    Log.d("ffs", "Sparar. categori är inte null")
                } /* else {
                   newCard.category = "Other"
                    saveTextCard(newCard)
                    Log.d("ffs", "sparar. Kategori är null! :-(")


                }

               */

            } else {
                val text = "Något blev fel. Försök igen!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()

                Log.d("ffs", "försöker SPARA. något blev fel.")
            }
            finish()
        }


    }

    fun saveTextCard(newCard:TextCard) {
        launch(Dispatchers.IO) {
            db.textcardDao().insert(newCard)
        }

    }
}