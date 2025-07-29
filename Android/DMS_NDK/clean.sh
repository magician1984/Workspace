#!/bin/bash

function remove_dir() {
    local dir="$1"
    if [ -d "$dir" ]; then
        echo "🗑️ Removing directory: $dir"
        rm -rf "$dir"
    else
        echo "ℹ️ Directory not found: $dir"
    fi
}

remove_dir "./dump"
remove_dir "./build"
remove_dir "./upload"