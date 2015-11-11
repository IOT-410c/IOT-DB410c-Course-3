from LEDProcessor import LEDBlock
import time
import math


# Number of devices (we used two)
numOfDevices = 2
led = LEDBlock(numOfDevices)

# Time for letter shift (seconds)
T = 0.2
  
try:   
    while True:
        string = raw_input("Message: ")
        if string == None:
            break
        else:
            string = string.upper()
            for i in range(0,len(string)):
                if i == 0:
                    led.printLetter(' ',True,1)
                    led.printLetter(string[i],True,2)
                    time.sleep(T)
                elif i == len(string)-1:
                    led.printLetter(string[i],True,1)
                    led.printLetter(' ',True,2)
                    time.sleep(T)
                else:
                    led.printLetter(string[i],True,1)
                    led.printLetter(string[i+1],True,2)
                    time.sleep(T)
            led.printLetter(' ',True,1)

finally:
    led.clearDisplays()
    led.cleanup()
    
