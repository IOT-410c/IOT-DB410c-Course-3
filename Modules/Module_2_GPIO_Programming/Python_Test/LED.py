from GPIOLibrary import GPIOProcessor
import time

GP = GPIOProcessor()

try:

    Pin34 = GP.getPin34()
    Pin34.out()


    for i in range(0,10):
        Pin34.high()
        time.sleep(0.5)
        Pin34.low()
        time.sleep(0.5)



finally:
    GP.cleanup()
