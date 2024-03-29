package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.ReviewsAdapter
import com.example.biblioteca_nazionale.databinding.FragmentReviewsBinding
import com.example.biblioteca_nazionale.model.Book
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel

class ReviewsFragment : Fragment(R.layout.fragment_reviews) {

    lateinit var binding: FragmentReviewsBinding
    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReviewsBinding.bind(view)

        val book = arguments?.getParcelable<Book>("book")

        book?.let {

            manageToolbar(book)

            fbViewModel.getUserByCommentsOfBooks(book.id).observe(viewLifecycleOwner) { users ->
                val commentsList = ArrayList<TemporaryReview>()
                println(users)
                for (user in users) {
                    for (comment in user.userSettings?.commenti!!) {
                        if (comment.isbn == book.id) {
                            commentsList.add(
                                TemporaryReview(
                                    comment.idComment,
                                    comment.reviewText,
                                    comment.reviewTitle,
                                    comment.isbn,
                                    comment.vote,
                                    comment.date,
                                    user.email
                                )
                            )
                        }
                    }
                }
                val adapter = ReviewsAdapter(commentsList)
                val layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewReviews.layoutManager = layoutManager
                binding.recyclerViewReviews.adapter = adapter
            }
        }
    }

    private fun manageToolbar(book: Book) {
        val toolbar = binding.toolbar

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            val action = ReviewsFragmentDirections.actionReviewsFragmentToBookInfoFragment(book)
            findNavController().navigate(action)
        }

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val action = ReviewsFragmentDirections.actionReviewsFragmentToBookListFragment(
                    focusSearchView = true
                )
                findNavController().navigate(action)
            }
        }
    }
}

data class TemporaryReview(
    var idComment: String,
    var reviewText: String,
    var reviewTitle: String,
    var isbn: String,
    var vote: Float,
    var date: String,
    val email: String,
)