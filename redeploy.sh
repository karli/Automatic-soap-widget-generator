#!/bin/sh
./stop.sh
git pull
ant build
./start.sh