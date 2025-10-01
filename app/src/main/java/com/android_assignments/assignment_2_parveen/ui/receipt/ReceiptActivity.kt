package com.android_assignments.assignment_2_parveen.ui.receipt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android_assignments.assignment_2_parveen.R
import com.android_assignments.assignment_2_parveen.data.models.Desk
import com.android_assignments.assignment_2_parveen.data.models.FoundItem
import com.android_assignments.assignment_2_parveen.data.models.PickupSlip
import com.android_assignments.assignment_2_parveen.data.models.SlipSettings
import com.android_assignments.assignment_2_parveen.data.repo.DataRepoModule
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_DESK
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_ITEM
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_SETTINGS
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_SLIP

class ReceiptActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView
    private lateinit var imageView: ImageView
    private lateinit var btnShare: Button

    private lateinit var foundItem: FoundItem
    private lateinit var desk: Desk
    private lateinit var settings: SlipSettings
    private lateinit var slip: PickupSlip

    private lateinit var viewModel: ReceiptViewModel

    private val shareLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)

        tvSummary = findViewById(R.id.tvSummary)
        imageView = findViewById(R.id.imageView)
        btnShare = findViewById(R.id.btnShare)

        foundItem = intent.getParcelableExtra<FoundItem>(KEY_ITEM)
            ?: throw IllegalStateException("Missing FoundItem extra")
        desk = intent.getParcelableExtra<Desk>(KEY_DESK)
            ?: throw IllegalStateException("Missing Desk extra")
        settings = intent.getParcelableExtra<SlipSettings>(KEY_SETTINGS) ?: SlipSettings()
        slip = intent.getParcelableExtra<PickupSlip>(KEY_SLIP)
            ?: throw IllegalStateException("Missing PickupSlip extra")

        val factory = ReceiptViewModel.Factory(
            foundItemRepo = DataRepoModule.provideFoundItemRepo()
        )
        viewModel = ViewModelProvider(this, factory)[ReceiptViewModel::class.java]

        bindUi()
        observeVm()
    }

    private fun bindUi() {
        tvSummary.text = slip.summary

        if (!foundItem.photoUri.isNullOrEmpty() && settings.includePhoto) {
            imageView.setImageURI(Uri.parse(foundItem.photoUri))
            imageView.visibility = ImageView.VISIBLE
        } else {
            imageView.visibility = ImageView.GONE
        }

        btnShare.setOnClickListener {
            viewModel.requestShare(foundItem, slip, settings)
        }
    }

    private fun observeVm() {
        viewModel.shareIntentEvent.observe(this) { event ->
            event.getIfNotHandled()?.let { intent ->
                val chooser = Intent.createChooser(intent, "Share pickup slip")
                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareLauncher.launch(chooser)
            }
        }
    }
}
