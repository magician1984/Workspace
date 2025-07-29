#!/bin/bash

if [ -d ./dump ]; then
    rm -rf ./dump
fi

adb pull /data/local/tmp/dms_dmp ./dump