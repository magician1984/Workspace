# DMS_PROTOTYPE
## Update
- 20250721:
    - Add frameProcessStep for decrease frame input rate
    - Fix OOM issue
    - Custom log print method

- 20250728
    - Fix incomplete image problem
    - Integrate log print into logcat
    - Add resolution and color format in FrameData_t
    - Add qcarcam_types.h for check color format only
    - Add dump image sample code
    - Add and update some tool scipts

## Known issue
- Dev log print incorrect

## Usage
- Define a class inherit FrameProcessorBase
- Inject into DMSService by constructor
- Call Dms start

## Build
- Change the NDK_PATH in build.sh to your installed path
- Run build.sh

## Test
- Change to root and remount
- Reboot if remount first time after flash
~~~
adb root
adb remount
~~~
- Using scripts
    - run.sh : push required files and execute
    ~~~
    #Push all needed files(ex. opencv libs)
    ./run.sh  

    #Fast mode, just push executable and libqcarcam_dms.so
    ./run.sh -f
    ~~~

    - download.sh : Download dump images to ./dump
    - clean.sh : clean build, dump, and upload folders


## Notice
- All source code for qcarcam_dms in ./doc/source_codes