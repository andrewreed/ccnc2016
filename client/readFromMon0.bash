#!/bin/bash
sudo tcpdump -i mon0 -n -w '-' -U | tshark -t a -i "-" -l 2>/dev/null | stdbuf -oL cut -d ' ' -f1,4,6,9 | stdbuf -oL grep SN | stdbuf -oL tr -d "SN=," | stdbuf -oL tr " " "\t" | stdbuf -oL scripts/preprocessor_01.py | stdbuf -oL scripts/preprocessor_02.py | stdbuf -oL scripts/tally.py $1 $2 | stdbuf -oL scripts/finalCheck.py
