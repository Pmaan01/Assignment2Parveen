package com.android_assignments.assignment_2_parveen.ui.review

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android_assignments.assignment_2_parveen.R
import com.android_assignments.assignment_2_parveen.data.models.Desk
import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import com.android_assignments.assignment_2_parveen.data.models.PickupSlip
import com.android_assignments.assignment_2_parveen.data.repo.DataRepoModule
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_DESK
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_ITEM
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_SETTINGS
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_SLIP

class ReviewItemActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var tvDetails: TextView
    private lateinit var tvDeskName: TextView
    private lateinit var btnChooseDesk: Button
    private lateinit var btnConfirm: Button

    private lateinit var foundItem: FoundItem
    private lateinit var settings: SlipSettings

    private lateinit var viewModel: ReviewItemViewModel

    private val selectDeskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val desk = result.data?.getParcelableExtra<Desk>(KEY_DESK)
            desk?.let {
                viewModel.setSelectedDesk(it)
                tvDeskName.text = it.displayName
                btnConfirm.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_item)

        imagePreview = findViewById(R.id.imagePreview)
        tvDetails = findViewById(R.id.tvDetails)
        tvDeskName = findViewById(R.id.tvDeskName)
        btnChooseDesk = findViewById(R.id.btnChooseDesk)
        btnConfirm = findViewById(R.id.btnConfirm)

        foundItem = intent.getParcelableExtra<FoundItem>(KEY_ITEM)
            ?: throw IllegalStateException("Missing FoundItem extra")
        settings = intent.getParcelableExtra<SlipSettings>(KEY_SETTINGS) ?: SlipSettings()

        val factory = ReviewItemViewModel.Factory(
            deskRepo = DataRepoModule.provideDeskRepo()
        )
        viewModel = ViewModelProvider(this, factory)[ReviewItemViewModel::class.java]

        setupUi()
        observeViewModel()
    }

    private fun setupUi() {
        if (!foundItem.photoUri.isNullOrEmpty()) imagePreview.setImageURI(Uri.parse(foundItem.photoUri))
        else imagePreview.setImageResource(android.R.color.darker_gray)

        val details = StringBuilder().apply {
            append("Name: ${foundItem.name}\n")
            foundItem.description?.let { append("Description: $it\n") }
            append("Found at: ${foundItem.foundLocation}\n")
            foundItem.notes?.let { append("Notes: $it\n") }
        }.toString()
        tvDetails.text = details

        btnChooseDesk.setOnClickListener {
            val i = Intent(this, com.android_assignments.assignment_2_parveen.ui.selectdesk.SelectDeskActivity::class.java)
            selectDeskLauncher.launch(i)
        }

        btnConfirm.setOnClickListener {
            viewModel.buildAndEmitSlip(foundItem, settings)
        }

        btnConfirm.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.navigateToReceipt.observe(this) { event ->
            event.getIfNotHandled()?.let { slip: PickupSlip ->
                val intent = Intent(this, com.android_assignments.assignment_2_parveen.ui.receipt.ReceiptActivity::class.java).apply {
                    putExtra(KEY_ITEM, foundItem)
                    putExtra(KEY_SETTINGS, settings)
                    putExtra(KEY_SLIP, slip)
                    putExtra(KEY_DESK, viewModel.state.value?.selectedDesk)
                }
                startActivity(intent)
            }
        }
    }
}
