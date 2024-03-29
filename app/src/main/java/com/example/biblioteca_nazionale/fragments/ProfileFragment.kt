package com.example.biblioteca_nazionale.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.biblioteca_nazionale.MainActivity
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentProfileBinding
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import java.net.SocketTimeoutException


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    private val fbViewModel: FirebaseViewModel = FirebaseViewModel()

    val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.textViewNoteModify.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser
        val email = currentUser?.email

        binding.currentEmail.text = email

        if (currentUser != null) {
            val providerData = currentUser.providerData
            val isGoogleSignIn =
                providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

            if (isGoogleSignIn) {
                binding.textViewNoteModify.visibility = View.VISIBLE
                binding.editTextTextEmailAddress.isEnabled = false
                binding.editTextTextPassword.isEnabled = false
                binding.editTextTextPassword2.isEnabled = false
                binding.updateButtonFrag.isEnabled = false
               // binding.deleteButton.isEnabled = false
            } else {
                binding.updateButtonFrag.setOnClickListener {
                    updateAll()
                }
            }
        }

        binding.toolbarMyProfile.setOnMenuItemClickListener {
            showLogoutConfirmationDialog()
            return@setOnMenuItemClickListener true
        }

        binding.deleteButton.setOnClickListener {
            Toast.makeText(
                context,
                "Profile deleted",
                Toast.LENGTH_SHORT
            ).show()

            deleteAccount()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Logout")
            .setMessage("Are you sure you want to disconnect?")
            .setPositiveButton("Confirm") { dialog, which ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateAll() {
        val user = auth.currentUser

        val newEmail = binding.editTextTextEmailAddress.text.toString()
        val newPassword = binding.editTextTextPassword.text.toString()

        if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
            user?.updateEmail(newEmail)
                ?.addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        fbViewModel.updateEmail(newEmail)
                        Toast.makeText(
                            context,
                            "Email changed succesfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { passwordTask ->
                                    if (passwordTask.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Password changed succesfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        updateErrorHandling(passwordTask)
                                    }
                                }
                            val navController = Navigation.findNavController(binding.root)
                            navController.navigate(R.id.action_profileInfoFragment_to_credentialUpdated)
                        }
                        else {
                            Toast.makeText(
                                context,
                                "Password must have the same lenght and characters",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        updateErrorHandling(emailTask)
                    }
                }
        } else if (binding.editTextTextEmailAddress.text.isNotEmpty() && binding.editTextTextPassword.text.isEmpty()) {
            user?.updateEmail(newEmail)
                ?.addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        fbViewModel.updateEmail(newEmail)
                        Toast.makeText(
                            context,
                            "Email changed succesfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val action =
                            ProfileFragmentDirections.actionProfileInfoFragmentToCredentialUpdated()
                        findNavController().navigate(action)
                    } else {
                        updateErrorHandling(emailTask)
                    }
                }
        } else if (binding.editTextTextEmailAddress.text.isEmpty() && binding.editTextTextPassword.text.isNotEmpty()) {
            if (binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()) {
                user?.updatePassword(newPassword)
                    ?.addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Password changed succesfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val action =
                                ProfileFragmentDirections.actionProfileInfoFragmentToCredentialUpdated()
                            findNavController().navigate(action)
                        } else {
                            updateErrorHandling(passwordTask)
                            Toast.makeText(
                                context,
                                "Password not changed, problems occured!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    context,
                    "Password must have the same lenght and characters",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (binding.editTextTextEmailAddress.text.isEmpty() && binding.editTextTextPassword.text.isEmpty()) {
            Toast.makeText(context, "No new credential inserted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateErrorHandling(it: Task<Void>){
        when (it.exception) {
            is FirebaseAuthWeakPasswordException -> {
                alertDialog("Registration error","The password must contain 6 characters, of which at least 1 capital letter")
            }
            is FirebaseAuthEmailException -> {
                alertDialog("Registration error","Email not valid, try again!")

            }
            is FirebaseAuthUserCollisionException -> {
                alertDialog("Registration error","The email address provided is already associated with another account")
            }
            is SocketTimeoutException -> {
                alertDialog("Network error","Check your internet connection")
            }
            else -> {
                alertDialog("General error","Pay attention to what you entered")
            }
        }
    }

    private fun alertDialog(title: String , description: String){
        var alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())

        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(description)
        alertDialogBuilder.setPositiveButton("I understand") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun logout() {
        auth.signOut()
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.action_profileInfoFragment_to_mainActivity)
    }

    private fun deleteAccount() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm delete")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Confirm") { dialog, which ->
                fbViewModel.deleteAccount().thenAccept {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}