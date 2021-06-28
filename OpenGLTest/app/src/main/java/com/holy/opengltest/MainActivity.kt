package com.holy.opengltest

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glSurfaceView = findViewById(R.id.gl_surface_view)
        glSurfaceView?.setRenderer(GameRenderer(this))
        glSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

}