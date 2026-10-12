#!/bin/bash

cd /home/kujatic/IdeaProjects/Jasper-Bot/ || exit 1

git fetch origin
git pull

./gradlew release
echo $DISCORD_TOKEN
java -jar /home/kujatic/IdeaProjects/Jasper-Bot/build/release/Discord-bot-1.0.0.jar -Xms50M -Xmx500M 
