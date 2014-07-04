#!/bin/bash

# Install JRE 7
sudo apt-get install openjdk-7-jre-headless -yq

# Install unzip
sudo apt-get install unzip

# Create user
sudo useradd --home-dir /var/run/eSC-engine -m -U esc-engine

# Copy stuff
sudo cp -r * /

# Change ownership to the new user
sudo chown -R esc-engine:esc-engine /etc/eSC /mnt/workflow /run/eSC-engine /var/log/eSC-engine

# Make scripts executable
sudo chmod a+x /etc/init.d/eSC-engine /usr/local/eSC-engine/bin/*

# Set engine to run on startup
sudo update-rc.d eSC-engine defaults

#####
#sudo vi /etc/hosts < add ip engine-XX
#sudo vi /etc/eSC/engine.xml < set OverriddenIP , change localhost to esc-server (the server's hostname)