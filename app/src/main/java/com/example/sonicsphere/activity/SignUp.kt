package com.example.sonicsphere.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.sonicsphere.databinding.SignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.submit.setOnClickListener {
            val strEmail: String = binding.email.text.toString().trim()
            val strPassword: String = binding.password.text.toString().trim()
            val strName: String = binding.name.text.toString().trim()
            val strMobile: String = binding.mobile.text.toString().trim()
            val dbRef = FirebaseDatabase.getInstance().reference

            if (strName.isEmpty()) {
                binding.name.error = "Name is required"
            } else if (strEmail.isEmpty()) {
                binding.email.error = "Email is required"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                binding.email.error = "Enter a valid email"
            } else if (strMobile.isEmpty()) {
                binding.mobile.error = "Mobile number is required"
            } else if (!Patterns.PHONE.matcher(strMobile).matches() || strMobile.length < 10) {
                binding.mobile.error = "Enter a valid mobile number"
            } else if (strPassword.isEmpty()) {
                binding.password.error = "Password is required"
            } else if (strPassword.length < 6) {
                binding.password.error = "Password must be at least 6 characters"
            } else {
                auth.createUserWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { verifyTask ->
                                    if (verifyTask.isSuccessful) {
                                        Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                val userInfo = mapOf(
                                    "name" to strName,
                                    "email" to strEmail,
                                    "mobile" to strMobile
                                )
                                dbRef.child("Users").child(userId).setValue(userInfo)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            Toast.makeText(this, "Sign Up successful", Toast.LENGTH_SHORT).show()
                                            // Redirect to Login Page
                                            val intent = Intent(this, LoginPage::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(this, "Sign Up Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }

        binding.redirect.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
    fun sendVarificationEmail(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(this,"Email sent to ${user.email}",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"${it.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
