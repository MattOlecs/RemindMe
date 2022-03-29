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
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.databinding.FragmentCreateFoodBinding
import mateusz.oleksik.remindeme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindeme.utils.CameraUtils
import mateusz.oleksik.remindeme.utils.DateUtils
import mateusz.oleksik.remindeme.utils.Extensions.Companion.toShortDateString

class FoodCreateFragment(
    private val listener: IFoodCreateDialogListener
) : DialogFragment() {

    private lateinit var binding: FragmentCreateFoodBinding
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var dateRecognitionResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data?.extras
                val bitmap = data?.get("data") as Bitmap
                val camComp = CameraUtils.getRotationCompensation(requireActivity())
                val image = InputImage.fromBitmap(bitmap, 0)


                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (visionText.textBlocks.isEmpty()) {
                            Toast.makeText(
                                context,
                                "No text found in the picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            tryExtractDateFromString(visionText.textBlocks)
                        }
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

    private var barcodeRecognitionResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data?.extras
                val bitmap = data?.get("data") as Bitmap
                val camComp = CameraUtils.getRotationCompensation(requireActivity())
                val image = InputImage.fromBitmap(bitmap, camComp)

                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_ALL_FORMATS
                    )
                    .build()

                val scanner = BarcodeScanning.getClient(options)

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->

                        for (bar in barcodes) {
                            Toast.makeText(
                                context,
                                "${bar.rawValue}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "No barcode found",
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
        binding.foodCreateBarecodeButton.setOnClickListener {
            scanBarcodeImage()
        }

        return binding.root
    }

    private fun getDateFromInt(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time.time
    }

    private fun tryExtractDateFromString(textBlocks: MutableList<Text.TextBlock>) {
        val regex =
            """(0?[1-9]|[12][0-9]|3[01])[- /.:](0?[1-9]|1[012])[- /.:](19|20)\d\d""".toRegex()

        var matchResult: MatchResult? = null
        for (block in textBlocks){
            matchResult = regex.find(block.text)

            if (matchResult != null){
                setDateOnCalendar(matchResult.value)
                return@tryExtractDateFromString
            }
        }

        Toast.makeText(
            context,
            "No date detected in the picture",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setDateOnCalendar(dateString: String) {
        val dateResult = DateUtils.tryParseDate(dateString)

        if (dateResult != null) {
            binding.foodCreateCalendarView.date = dateResult.time
            Toast.makeText(
                context,
                "Date extracted from picture: ${dateResult.time.toShortDateString()}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getExpirationDateFromImage() {

        if (allPermissionsGranted()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            dateRecognitionResultLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                CameraUtils.CAMERA_REQUIRED_PERMISSIONS,
                CameraUtils.CAMERA_REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun scanBarcodeImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        barcodeRecognitionResultLauncher.launch(intent)
    }

    private fun allPermissionsGranted() = CameraUtils.CAMERA_REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

