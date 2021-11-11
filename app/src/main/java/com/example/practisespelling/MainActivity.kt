package com.example.practisespelling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var db: AppDatabase
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var mainText: TextView
    lateinit var userInput: TextView
    lateinit var addButton: ImageView
    lateinit var removeButton: ImageView
    lateinit var previousImage: ImageView
    lateinit var nextImage: ImageView
    lateinit var rewardImage: ImageView
    private var shortAnimationDuration: Int = 400
    lateinit var loadAllCat: ImageView
    lateinit var loadpersonCat: ImageView
    lateinit var loadvehicleCat: ImageView
    lateinit var loadanimalsCat: ImageView
    lateinit var loadcolorsCat: ImageView
    lateinit var loadotherCat: ImageView
    var listOfCards = mutableListOf<TextCard>()
    var currentTextCardindex = 0
    var currentTextCard: TextCard? = null
    var userInputText = " "
    var currentCategory = "default"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rewardImage = findViewById(R.id.rewardImage)
        rewardImage.visibility = View.GONE
        mainText = findViewById(R.id.mainText)
        userInput = findViewById(R.id.userInputText)
        addButton = findViewById(R.id.addButtonImage)
        removeButton = findViewById(R.id.removeButtonImage)
        previousImage = findViewById(R.id.previousButtonImage)
        nextImage = findViewById(R.id.nextButtonImage)
        loadAllCat = findViewById(R.id.allImage)
        loadpersonCat = findViewById(R.id.personImage)
        loadvehicleCat = findViewById(R.id.vehicleImage)
        loadanimalsCat = findViewById(R.id.animalsImage)
        loadcolorsCat = findViewById(R.id.colorImage)
        loadotherCat = findViewById(R.id.otherImage)

        job = Job()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "text-cards"
        )
            .fallbackToDestructiveMigration()
            .build()

        loadAllCards()


        val compareButton = findViewById<Button>(R.id.compareButton)
        compareButton.setOnClickListener {
            userInputText = userInput.text.toString().lowercase()
            compare()

        }

        nextImage.setOnClickListener {

            loadNewCard()

        }
        previousImage.setOnClickListener {
            loadPreviousCard()
        }

        removeButton.setOnClickListener {
            if (currentTextCard != null) {
                delete(currentTextCard!!)
                Log.d("ffs", "$currentTextCard deleted")

            }
        }

        addButton.setOnClickListener {

            val intent = Intent(this, CreateTextCardActivity::class.java)
            startActivity(intent)

        }
        loadAllCat.setOnClickListener {

            currentTextCardindex = 1
            loadAllCards()
            currentCategory = "load all"
        }

        loadpersonCat.setOnClickListener {

            currentTextCardindex = 1
            loadByCategory("Person")
            currentCategory = "Person"

        }

        loadvehicleCat.setOnClickListener {

            currentTextCardindex = 1
            loadByCategory("Fordon")
            currentCategory = "Fordon"

        }

        loadanimalsCat.setOnClickListener {

            currentTextCardindex = 1
            loadByCategory("Djur")
            currentCategory = "Djur"

        }

        loadcolorsCat.setOnClickListener {

            currentTextCardindex = 1
            loadByCategory("Färger")
            currentCategory = "Färger"

        }

        loadotherCat.setOnClickListener {

            currentTextCardindex = 1
            loadByCategory("Other")
            currentCategory = "Other"
        }


    }

    override fun onResume() {
        super.onResume()

       refresh()

    }


    private fun refresh() {

        when (currentCategory) {

            "default" -> loadAllCards()

            "load all" -> loadAllCards()

            else -> loadByCategory(currentCategory)

        }


    }


    private fun compare() {
        if (currentTextCard != null) {
            val mainText = currentTextCard!!.name!!.lowercase()

            if (mainText == userInputText) {

                reward()
                userInput.text = ""
            } else {

                userInput.text = ""
            }
        }
    }

    private fun reward() {
        fadeIn()
        Handler(Looper.getMainLooper()).postDelayed({
            hideReward()
        }, 1000)
        loadNewCard()
    }

    private fun hideReward() {
        rewardImage.visibility = View.GONE
    }

    private fun fadeIn() {
        rewardImage.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
    }


    private fun loadNewCard() {
        if (currentTextCardindex < listOfCards.size - 1) {
            currentTextCardindex++

        } else {
            currentTextCardindex = 0

        }

        if (!listOfCards.isEmpty()) {
            currentTextCard = listOfCards[currentTextCardindex]
        }

        if (currentTextCard != null) {
            mainText.text = currentTextCard!!.name
        } else {
            mainText.text = ""

        }


    }

    private fun loadPreviousCard() {
        if (currentTextCardindex > 0) {
            currentTextCardindex--


        } else {
            val reset = listOfCards.size - 1
            currentTextCardindex = reset   // backar till sista kortet i listan om man var på första kortet.

        }
        currentTextCard = listOfCards[currentTextCardindex]
        if (currentTextCard != null) {

            mainText.text = currentTextCard!!.name
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun delete(textCard: TextCard) =
        launch(Dispatchers.IO) {
            db.textcardDao().delete(textCard)
            refresh()

        }

    private fun loadByCategory(category: String) {
        Log.d("TAG", "loadByCategory: $currentTextCardindex ")

        val tempCardList = async(Dispatchers.IO) {
            db.textcardDao().findByCategory(category)


        }
        launch {
            val list = tempCardList.await().toMutableList()
            listOfCards = list
            loadNewCard()

        }
    }

    private fun loadAllCards() {
        val tempCardList = async(Dispatchers.IO) {
            db.textcardDao().getAll()
        }
        launch {
            val list = tempCardList.await().toMutableList()
            listOfCards = list
            if (listOfCards.isEmpty()) {
                createDatabase()
            }
            loadNewCard()
        }
    }

    private fun saveCard(textCard: TextCard) {

        launch(Dispatchers.IO) {

            db.textcardDao().insert(textCard)
        }


    }

    private fun createDatabase() {


        val card1 = TextCard(0, "Leon", "Person")
        val card2 = TextCard(0, "Mamma", "Person")
        val card3 = TextCard(0, "Pappa", "Person")
        val card4 = TextCard(0, "Jenny", "Person")
        val card5 = TextCard(0, "Robin", "Person")

        val card6 = TextCard(0, "Häst", "Djur")
        val card7 = TextCard(0, "Hund", "Djur")
        val card8 = TextCard(0, "Katt", "Djur")
        val card9 = TextCard(0, "Råtta", "Djur")

        val card10 = TextCard(0, "Bil", "Fordon")
        val card11 = TextCard(0, "Lastbil", "Fordon")
        val card12 = TextCard(0, "Brandbil", "Fordon")
        val card13 = TextCard(0, "Polisbil", "Fordon")

        val card14 = TextCard(0, "Blå", "Färger")
        val card15 = TextCard(0, "Grön", "Färger")
        val card16 = TextCard(0, "Lila", "Färger")
        val card17 = TextCard(0, "Röd", "Färger")
        val card18 = TextCard(0, "Gul", "Färger")
        val card19 = TextCard(0, "Orange", "Färger")

        val card20 = TextCard(0, "Sol", "Other")

        saveCard(card1)
        saveCard(card2)
        saveCard(card3)
        saveCard(card4)
        saveCard(card5)
        saveCard(card6)
        saveCard(card7)
        saveCard(card8)
        saveCard(card9)
        saveCard(card10)
        saveCard(card11)
        saveCard(card12)
        saveCard(card13)
        saveCard(card14)
        saveCard(card15)
        saveCard(card16)
        saveCard(card17)
        saveCard(card18)
        saveCard(card19)
        saveCard(card20)

    }
}
