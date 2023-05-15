package com.example.biblioteca_nazionale.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblioteca_nazionale.data.Book
import com.example.biblioteca_nazionale.data.BookRepository

class BookDetailViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _book = MutableLiveData<Book>()
    val book: LiveData<Book> = _book

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _book.value = bookRepository.getBookById(bookId)
        }
    }
}
