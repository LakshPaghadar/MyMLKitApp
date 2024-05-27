

# ML Kit Integration in Jetpack Compose


This app demonstrates the integration of ML Kit in a Jetpack Compose Android application. The app includes the following features:

1. **Face Detection**
2. **Text Recognition**
3. **Barcode Scanning**
4. **Image Labeling**

## Features


### 1. Jetpack Compose for UI
Jetpack Compose is utilized for building the user interface, offering a modern, declarative approach to UI development.

### 2. Camera Preview and Image Capture
CameraX is used for camera preview and image capture, providing a seamless and modern camera experience within the app.

### 3. Face Detection
The app uses ML Kit's Face Detection API to identify faces in camera. It provides information such as position, orientation, and landmarks (e.g., eyes, nose, mouth).

### 4. Text Recognition
Using ML Kit's Text Recognition API, the app can recognize and extract text from images. This feature is useful for digitizing documents, reading signs, and more.

### 5. Barcode Scanning
The Barcode Scanning feature allows the app to scan various types of barcodes, including QR codes, and extract useful information from them.

### 6. Image Labeling
ML Kit's Image Labeling API is used to identify objects in images and assign labels to them. This feature can recognize common items like animals, plants, and more.

## Getting Started

### Prerequisites
- Android Studio Bumblebee | 2021.1.1 Patch 3 or later
- Kotlin 1.5.30 or later
- Jetpack Compose 1.0.5 or later