package com.example.my_blog


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.my_blog.BlogDbHelper
import com.example.my_blog.Blog

class ReadBlogActivity : AppCompatActivity() {

    private lateinit var blogContainer: LinearLayout
    private lateinit var dbHelper: BlogDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_blog)

        blogContainer = findViewById(R.id.blogContainer)
        dbHelper = BlogDbHelper(this)

        // Static Blogs
        addStaticBlogs()
    }

    override fun onResume() {
        super.onResume()
        loadBlogs()
    }

    private fun loadBlogs() {
        Thread {
            val blogs = dbHelper.getAllBlogs()
            runOnUiThread {
                blogs.forEach { blog ->
                    blogContainer.addView(createBlogView(blog))
                }
            }
        }.start()
    }

    private fun createBlogView(blog: Blog): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 24, 0, 24)
            }
            setBackgroundResource(android.R.color.white)
            elevation = 8f
        }

        val titleView = TextView(this).apply {
            text = "📝 ${blog.title}"
            textSize = 22f
            setPadding(0, 0, 0, 8)
        }

        val contentView = TextView(this).apply {
            text = blog.content
            textSize = 18f
            setPadding(0, 0, 0, 12)
        }

        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val likeBtn = Button(this).apply {
            text = "❤️ Like"
            setOnClickListener {
                Toast.makeText(this@ReadBlogActivity, "Liked: ${blog.title}", Toast.LENGTH_SHORT).show()
            }
        }

        val deleteBtn = Button(this).apply {
            text = "🗑️ Delete"
            setOnClickListener {
                dbHelper.deleteBlog(blog.id)
                Toast.makeText(this@ReadBlogActivity, "Deleted: ${blog.title}", Toast.LENGTH_SHORT).show()
                recreate()
            }
        }

        val editBtn = Button(this).apply {
            text = "✏️ Edit"
            setOnClickListener {
                val intent = Intent(this@ReadBlogActivity, WriteBlogActivity::class.java)
                intent.putExtra("blog_id", blog.id)
                startActivity(intent)
            }
        }

        btnLayout.addView(likeBtn)
        btnLayout.addView(editBtn)
        btnLayout.addView(deleteBtn)

        layout.addView(titleView)
        layout.addView(contentView)
        layout.addView(btnLayout)

        return layout
    }

    private fun addStaticBlogs() {
        val staticBlogs = listOf(
            Blog(0, "The Joy of Writing", "Writing helps us reflect and grow each day.",1,true),
            Blog(0, "Time Management Tips", "Plan your day, minimize distractions, and focus on goals.",2,true)
        )

        staticBlogs.forEach { blog ->
            val staticView = createBlogView(blog)
            blogContainer.addView(staticView)
        }
    }
}
