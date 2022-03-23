package mateusz.oleksik.remindeme.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.R
import mateusz.oleksik.remindeme.interfaces.FoodCreateDialogListener
import mateusz.oleksik.remindeme.utils.CameraUtils
import java.sql.Date
import java.time.LocalDate

class FoodCreateFragment(
    private val listener: FoodCreateDialogListener
    ) : DialogFragment() {

    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private val REQUEST_CODE_PERMISSIONS = 10

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data = result.data?.extras
            val bitmap = data?.get("data") as Bitmap
            val camComp = CameraUtils.getRotationCompensation(requireActivity())
            val image = InputImage.fromBitmap(bitmap, 0)
            imageView.setImageBitmap(bitmap)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    var resultText = visionText.textBlocks[0].text
                    nameTextView.text = resultText
                    Toast.makeText(context,"Text recognition succesful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context,"Nie udało się! Camera compensation: $camComp. ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_create_food, container, false)

        val cancelButton = view.findViewById<Button>(R.id.food_create_cancel_button)
        val createButton = view.findViewById<Button>(R.id.food_create_confirm_button)
        nameTextView = view.findViewById<TextView>(R.id.food_create_name_text_view)
        val calendarView = view.findViewById<CalendarView>(R.id.food_create_calendar_view)
        val cameraButton = view.findViewById<FloatingActionButton>(R.id.food_create_camera_button)
        imageView = view.findViewById<ImageView>(R.id.imageView)

        calendarView.setOnDateChangeListener{calendarView, year, month, dayOfMonth ->

            calendarView.date = getDateFromInt(year, month, dayOfMonth)
        }

        cancelButton.setOnClickListener{
            dismiss()
        }

        createButton.setOnClickListener{
            val foodName = nameTextView.text.toString()
            val expirationDate = calendarView.date


            listener.onCreatedFood(Food(0, foodName, expirationDate))
            dismiss()
        }

        cameraButton.setOnClickListener{
            getExpirationDateFromImage()
        }

        return view
    }

    private fun getDateFromInt(year: Int, month: Int, dayOfMonth: Int) : Long{
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        return calendar.time.time
    }

    private fun getExpirationDateFromImage(){

        if (allPermissionsGranted()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), CameraUtils.REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted() = CameraUtils.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
}