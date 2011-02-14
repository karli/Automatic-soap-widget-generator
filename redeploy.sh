#!/bin/sh
./stop.sh
git pull
ant build-all
./start.sh