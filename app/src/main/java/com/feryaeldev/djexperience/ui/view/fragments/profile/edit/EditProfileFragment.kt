package com.feryaeldev.djexperience.ui.view.fragments.profile.edit

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.createBitmap
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.util.asMap
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File


class EditProfileFragment : BaseFragment() {

    private lateinit var user: User
    private lateinit var progressCircle: FragmentContainerView
    private lateinit var profileDataLayout: LinearLayoutCompat
    private var selectedCategory = ""

    private lateinit var uri: Uri

    private var counterPermissions = 0
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
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
        val name: EditText = view.findViewById(R.id.editprofile_name)
        val surnames: EditText = view.findViewById(R.id.editprofile_surnames)
        val country: EditText = view.findViewById(R.id.editprofile_country)
        val age: EditText = view.findViewById(R.id.editprofile_age)
        val website: EditText = view.findViewById(R.id.editprofile_website)
        val categorySpinner: Spinner = view.findViewById(R.id.editprofile_category_sp)

        val userListTypes = resources.getStringArray(R.array.user_categories)
        val adapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, userListTypes)
        categorySpinner.adapter = adapter
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    if (user.category == "User") {
                        categorySpinner.setSelection(0)
                    } else {
                        categorySpinner.setSelection(1)
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
                            if (user.category == "User") {
                                categorySpinner.setSelection(0)
                            } else {
                                categorySpinner.setSelection(1)
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
                        if (user.category == "User") {
                            categorySpinner.setSelection(0)
                        } else {
                            categorySpinner.setSelection(1)
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
                    if (user.category == "User") {
                        categorySpinner.setSelection(0)
                    } else {
                        categorySpinner.setSelection(1)
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


        // Save user profile picture
        // Don't put result launcher register for activity result inside listeners cause fragment is not created (throws error)
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //val uri = result.data?.data
                    uri = result.data?.data!!
                    uri.let { url ->
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
        image.setOnClickListener {
            chooseImageUploadMethod()
        }

        // Save
        view.findViewById<MaterialButton>(R.id.editprofile_saveBtn).setOnClickListener {
            if (name.text.toString().isNotBlank() && surnames.text.toString()
                    .isNotBlank() && country.text.toString().isNotBlank() && age.text.toString()
                    .isNotBlank() && website.text.toString().isNotBlank()
            ) {
                user.name = name.text.toString()
                user.surnames = surnames.text.toString()
                user.country = country.text.toString()
                user.category = selectedCategory
                user.age = age.text.toString().toLongOrNull()
                user.website = website.text.toString()
                Log.d("user", user.toString())

                when (selectedCategory) {
                    "User" -> {
                        userDocRef?.set(user.asMap())
                            ?.addOnSuccessListener {
                                showMessageLong("User updated!")
                                findNavController().popBackStack()
                            }?.addOnFailureListener {
                                showMessageShort("Failed!")
                            }
                        artistDocRef?.delete()
                    }
                    "Artist" -> {
                        artistDocRef?.set(user.asMap())
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
            } else {
                Toast.makeText(
                    view.context,
                    "At least one of the fields is empty!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_editprofile_close).setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun chooseImageUploadMethod() {
        val alertBuilder = AlertDialog.Builder(view?.context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog_chooseuploadphotomethod, null)
        alertBuilder.setCancelable(false)
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()

        dialogView.findViewById<ImageView>(R.id.cameraMethod).setOnClickListener {
            alertDialog.dismiss()
            if (checkPermissions()) {
                takePictureFromCamera()
            }
        }
        dialogView.findViewById<ImageView>(R.id.galleryMethod).setOnClickListener {
            alertDialog.dismiss()
            if (checkPermissions()) {
                takePictureFromGallery()
            }
        }

        alertDialog.show()

    }

    private fun takePictureFromCamera() {
        cameraResultLauncher.launch(uri)
    }

    private fun takePictureFromGallery() {
        val intentCameraPick = Intent(ACTION_PICK)
        intentCameraPick.type = "image/*"
        galleryResultLauncher.launch(intentCameraPick)
    }

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


    // Fix correct orientation when upload
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

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }
}