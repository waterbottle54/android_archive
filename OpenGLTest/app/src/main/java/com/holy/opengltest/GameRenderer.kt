package com.holy.opengltest

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GameRenderer(private val context: Context): GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        gl?.glEnable(GL10.GL_DEPTH_TEST)
        gl?.glEnable(GL10.GL_TEXTURE_2D)
        gl?.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl?.glClearColor(0.529f, 0.808f, 0.922f, 1.0f)

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wood)
        val textures = IntArray(1)
        gl?.glGenTextures(1, textures, 0);
        gl?.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl?.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl?.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        gl?.glViewport(0, 0, width, height)

        gl?.glMatrixMode(GL10.GL_PROJECTION)
        gl?.glLoadIdentity()
        GLU.gluPerspective(gl, 60f, width.toFloat() / height, 0.1f, 100f)
    }

    override fun onDrawFrame(gl: GL10?) {

        gl?.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl?.glMatrixMode(GL10.GL_MODELVIEW)
        gl?.glLoadIdentity()
        GLU.gluLookAt(
            gl,
            0f, 5f, -3f,
            0f, 0f, -0.5f,
            0f, 1f, 0f
        )

        gl?.glColor4f(0f, 0f, 0f, 1f)
        gl?.glVertexPointer(3, GL10.GL_FLOAT, 0, getBoardBuffer())
        gl?.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl?.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)

        gl?.glColor4f(1f, 0.3f, 0.3f, 1f)
        for (row in 0..4) {
            for (col in 0..4) {
                gl?.glVertexPointer(3, GL10.GL_FLOAT, 0, getCellBuffer(row, col))
                gl?.glTexCoordPointer(2, GL10.GL_FLOAT, 0, getRectTextureBuffer());

                val indexBuffer = getRectIndexBuffer()
                gl?.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
                gl?.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
                    GL10.GL_UNSIGNED_BYTE, indexBuffer);
            }
        }
    }

    private fun getRectBuffer(left: Float, top: Float, right: Float, bottom: Float, height: Float) : FloatBuffer {

        val vertices = floatArrayOf(
            left, height, top,
            left, height, bottom,
            right, height, top,
            right, height, bottom,
        )

        val byteBuffer = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES)
        byteBuffer.order(ByteOrder.nativeOrder())

        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(vertices)
        floatBuffer.position(0)

        return floatBuffer
    }

    private fun getRectIndexBuffer() : ByteBuffer {

        val indices = byteArrayOf(
            0, 1, 2, 3,
        )

        val byteBuffer = ByteBuffer.allocateDirect(indices.size)
        byteBuffer.put(indices)
        byteBuffer.position(0)

        return byteBuffer
    }

    private fun getRectTextureBuffer() : FloatBuffer {

        val texts = floatArrayOf(
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f
        )

        val byteBuffer = ByteBuffer.allocateDirect(texts.size * Float.SIZE_BYTES)
        byteBuffer.order(ByteOrder.nativeOrder())

        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(texts)
        floatBuffer.position(0)

        return floatBuffer
    }

    private fun getCellBuffer(row: Int, col: Int) : FloatBuffer {

        val left = col.toFloat() - 2.5f;
        val bottom = row.toFloat() - 2.5f;
        val top = bottom + 0.95f;
        val right = left + 0.95f;

        return getRectBuffer(left, top, right, bottom, 0f)
    }

    private fun getBoardBuffer() : FloatBuffer {

        val left = -2.55f
        val bottom = -2.55f
        val top = 2.5f
        val right = 2.5f

        return getRectBuffer(left, top, right, bottom, -0.01f)
    }

}