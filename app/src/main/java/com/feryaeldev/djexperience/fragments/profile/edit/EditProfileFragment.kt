package com.feryaeldev.djexperience.fragments.profile.edit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.data.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class EditProfileFragment : BaseFragment() {

    private lateinit var user: User
    private lateinit var progressCircle: FragmentContainerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val profileDataLayout = view.findViewById<LinearLayoutCompat>(R.id.editprofile_data)
        profileDataLayout.visibility = View.GONE

        progressCircle = view.findViewById(R.id.editprofile_fragmentProgressBar)
        progressCircle.visibility = View.VISIBLE

        val nickname: TextView = view.findViewById(R.id.editprofile_nickname)
        val category: TextView = view.findViewById(R.id.editprofile_category)
        val country: TextView = view.findViewById(R.id.editprofile_country)

        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid
        val docRef = userId?.let { db.collection("users").document(it) }

        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                Log.d("datasuccess", "DocumentSnapshot data: ${document.data}")
                nickname.text = document.data?.get("nickname").toString()
                category.text = document.data?.get("category").toString()
                country.text = document.data?.get("country").toString()

                // Get current user to edit
                user = document.data?.let { mapToObject(it, User::class) }!!

                progressCircle.visibility = View.GONE
                profileDataLayout.visibility = View.VISIBLE
            } else {
                Log.d("nodata", "No such document")
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "get failed with ", exception)
        }

        val profilePicRef =
            Firebase.storage.reference.child("profile_images/${userId}.jpg")
        val image: ImageView = view.findViewById(R.id.editprofile_photo)
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()

        // Save
        view.findViewById<Button>(R.id.editprofile_saveBtn).setOnClickListener {
            //onBackPressed()
            user.nickname = nickname.text.toString()
            user.category = category.text.toString()
            user.country = country.text.toString()
            docRef?.set(user.asMap())
                ?.addOnSuccessListener {
                    showMessageLong("Updated!")
                    findNavController().popBackStack()
                }?.addOnFailureListener {
                    showMessageShort("Failed!")
                }
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_editprofile_close).setOnClickListener {
            findNavController().popBackStack()
        }

        // overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)

        return view
    }

    // Object to Map
    inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) }
    }

    // Map to Object
    private fun <T : Any> mapToObject(map: Map<String, Any>, clazz: KClass<T>): T {
        //Get default constructor
        val constructor = clazz.constructors.first()

        //Map constructor parameters to map values
        val args = constructor
            .parameters
            .map { it to map[it.name] }
            .toMap()

        //return object from constructor call
        return constructor.callBy(args)
    }

}