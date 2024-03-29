package com.example.biblioteca_nazionale.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.adapter.NotificationAdapter
import com.example.biblioteca_nazionale.databinding.FragmentNotificationsBinding
import com.example.biblioteca_nazionale.utils.NotificationReceiver
import com.example.biblioteca_nazionale.viewmodel.FirebaseViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var _binding: FragmentNotificationsBinding? = null
    private val model: FirebaseViewModel = FirebaseViewModel()
    private val binding get() = _binding!!

    companion object {
        var flag = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        binding.layoutTotal.visibility=View.GONE

        if (flag) {
            flag = false
            model.getAllDate().thenAccept { expirationBook ->
                val inputFormat = "dd/MM/yyyy"
                val outputFormat = "dd/MM/yyyy"
                val inputFormatter = SimpleDateFormat(inputFormat)
                val outputFormatter = SimpleDateFormat(outputFormat)
                for (book in expirationBook) {
                    val context: Context = requireContext()
                    val intent = Intent(context, NotificationReceiver::class.java)
                    val uid = model.firebase.getCurrentUid()
                    // Ricordiamoci che è la data di prenotazione non la expiring date, quindi
                    // dobbiamo sommare i 14 giorni
                    val date = inputFormatter.parse(book.date)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.add(Calendar.DAY_OF_MONTH, 14)
                    val datePlus14Days = calendar.time
                    val datePlus14DaysString = outputFormatter.format(datePlus14Days)
                    intent.putExtra("uid", uid)
                    intent.putExtra("title", "Book return")
                    intent.putExtra(
                        "text",
                        "Your book ${book.title} taken from the library ${book.bookPlace} will expire ${datePlus14DaysString}"
                    )
                    context.sendBroadcast(intent)
                }
            }
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("notification_data", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        val notificationIds = allEntries.keys
            .filter { it.startsWith("title_") }
            .map { it.removePrefix("title_").toInt() }
            .toSet()

        val uid = model.firebase.getCurrentUid()
        val filteredNotificationIds = notificationIds.filter { notificationId ->
            sharedPreferences.getString("uid_$notificationId", "") == uid
        }

        val notificationList = mutableListOf<Triple<Int, String, String>>()
        if (filteredNotificationIds.isNotEmpty()) {
            binding.progressBar.visibility = View.GONE
            binding.layoutPrincipale.visibility = View.GONE
            binding.layoutTotal.visibility=View.VISIBLE

            for (notificationId in filteredNotificationIds) {
                val title = sharedPreferences.getString("title_$notificationId", "")
                val text = sharedPreferences.getString("text_$notificationId", "")
                val notificationInfo = Triple(notificationId, title, text)
                notificationList.add(notificationInfo as Triple<Int, String, String>)
            }
        }else{
            binding.progressBar.visibility = View.GONE
            binding.layoutPrincipale.visibility = View.VISIBLE
            binding.layoutTotal.visibility=View.VISIBLE
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNotifications.layoutManager = layoutManager

        val adapter = NotificationAdapter(notificationList)
        binding.recyclerViewNotifications.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
