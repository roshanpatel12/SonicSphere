package com.example.sonicsphere.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sonicsphere.R
import com.example.sonicsphere.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()

        binding.loginSubmit.setOnClickListener {
            val strEmail: String = binding.loginEmail.text.toString().trim()
            val strPassword: String = binding.loginPassword.text.toString().trim()

            if (strEmail.isEmpty()) {
                binding.loginEmail.error = "Email is required"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                binding.loginEmail.error = "Enter a valid email"
            } else if (strPassword.isEmpty()) {
                binding.loginPassword.error = "Password is required"
            } else {
                auth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                if (user.isEmailVerified) {
                                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, HomePage::class.java)
                                    intent.putExtra("email", strEmail)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.forgetLoginPass.setOnClickListener {
            val builder: AlertDialog = AlertDialog.Builder(this).create()
            val dialogView: View = layoutInflater.inflate(R.layout.forgrtpass, null)
            builder.setView(dialogView)
            builder.show()

            dialogView.findViewById<Button>(R.id.btnReset).setOnClickListener {
                val strEmail: String = dialogView.findViewById<EditText>(R.id.edForgetPass).text.toString().trim()

                if (TextUtils.isEmpty(strEmail)) {
                    Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
                } else {
                    auth.sendPasswordResetEmail(strEmail).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                            builder.dismiss()
                        } else {
                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                builder.dismiss()
            }
        }

        binding.createAcctxt.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
