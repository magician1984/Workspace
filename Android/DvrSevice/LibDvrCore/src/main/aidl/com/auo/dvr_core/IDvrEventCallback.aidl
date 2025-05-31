// IDvrEventCallback.aidl
package com.auo.dvr_core;

import com.auo.dvr_core.RecordGroup;
import com.auo.dvr_core.DvrState;
import com.auo.dvr_core.DvrConfigure;

// Declare any non-default types here with import statements

interface IDvrEventCallback {
    void onRecordUpdate(in List<RecordGroup> groups);
    void onStateUpdate(in DvrState state);
    void onConfigureUpdate(in DvrConfigure configure);
}