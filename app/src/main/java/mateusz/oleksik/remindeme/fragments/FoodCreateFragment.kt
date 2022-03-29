package mateusz.oleksik.remindeme.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import mateusz.oleksik.remindeme.models.Food
import mateusz.oleksik.remindeme.databinding.FragmentCreateFoodBinding
import mateusz.oleksik.remindeme.exceptions.OCRException
import mateusz.oleksik.remindeme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindeme.utils.CameraUtils
import mateusz.oleksik.remindeme.utils.Constants
import mateusz.oleksik.remindeme.utils.DateUtils
import mateusz.oleksik.remindeme.utils.Extensions.Companion.toShortDateString
import java.lang.Exception

class FoodCreateFragment(
    private val listener: IFoodCreateDialogListener
) : DialogFragment() {

    private lateinit var dateRecognitionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var barcodeRecognitionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentCreateFoodBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateFoodBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constants.CreateFoodRequiredPermissions,
                Constants.CreateFoodPermissionsRequestCode
            )
        }
        initializeBarcodeScanner()
        initializeDateScanner()

        binding.foodCreateCalendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            calendarView.date = getDateFromInt(year, month, dayOfMonth)
        }
        binding.foodCreateCancelButton.setOnClickListener {
            dismiss()
        }
        binding.foodCreateConfirmButton.setOnClickListener {
            createFood()
        }
        binding.foodCreateCameraButton.setOnClickListener {
            getExpirationDateFromImage()
        }
        binding.foodCreateBarecodeButton.setOnClickListener {
            scanBarcodeImage()
        }

        return binding.root
    }

    private fun createFood() {
        val foodName = binding.foodCreateNameTextView.text.toString()
        val expirationDate = binding.foodCreateCalendarView.date

        listener.onCreatedFood(Food(0, foodName, expirationDate))
        dismiss()
    }

    private fun scanBarcodeImage() {
        if (allPermissionsGranted()) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                barcodeRecognitionResultLauncher.launch(intent)
            } catch (ex: OCRException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constants.CreateFoodRequiredPermissions,
                Constants.CreateFoodPermissionsRequestCode
            )
        }
    }

    private fun getExpirationDateFromImage() {
        if (allPermissionsGranted()) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                dateRecognitionResultLauncher.launch(intent)
            } catch (ex: OCRException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constants.CreateFoodRequiredPermissions,
                Constants.CreateFoodPermissionsRequestCode
            )
        }
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
        for (block in textBlocks) {
            matchResult = regex.find(block.text)

            if (matchResult != null) {
                setDateOnCalendar(matchResult.value)
                return@tryExtractDateFromString
            }
        }

        throw OCRException("No date detected in the picture")
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

    private fun setFoodName(name: String) {
        binding.foodCreateNameTextView.setText(name)
    }

    private fun sendAPICall(productBarcode: String) {
        val url = "https://world.openfoodfacts.org/api/v0/product/$productBarcode.json"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val foodName = response.getJSONObject("product").getString("product_name")
                setFoodName(foodName)
            },
            { error ->
                Log.d(Constants.DebugLogTag, "API call failed: ${error.message}")
                Toast.makeText(context, "API call unsuccessful", Toast.LENGTH_LONG).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }

    private fun allPermissionsGranted() = Constants.CreateFoodRequiredPermissions.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initializeDateScanner() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        dateRecognitionResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val data = result.data?.extras
                    val bitmap = data?.get("data") as Bitmap
                    val camComp = CameraUtils.getRotationCompensation(requireActivity())
                    val image = InputImage.fromBitmap(bitmap, camComp)

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            try {
                                if (visionText.textBlocks.isNotEmpty()) {
                                    tryExtractDateFromString(visionText.textBlocks)
                                } else {
                                    throw OCRException("No text detected in the picture")
                                }
                            } catch (ex: Exception) {
                                Toast.makeText(
                                    context,
                                    "Text recognition failed ${ex.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { ex ->
                            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun initializeBarcodeScanner() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)

        barcodeRecognitionResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.extras
                    val bitmap = data?.get("data") as Bitmap
                    val camComp = CameraUtils.getRotationCompensation(requireActivity())
                    val image = InputImage.fromBitmap(bitmap, camComp)


                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            try {
                                if (barcodes.isNotEmpty()) {
                                    sendAPICall(barcodes[0].rawValue.toString())
                                } else {
                                    throw OCRException("No barcodes detected in the picture")
                                }
                            } catch (ex: Exception) {
                                Toast.makeText(
                                    context,
                                    "Text recognition failed ${ex.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { ex ->
                            Toast.makeText(
                                context,
                                "Barcode scanning failed: ${ex.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }
}

