package com.android_assignments.assignment_2_parveen.ui.selectdesk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android_assignments.assignment_2_parveen.R
import com.android_assignments.assignment_2_parveen.data.models.Desk
import com.android_assignments.assignment_2_parveen.data.repo.DataRepoModule
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_DESK

class SelectDeskActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var viewModel: SelectDeskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_desk)

        container = findViewById(R.id.container)

        val factory = SelectDeskViewModel.Factory(DataRepoModule.provideDeskRepo())
        viewModel = ViewModelProvider(this, factory)[SelectDeskViewModel::class.java]

        observeDesks()
    }

    private fun observeDesks() {
        viewModel.desks.observe(this) { desks ->
            renderDeskCards(desks)
        }

        viewModel.deskSelectedEvent.observe(this) { event ->
            event.getIfNotHandled()?.let { desk ->
                val data = Intent().putExtra(KEY_DESK, desk)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    private fun renderDeskCards(desks: List<Desk>) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        desks.forEach { desk ->
            val card = inflater.inflate(R.layout.desk_card, container, false)
            val tvName = card.findViewById<TextView>(R.id.tvDeskName)
            val tvPhone = card.findViewById<TextView>(R.id.tvDeskPhone)
            val tvMeta = card.findViewById<TextView>(R.id.tvDeskMeta)

            tvName.text = desk.displayName
            tvPhone.text = desk.phone
            tvMeta.text = "Rating: ${desk.rating} ★ • handled: ${desk.handledItems}"

            card.isClickable = true
            card.isFocusable = true
            card.setOnClickListener {
                if (!desk.open) {
                    Toast.makeText(this, "${desk.displayName} is temporarily closed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.selectDesk(desk)
            }

            container.addView(card)
        }
    }
}
