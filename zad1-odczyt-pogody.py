#!/usr/bin/python3

from sense_emu import SenseHat
import sys
import getopt
import json
import time

sense = SenseHat()

cz_probkowania = 0.1
dict_data = {"temperature" : 0.0, "humidity" : 0.0, "pressure" : 0.0}

try:
	while 1: 
		dict_data["temperature"]= sense.get_temperature()
		dict_data["humidity"]= sense.get_humidity()
		dict_data["pressure"]= sense.get_pressure()

		try:
			datafile = open("/home/pi/server_examples/ANDROID/data.json", "w")
			datafile.write(json.dumps(dict_data))
		except:
			print("write error")
		finally:
			datafile.close()
		time.sleep(cz_probkowania)
		
except KeyboardInterrupt:
	pass

