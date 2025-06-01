// IDvrService.aidl
package com.auo.dvr_core;

import com.auo.dvr_core.RecordGroup;
import com.auo.dvr_core.IDvrEventCallback;
import com.auo.dvr_core.DvrState;
import com.auo.dvr_core.DvrConfigure;

// Declare any non-default types here with import statements

interface IDvrService {
    List<RecordGroup> getRecordGoups();
    DvrState getState();
    DvrConfigure getConfigure();

    void updataConfigure(in DvrConfigure configure);

    void lockFile(in List<RecordGroup> recordGroup);
    void unlockFile(in List<RecordGroup> recordGroup);
    void deleteFile(in List<RecordGroup> recordGroup);

    void registerCallback(in IDvrEventCallback callback);
    void unregisterCallback(in IDvrEventCallback callback);

    void unmountFlash();
}