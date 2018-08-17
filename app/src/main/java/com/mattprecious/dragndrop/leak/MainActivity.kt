package com.mattprecious.dragndrop.leak

import android.annotation.SuppressLint
import android.content.ClipData
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
  private lateinit var container: ViewGroup

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    StrictMode.setVmPolicy(
        VmPolicy.Builder()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .penaltyDeath()
            .build()
    )

    container = LinearLayout(this).apply { gravity = Gravity.CENTER }
    setContentView(container)

    val dragView = Button(this).apply { text = "Drag Me Anywhere" }
    container.addView(dragView)

    dragView.setOnTouchListener { v, event ->
      if (event.action == MotionEvent.ACTION_DOWN) startDrag(v)
      return@setOnTouchListener false
    }

    container.setOnDragListener { _, event ->
      if (event.action == DragEvent.ACTION_DRAG_ENDED) endDrag()
      return@setOnDragListener true
    }
  }

  private fun startDrag(v: View) {
    v.startDragAndDrop(ClipData.newPlainText("test", "test"), DragShadowBuilder(v), null, 0)
  }

  private fun endDrag() {
    container.removeAllViews()

    // Force GC of the previous view for reliable reproduction.
    Runtime.getRuntime().gc()
  }
}
