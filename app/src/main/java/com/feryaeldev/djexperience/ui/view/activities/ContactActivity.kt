package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.feryaeldev.djexperience.util.authorMail
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ContactActivity : BaseActivity() {

    private var mailOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val subject = findViewById<TextInputEditText>(R.id.contact_subject)
        val content = findViewById<TextInputEditText>(R.id.contact_content)
        val contactBtn = findViewById<MaterialButton>(R.id.contact_btn)

        subject.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 40){
                subject.error = getString(R.string.inputNotValidError)
            }else{
                subject.error = null
            }
        }

        contactBtn.setOnClickListener {
            val subjectText = subject.text.toString()
            val contentText = content.text.toString()
            if (subjectText.isNotBlank() && contentText.isNotBlank()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, authorMail.split(",".toRegex()).toTypedArray())
                    putExtra(Intent.EXTRA_SUBJECT, subjectText)
                    putExtra(Intent.EXTRA_TEXT, contentText)
                }
                startActivity(intent)
                /*
                intent.type = "message/rfc822"
                startActivity(Intent.createChooser(intent, "Select email"))
                 */
                resetFields()
            } else {
                showMessageLong(getString(R.string.someEmptyFieldsError))
            }
        }
    }

    private fun resetFields() {
        // Reset fields
        findViewById<TextInputEditText>(R.id.contact_subject).setText("")
        findViewById<TextInputEditText>(R.id.contact_content).setText("")
    }

    override fun onStop() {
        super.onStop()
        // A mail client has opened
        mailOpened = true
    }

    override fun onResume() {
        super.onResume()
        if (mailOpened) {
            resetFields()
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mailOpened) {
            resetFields()
            onBackPressed()
        }
    }
}