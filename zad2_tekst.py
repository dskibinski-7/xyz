#!/usr/bin/python3
from sense_emu import SenseHat
import json

sense = SenseHat()

with open("/home/pi/server_examples/ANDROID/text_data.json") as f:
	data = json.load(f)
	
text_to_show = data["tekst"]

sense.show_message(text_to_show, text_colour=[255, 0, 0])