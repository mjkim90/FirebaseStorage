package km.project.firebaseapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.Gravity
import android.widget.Toast

object Util {

    fun toast(context: Context, body: String?) {
        Toast.makeText(context, body, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER, 0, 0)
        }.show()
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}