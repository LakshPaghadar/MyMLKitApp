package com.laksh.mydocscannerapp.utils

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

sealed class CanvasCommand {
  data class DrawRect(val color: Color, val rect: Rect) : CanvasCommand()
  data class DrawFaceHighlight(val rect: Rect) : CanvasCommand()
  // ... other commands for your specific drawing needs
}