package com.auo.dvr_ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RestrictTo
import com.auo.dvr.DvrService
import com.auo.dvr_core.IDvrService
import com.auo.dvr_ui.datasource.Datasource
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.usecase.IDataSource
import com.auo.dvr_ui.usecase.IPresenter
import com.auo.dvr_ui.usecase.UseCaseDeleteFile
import com.auo.dvr_ui.usecase.UseCaseGetCacheFile
import com.auo.dvr_ui.usecase.UseCaseGetDvrState
import com.auo.dvr_ui.usecase.UseCaseGetListFiles
import com.auo.dvr_ui.usecase.UseCaseLockFile
import com.auo.dvr_ui.usecase.UseCaseRegisterDvrStateListener
import com.auo.dvr_ui.usecase.UseCaseRegisterListener
import com.auo.dvr_ui.usecase.UseCaseUnlockFile
import com.auo.dvr_ui.utils.MockService
import java.util.concurrent.ExecutorService
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
                    mService = IDvrService.Stub.asInterface(service)
                    serviceReadyCondition.signal()
                } finally {
                    mLock.unlock()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("Not yet implemented")
            }
        }, BIND_AUTO_CREATE)

//        mService = MockService(this)
    }

    private fun initializePresenter(){
        mPresenter = Presenter(this)

        mPresenter.onLoading()
    }

    private fun initializeDataSource(){
        val initializeThread = Executors.newSingleThreadExecutor()

        initializeThread.submit{
            if(!::mService.isInitialized){
                mLock.lock()
                serviceReadyCondition.await()
                mLock.unlock()
            }

            mDataSource = Datasource(this, mService, cacheDir)

            mPresenter.summit(
                UseCaseGetListFiles(mDataSource),
                UseCaseRegisterListener(mDataSource),
                UseCaseLockFile(mDataSource),
                UseCaseUnlockFile(mDataSource),
                UseCaseDeleteFile(mDataSource),
                UseCaseGetCacheFile(mDataSource),
                UseCaseGetDvrState(mDataSource),
                UseCaseRegisterDvrStateListener(mDataSource)
            )

            runOnUiThread {
                mPresenter.onReady()
            }
        }
    }
}
