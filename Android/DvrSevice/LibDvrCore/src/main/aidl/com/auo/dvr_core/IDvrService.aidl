// IDvrService.aidl
package com.auo.dvr_core;

import com.auo.dvr_core.RecordFile;
import com.auo.dvr_core.OnRecordUpdateListener;
import com.auo.dvr_core.OnStateUpdateListener;
import com.auo.dvr_core.DvrState;
import com.auo.dvr_core.OnConfigureUpdateListener;
import com.auo.dvr_core.DvrConfigure;

// Declare any non-default types here with import statements

interface IDvrService {
    List<RecordFile> getRecordFiles();
    DvrState getState();
    DvrConfigure getConfigure();

    void updataConfigure(in DvrConfigure configure);

    void lockFile(in RecordFile recordFile);
    void unlockFile(in RecordFile recordFile);

    void deleteFile(in RecordFile recordFile);

    void copyFile(in RecordFile recordFile, in String destPath);

    void registerListener(in OnRecordUpdateListener listener);
    void unregisterListener(in OnRecordUpdateListener listener);

    void registerStateListener(in OnStateUpdateListener listener);
    void unregisterStateListener(in OnStateUpdateListener listener);

    void registerConfigureListener(in OnConfigureUpdateListener listener);
    void unregisterConfigureListener(in OnConfigureUpdateListener listener);

    void unmountFlash();

    void forceClone();
}