package com.example.wellnessmate

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wellnessmate.databinding.ActivityRegisterBinding
import com.example.wellnessmate.utils.AuthManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = AuthManager(this)

        binding.registerButton.setOnClickListener { attemptRegister() }
        binding.confirmPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister(); true
            } else false
        }
        binding.gotoLoginText.setOnClickListener {
            finish()
        }
    }

    private fun attemptRegister() {
        val email = binding.emailEditText.text?.toString()?.trim().orEmpty()
        val password = binding.passwordEditText.text?.toString()?.trim().orEmpty()
        val confirm = binding.confirmPasswordEditText.text?.toString()?.trim().orEmpty()

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        val result = auth.register(email, password)
        if (result.isSuccess) {
            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        } else {
            Toast.makeText(this, result.exceptionOrNull()?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
        }
    }
}
