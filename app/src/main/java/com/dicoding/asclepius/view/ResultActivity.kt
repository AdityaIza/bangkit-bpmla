package com.dicoding.asclepius.view

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)


        if (imageUri != null) {
            val imageUriParse = Uri.parse(imageUri.toString())
            displayImage(imageUriParse)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMsg: String) {
                        Log.d(ContentValues.TAG, "Error: $errorMsg")
                        runOnUiThread {
                            Toast.makeText(this@ResultActivity, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { showResults(it) }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(imageUriParse)
        } else {
            Log.e(ContentValues.TAG, "No image provided")
            finish()
        }
    }

    private fun showResults(results: List<Classifications>) {
        val topResult = results[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resultText.text = "$label ${score.formatToString()}"
    }

    private fun displayImage(uri: Uri) {
        Log.d(ContentValues.TAG, "Displaying image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT_LABEL = "extra_result_label"
        const val EXTRA_RESULT_SCORE = "extra_result_score"
    }


}