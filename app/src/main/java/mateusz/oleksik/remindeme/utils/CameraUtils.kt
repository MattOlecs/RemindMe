package mateusz.oleksik.remindeme.utils

import android.Manifest
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.SparseIntArray
import android.view.Surface
import androidx.annotation.RequiresApi

class CameraUtils {

    companion object{

        private val ORIENTATIONS = SparseIntArray()

        val CAMERA_REQUEST_CODE_PERMISSIONS = 10
        val CAMERA_REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
            }.toTypedArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 0)
            ORIENTATIONS.append(Surface.ROTATION_90, 90)
            ORIENTATIONS.append(Surface.ROTATION_180, 180)
            ORIENTATIONS.append(Surface.ROTATION_270, 270)
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Throws(CameraAccessException::class)
        fun getRotationCompensation(activity: Activity, isFrontFacing: Boolean = false): Int {
            // Get the device's current rotation relative to its "native" orientation.
            // Then, from the ORIENTATIONS table, look up the angle the image must be
            // rotated to compensate for the device's rotation.
            val deviceRotation = activity.windowManager.defaultDisplay.rotation
            var rotationCompensation = ORIENTATIONS.get(deviceRotation)



            // Get the device's sensor orientation.
            var cameraID: String = ""
            val cameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager
            for (id in cameraManager.cameraIdList){
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val cameraPosition = characteristics.get(CameraCharacteristics.LENS_FACING)

                if(cameraPosition == CameraCharacteristics.LENS_FACING_BACK){
                    cameraID = id
                    break
                }
            }

            val sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraID)
                .get(CameraCharacteristics.SENSOR_ORIENTATION)!!

            if (isFrontFacing) {
                rotationCompensation = (sensorOrientation + rotationCompensation) % 360
            } else { // back-facing
                rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360
            }
            return rotationCompensation
        }
    }
}