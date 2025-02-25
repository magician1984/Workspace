package idv.bruce.camera_native

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.auo.qcarcam.IQCarCamLib
import com.auo.qcarcam.QCarCamLibAIDL

class AVMService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize your service here

        val lib : IQCarCamLib = QCarCamLibAIDL()
    }
}