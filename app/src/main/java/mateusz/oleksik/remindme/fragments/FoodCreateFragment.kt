package mateusz.oleksik.remindme.fragments

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
import mateusz.oleksik.remindme.databinding.FragmentCreateFoodBinding
import mateusz.oleksik.remindme.models.Food
import mateusz.oleksik.remindme.exceptions.OCRException
import mateusz.oleksik.remindme.interfaces.IFoodCreateDialogListener
import mateusz.oleksik.remindme.utils.Constants
import mateusz.oleksik.remindme.utils.DateUtils
import mateusz.oleksik.remindme.utils.Extensions.Companion.toShortDateString
import java.lang.Exception

class FoodCreateFragment(
    private val listener: IFoodCreateDialogListener
) : DialogFragment() {

    private lateinit var _dateRecognitionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var _barcodeRecognitionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var _binding: FragmentCreateFoodBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateFoodBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constants.CreateFoodRequiredPermissions,
                Constants.CreateFoodPermissionsRequestCode
            )
        }
        initializeBarcodeScanner()
        initializeDateScanner()

        _binding.foodCreateCalendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            calendarView.date = getDateFromInt(year, month, dayOfMonth)
        }
        _binding.foodCreateCancelButton.setOnClickListener {
            dismiss()
        }
        _binding.foodCreateConfirmButton.setOnClickListener {
            createFood()
        }
        _binding.foodCreateDateRecognitionButton.setOnClickListener {
            scanExpirationDateImage()
        }
        _binding.foodCreateBarcodeScanButton.setOnClickListener {
            scanBarcodeImage()
        }

        return _binding.root
    }

    private fun createFood() {
        if (_binding.foodCreateNameTextView.text.isEmpty()){
            Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val foodName = _binding.foodCreateNameTextView.text.toString()
        val expirationDate = _binding.foodCreateCalendarView.date

        listener.onCreatedFood(Food(0, foodName, expirationDate))
        dismiss()
    }

    private fun scanBarcodeImage() {
        if (allPermissionsGranted()) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                _barcodeRecognitionResultLauncher.launch(intent)
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

    private fun scanExpirationDateImage() {
        if (allPermissionsGranted()) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                _dateRecognitionResultLauncher.launch(intent)
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
        val regex = Constants.DateDetectionPatternRegex.toRegex()

        var matchResult: MatchResult?
        for (block in textBlocks) {
            matchResult = regex.find(block.text)

            if (matchResult != null) {
                setDateOnCalendar(matchResult.value)
                return
            }
        }

        throw OCRException("No date detected in the picture")
    }

    private fun setDateOnCalendar(dateString: String) {
        val dateResult = DateUtils.tryParseDate(dateString)

        if (dateResult != null) {
            _binding.foodCreateCalendarView.date = dateResult.time
            Toast.makeText(
                context,
                "Date extracted from picture: ${dateResult.time.toShortDateString()}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setFoodName(name: String) {
        _binding.foodCreateNameTextView.setText(name)
    }

    private fun sendAPICall(productBarcode: String) {
        val url = "https://world.openfoodfacts.org/api/v2/product/$productBarcode.json?fields=product_name"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val foodName = response.getJSONObject("product").getString("product_name")
                    setFoodName(foodName)
                } catch (ex: Exception) {
                    Log.d(Constants.DebugLogTag, "Parsing JSON failed: ${ex.message}")
                }
            },
            { error ->
                Log.d(Constants.DebugLogTag, "API call failed: ${error.message}")
                Toast.makeText(
                    context,
                    "Product with barcode $productBarcode not found. Try scanning again.",
                    Toast.LENGTH_LONG
                ).show()
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

        _dateRecognitionResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.extras
                    val bitmap = data?.get("data") as Bitmap
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
                                try {
                                    tryExtractDateFromString(visionText.textBlocks)
                                } catch (ex: Exception) {
                                    Toast.makeText(
                                        context,
                                        ex.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Text recognition failed: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
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

        _barcodeRecognitionResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.extras
                    val bitmap = data?.get("data") as Bitmap
                    val image = InputImage.fromBitmap(bitmap, 0)

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
                                    "Barcode scanning failed: ${ex.message}",
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

