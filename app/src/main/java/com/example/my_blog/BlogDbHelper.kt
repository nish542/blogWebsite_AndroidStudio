package com.example.my_blog
// BlogDbHelper.kt
import android.content.ContentValues
import android.content.Context



import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
data class Blog(
    val id: Int,
    val title: String,
    val content: String,
    val authorId: Int,
    val isDraft: Boolean
)


class BlogDbHelper(context: Context) : SQLiteOpenHelper(context, "blogs.db", null, 1) {

    companion object {
        const val TABLE_NAME = "blogs"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_AUTHOR = "authorId"
        const val COLUMN_IS_DRAFT = "isDraft"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val create = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_AUTHOR INTEGER NOT NULL,
                $COLUMN_IS_DRAFT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertBlog(title: String, content: String, authorId: Int, isDraft: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
            put(COLUMN_AUTHOR, authorId)
            put(COLUMN_IS_DRAFT, if (isDraft) 1 else 0)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun updateBlog(id: Int, title: String, content: String, isDraft: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
            put(COLUMN_IS_DRAFT, if (isDraft) 1 else 0)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getAllBlogs(): List<Blog> {
        val blogs = mutableListOf<Blog>()
        val cursor = readableDatabase.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_ID DESC")
        while (cursor.moveToNext()) {
            val blog = Blog(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DRAFT)) == 1
            )
            blogs.add(blog)
        }
        cursor.close()
        return blogs
    }

    fun getBlogById(id: Int): Blog? {
        val cursor = readableDatabase.query(TABLE_NAME, null, "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, null)
        var blog: Blog? = null
        if (cursor.moveToFirst()) {
            blog = Blog(
                id,
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DRAFT)) == 1
            )
        }
        cursor.close()
        return blog
    }

    fun deleteBlog(id: Int): Int {
        return writableDatabase.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}
