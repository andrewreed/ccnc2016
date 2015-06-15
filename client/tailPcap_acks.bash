#!/bin/bash
tail -c +0 -f $1 | tshark -Y "wlan.fc.type_subtype == 0x0019" -t a -i "-" -l -T fields -e frame.time -e wlan.ta -e wlan_mgt.fixed.sequence 2>/dev/null | cut -c14- | stdbuf -oL scripts/preprocessor_01v2.py | stdbuf -oL scripts/preprocessor_02.py | stdbuf -oL scripts/tally.py $2 $3 | stdbuf -oL scripts/finalCheck.py
