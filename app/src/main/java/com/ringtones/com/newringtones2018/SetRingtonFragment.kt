package com.ringtones.com.newringtones2018

import android.Manifest
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.media.RingtoneManager
import android.provider.MediaStore
import java.nio.file.Files.delete
import android.content.ContentValues
import android.net.Uri
import java.io.File
import android.widget.Toast
import android.content.ContentResolver
import android.os.Environment.getExternalStorageDirectory
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.content.res.AssetFileDescriptor
import android.content.Intent
import android.provider.Settings.System.canWrite
import android.os.Build
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.Log
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class SetRingtonFragment : Fragment(){

    @get:JvmName("getContext_")
    private var view: View? = null
    private var mp: MediaPlayer? = null
    private var stopRingtone: LinearLayout? = null
    private var setDefaultRingtone:LinearLayout? = null
    private var isStopped: Boolean = false
    private var fileName:String = ""
    private val fPAth = "android.resource://com.ringtones.com.newringtones2018/raw/"+fileName
    private val PERMISSION_REQUEST_CODE = 1

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater!!.inflate(R.layout.set_ringtone, container, false)
        stopRingtone = view!!.findViewById(R.id.stop_ringtone)
        setDefaultRingtone = view!!.findViewById(R.id.call_ringtone)


        mp!!.start()

        setDefaultRingtone!!.setOnClickListener(View.OnClickListener {

            isStopped=true

            if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        if (Settings.System.canWrite(context!!)) {
                            setRingtone()
                        } else {
                            var intent: Intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                    .setData(Uri.parse("package:" + context!!.getPackageName()))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }


                        Log.e("value", "Permission already Granted, Now you can save image.");
                    } else {
                        requestPermission();
                    }
                } else {
                    setRingtone();
                    Log.e("value", "Not required for requesting runtime permission");
                }
        })


        stopRingtone!!.setOnClickListener(View.OnClickListener {
            isStopped=true
            stopMedia()
        })



        return view
    }

    fun setMedia(media: MediaPlayer,fileN: String){
        mp = media
        fileName= fileN
    }

    override fun onStop() {
        super.onStop()

        if(!isStopped) {
          stopMedia()
        }
    }

    fun stopMedia(){
        mp!!.stop()// stops any current playing song
        mp!!.release()
        fragmentManager!!.popBackStack()
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            false
        }
    }

    private fun requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context!!, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.System.canWrite(context!!)) {
                        setRingtone()
                    } else {
                        val intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                .setData(Uri.parse("package:" + context!!.getPackageName()))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            } else {

            }
        }
    }


    private fun setRingtone() {
        var openAssetFileDescriptor: AssetFileDescriptor?
        (context!!.getSystemService(AUDIO_SERVICE) as AudioManager).ringerMode = 2
        val file = File(Environment.getExternalStorageDirectory(), fileName)
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs()
        }
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        val parse = Uri.parse(fPAth+fileName)
        val contentResolver = context!!.getContentResolver()
        try {
            openAssetFileDescriptor = contentResolver.openAssetFileDescriptor(parse, "r")
        } catch (e2: FileNotFoundException) {
            openAssetFileDescriptor = null
        }

        try {
            val bArr = ByteArray(1024)
            val createInputStream = openAssetFileDescriptor!!.createInputStream()
            val fileOutputStream = FileOutputStream(file)
            var read = createInputStream.read(bArr)
            while (read != -1) {
                fileOutputStream.write(bArr, 0, read)
                read = createInputStream.read(bArr)
            }
            fileOutputStream.close()
        } catch (e3: IOException) {
            e3.printStackTrace()
        }

        val contentValues = ContentValues()
        contentValues.put("_data", file.getAbsolutePath())
        contentValues.put("title", "nkDroid ringtone")
        contentValues.put("mime_type", "audio/mp3")
        contentValues.put("_size", java.lang.Long.valueOf(file.length()))
        contentValues.put("artist", Integer.valueOf(R.string.app_name))
        contentValues.put("is_ringtone", java.lang.Boolean.valueOf(true))
        contentValues.put("is_notification", java.lang.Boolean.valueOf(false))
        contentValues.put("is_alarm", java.lang.Boolean.valueOf(false))
        contentValues.put("is_music", java.lang.Boolean.valueOf(false))
        try {
            Toast.makeText(context!!, StringBuilder().append("Ringtone set successfully"), Toast.LENGTH_LONG).show()
            RingtoneManager.setActualDefaultRingtoneUri(context!!, 1, contentResolver.insert(MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath()), contentValues))
        } catch (th: Throwable) {
            Toast.makeText(context!!, StringBuilder().append("Ringtone feature is not working"), Toast.LENGTH_LONG).show()
        }

    }



}