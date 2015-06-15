# Overview
This is the GitHub repo for the code used in the IEEE WIFS 2015 paper. You can obtain the .pcap's from our tests from:

https://drive.google.com/folderview?id=0B3cMrOhPT_6zfjl6WWFMZHgwSFVZS1lUS0xGRWdMMF9tektEWmxSZUdwVnl6YkoyVGEyTlk&usp=sharing

## Usage
Perhaps the easiest way to get started is to run the program on our .pcap's:

1. Open a terminal, navigate to the server subdirectory, and start the server with the provided script. Make sure to specify a fingerprint dataset, like so: `./runServer.bash db/Dataset_A_21_May.txt`
2. Open a second terminal, navigate to the client subdirectory, and run the client on a .pcap, like so: `./readFromPcap_acks.bash /root/tests/01.pcap 127.0.0.1 10007`

Further information on the server and client can be obtained in their respective READMEs.

