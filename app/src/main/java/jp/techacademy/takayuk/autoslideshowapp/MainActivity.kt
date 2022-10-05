package jp.techacademy.takayuk.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
        )

        button1.setOnClickListener {
            if (cursor!!.moveToNext()) {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                return@setOnClickListener

            } else {
                cursor.moveToFirst()

                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                return@setOnClickListener
            }
            cursor.close()
        }

        button2.setOnClickListener {
            if (cursor!!.moveToPrevious()) {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                return@setOnClickListener
            } else {
                cursor.moveToLast()

                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                return@setOnClickListener
            }
            cursor.close()
        }

        var a = 0
        button3.setOnClickListener {
            a += 1
            if(a % 2 == 1){
                button3.text = "一時停止"
                button1.isEnabled = false
                button2.isEnabled = false
                if (mTimer == null){
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                if (cursor!!.moveToNext()) {
                                    // indexからIDを取得し、そのIDから画像のURIを取得する
                                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = cursor.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                    imageView.setImageURI(imageUri)
                                } else {
                                    cursor.moveToFirst()

                                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = cursor.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }
                    }, 100, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を2000ミリ秒 に設定
                }
            } else{
                button3.text = "再生"
                button1.isEnabled = true
                button2.isEnabled = true
                if (mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
        }
    }
}