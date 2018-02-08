#!/bin/bash

#
# script for finishing up (for failure) of TvShoptime import...
#
# mario.vera@b2wdigital.com

DATA_BASEDIR=./var/data
FAIL_BASEDIR=$DATA_BASEDIR/fail
BLOCKS_UPLOAD_DIR=$DATA_BASEDIR/upload/blocks
TIMELINE_UPLOAD_DIR=$DATA_BASEDIR/upload/timeline

echo "Starting processing close-up..."

mv $BLOCKS_UPLOAD_DIR/* $FAIL_BASEDIR/blocks
mv $TIMELINE_UPLOAD_DIR/* $FAIL_BASEDIR/timeline

echo "Finished!"
