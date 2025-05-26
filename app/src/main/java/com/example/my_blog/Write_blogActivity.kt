package com.example.my_blog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class WriteBlogActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSaveDraft: Button
    private lateinit var btnPublish: Button

    private lateinit var dbHelper: BlogDbHelper

    private var editingBlogId: Int? = null

    private val PREFS_NAME = "blog_drafts"
    private val DRAFT_TITLE_KEY = "draft_title"
    private val DRAFT_CONTENT_KEY = "draft_content"

    // Assume current user ID is 1 for demo; replace with your user session logic
    private val currentUserId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_blog)

        etTitle = findViewById(R.id.etBlogTitle)
        etContent = findViewById(R.id.etBlogContent)
        btnSaveDraft = findViewById(R.id.btnSaveDraft)
        btnPublish = findViewById(R.id.btnPublish)

        dbHelper = BlogDbHelper(this)

        editingBlogId = intent.getIntExtra("blog_id", -1).takeIf { it != -1 }

        if (editingBlogId != null) {
            loadBlogForEdit(editingBlogId!!)
        } else {
            loadDraft()
        }

        btnSaveDraft.setOnClickListener {
            saveDraft()
        }

        btnPublish.setOnClickListener {
            publishBlog()
        }
    }

    private fun loadBlogForEdit(id: Int) {
        Thread {
            val blog = dbHelper.getBlogById(id)
            runOnUiThread {
                if (blog != null) {
                    etTitle.setText(blog.title)
                    etContent.setText(blog.content)
                } else {
                    Toast.makeText(this, "Blog not found!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }.start()
    }

    private fun loadDraft() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val draftTitle = prefs.getString(DRAFT_TITLE_KEY, "")
        val draftContent = prefs.getString(DRAFT_CONTENT_KEY, "")

        etTitle.setText(draftTitle)
        etContent.setText(draftContent)
    }

    private fun saveDraft() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Nothing to save as draft", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putString(DRAFT_TITLE_KEY, title)
        prefs.putString(DRAFT_CONTENT_KEY, content)
        prefs.apply()
        Toast.makeText(this, "Draft saved locally", Toast.LENGTH_SHORT).show()
    }

    private fun clearDraft() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.remove(DRAFT_TITLE_KEY)
        prefs.remove(DRAFT_CONTENT_KEY)
        prefs.apply()
    }

    private fun publishBlog() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            if (editingBlogId != null) {
                // Update existing blog - mark as published (isDraft=false)
                val rows = dbHelper.updateBlog(editingBlogId!!, title, content, false)
                runOnUiThread {
                    if (rows > 0) {
                        clearDraft()
                        Toast.makeText(this, "Blog updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update blog", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Insert new blog, authorId = currentUserId, isDraft=false for published
                val newId = dbHelper.insertBlog(title, content, currentUserId, false)
                runOnUiThread {
                    if (newId > 0) {
                        clearDraft()
                        Toast.makeText(this, "Blog published successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to publish blog", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.start()
    }
}
