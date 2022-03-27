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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.databinding.FragmentCreateFoodBinding
import mateusz.oleksik.remindeme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindeme.utils.CameraUtils

class FoodCreateFragment(
    private val listener: IFoodCreateDialogListener
) : DialogFragment() {

    private lateinit var binding: FragmentCreateFoodBinding
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data?.extras
                val bitmap = data?.get("data") as Bitmap
                val camComp = CameraUtils.getRotationCompensation(requireActivity())
                val image = InputImage.fromBitmap(bitmap, 0)

                binding.imageView.setImageBitmap(bitmap)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val resultText = visionText.textBlocks[0].text
                        binding.foodCreateNameTextView.setText(resultText)
                        Toast.makeText(context, "Text recognition successful", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Text recognition failed: $camComp. ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateFoodBinding.inflate(inflater, container, false)

        binding.foodCreateCalendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            calendarView.date = getDateFromInt(year, month, dayOfMonth)
        }
        binding.foodCreateCancelButton.setOnClickListener {
            dismiss()
        }
        binding.foodCreateConfirmButton.setOnClickListener {
            val foodName = binding.foodCreateNameTextView.text.toString()
            val expirationDate = binding.foodCreateCalendarView.date

            listener.onCreatedFood(Food(0, foodName, expirationDate))
            dismiss()
        }
        binding.foodCreateCameraButton.setOnClickListener {
            getExpirationDateFromImage()
        }

        return binding.root
    }

    private fun getDateFromInt(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        return calendar.time.time
    }

    private fun getExpirationDateFromImage() {

        if (allPermissionsGranted()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                CameraUtils.CAMERA_REQUIRED_PERMISSIONS,
                CameraUtils.CAMERA_REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = CameraUtils.CAMERA_REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }
}