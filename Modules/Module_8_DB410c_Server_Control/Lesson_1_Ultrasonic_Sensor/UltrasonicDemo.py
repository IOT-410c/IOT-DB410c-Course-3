from GPIOLibrary import GPIOProcessor
import time
import math

GP = GPIOProcessor()

# GPIO assignment
# TRIG      Pin 23
# ECHO      Pin 27
# GREEN     Pin 24
# YELLOW    Pin 25
# RED       Pin 26

try:
    
    # Create GPIO variables
    trig    = GP.getPin34()
    echo    = GP.getPin27()
    green   = GP.getPin24()
    yellow  = GP.getPin25()
    red     = GP.getPin26()

    trig.out()
    echo.input()
    green.out()
    yellow.out()
    red.out()

    # Duration of Activation (seconds)
    D = 10 

    # Approximate Speed of Sound (cm/s)
    speed = 34029

    print "Begin"
    timeout=time.time()+D
    while True:
        trig.low()

        time.sleep(0.5)

        trig.high()
        time.sleep(0.00001)
        trig.low()
        print echo.getValue()
        # Wait for pulse to be sent
        while echo.getValue()==0:
            pulse_start=time.time()
        print echo.getValue()
        # Pulse is out when echo = 1
        while echo.getValue()==1:
            pulse_end=time.time()
        print echo.getValue()
        # Calculate total pulse duration
        pulse_duration=pulse_end-pulse_start

        # Use pulse duration to calculate distance
        # Remember that the pulse has to go there and come back
        distance=pulse_duration*speed/2

        distance=round(distance,2)

        # LED's light up as an object gets closer to the sensor
        if distance > 30:
            red.low()
            yellow.low()
            green.low()
        elif distance < 30 and distance > 20:
            red.low()
            yellow.low()
            green.high()
        elif distance < 20 and distance > 10:
            red.low()
            yellow.high()
            green.high()
        elif distance < 10:
            red.high()
            yellow.high()
            green.high()

        print "Distance",distance,"cm"
        
        if time.time() > timeout:
            break

    print "Done"
    
finally:
    GP.cleanup()
