package com.auo.dvr_ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.auo.dvr.DvrService
import com.auo.dvr_core.IDvrService
import com.auo.dvr_ui.datasource.Datasource
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.usecase.IDataSource
import com.auo.dvr_ui.usecase.IPresenter
import com.auo.dvr_ui.usecase.UseCaseDeleteFile
import com.auo.dvr_ui.usecase.UseCaseGetCacheFile
import com.auo.dvr_ui.usecase.UseCaseGetConfigure
import com.auo.dvr_ui.usecase.UseCaseGetDvrState
import com.auo.dvr_ui.usecase.UseCaseGetListFiles
import com.auo.dvr_ui.usecase.UseCaseLockFile
import com.auo.dvr_ui.usecase.UseCaseRegisterDvrStateListener
import com.auo.dvr_ui.usecase.UseCaseRegisterListener
import com.auo.dvr_ui.usecase.UseCaseSetConfigure
import com.auo.dvr_ui.usecase.UseCaseUnlockFile
import com.auo.dvr_ui.usecase.UseCaseUnmountStorage
import java.util.concurrent.Executors
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class MainActivity : ComponentActivity() {
    private val mLock = ReentrantLock()

    private val serviceReadyCondition: Condition = mLock.newCondition()

    private lateinit var mService: IDvrService

    private lateinit var mPresenter: IPresenter

    private lateinit var mDataSource : IDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Workaround: Service should be start complete at booting time
        startForegroundService(Intent(this, DvrService::class.java))

        initializePresenter()

        initializeService()

        initializeDataSource()
    }

    private fun initializeService() {
        val intent = Intent(this, DvrService::class.java)
        bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mLock.lock()
                try {
                    Log.d("MainActivity", "onServiceConnected:")
                    mService = IDvrService.Stub.asInterface(service)
                    Log.d("MainActivity", "onServiceConnected: service ready")
                    serviceReadyCondition.signal()
                } finally {
                    mLock.unlock()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("Not yet implemented")
            }
        }, BIND_AUTO_CREATE)
    }

    private fun initializePresenter(){
        mPresenter = Presenter(this)

        mPresenter.onLoading()
    }

    private fun initializeDataSource(){
        val initializeThread = Executors.newSingleThreadExecutor()

        initializeThread.submit{
            Log.d("MainActivity", "initializeDataSource: waiting for service ready")
            if(!::mService.isInitialized){
                mLock.lock()
                serviceReadyCondition.await()
                mLock.unlock()
            }
            Log.d("MainActivity", "initializeDataSource: service ready")

            mDataSource = Datasource(mService, cacheDir)

            Log.d("MainActivity", "initializeDataSource: datasource ready")
            mPresenter.summit(
                UseCaseGetListFiles(mDataSource),
                UseCaseRegisterListener(mDataSource),
                UseCaseLockFile(mDataSource),
                UseCaseUnlockFile(mDataSource),
                UseCaseDeleteFile(mDataSource),
                UseCaseGetCacheFile(mDataSource),
                UseCaseGetDvrState(mDataSource),
                UseCaseRegisterDvrStateListener(mDataSource),
                UseCaseGetConfigure(mDataSource),
                UseCaseSetConfigure(mDataSource),
                UseCaseUnmountStorage(mDataSource)
            )

            Log.d("MainActivity", "initializeDataSource: presenter ready")
            runOnUiThread {
                mPresenter.onReady()
            }
        }
    }
}
