package com.example.biblioteca_nazionale.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class UserSettings(
    var libriPrenotati: ArrayList<MiniBook>?,
    var commenti: ArrayList<Review>,
    var miPiace: ArrayList<Like>?
) {

    override fun toString(): String = "Libri prenotati: " + libriPrenotati.toString() + " " + "Commenti: " + commenti.toString()

    fun addNewBook(bookName: String, isbn: String, bookedPlace: String, image: String,title:String, dateOfBooking: String) {
        val newElement = MiniBook(isbn, bookedPlace, image, dateOfBooking ,title)

        if (libriPrenotati == null) {
            libriPrenotati = ArrayList()
        }

        libriPrenotati?.add(newElement)

    }

    fun addNewLike(bookId:String) {
        val newElement = Like(bookId)

        if (miPiace == null) {
            miPiace = ArrayList()
        }

        miPiace?.add(newElement)
    }

    fun deleteLike(bookId:String) {
        val iterator = miPiace?.iterator()

        if (iterator != null) {
            while (iterator.hasNext()) {
                val miPiace = iterator.next()
                if (bookId == miPiace.bookId) {
                    iterator.remove()
                }
            }
        }
    }



    fun removeBook(bookName: String) {
        val iterator = libriPrenotati?.iterator()

        if (iterator != null) {
            while (iterator.hasNext()) {
                val book = iterator.next()
                if (book.isbn == bookName) {
                    iterator.remove()
                    break
                }
            }
        }
    }

    fun addNewComment(reviewText: String, reviewTitle: String, isbn: String, vote: Float,idComment: String? = null,title: String,image:String) {

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)
        var idC:String
        if (idComment==null){
            idC="C${UUID.randomUUID().toString()}"
        }
        else{
            idC=idComment
        }
        val newComment = Review(idC, reviewText, reviewTitle, isbn, vote, formattedDateTime,title,image)

        println(newComment)

        if (commenti == null) {
            commenti = ArrayList()
        }

        commenti?.add(newComment)
    }

    fun removeComment(idComment: String){
        val iterator = commenti?.iterator()

        if (iterator != null) {
            while (iterator.hasNext()) {
                val comment = iterator.next()
                if (comment.idComment == idComment) {
                    iterator.remove()
                    println(commenti)
                    break
                }
            }
        }
    }
}
