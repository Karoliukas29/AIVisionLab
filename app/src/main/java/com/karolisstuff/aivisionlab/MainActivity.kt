package com.karolisstuff.aivisionlab

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.karolisstuff.aivisionlab.ui.theme.AIVisionLabTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIVisionLabTheme {
                // Jetpack Compose UI content
                ImageAndFaceDetectionScreen(viewModel = viewModel, this)
            }
        }
    }
}
@Composable
fun ImageAndFaceDetectionScreen(viewModel: MainViewModel, context: Context) {
    val labels by viewModel.labels.collectAsState()
    val faceAttributes by viewModel.faceAttributes.collectAsState() // Collect face attributes

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var detectFaces by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            if (detectFaces) {
                viewModel.detectFaces(context, it)
            } else {
                viewModel.processImage(context, it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { detectFaces = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!detectFaces) Color.Blue else Color.Gray
                )
            ) {
                Text("Label Image", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { detectFaces = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (detectFaces) Color.Blue else Color.Gray
                )
            ) {
                Text("Detect Faces", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (detectFaces) "Face detection mode is active" else "Image labeling mode is active",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { pickImageLauncher.launch("image/*") }) {
            Text("Pick an Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display detected face attributes or labels
        if (detectFaces && faceAttributes.isNotEmpty()) {
            Text("Detected Faces:", style = MaterialTheme.typography.titleMedium)
            faceAttributes.forEach { attribute ->
                Text(attribute, style = MaterialTheme.typography.bodyLarge)
            }
        } else if (!detectFaces && labels.isNotEmpty()) {
            Text("Detected Labels:", style = MaterialTheme.typography.titleMedium)
            labels.forEach { label ->
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Text("No data detected", color = Color.Gray)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ImageAndFaceDetectionScreenPreview() {
    AIVisionLabTheme {
        ImageAndFaceDetectionScreen(viewModel = MainViewModel(), context = LocalContext.current)
    }
}