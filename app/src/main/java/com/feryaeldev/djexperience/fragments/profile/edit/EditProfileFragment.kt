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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

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

                user = document.data?.let { mapToObject(it, User::class) }!!
                /*
                user.id = document.data?.get("id").toString()
                user.name = document.data?.get("name").toString()
                user.surnames = document.data?.get("surnames").toString()
                user.nickname = document.data?.get("nickname").toString()
                user.email = document.data?.get("email").toString()
                user.country = document.data?.get("country").toString()
                user.category = document.data?.get("category").toString()
                user.age = document.data?.get("age").toString().toInt()
                user.website = document.data?.get("website").toString()
                user.following = arrayListOf()
                Until i discover how to get array from firestore
                 */
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

        view.findViewById<Button>(R.id.editprofile_saveBtn).setOnClickListener {
            //onBackPressed()
            findNavController().popBackStack()
        }

        view.findViewById<ImageView>(R.id.fragment_editprofile_close).setOnClickListener {
            val db = Firebase.firestore
            userId?.let { id ->
                user.nickname = nickname.text.toString()
                user.category = category.text.toString()
                user.country = country.text.toString()
                db.collection("users").document(id).set(user.asMap())
                    .addOnSuccessListener {
                        showMessageLong("Updated!")
                    }.addOnFailureListener {
                        showMessageShort("Failed!")
                    }
            }
            findNavController().popBackStack()
        }

        //  overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)

        return view
    }

    // Object to Map
    inline fun <reified T : Any> T.asMap() : Map<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) }
    }

    // Map to Object
    private inline fun <T : Any> mapToObject(map: Map<String, Any>, clazz: KClass<T>): T {
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