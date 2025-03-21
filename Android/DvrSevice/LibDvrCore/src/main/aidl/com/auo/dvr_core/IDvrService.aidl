// IDvrService.aidl
package com.auo.dvr_core;

import com.auo.dvr_core.RecordFile;
import com.auo.dvr_core.OnRecordUpdateListener;
// Declare any non-default types here with import statements

interface IDvrService {
    List<RecordFile> getRecordFiles();

    void lockFile(in RecordFile recordFile);
    void unlockFile(in RecordFile recordFile);

    void deleteFile(in RecordFile recordFile);

    void holdFile(in RecordFile recordFile);
    String getRecodFilePath(in RecordFile recordFile);
    void releaseFile(in RecordFile recordFile);

    void registerListener(in OnRecordUpdateListener listener);
    void unregisterListener(in OnRecordUpdateListener listener);

    void forceClone();
}