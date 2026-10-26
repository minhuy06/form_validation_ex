package com.example.form_validation_ex.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.form_validation_ex.databinding.ActivityHomeBinding
import com.example.form_validation_ex.model.User

class HomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER = "extra_user"
    }

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_USER, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_USER) as? User
        }

        user?.let {
            binding.tvWelcome.text = "Chúc mừng tân sinh viên ${it.fullName} đã đăng ký thành công!"
            binding.tvUserInfo.text = buildString {
                appendLine("• Họ và tên: ${it.fullName}")
                appendLine("• Email: ${it.email}")
                appendLine("• Số điện thoại: ${it.phone}")
                appendLine("• Giới tính: ${it.gender}")
                append("• Mật khẩu: ${"*".repeat(it.password.length)}")
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}