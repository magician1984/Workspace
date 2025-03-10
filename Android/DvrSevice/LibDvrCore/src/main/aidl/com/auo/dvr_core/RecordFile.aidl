// RecordFile.aidl
package com.auo.dvr_core;

import com.auo.dvr_core.CamLocation;
import com.auo.dvr_core.RecordFileType;

parcelable RecordFile;

/** RecordFileInfo
 *  This class is used to store information about a record file.
 *  @param name:The name of the record file.
 *  @param location: The location of the camera that recorded the file.
 *  @param createTime: The time when the record was actual created.
 *  @param type: The type of the record file. Normal: normal, lock: locked by user, playing: on playing, protected: locked automatically.
 *  @param isCloned: Whether the file has been cloned or not.
 *  **/
parcelable RecordFile {
    String name;
    CamLocation location;
    long createTime;
    RecordFileType type;
    boolean isCloned;
    boolean isLocked;
}