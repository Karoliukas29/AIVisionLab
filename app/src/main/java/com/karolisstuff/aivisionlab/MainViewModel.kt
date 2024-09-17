package com.karolisstuff.aivisionlab

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _labels = MutableStateFlow<List<String>>(emptyList())// state for labelling
    val labels: StateFlow<List<String>> = _labels

    // Store a list of face attributes (smile probability, eye openness)
    private val _faceAttributes = MutableStateFlow<List<String>>(emptyList()) // store face attributes
    val faceAttributes: StateFlow<List<String>> = _faceAttributes

    // Method for Image Labeling
    fun processImage(context: Context, imageUri: Uri) {
        val image = InputImage.fromFilePath(context, imageUri)
        val labeler: ImageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val labelTexts = labels.map { it.text }
                _labels.value = labelTexts
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    // Face detection process
    fun detectFaces(context: Context, imageUri: Uri) {
        val image = InputImage.fromFilePath(context, imageUri)

        // Configure the face detector to detect landmarks and face contours
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            // Needed for smile and eye detection
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)

            .build()

        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                val attributes = faces.map { face ->
                    val smileProb = face.smilingProbability ?: -1f
                    val leftEyeOpenProb = face.leftEyeOpenProbability ?: -1f
                    val rightEyeOpenProb = face.rightEyeOpenProbability ?: -1f

                    //description of facial attributes
                    val smileStatus = if (smileProb > 0.5) "Smiling" else "Not Smiling"
                    val leftEyeStatus = if (leftEyeOpenProb > 0.5) "Left Eye Open" else "Left Eye Closed"
                    val rightEyeStatus = if (rightEyeOpenProb > 0.5) "Right Eye Open" else "Right Eye Closed"

                    // Return a string with the face's attributes
                    "Face detected: $smileStatus, $leftEyeStatus, $rightEyeStatus"
                }
                _faceAttributes.value = attributes // Assign the face attributes to _faceAttributes
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
