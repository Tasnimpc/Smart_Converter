package com.example.smartconverter

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnHamburger: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var favButton: LinearLayout
    private lateinit var infoButton: LinearLayout

    private lateinit var cardMap: Map<String, CardView>
    private val favourites = mutableSetOf<String>()
    private var showingFavourites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        btnHamburger = findViewById(R.id.btn_hamburger)

        val searchLayout = findViewById<LinearLayout>(R.id.drawer_search)
        searchEditText = searchLayout.findViewById(R.id.et_search)
        searchIcon = searchLayout.getChildAt(0) as ImageView

        favButton = findViewById(R.id.drawer_favourites)
        infoButton = findViewById(R.id.drawer_info)

        btnHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupCards()
        setupSearch()
        setupFavourites()
        setupInfo()
        setupSettings()
    }

    // -------------------- CARD SETUP --------------------
    private fun setupCards() {
        cardMap = mapOf(
            "temperature" to findViewById(R.id.cv_temp),
            "length" to findViewById(R.id.cv_length),
            "volume" to findViewById(R.id.cv_volume),
            "weight" to findViewById(R.id.cv_weight),
            "speed" to findViewById(R.id.cv_speed),
            "time" to findViewById(R.id.cv_time),
            "area" to findViewById(R.id.cv_area),
            "storage" to findViewById(R.id.cv_storage)
        )

        cardMap.forEach { (name, card) ->

            // ✅ NORMAL CLICK → OPEN CALCULATION SCREEN
            card.setOnClickListener {
                val intent = when (name) {
                    "temperature" -> Intent(this, TemperatureActivity::class.java)
                    "length" -> Intent(this, LengthActivity::class.java)
                    "volume" -> Intent(this, VolumeActivity::class.java)
                    "weight" -> Intent(this, WeightActivity::class.java)
                    "speed" -> Intent(this, SpeedActivity::class.java)
                    "time" -> Intent(this, TimeActivity::class.java)
                    "area" -> Intent(this, AreaActivity::class.java)
                    "storage" -> Intent(this, StorageActivity::class.java)
                    else -> null
                }

                intent?.let { startActivity(it) }
            }

            // ⭐ LONG CLICK → ADD / REMOVE FAVOURITES
            card.setOnLongClickListener {
                if (favourites.contains(name)) {
                    favourites.remove(name)
                    Toast.makeText(this, "$name removed from favourites", Toast.LENGTH_SHORT).show()
                } else {
                    favourites.add(name)
                    Toast.makeText(this, "$name added to favourites", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }

    // -------------------- SEARCH --------------------
    private fun setupSearch() {

        fun applySearch(query: String) {
            if (query.isBlank()) {
                showAllCards()
                return
            }

            cardMap.forEach { (name, card) ->
                card.visibility =
                    if (name.contains(query.lowercase())) View.VISIBLE else View.GONE
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applySearch(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchIcon.setOnClickListener {
            applySearch(searchEditText.text.toString())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    // -------------------- FAVOURITES --------------------
    private fun setupFavourites() {
        favButton.setOnClickListener {

            if (showingFavourites) {
                showAllCards()
                showingFavourites = false
            } else {
                if (favourites.isEmpty()) {
                    Toast.makeText(this, "No favourites added yet", Toast.LENGTH_SHORT).show()
                }

                cardMap.forEach { (name, card) ->
                    card.visibility =
                        if (favourites.contains(name)) View.VISIBLE else View.GONE
                }
                showingFavourites = true
            }

            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    // -------------------- INFO & HELP --------------------
    private fun setupInfo() {
        infoButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, InfoActivity::class.java))
        }
    }

    // -------------------- UTIL --------------------
    private fun showAllCards() {
        cardMap.values.forEach { it.visibility = View.VISIBLE }
        searchEditText.text.clear()
        showingFavourites = false
    }

    private fun setupSettings() {
        findViewById<LinearLayout>(R.id.drawer_settings).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

}
