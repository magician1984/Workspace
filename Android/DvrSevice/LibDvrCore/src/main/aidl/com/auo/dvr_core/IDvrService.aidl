// IDvrService.aidl
package com.auo.dvr_core;

import com.auo.dvr_core.RecordGroup;
import com.auo.dvr_core.OnRecordUpdateListener;
import com.auo.dvr_core.OnStateUpdateListener;
import com.auo.dvr_core.DvrState;
import com.auo.dvr_core.OnConfigureUpdateListener;
import com.auo.dvr_core.DvrConfigure;

// Declare any non-default types here with import statements

interface IDvrService {
    List<RecordGroup> getRecordGoups();
    DvrState getState();
    DvrConfigure getConfigure();

    void updataConfigure(in DvrConfigure configure);

    void lockFile(in RecordGroup recordGroup);
    void unlockFile(in RecordGroup recordGroup);

    void deleteFile(in RecordGroup recordGroup);

    void registerListener(in OnRecordUpdateListener listener);
    void unregisterListener(in OnRecordUpdateListener listener);

    void registerStateListener(in OnStateUpdateListener listener);
    void unregisterStateListener(in OnStateUpdateListener listener);

    void registerConfigureListener(in OnConfigureUpdateListener listener);
    void unregisterConfigureListener(in OnConfigureUpdateListener listener);

    void unmountFlash();
}