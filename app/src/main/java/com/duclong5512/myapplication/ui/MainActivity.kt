package com.duclong5512.myapplication.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.duclong5512.myapplication.R
import com.duclong5512.myapplication.fragment.BaseFragment
import com.duclong5512.myapplication.fragment.SearchResultsFragment
import com.duclong5512.myapplication.utils.REQUEST_PICK_PHOTO
import com.duclong5512.myapplication.utils.REQUEST_SEARCH
import com.duclong5512.myapplication.utils.REQUEST_TAKE_PICTURE
import com.duclong5512.myapplication.utils.REQUEST_VOCE_SEARCH
import java.util.*


class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var btnSearch: Button
    private lateinit var btnCamera: Button
    private lateinit var btnVoice: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()


        btnSearch.setOnClickListener(this)
        btnCamera.setOnClickListener(this)
        btnVoice.setOnClickListener(this)

        if (savedInstanceState == null) {
            loadFragment(null)
        }

    }

    private fun initView() {
        btnSearch = findViewById(R.id.btn_search)
        btnCamera = findViewById(R.id.btn_camera)
        btnVoice = findViewById(R.id.btn_voice)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                REQUEST_SEARCH -> onRequestSearch(resultCode, data)
                REQUEST_TAKE_PICTURE -> onRequestTakePicture(resultCode, data)
                REQUEST_PICK_PHOTO -> onRequestPickPhoto(resultCode, data)
                REQUEST_VOCE_SEARCH -> onRequestVoiceSearch(resultCode, data)
            }
        }
    }

    private fun onRequestSearch(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            val query: String? = data.getStringExtra("QUERY")
            query?.let { Log.d(TAG, it) }
            loadFragment(query?.replace(" ", "+"))
        }
    }

    private fun onRequestTakePicture(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            val bitmap = data.extras?.get("data") as Bitmap
        }
    }

    private fun onRequestPickPhoto(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            val selectedPhoto = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (selectedPhoto != null) {
                val cursor = contentResolver.query(selectedPhoto, filePathColumn, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                    cursor.close()
                }
            }

        }
    }

    private fun onRequestVoiceSearch(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null) {
                loadFragment(results[0].replace(" ", "+"))
            }
        }
    }

    private fun loadFragment(query: String?) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, SearchResultsFragment.newInstance(query), "results")
            .commit()
    }

    private fun showPopupMenu(v: View, @MenuRes menuRes: Int) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener(this@MainActivity)
            inflate(menuRes)
            show()
        }
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        var handled = false
        for (fragment in fragmentList) {
            if (fragment is BaseFragment) {
                handled = fragment.onBackPressed()
                Log.d(TAG, fragment.toString() + handled.toString())
                if (handled) {
                    break
                }
            }
        }
        if (!handled) {
            super.onBackPressed()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.act_camera -> {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, REQUEST_TAKE_PICTURE)
                true
            }
            R.id.act_gallery -> {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO)
                true
            }
            else -> false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_search -> startActivityForResult(SearchActivity.getIntent(this@MainActivity),
                0)
            R.id.btn_camera -> showPopupMenu(v, R.menu.camera_menu)
            R.id.btn_voice -> {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
                try {
                    startActivityForResult(intent, REQUEST_VOCE_SEARCH)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, " " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}