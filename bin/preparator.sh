#!/bin/bash

#
# script for preparation of TvShoptime input files...
#
# mario.vera@b2wdigital.com

DATA_DIR=./var/data/upload
BLOCKS_UPLOAD_DIR=$DATA_DIR/blocks
TIMELINE_UPLOAD_DIR=$DATA_DIR/timeline

CURRENT_DATE=`date +"%m%d%y%H%M"`

echo "Starting preparation for blocks ..."
BLOCK_FILE=$BLOCKS_UPLOAD_DIR/blocks_data-$CURRENT_DATE.cat
cat $BLOCKS_UPLOAD_DIR/* > $BLOCK_FILE

echo "Starting preparation for timeline ..."
TIMELINE_FILE=$TIMELINE_UPLOAD_DIR/timeline_data-$CURRENT_DATE.cat
cat $TIMELINE_UPLOAD_DIR/* > $TIMELINE_FILE

SWAP_FILE="/tmp/tvshoptime.swp"

dos2unix $BLOCK_FILE
dos2unix $TIMELINE_FILE

sed '/^$/d' $BLOCK_FILE > $SWAP_FILE
sed '/^;/d' $SWAP_FILE > $BLOCK_FILE
sed '/^$/d' $TIMELINE_FILE > $SWAP_FILE
sed '/^;/d' $SWAP_FILE > $TIMELINE_FILE

echo "Files are ready!"
