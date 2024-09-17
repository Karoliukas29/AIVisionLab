package com.example.mlkitapp

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
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

    private val _faces = MutableStateFlow<List<String>>(emptyList()) // state for face detection
    val faces: StateFlow<List<String>> = _faces

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

    // method for Face Detection
    fun detectFaces(context: Context, imageUri: Uri) {
        val image = InputImage.fromFilePath(context, imageUri)

        // Configure face detector with options
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking() // Optional if you want to track faces
            .build()

        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                val faceDescriptions = faces.map { face ->
                    val boundingBox = face.boundingBox
                    "Face detected with bounds: $boundingBox"
                }
                _faces.value = faceDescriptions
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
