#!/bin/bash
rm -rf ./fanctrl
apt install lm-sensors openjdk-11-jdk git -y
git clone https://github.com/ice3x2/fanctrl
cd fanctrl/fanCtrl
chmod 755 ./gradlew
./gradlew build
cd ..
rm -rf /usr/etc/fanctrl
rm /usr/bin/fanctrl
rm -rf /usr/lib/fanctrl

mkdir /usr/etc/fanctrl -p
mkdir /usr/lib/fanctrl -p
pwd
cp ./deploy/*.jar /usr/lib/fanctrl/
cp ./deploy/fanctrl.sh /usr/bin/fanctrl
cp ./deploy/fanctrl.properties /usr/etc/fanctrl/
sed -i 's/$DIR_ROOT"\/fanCtrl-/\/usr\/lib\/fanctrl\/fanCtrl-/' /usr/bin/fanctrl
sed -i 's/$DIR_ROOT\/\*\.log/\/var\/log\/fanctrl.log/' /usr/bin/fanctrl
sed -i 's/logFile=/logFile=\/var\/log\/fanctrl.log/' /usr/etc/fanctrl/fanctrl.properties
