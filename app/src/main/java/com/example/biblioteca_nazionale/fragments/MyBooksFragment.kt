package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.BookAdapter
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.model.ImageLinks
import com.example.biblioteca_nazionale.model.InfoBook
import com.example.biblioteca_nazionale.model.Users
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class MyBooksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val firebaseViewModel: FirebaseViewModel by viewModels()
        val firebaseViewModel: FirebaseViewModel = ViewModelProvider(requireActivity()).get(FirebaseViewModel::class.java)

        val utente: Users = firebaseViewModel.getCurrentUser(firebaseViewModel.firebase.getCurrentUid().toString()).get()

        val libriPrenotati: HashMap<String,ArrayList<String>>? = utente.userSettings?.libriPrenotati

        val titoliLibri = utente.userSettings?.libriPrenotati?.keys?.toList()


        val view = inflater.inflate(R.layout.fragment_my_books, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewMyBooks)
        //val adapter = BookAdapter(titoliLibri) // Quì ci va la lista dei libri dell'utente, acquisibile dal database

        //recyclerView.adapter = adapter
        //recyclerView.layoutManager = LinearLayoutManager(requireContext()) // o LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) se desideri un layout orizzontale

        return view
        //return inflater.inflate(R.layout.fragment_my_books, container, false)
    }


}