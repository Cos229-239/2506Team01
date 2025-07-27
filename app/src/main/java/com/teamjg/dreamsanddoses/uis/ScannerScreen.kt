package com.teamjg.dreamsanddoses.uis

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.AnimatedScreenWrapper
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Scanner screen will use CameraX and ML Kit to scan the text from images.
 * This will also allow users to categorize the scanned text.
 */
@Composable
fun ScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    //State to track camera perms
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val window = (context as? ComponentActivity)?.window
    val view = LocalView.current

    // For proper contrast
    val statusBarColor = Color.LightGray
    val useDarkIcons = statusBarColor.luminance() > 0.5

    SideEffect {
        window?.statusBarColor = statusBarColor.toArgb()
        WindowCompat.getInsetsController(window!!, view).isAppearanceLightStatusBars = useDarkIcons
    }

    //Show scan results
    var scanResult by remember {mutableStateOf<String?>(null)}
    var isScanning by remember {mutableStateOf(false)}

    //Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted }

    //Request camera perms on first load, if not already granted
    LaunchedEffect(Unit) {
        if(!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    AnimatedScreenWrapper(navController = navController) {

        Scaffold(
            modifier = Modifier.background(Color.LightGray),
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Files, // Using Files type since scanner is part of file management
                    navController = navController,
                    useIconHeader = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            },
            bottomBar = {
                BottomNavigationBar(
                    type = NavigationBarType.Files,
                    navController = navController,
                )
            }
        ) { innerPadding ->
            if (!hasCameraPermission) {
                // Show permission request UI
                PermissionRequestContent(
                    onRequestPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                // Show camera scanner
                CameraScannerContent(
                    onTextScanned = { text: String ->
                        if (text.isNotBlank()) {
                            scanResult = text
                            isScanning = false
                            Log.d("Scanner", "Scanned text: $text")
                        }
                    },
                    onScanningStateChanged = { scanning: Boolean ->
                        isScanning = scanning
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            // Show scan result overlay if we have results
            scanResult?.let { result ->
                ScanResultScreen(
                    scannedText = result,
                    onDismiss = { scanResult = null },
                    onSaveContent = { text, type, title ->
                        // UPDATED: Now saves to Firestore based on type
                        Log.d("Scanner", "Saving as $type with title '$title': $text")

                        // Get current user ID (you may need to implement proper user management)
                        val userId = "current_user" // TODO: Replace with actual user ID from authentication

                        // Save to Firestore
                        FirestoreService.saveScannedContent(
                            userID = userId,
                            title = title,
                            content = text,
                            type = type,
                            onSuccess = {
                                Log.d("Scanner", "Successfully saved $type to Firestore")
                                scanResult = null
                                navController.popBackStack()
                            },
                            onFailure = { exception ->
                                Log.e("Scanner", "Failed to save to Firestore", exception)
                                // Still dismiss for now, but you could show an error message
                                scanResult = null
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        }
    }
}

/**
 * Content shown when camera permission is not granted
 */
@Composable
fun PermissionRequestContent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_camera_icon),
            contentDescription = "Camera",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "To scan documents and medications, please grant camera permission.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRequestPermission) {
            Text("Grant Camera Permission")
        }
    }
}

/**
 * Main scanner content with text recognition
 */
@Composable
fun CameraScannerContent(
    onTextScanned: (String) -> Unit,
    onScanningStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    //Camera executor for background processing
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    //Camera preview view
    val previewView = remember { PreviewView(context) }


    //Set up camera when this composable is first created.
    LaunchedEffect(previewView) {
        setupCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            onTextScanned = onTextScanned,
            onScanningStateChanged = onScanningStateChanged,
            cameraExecutor = cameraExecutor
        )
    }

    //Clean up camera executor when composable is done.
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    Box(modifier = modifier.fillMaxSize().background(Color.LightGray)) {
        //Camera preview takes full screen
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        //Overlay with the instructions
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            //Top instructions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Text(
                    text = "Point your camera at the text to scan",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            //Bottom instructions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ){
                Text(
                    text = "Hold steady and ensure text is clearly visible. Scanning will happen automatically once the text is detected.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Sets up the camera with ML Kit text recognition
 * This function handles the complex CameraX setup and ML Kit integration
 */
@androidx.camera.core.ExperimentalGetImage
private fun setupCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onTextScanned: (String) -> Unit,
    onScanningStateChanged: (Boolean) -> Unit,
    cameraExecutor: ExecutorService
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            // Create preview use case
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Create image analysis use case for ML Kit
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Set up ML Kit text recognizer
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            // Set the analyzer for the image analysis use case
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    // Process the image with ML Kit
                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            // Extract text from the result
                            val resultText = visionText.text
                            if (resultText.isNotBlank()) {
                                Log.d("MLKit", "Detected text: $resultText")
                                onTextScanned(resultText)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("MLKit", "Text recognition failed", e)
                        }
                        .addOnCompleteListener {
                            // Important: close the image proxy to prevent memory leaks
                            imageProxy.close()
                        }
                }
            }

            // Camera selector (use back camera)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                Log.d("CameraX", "Camera setup successful")
                onScanningStateChanged(true)

            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }

        } catch (exc: Exception) {
            Log.e("CameraX", "Camera initialization failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

/**
 * Screen shown when scan results are available
 * This is where users can verify the text and choose how to save it
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    scannedText: String,
    onDismiss: () -> Unit,
    onSaveContent: (String, String, String) -> Unit
) {
    var selectedType by remember { mutableStateOf("Note") }
    var title by remember { mutableStateOf("") }
    var editedText by remember { mutableStateOf(scannedText) }

    val typeOptions = listOf("Medication", "Journal Entry", "Note")

    // Full screen overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scan Results",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(111.dp))

            // Type selection
            Text(
                text = "What type of content is this?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Radio buttons for content type
            typeOptions.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = type)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("Enter a title for this item") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scanned text (editable)
            Text(
                text = "Scanned Text (you can edit this):",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = Int.MAX_VALUE
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSaveContent(editedText, selectedType, title)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}