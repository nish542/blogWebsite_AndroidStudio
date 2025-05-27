package com.example.my_blog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.my_blog.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var btnWrite: Button
    private lateinit var btnRead: Button
    private lateinit var tvWelcome: TextView
    private lateinit var binding: ActivityMainBinding // Declare the binding variable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate the layout
        setContentView(binding.root) // Set the content view to the root of the binding

        btnWrite = findViewById(R.id.btnWrite)
        btnRead = findViewById(R.id.btnRead)
        tvWelcome = findViewById(R.id.tvWelcome)

        val username = intent.getStringExtra("username") ?: "Blogger"
        tvWelcome.text = "👋 Welcome, $username!"

        btnWrite.setOnClickListener {
            val intent = Intent(this, WriteBlogActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        btnRead.setOnClickListener {
            val intent = Intent(this, ReadBlogActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }
}
