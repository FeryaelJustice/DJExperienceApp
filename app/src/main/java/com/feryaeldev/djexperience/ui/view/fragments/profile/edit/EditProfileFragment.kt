package com.feryaeldev.djexperience.ui.view.fragments.profile.edit

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.data.model.enums.Category
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.util.checkPermissions
import com.feryaeldev.djexperience.util.countryIsValid
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class EditProfileFragment : BaseFragment() {

    private lateinit var user: User
    private lateinit var progressCircle: FragmentContainerView
    private lateinit var profileDataLayout: LinearLayoutCompat
    private var selectedCategory = ""
    private val categories = Category.values()
    private var autoCompleteTextViewInputType = 0

    private lateinit var uri: Uri

    private var counterPermissions = 0
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    /* External Library
    private lateinit var galleryCropResultLauncher: ActivityResultLauncher<Any?>
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
     override fun createIntent(context: Context, input: Any?): Intent {
         CropImage.activity().setAspectRatio(16,9).getIntent(this@EditProfileFragment)
     }

     override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
         return CropImage.getActivityResult(intent)?.uri
     }

     override fun getSynchronousResult(context: Context, input: Any?): SynchronousResult<Uri?>? {
         return super.getSynchronousResult(context, input)
     }
    }*/
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Uri>
    private lateinit var requestMultiplePermissionLauncher: ActivityResultLauncher<Array<String>>

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        user = User()

        profileDataLayout = view.findViewById(R.id.editprofile_data)
        progressCircle = view.findViewById(R.id.editprofile_fragmentProgressBar)

        profileDataLayout.visibility = View.GONE
        progressCircle.visibility = View.VISIBLE

        val image: ImageView = view.findViewById(R.id.editprofile_photo)
        val name: TextInputEditText = view.findViewById(R.id.editprofile_name)
        val surnames: TextInputEditText = view.findViewById(R.id.editprofile_surnames)
        val country: AppCompatAutoCompleteTextView =
            view.findViewById(R.id.editprofile_country)
        val age: TextInputEditText = view.findViewById(R.id.editprofile_age)
        val website: TextInputEditText = view.findViewById(R.id.editprofile_website)
        val categoryField: Spinner = view.findViewById(R.id.editprofile_category_sp)

        autoCompleteTextViewInputType = country.inputType // Save input type

        // Country AutoCompleteTextView
        //disableAutoCompleteTextTextView(country)

        // Get countries
        val countryList = arrayListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val locales = Locale.getAvailableLocales()
            locales.forEach {
                countryList.add(it.displayName)
            }
            countryList.sort()
            activity?.runOnUiThread {
                val autoCompleteTextViewCountryAdapter =
                    ArrayAdapter(
                        view.context,
                        android.R.layout.simple_dropdown_item_1line,
                        countryList
                    )
                autoCompleteTextViewCountryAdapter.setNotifyOnChange(true)
                country.setAdapter(autoCompleteTextViewCountryAdapter)
            }
        }

        // User type Category Spinner

        //val userListTypes = resources.getStringArray(R.array.user_categories)
        val userListTypes = arrayListOf<String>()
        categories.forEach {
            userListTypes.add(it.name)
        }
        val adapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, userListTypes)
        categoryField.adapter = adapter
        categoryField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = userListTypes[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d("todo", p0.toString())
            }
        }

        // Get current user or artist (we dont know if its user or artist here)
        val db = Firebase.firestore
        val userOrArtistID = Firebase.auth.currentUser?.uid

        val userDocRef = userOrArtistID?.let { db.collection("users").document(it) }
        val artistDocRef = userOrArtistID?.let { db.collection("artists").document(it) }
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/$userOrArtistID.jpg")

        // Search for the user or artist
        userDocRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                if (document.data?.size != null) {
                    // Get current user to edit
                    user = User(
                        document.data?.get("id").toString(),
                        document.data?.get("name").toString(),
                        document.data?.get("surnames").toString(),
                        document.data?.get("username").toString(),
                        document.data?.get("email").toString(),
                        document.data?.get("country").toString(),
                        document.data?.get("category").toString(),
                        document.data?.get("age").toString().toLongOrNull(),
                        document.data?.get("website").toString(),
                        document.data?.get("following") as ArrayList<String>?
                    )

                    name.setText(user.name)
                    surnames.setText(user.surnames)
                    country.setText(user.country)
                    age.setText(user.age.toString())
                    website.setText(user.website)
                    selectedCategory = user.category.toString()
                    if (user.category == Category.User.name) {
                        categoryField.setSelection(1)
                    } else {
                        categoryField.setSelection(0)
                    }

                    progressCircle.visibility = View.GONE
                    profileDataLayout.visibility = View.VISIBLE
                } else {
                    Log.d("error", "User no such document")

                    artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                        if (documentArtist != null) {
                            // Get current artist to edit
                            user = User(
                                documentArtist.data?.get("id").toString(),
                                documentArtist.data?.get("name").toString(),
                                documentArtist.data?.get("surnames").toString(),
                                documentArtist.data?.get("username").toString(),
                                documentArtist.data?.get("email").toString(),
                                documentArtist.data?.get("country").toString(),
                                documentArtist.data?.get("category").toString(),
                                documentArtist.data?.get("age").toString().toLongOrNull(),
                                documentArtist.data?.get("website").toString(),
                                documentArtist.data?.get("following") as ArrayList<String>?
                            )

                            name.setText(user.name)
                            surnames.setText(user.surnames)
                            country.setText(user.country)
                            age.setText(user.age.toString())
                            website.setText(user.website)
                            selectedCategory = user.category.toString()
                            if (user.category == Category.User.name) {
                                categoryField.setSelection(1)
                            } else {
                                categoryField.setSelection(0)
                            }

                            progressCircle.visibility = View.GONE
                            profileDataLayout.visibility = View.VISIBLE
                        } else {
                            Log.d("error", "Artist no such document")
                        }
                    }?.addOnFailureListener { exceptionArtist ->
                        Log.d("error", "Artist get failed with ", exceptionArtist)
                    }
                }
            } else {
                Log.d("error", "User no such document")

                artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                    if (documentArtist != null) {
                        // Get current artist to edit
                        user = User(
                            documentArtist.data?.get("id").toString(),
                            documentArtist.data?.get("name").toString(),
                            documentArtist.data?.get("surnames").toString(),
                            documentArtist.data?.get("username").toString(),
                            documentArtist.data?.get("email").toString(),
                            documentArtist.data?.get("country").toString(),
                            documentArtist.data?.get("category").toString(),
                            documentArtist.data?.get("age").toString().toLongOrNull(),
                            documentArtist.data?.get("website").toString(),
                            documentArtist.data?.get("following") as ArrayList<String>?
                        )

                        name.setText(user.name)
                        surnames.setText(user.surnames)
                        country.setText(user.country)
                        age.setText(user.age.toString())
                        website.setText(user.website)
                        selectedCategory = user.category.toString()
                        if (user.category == Category.User.name) {
                            categoryField.setSelection(1)
                        } else {
                            categoryField.setSelection(0)
                        }

                        progressCircle.visibility = View.GONE
                        profileDataLayout.visibility = View.VISIBLE
                    } else {
                        Log.d("error", "Artist no such document")
                    }
                }?.addOnFailureListener { exceptionArtist ->
                    Log.d("error", "Artist get failed with ", exceptionArtist)
                }
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "User get failed with ", exception)

            artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                if (documentArtist != null) {
                    // Get current artist to edit
                    user = User(
                        documentArtist.data?.get("id").toString(),
                        documentArtist.data?.get("name").toString(),
                        documentArtist.data?.get("surnames").toString(),
                        documentArtist.data?.get("username").toString(),
                        documentArtist.data?.get("email").toString(),
                        documentArtist.data?.get("country").toString(),
                        documentArtist.data?.get("category").toString(),
                        documentArtist.data?.get("age").toString().toLongOrNull(),
                        documentArtist.data?.get("website").toString(),
                        documentArtist.data?.get("following") as ArrayList<String>?
                    )

                    name.setText(user.name)
                    surnames.setText(user.surnames)
                    country.setText(user.country)
                    age.setText(user.age.toString())
                    website.setText(user.website)
                    selectedCategory = user.category.toString()
                    if (user.category == Category.User.name) {
                        categoryField.setSelection(1)
                    } else {
                        categoryField.setSelection(0)
                    }

                    progressCircle.visibility = View.GONE
                    profileDataLayout.visibility = View.VISIBLE
                } else {
                    Log.d("error", "Artist no such document")
                }
            }?.addOnFailureListener { exceptionArtist ->
                Log.d("error", "Artist get failed with ", exceptionArtist)
            }
        }

        // Get profile picture
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()

        // LAUNCHERS
        // Don't put result launcher register for activity result inside listeners cause fragment is not created (throws error)
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    uri = result.data?.data!!
                    uri.let { url ->
                        checkImageOrientation(url)
                        profilePicRef.putFile(url).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Picasso.get().load(url).into(image)
                                showMessageLong("Image uploaded successfully!")
                            } else {
                                showMessageLong("Error on uploading image...")
                            }
                        }
                    }
                }
            }

        /* With crop library
        galleryCropResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let { uri ->
                profilePicRef.putFile(uri).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Picasso.get().load(uri).into(image)
                        showMessageLong("Image uploaded successfully!")
                    } else {
                        showMessageLong("Error on uploading image...")
                    }
                }
            }
        }*/

        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    uri.let { url ->
                        profilePicRef.putFile(url).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Picasso.get().load(url).into(image)
                                showMessageLong("Image uploaded successfully!")
                            } else {
                                showMessageLong("Error on uploading image...")
                            }
                        }
                    }
                }

            }
        requestMultiplePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
                resultsMap.forEach {
                    Log.d("launcher", "Permission: ${it.key}, granted: ${it.value}")
                    if (it.value == true) {
                        counterPermissions++
                    }
                }
            }

        // SAVE
        // Save user profile picture
        image.setOnClickListener {
            chooseImageUploadMethod(it)
        }

        // Save
        view.findViewById<MaterialButton>(R.id.editprofile_saveBtn).setOnClickListener {
            if (name.text.toString().isNotBlank() && surnames.text.toString()
                    .isNotBlank() && country.text.toString().isNotBlank() && age.text.toString()
                    .isNotBlank() && website.text.toString()
                    .isNotBlank() && selectedCategory.isNotBlank()
            ) {
                if (countryIsValid(country.text.toString())) {
                    user.name = name.text.toString()
                    user.surnames = surnames.text.toString()
                    user.country = country.text.toString()
                    user.category = selectedCategory
                    user.age = age.text.toString().toLongOrNull()
                    user.website = website.text.toString()
                    Log.d("user", user.toString())

                    when (selectedCategory) {
                        Category.User.name -> {
                            userDocRef?.set(user)
                                ?.addOnSuccessListener {
                                    showMessageLong("User updated!")
                                    findNavController().popBackStack()
                                }?.addOnFailureListener {
                                    showMessageShort("Failed!")
                                }
                            artistDocRef?.delete()
                        }
                        Category.Artist.name -> {
                            artistDocRef?.set(user)
                                ?.addOnSuccessListener {
                                    showMessageLong("Artist updated!")
                                    findNavController().popBackStack()
                                }?.addOnFailureListener {
                                    showMessageShort("Failed!")
                                }
                            userDocRef?.delete()
                        }
                        else -> {
                            Log.d("error", "nosaved")
                        }
                    }
                }else{
                    showMessageLong("Invalid country! Select it from the list!")
                }
            } else {
                showMessageLong(view.context.getString(R.string.someEmptyFieldsError))
            }
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_editprofile_close).setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    /*
    private fun disableAutoCompleteTextTextView(autoCompleteTextView: AppCompatAutoCompleteTextView) {
        // Disable editable and keyboard
        autoCompleteTextView.inputType = 0
        autoCompleteTextView.keyListener = null
    }

    private fun enableAutoCompleteTextTextView(autoCompleteTextView: AppCompatAutoCompleteTextView) {
        // Enable editable and keyboard
        autoCompleteTextView.inputType = autoCompleteTextViewInputType // saved input type
        autoCompleteTextView.keyListener = TextKeyListener.getInstance()
    }*/


    private fun checkImageOrientation(url: Uri) {
        // Check image orientation before upload to server
        /*
        val convertedBitmap = rotateImageIfRequired(url)
        convertedBitmap?.compress(
            Bitmap.CompressFormat.JPEG,
            1100,
            ByteArrayOutputStream()
        )
        val finalUri = convertedBitmap?.let { convBitmap ->
            getImageUriFromBitmap(
                view.context,
                convBitmap
            )
        }
        finalUri?.let { finalUrl ->
            profilePicRef.putFile(finalUrl).addOnCompleteListener {
                if (it.isSuccessful) {
                    Picasso.get().load(finalUrl).into(image)
                    showMessageLong("Image uploaded successfully!")
                } else {
                    showMessageLong("Error on uploading image...")
                }
            }
        }
        */
    }

    private fun chooseImageUploadMethod(view: View) {
        val alertBuilder = AlertDialog.Builder(view.context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog_chooseuploadphotomethod, null)
        alertBuilder.setCancelable(false)
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()

        dialogView.findViewById<ImageView>(R.id.cameraMethod).setOnClickListener {
            alertDialog.dismiss()
            if (checkPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), requestMultiplePermissionLauncher, view.context
                )
            ) {
                takePictureFromCamera()
            }
        }
        dialogView.findViewById<ImageView>(R.id.galleryMethod).setOnClickListener {
            alertDialog.dismiss()
            if (checkPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), requestMultiplePermissionLauncher, view.context
                )
            ) {
                takePictureFromGallery()
            }
        }

        alertDialog.show()

    }

    private fun takePictureFromCamera() {
        cameraResultLauncher.launch(uri)
    }

    private fun takePictureFromGallery() {
        val intentPick = Intent(ACTION_PICK)
        intentPick.type = "image/*"
        galleryResultLauncher.launch(intentPick)
        // With crop library: galleryCropResultLauncher.launch(intentPick)
    }

    /*
    private fun checkPermissions(): Boolean {
        requestMultiplePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
        return if (counterPermissions == 2) {
            counterPermissions = 0
            true
        } else {
            counterPermissions = 0
            false
        }
    }
     */

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    /*
    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "file_name")
            contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "image/png")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                (context.getExternalFilesDir(null)?.absolutePath ?: "") + "relativePath"
            )
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1)
            Uri.parse(contentValues.getAsString(MediaStore.MediaColumns.RELATIVE_PATH))
        } else {
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
            Uri.parse(path.toString())
        }
    }

    private fun rotateImageIfRequired(selectedImage: Uri): Bitmap? {
        val bitmap = context?.let { context ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        selectedImage
                    )
                )
            } else {
                createBitmap(0, 0, Bitmap.Config.RGB_565)
            }
        }
        val exifInterface = ExifInterface(selectedImage.path!!);
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        );

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap?.let { rotateImage(it, 90) }
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap?.let { rotateImage(it, 180) }
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap?.let { rotateImage(it, 270) }
            else -> Bitmap.createBitmap(0, 0, Bitmap.Config.RGB_565)
        }
    }
    */
}