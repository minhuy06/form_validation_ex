package com.example.form_validation_ex

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.form_validation_ex.R
import com.example.form_validation_ex.databinding.ActivityMainBinding
import com.example.form_validation_ex.model.User
import com.example.form_validation_ex.validator.FormValidator
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var lastClickTime: Long = 0L
    private val CLICK_DEBOUNCE_DELAY = 1000L

    private var selectedGender: String = "Nam"
    private var backupUser: User? = null
    private var backupConfirmPass: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRealtimeValidation()
        setupSelectionListeners()
        setupButtons()
    }

    private fun setupRealtimeValidation() {
        binding.etFullName.doAfterTextChanged {
            if (binding.tilFullName.error != null) {
                binding.tilFullName.error = null
            }
        }

        binding.etEmail.doAfterTextChanged {
            if (binding.tilEmail.error != null) {
                binding.tilEmail.error = null
            }
        }

        binding.etPhone.doAfterTextChanged {
            if (binding.tilPhone.error != null) {
                binding.tilPhone.error = null
            }
        }

        binding.etPassword.doAfterTextChanged { pass ->
            if (binding.tilPassword.error != null) {
                binding.tilPassword.error = null
            }
            val confirm = binding.etConfirmPassword.text.toString()
            if (confirm.isNotEmpty() && pass.toString() != confirm) {
                binding.tilConfirmPassword.error = getString(R.string.err_confirm_pass)
            } else {
                binding.tilConfirmPassword.error = null
            }
        }

        binding.etConfirmPassword.doAfterTextChanged { confirm ->
            val pass = binding.etPassword.text.toString()
            if (confirm.toString().isNotEmpty() && confirm.toString() != pass) {
                binding.tilConfirmPassword.error = getString(R.string.err_confirm_pass)
            } else {
                binding.tilConfirmPassword.error = null
            }
        }
    }

    private fun setupSelectionListeners() {
        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.rbMale -> "Nam"
                R.id.rbFemale -> "Nữ"
                else -> "Khác"
            }
            Log.d("Register", "Giới tính đã chọn: $selectedGender")
        }
    }

    private fun setupButtons() {
        binding.btnSubmit.setOnClickListener {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) {
                return@setOnClickListener
            }
            lastClickTime = currentTime

            if (validateForm()) {
                val user = User(
                    fullName = binding.etFullName.text.toString().trim(),
                    email = binding.etEmail.text.toString().trim(),
                    phone = binding.etPhone.text.toString().trim(),
                    gender = selectedGender,
                    password = binding.etPassword.text.toString()
                )
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra(HomeActivity.EXTRA_USER, user)
                }
                startActivity(intent)
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.err_form_snackbar),
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(getColor(R.color.ute_accent))
                    .show()
            }
        }

        binding.btnCancel.setOnClickListener {
            saveBackupBeforeClear()
            clearForm()

            Snackbar.make(
                binding.root,
                "Đã xóa toàn bộ thông tin đã nhập",
                Snackbar.LENGTH_LONG
            ).setAction("HOÀN TÁC") {
                restorePreviousFormData()
            }.setActionTextColor(getColor(R.color.ute_accent))
                .show()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        var firstErrorView: View? = null

        val name = binding.etFullName.text.toString().trim()
        if (!FormValidator.isValidName(name)) {
            binding.tilFullName.error = getString(R.string.err_empty_name)
            if (firstErrorView == null) firstErrorView = binding.etFullName
            isValid = false
        }

        val email = binding.etEmail.text.toString().trim()
        if (!FormValidator.isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.err_invalid_email)
            if (firstErrorView == null) firstErrorView = binding.etEmail
            isValid = false
        }

        val phone = binding.etPhone.text.toString().trim()
        if (!FormValidator.isValidPhone(phone)) {
            binding.tilPhone.error = getString(R.string.err_invalid_phone)
            if (firstErrorView == null) firstErrorView = binding.etPhone
            isValid = false
        }

        val password = binding.etPassword.text.toString()
        if (!FormValidator.isValidPassword(password)) {
            binding.tilPassword.error = getString(R.string.err_short_pass)
            if (firstErrorView == null) firstErrorView = binding.etPassword
            isValid = false
        }

        val confirmPass = binding.etConfirmPassword.text.toString()
        if (!FormValidator.isPasswordMatch(password, confirmPass)) {
            binding.tilConfirmPassword.error = getString(R.string.err_confirm_pass)
            if (firstErrorView == null) firstErrorView = binding.etConfirmPassword
            isValid = false
        }

        if (!binding.cbTerms.isChecked) {
            isValid = false
        }

        firstErrorView?.requestFocus()
        return isValid
    }

    private fun saveBackupBeforeClear() {
        backupUser = User(
            fullName = binding.etFullName.text.toString(),
            email = binding.etEmail.text.toString(),
            phone = binding.etPhone.text.toString(),
            gender = selectedGender,
            password = binding.etPassword.text.toString()
        )
        backupConfirmPass = binding.etConfirmPassword.text.toString()
    }

    private fun clearForm() {
        binding.etFullName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPhone.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()

        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPhone.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        binding.rbMale.isChecked = true
        binding.cbTerms.isChecked = true
        binding.etFullName.requestFocus()
    }

    private fun restorePreviousFormData() {
        backupUser?.let { user ->
            binding.etFullName.setText(user.fullName)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone)
            binding.etPassword.setText(user.password)
            binding.etConfirmPassword.setText(backupConfirmPass)
            when (user.gender) {
                "Nam" -> binding.rbMale.isChecked = true
                "Nữ" -> binding.rbFemale.isChecked = true
                else -> binding.rbOther.isChecked = true
            }
        }
    }
}