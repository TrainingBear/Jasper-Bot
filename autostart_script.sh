#!/bin/bash

cd /home/kujatic/IdeaProjects/Jasper-Bot/ || exit 1

git fetch origin
git pull

./gradlew shadowJar
echo $DISCORD_TOKEN
java -jar build/libs/Discord-bot-1.0.0-all.jar -Xmx500M -Xms400M
