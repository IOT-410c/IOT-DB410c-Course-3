from LEDProcessor import LEDBlock
import time
import math
import random as R

# Number of devices (we used two)
numOfDevices = 2
led = LEDBlock(numOfDevices)

# For 6 sided dice
a=1
b=6

# Possible expansion up to 8 devices
L = [1,2,3,4,5,6,7,8]
L = L[0:numOfDevices]

  
try: 
    while True:
        r = raw_input('Roll Dice? [y/n] ')
        if r == 'y':
            for i in range(0,numOfDevices):
                c = R.randint(a,b)
                led.printDice(c,L[i])

        else:
            break         
            
finally:
    led.clearDisplays()
    led.cleanup()
