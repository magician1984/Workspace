#!/bin/bash
set -e

UPLOAD_PATH=upload
TARGET_PATH=dms

function link_device() {
    # Link device
    adb wait-for-device
    adb root
    adb remount
}

function delete_conflic() {
    # Remove conflic files
    adb shell rm -f /vendor/bin/qcarcam_dms
    adb shell rm -f /vendor/lib64/libopencv_*.so
}

function clean_upload() {
    # Remove upload folder
    if [ -d ./${UPLOAD_PATH} ]; then
        rm -rf ./${UPLOAD_PATH}/*
    else
        mkdir -p ./${UPLOAD_PATH}
    fi
}

function copy_required_files() {
    # Copy required file into upload folder
    cp ./build/qcarcam_dms ./${UPLOAD_PATH}/
    cp ./libs/*.so ./${UPLOAD_PATH}/
    cp ./third_party/opencv/libs/*.so ./${UPLOAD_PATH}/
}


function upload() {
    adb shell mkdir -p /data/local/tmp/${TARGET_PATH}

    adb push ./${UPLOAD_PATH}/. /data/local/tmp/${TARGET_PATH}/

    adb shell chmod -R 755 /data/local/tmp/${TARGET_PATH}
}

function fast_upload(){
    adb push ./${UPLOAD_PATH}/qcarcam_dms /data/local/tmp/${TARGET_PATH}/
    adb push ./${UPLOAD_PATH}/libqcarcam_dms.so /data/local/tmp/${TARGET_PATH}/
}


function execute() {
    adb shell "LD_LIBRARY_PATH=/data/local/tmp/${TARGET_PATH} /data/local/tmp/${TARGET_PATH}/qcarcam_dms"
}

link_device

delete_conflic

clean_upload

copy_required_files

if [ "$1" == "-f" ]; then
    fast_upload
else
    upload
fi

execute

