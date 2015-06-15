# Overview
Client-side code for a program that identifies Netflix videos from wireless traffic. This "client" is actually a series of Python scripts that run via a Linux pipeline, as opposed to a single program. These modular scripts allow you to observe intermediate data by simply breaking the pipeline and observing the output via stdout.

## Usage
You do not run the modular scripts directly. Instead, there are easy-to-use "master scripts" that you use. The master scripts are as follows:
* __readFromMon0__: This script allows you to read directly from a live wireless interface that has been setup in _monitor mode_.
* __tailPcap__: This script allows you to read from a .pcap file that is actively being written to.
* __readFromPcap__: This script allows you to read from a .pcap that is no longer being written to.

Moreoever, each script has an __acks__ variant. These acks-based versions use BlockAcks instead of data frames to estimate throughput. It is these acks-based versions that are used in the WIFS 2015 paper.

## Creating mon0
First, download the latest Kali Linux VMware image and run it with a USB wireless adapter. Then, from within the terminal, issue the following commands:
    (1) root@kali:~# lsusb
    (2) root@kali:~# iwconfig
    (3) root@kali:~# airmon-ng check kill
    (4) root@kali:~# airmon-ng start wlan1 11

(1) Verifies that the USB adapter is connected to Kali.
(2) Shows the interface associated with the USB adapter.
(3) Kills processes that will interfere with monitor mode.
(4) Creates the mon0 (monitor mode) interface. You must specify the wireless interface shown in (2). Optionally, you can specify a channel to stay on.

## Creating a pcap
Our test data (available on Google Drive; see the main folder's README) was created using the following command:
    root@kali:~# timeout 5m tcpdump -y ieee802_11_radio -i mon0 -w 01.pcap

This command ensures that the capture only runs for 5 minutes (not necessary for real world usage). Additionally, the optional -y argument will ensure that Radiotap headers are included in the trace (not necessary, but nice to have).

