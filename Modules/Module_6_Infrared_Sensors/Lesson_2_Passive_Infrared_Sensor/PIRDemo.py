from GPIOLibrary import GPIOProcessor
import time

GP = GPIOProcessor()

try:

    # Set up GPIO
    vcc = GP.getPin34()
    vcc.out()

    while True:
        print 'Turn on? [y/n]'
        r = raw_input()
        if r == 'y':
            vcc.high()
            
            # Sensor is on
            print 'Sensor is on.'
            print 'Exit? [y]'
            r = raw_input()
            if r == 'y':
                vcc.low()
            
        else:
            break

finally:
    GP.cleanup()
