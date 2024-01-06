#!/usr/bin/env bash
fileName="interviewing/src/main/resources/db/migration/V"$(date +%s)".sql"
echo $fileName
touch $fileName
