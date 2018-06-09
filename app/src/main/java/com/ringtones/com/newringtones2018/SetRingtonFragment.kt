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
import android.database.Cursor
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.provider.Settings
import android.support.v4.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class SetRingtonFragment : Fragment(){

    @get:JvmName("getContext_")
    private var view: View? = null
    private var mp: MediaPlayer? = null
    private var stopRingtone: LinearLayout? = null
    private var setDefaultRingtone:LinearLayout? = null
    private var setNotificationRingtone:LinearLayout? = null
    private var setAlarmRingtone:LinearLayout? = null
    private var rateIt:LinearLayout? = null
    private var shareIt:LinearLayout? = null
    private var isStopped: Boolean = false
    private var fileName:String = ""
    private val fPAth = "android.resource://com.ringtones.com.newringtones2018/raw/"+fileName
    private val PERMISSION_REQUEST_CODE = 1
    private var isRingtone: Boolean = false
    private var isNotification: Boolean = false
    private var isAlarm: Boolean = false
    private var resourceId: Int = 0
    private lateinit var mAdViewRingtone : AdView
    private lateinit var mMainAdView : AdView



    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater!!.inflate(R.layout.set_ringtone, container, false)
        stopRingtone = view!!.findViewById(R.id.stop_ringtone)
        setDefaultRingtone = view!!.findViewById(R.id.call_ringtone)
        setNotificationRingtone = view!!.findViewById(R.id.notification_ringtone)
        setAlarmRingtone = view!!.findViewById(R.id.alarm_ringtone)
        rateIt = view!!.findViewById(R.id.rate)
        shareIt = view!!.findViewById(R.id.share)

        mAdViewRingtone = view!!.findViewById(R.id.adBigBanner)
        val adRequest = AdRequest.Builder().build()
        mAdViewRingtone.loadAd(adRequest)


        mp!!.start()

        setDefaultRingtone!!.setOnClickListener(View.OnClickListener {
            isRingtone=true

            if (isRingtone)
                performOperation()
        })

        setNotificationRingtone!!.setOnClickListener(View.OnClickListener {
            isNotification=true

            if (isNotification)
                performOperation()
        })

        setAlarmRingtone!!.setOnClickListener(View.OnClickListener {
            isAlarm=true

            if (isAlarm)
                performOperation()
        })

        stopRingtone!!.setOnClickListener(View.OnClickListener {
            isStopped=true
            stopMedia()
            mMainAdView.visibility=View.VISIBLE
        })

        rateIt!!.setOnClickListener(View.OnClickListener {
            isStopped = true
            stopMedia()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context!!.packageName)))

        })

        shareIt!!.setOnClickListener(View.OnClickListener {
            isStopped = true
            stopMedia()
            try {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                var sAux = "\nLet me recommend you this application\n\n"
                sAux = sAux + "https://play.google.com/store/apps/details?id=" + context!!.packageName
                i.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(i, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        })

        return view
    }

    fun setMedia(media: MediaPlayer,fileN: String,resId: Int){
        mp = media
        fileName= fileN
        resourceId = resId
    }

    private fun performOperation(){

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                if (Settings.System.canWrite(context!!)) {
                    if (isAlarm){
                        setAlarmRingtone()
                    }
                    else if(isNotification){
                        setNotificationRingtone()
                    }
                    else{
                        setRingtone()
                    }
                } else {
                    var intent: Intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                            .setData(Uri.parse("package:" + context!!.getPackageName()))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            } else {
                requestPermission();
            }
        } else {
            if (isAlarm){
                setAlarmRingtone()
            }
            else if(isNotification){
                setNotificationRingtone()
            }
            else{
                setRingtone()
            }
        }
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
            Toast.makeText(context!!, "Write External Storage permission allows us to do store audios. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.System.canWrite(context!!)) {
                        if (isAlarm){
                            setAlarmRingtone()
                        }
                        else if(isNotification){
                            setNotificationRingtone()
                        }
                        else{
                            setRingtone()
                        }
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


    private fun setAlarmRingtone() {
        isAlarm=false
        val dir: File
        val what = "alarms"
        var newUri: Uri? = null
        val values = ContentValues()
        var isRingTone1 = false

        if (Environment.getExternalStorageState() == "mounted") {
            dir = File(Environment.getExternalStorageDirectory(), what)
        } else {
            dir = context!!.getCacheDir()
        }

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, fileName+".mp3")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                val inputStream = context!!.getResources().openRawResource(resourceId)
                val fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                while (true) {
                    val length = inputStream.read(buffer)
                    if (length <= 0) {
                        break
                    }
                    fileOutputStream.write(buffer, 0, length)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
            } catch (e2: Exception) {
            }
        }

        var cursor : Cursor = context!!.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf("_data", "_id", "is_alarm"), "_data = '" + file.getAbsolutePath() + "'",
                null, null);
        if (cursor != null) {
            var  idColumn = cursor.getColumnIndex("_id");
            var fileColumn = cursor.getColumnIndex("_data");
            var ringtoneColumn = cursor.getColumnIndex("is_alarm");
            while (cursor.moveToNext()) {
                var audioFilePath = cursor.getString(fileColumn);
                if (cursor.getString(ringtoneColumn) != null && cursor.getString(ringtoneColumn).equals("1")) {
                    newUri = Uri.withAppendedPath(MediaStore.Audio.Media.getContentUriForPath(audioFilePath), cursor.getString(idColumn));
                    isRingTone1 = true;
                    Toast.makeText(context!!, StringBuilder().append("Alarm tone set successfully !"), Toast.LENGTH_SHORT).show()
                }
            }
            cursor.close();
        }

        if (isRingTone1) {
            RingtoneManager.setActualDefaultRingtoneUri(context, 4, newUri);
            return;
        }
        values.put("_data", file.getAbsolutePath());
        values.put("title", fileName);
        values.put("_size", java.lang.Long.valueOf(file.length()));
        values.put("mime_type", "audio/mp3");
        values.put("is_alarm", true);
        RingtoneManager.setActualDefaultRingtoneUri(context, 4, context!!.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values));
        Toast.makeText(context!!, StringBuilder().append("Alarm tone set successfully !"), Toast.LENGTH_SHORT).show()
    }


    private fun setNotificationRingtone() {
        isNotification=false
        val dir: File
        val what = "notifications"
        var newUri: Uri? = null
        val values = ContentValues()
        var isRingTone1 = false

        if (Environment.getExternalStorageState() == "mounted") {
            dir = File(Environment.getExternalStorageDirectory(), what)
        } else {
            dir = context!!.getCacheDir()
        }

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, fileName+".mp3")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                val inputStream = context!!.getResources().openRawResource(resourceId)
                val fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                while (true) {
                    val length = inputStream.read(buffer)
                    if (length <= 0) {
                        break
                    }
                    fileOutputStream.write(buffer, 0, length)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
            } catch (e2: Exception) {
            }
        }

        var cursor : Cursor = context!!.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf("_data", "_id", "is_notification"), "_data = '" + file.getAbsolutePath() + "'",
                null, null);
        if (cursor != null) {
            var  idColumn = cursor.getColumnIndex("_id");
            var fileColumn = cursor.getColumnIndex("_data");
            var ringtoneColumn = cursor.getColumnIndex("is_notification");
            while (cursor.moveToNext()) {
                var audioFilePath = cursor.getString(fileColumn);
                if (cursor.getString(ringtoneColumn) != null && cursor.getString(ringtoneColumn).equals("1")) {
                    newUri = Uri.withAppendedPath(MediaStore.Audio.Media.getContentUriForPath(audioFilePath), cursor.getString(idColumn));
                    isRingTone1 = true;
                    Toast.makeText(context!!, StringBuilder().append("Notification tone set successfully !"), Toast.LENGTH_SHORT).show()
                }
            }
            cursor.close();
        }

        if (isRingTone1) {
            RingtoneManager.setActualDefaultRingtoneUri(context, 2, newUri);
            return;
        }
        values.put("_data", file.getAbsolutePath());
        values.put("title", fileName);
        values.put("_size", java.lang.Long.valueOf(file.length()));
        values.put("mime_type", "audio/mp3");
        values.put("is_notification", true);
        RingtoneManager.setActualDefaultRingtoneUri(context, 2, context!!.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values));
        Toast.makeText(context!!, StringBuilder().append("Notification tone set successfully !"), Toast.LENGTH_SHORT).show()
    }

    private fun setRingtone() {
        isRingtone=false
        val dir: File
        val what = "default"
        var newUri: Uri? = null
        val values = ContentValues()
        var isRingTone1 = false

        if (Environment.getExternalStorageState() == "mounted") {
            dir = File(Environment.getExternalStorageDirectory(), what)
        } else {
            dir = context!!.getCacheDir()
        }

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, fileName+".mp3")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                val inputStream = context!!.getResources().openRawResource(resourceId)
                val fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                while (true) {
                    val length = inputStream.read(buffer)
                    if (length <= 0) {
                        break
                    }
                    fileOutputStream.write(buffer, 0, length)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
                inputStream.close()
            } catch (e2: Exception) {
            }
        }

        var cursor : Cursor = context!!.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf("_data", "_id", "is_ringtone"), "_data = '" + file.getAbsolutePath() + "'",
                null, null);
        if (cursor != null) {
            var  idColumn = cursor.getColumnIndex("_id");
            var fileColumn = cursor.getColumnIndex("_data");
            var ringtoneColumn = cursor.getColumnIndex("is_ringtone");
            while (cursor.moveToNext()) {
                var audioFilePath = cursor.getString(fileColumn);
                if (cursor.getString(ringtoneColumn) != null && cursor.getString(ringtoneColumn).equals("1")) {
                    newUri = Uri.withAppendedPath(MediaStore.Audio.Media.getContentUriForPath(audioFilePath), cursor.getString(idColumn));
                    isRingTone1 = true;
                    Toast.makeText(context!!, StringBuilder().append("Ringtone set successfully !"), Toast.LENGTH_SHORT).show()
                }
            }
            cursor.close();
        }

        if (isRingTone1) {
            RingtoneManager.setActualDefaultRingtoneUri(context, 1, newUri);
            return;
        }
        values.put("_data", file.getAbsolutePath());
        values.put("title", fileName);
        values.put("_size", java.lang.Long.valueOf(file.length()));
        values.put("mime_type", "audio/mp3");
        values.put("is_ringtone", true);
        RingtoneManager.setActualDefaultRingtoneUri(context, 1, context!!.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values));
        Toast.makeText(context!!, StringBuilder().append("Ringtone set successfully !"), Toast.LENGTH_SHORT).show()
    }

    fun passAdReference(mAdView: AdView) {
        mMainAdView = mAdView
    }


}