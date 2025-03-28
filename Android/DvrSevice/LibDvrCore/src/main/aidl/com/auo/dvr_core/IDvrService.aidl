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

    void copyFile(in RecordFile recordFile, in String destPath);

    void registerListener(in OnRecordUpdateListener listener);
    void unregisterListener(in OnRecordUpdateListener listener);

    void forceClone();
}