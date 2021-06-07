#!/usr/bin/python3
from sense_emu import SenseHat
import json

sense = SenseHat()

with open("/home/pi/server_examples/ANDROID/led_data.json") as f:
	data = json.load(f)
	
kolor = data["kolor"]
yvalue = data["wiersz"]
xvalue = data["kolumna"]

print("Skrypt pythona")
print(kolor)

if kolor == "red":
	sense.set_pixel(xvalue,yvalue,(255,0,0))
	print("red set!")	
elif kolor == "green":
	sense.set_pixel(xvalue,yvalue,(0,255,0)) 
	print("green set!")
elif kolor == "blue":
	sense.set_pixel(xvalue,yvalue,(0,0,255))
	print("blue set!")	
else:
    print("Wrong colour!")
