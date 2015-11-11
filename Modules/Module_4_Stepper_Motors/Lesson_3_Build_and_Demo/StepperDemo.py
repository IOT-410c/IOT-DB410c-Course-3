from GPIOLibrary import GPIOProcessor
import time
import math

GP = GPIOProcessor()

try:
    # Stepper Motor Controls
    A1 = GP.getPin34()    # Green
    A2 = GP.getPin24()    # Black
    B1 = GP.getPin33()    # White
    B2 = GP.getPin26()    # Yellow

    A1.out()
    A2.out()
    B1.out()
    B2.out()

    # Delay time 
    T = 0.001

    # Stepper Sequence (Forward ; Reverse)
    SS = [[[0,1,0,1],[1,0,0,1],[1,0,1,0],[0,1,1,0]],
         [[0,1,0,1],[0,1,1,0],[1,0,1,0],[1,0,0,1]]]

    # Forward/Reverse Indicator (0 - Forward, 1 - Reverse)
    FR = 0

    # Step Angle
    SA = 1.8    # 1.8 degrees per step
    
    while True:
        print 'Enter degrees:'
        x = input()
        
        if x < 0:
            FR = 1
            x = abs(x)
        else:
            FR = 0
        
        x = int(x/SA)
        # Run Stepper Motor sequencen
        for i in range(0,x):
            A1.setValue(SS[FR][i%4][0])
            time.sleep(T)
            B1.setValue(SS[FR][i%4][1])
            time.sleep(T)
            A2.setValue(SS[FR][i%4][2])
            time.sleep(T)
            B2.setValue(SS[FR][i%4][3])
            time.sleep(T)
            
        print 'again? [y/n]'
        r = raw_input()
        if r == 'n':
            break

finally:
    GP.cleanup()
