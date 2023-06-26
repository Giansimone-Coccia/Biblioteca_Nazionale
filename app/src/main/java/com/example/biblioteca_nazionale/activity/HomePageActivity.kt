package com.example.biblioteca_nazionale.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookListAdapter
import com.example.biblioteca_nazionale.databinding.HomePageBinding
import com.example.biblioteca_nazionale.viewmodel.BooksViewModel
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomePageActivity : AppCompatActivity() {

    lateinit var binding: HomePageBinding
    var model:BooksViewModel = BooksViewModel()
    lateinit var adapter:BookListAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        binding = HomePageBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setupWithNavController(navController)


// INIZIO PROVA CHIAMATE DB FIREBASE CON PATTTERN MVVVM


        val firebaseViewModel: FirebaseViewModel by viewModels()

        val uid = FirebaseAuth.getInstance().uid.toString()
        val currentUser = firebaseViewModel.getCurrentUser(uid)
        currentUser.thenAccept { user ->
            // Qui puoi utilizzare il valore dell'utente ottenuto
            Log.d("/HomePageActivity", user.toString())
           // firebaseViewModel.removeCommentUserSide("0",user)

        }.exceptionally { throwable ->
            // Gestione di eventuali errori nel recupero dell'utente
            Log.e("/HomePageActivity", "Errore nel recupero dell'utente: ${throwable.message}")
            null
        }

        // AGGIUNTA NUOVO LIBRO PRENOTATO
        //firebaseViewModel.addNewBookBooked("Libro di Luca", "123","Biblioteca di Ancona","Immagine")
        //firebaseViewModel.removeBookBooked("Libro di Luca")
         //firebaseViewModel.addNewCommentUserSide("23/06/2023" , "2 COMMENTO")

        val BookInfoObserver = Observer<DocumentSnapshot> { currentBookInfo ->
            // Update the UI, in this case, a TextView.
            //Log.d("/HomePageActivity", currentUserInfo.data.toString())
            //Log.d("/HomePageActivity",firebaseViewModel.getBookInfo("ID_LIBRO").toString())

            //firebaseViewModel.getCurrentUser("provaUser").toString()
             //Log.d("/HomePageActivity",firebaseViewModel.getCurrentUser("provaUser").toString())
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        // model.currentName.observe(this, nameObserver)
        firebaseViewModel.getBookInfoResponseFromDB("ID_LIBRO").observe(this,BookInfoObserver)

// FINE PROVA CHIAMATE DB FIREBASE CON PATTTERN MVVVM
        //firebaseViewModel.addNewBookBooked("123","Il libro di Luca","Biblioteca comunale di termoli, Termoli 86039"," Link al immagine")


    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}