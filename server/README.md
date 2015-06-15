# Overview
Server-side code for a program that identifies Netflix videos from wireless traffic.

## Usage
__run_server.bash__ is provided as an easy-to-use script for starting the server. It takes a single command-line argument: the text file of Netflix fingerprints to use as the database. e.g.: `./runServer.bash db/Dataset_A_21_May.txt`

## Compiling
If you would like to modify the code, a script is provided that will compile the code and create a jar file. Simply navigate to the __src__ subdirectory and run it: `./compileServer.bash`
