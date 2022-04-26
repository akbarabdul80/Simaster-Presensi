package com.zero.simasterpresensi.utils

import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText


object Validation {

    fun validateEditText(item: List<EditText>): Boolean {
        var data = true
        item.forEach {
            if (!it.validate() && data) {
                data = false
            }
        }

        return data
    }

    fun EditText.validate(): Boolean {
        val type = this.inputType
        Log.e("TAG", "validate: $type")
        when {
            this.itText().isEmpty() -> {
                this.error = "Silahkan isi filed!"
                return false
            }
            type == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + 1 -> {
                return if (this.isValidEmail()) {
                    true
                } else {
                    this.error = "Silahkan isi filed ini dengan banar"
                    false
                }
            }
            type == 129 || type == InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                return if (this.itText().length >= 3) {
                    true
                } else {
                    this.error = "Isi password minimal 3 karakter"
                    false
                }
            }

            else -> {
                return true
            }
        }

    }

    private fun EditText.itText(): String {
        return this.text.toString()
    }


    private fun EditText.isValidEmail(): Boolean {
        return !TextUtils.isEmpty(this.text) && Patterns.EMAIL_ADDRESS.matcher(this.text).matches()
    }


}