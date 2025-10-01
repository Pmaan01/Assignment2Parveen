package com.android_assignments.assignment_2_parveen.ui.newfounditem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.android_assignments.assignment_2_parveen.R
import com.android_assignments.assignment_2_parveen.data.repo.DataRepoModule
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_ITEM
import com.android_assignments.assignment_2_parveen.util.IntentKeys.KEY_SETTINGS
import java.io.File

class NewFoundItemActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var buttonTakePhoto: Button
    private lateinit var buttonPickGallery: Button
    private lateinit var buttonReview: Button
    private lateinit var etName: EditText
    private lateinit var etFoundLocation: EditText
    private lateinit var etDescription: EditText
    private lateinit var etNotes: EditText

    private lateinit var takePhotoUri: Uri

    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.setPhotoUri(takePhotoUri.toString())
            } else {
                Toast.makeText(this, "Camera canceled", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.setPhotoUri(it.toString()) }
        }

    private lateinit var viewModel: NewFoundItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_found_item)

        imageView = findViewById(R.id.imageView)
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto)
        buttonPickGallery = findViewById(R.id.buttonPickGallery)
        buttonReview = findViewById(R.id.buttonReview)
        etName = findViewById(R.id.etName)
        etFoundLocation = findViewById(R.id.etFoundLocation)
        etDescription = findViewById(R.id.etDescription)
        etNotes = findViewById(R.id.etNotes)

        val factory = NewFoundItemViewModel.Factory(
            foundItemRepo = DataRepoModule.provideFoundItemRepo(),
            settingsRepo = DataRepoModule.provideSlipSettingsRepo(this)
        )
        viewModel = ViewModelProvider(this, factory)[NewFoundItemViewModel::class.java]

        wireUi()
        observeViewModel()
    }

    private fun wireUi() {
        buttonTakePhoto.setOnClickListener {
            val tmpFile = createImageFile()
            takePhotoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", tmpFile)
            takePictureLauncher.launch(takePhotoUri)
        }

        buttonPickGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        buttonReview.setOnClickListener {
            // includePhotoInSettings could come from a settings toggle; set true here
            viewModel.onReviewRequested(includePhotoInSettings = true)
        }

        etName.addTextChangedListener { viewModel.name.value = it?.toString().orEmpty() }
        etFoundLocation.addTextChangedListener { viewModel.foundLocation.value = it?.toString().orEmpty() }
        etDescription.addTextChangedListener { viewModel.description.value = it?.toString().orEmpty() }
        etNotes.addTextChangedListener { viewModel.notes.value = it?.toString().orEmpty() }
    }

    private fun observeViewModel() {
        viewModel.photoUriString.observe(this) { uriStr ->
            if (!uriStr.isNullOrEmpty()) imageView.setImageURI(Uri.parse(uriStr))
            else imageView.setImageResource(android.R.color.darker_gray)
        }

        viewModel.validation.observe(this) { v ->
            etName.error = v.nameError
            etFoundLocation.error = v.locationError
        }

        viewModel.navigateToReview.observe(this) { event ->
            event.getIfNotHandled()?.let { payload ->
                val intent = Intent(this, com.android_assignments.assignment_2_parveen.ui.review.ReviewItemActivity::class.java).apply {
                    putExtra(KEY_ITEM, payload.foundItem)
                    putExtra(KEY_SETTINGS, payload.settings)
                }
                startActivity(intent)
            }
        }
    }

    private fun createImageFile(): File {
        val imagesDir = File(cacheDir, "images").apply { if (!exists()) mkdirs() }
        return File.createTempFile("found_${System.currentTimeMillis()}", ".jpg", imagesDir)
    }
}
