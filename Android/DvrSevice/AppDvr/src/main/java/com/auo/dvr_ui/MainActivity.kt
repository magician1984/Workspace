package com.auo.dvr_ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.auo.dvr.DvrService
import com.auo.dvr_ui.datasource.Datasource
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.usecase.IDataSource
import com.auo.dvr_ui.usecase.IPresenter
import com.auo.dvr_ui.usecase.UseCaseDeleteGroup
import com.auo.dvr_ui.usecase.UseCaseGetDvrState
import com.auo.dvr_ui.usecase.UseCaseGetListFiles
import com.auo.dvr_ui.usecase.UseCaseLockGroup
import com.auo.dvr_ui.usecase.UseCaseRegisterDvrStateListener
import com.auo.dvr_ui.usecase.UseCaseRegisterRecordUpdateListener
import com.auo.dvr_ui.usecase.UseCaseUnlockGroup
import com.auo.dvr_ui.usecase.UseCaseUnmountStorage

class MainActivity : ComponentActivity() {
    private lateinit var mPresenter: IPresenter

    private lateinit var mDataSource : IDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Workaround: Service should be start complete at booting time
        startForegroundService(Intent(this, DvrService::class.java))

        initialize()
    }

    private fun initialize(){
        mDataSource = Datasource(this)

        mPresenter = Presenter(this)

        mPresenter.summitUseCases(
            UseCaseGetListFiles(mDataSource),
            UseCaseRegisterRecordUpdateListener(mDataSource),
            UseCaseGetDvrState(mDataSource),
            UseCaseLockGroup(mDataSource),
            UseCaseUnlockGroup(mDataSource),
            UseCaseDeleteGroup(mDataSource),
            UseCaseUnmountStorage(mDataSource),
            UseCaseRegisterDvrStateListener(mDataSource)
        )

        mPresenter.render()
    }
}
