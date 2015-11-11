from GPIOLibrary import GPIOProcessor
import time

GP = GPIOProcessor()

# GPIO Assignments
#Din    = 27
#A1     = 34	Green
#A2     = 33	White
#A3     = 24   	Black
#A4     = 26	Yellow
#PIR    = 29
#Ind    = 30

Din = GP.getPin27()
Din.input()
A1 = GP.getPin34()
A1.out()
A2 = GP.getPin33()
A2.out()
A3 = GP.getPin24()
A3.out()
A4 = GP.getPin26()
A4.out()
PIR = GP.getPin29()
PIR.out()
PIR.low()
Ind = GP.getPin30()
Ind.out()
Ind.low()

# Remote Average Pulse
M = 800

# Stepper Motor Delay
t = 0.001

# Stepper Motor Sequence (Forward / Reverse)
A = [[[0,1,0,1],[1,0,0,1],[1,0,1,0],[0,1,1,0]],
     [[0,1,0,1],[0,1,1,0],[1,0,1,0],[1,0,0,1]]]

# Indicators
FR = 0
PIR_status = 0

# Number of clicks
n_PIR   = 1
n_90    = 2
n_R90   = 3
n_180   = 4

try:
    print 'Calibrate? [y/n]'
    r = raw_input()
    if r == 'y':
        while True:
            print 'Click button 5 times.'
            counter = 0
            time.sleep(0.2)
            timeout = time.time() + 2.5
            while True:
                if Din.getValue() == 0:
                    counter += 1
                if time.time() > timeout:
                    break;
            
            M = counter/5
            print M
            print 'Retry? [y/n]'
            r = raw_input()
            if r == 'n':
                break


    while True:
        read = 0
        counter = 0
        timeout = time.time() + 0.2

        # Determine if read mode should be activated
        while True:
            if Din.getValue() == 0:
                counter += 1
            if counter > 0.2*M:               
                read = 1
            if time.time() > timeout:
                break;

        # Enter read mode
        if  read == 1:
            Ind.high()
            x = 0
            counter = 0
            print 'Read:'
            stop_time = time.time() + 2
            while True:
                if Din.getValue() == 0:
                    counter += 1

                if time.time() > stop_time:
                    break

            # Decide what was chosen
            Ind.low()
            time.sleep(0.5)
            if counter < 0.5*M:
                print 'No Input'
                
            elif n_PIR*M - 0.5*M < counter < n_PIR*M + 0.5*M:
                if PIR_status == 0:
                    PIR.high()
                    PIR_status = 1
                    print 'PIR on'
                else:
                    PIR.low()
                    PIR_status = 0
                    print 'PIR off'
                    
            elif n_90*M - 0.5*M < counter < n_90*M + 0.5*M:
                FR = 0
                x = int(90/1.8)
                print '90'
            elif n_R90*M - 0.5*M < counter < n_R90*M + 0.5*M:
                FR = 1
                x = int(90/1.8)
                print '-90'
            elif n_180*M - 0.5*M < counter < n_180*M + 0.5*M:
                FR = 0
                x = int(180/1.8)
                print '180'
            else:
                clicks = counter/M
                print counter

            # Sequencing for Stepper Motor
            for i in range(0,x):
                A1.setValue(A[FR][i%4][0])
                time.sleep(t)
                A2.setValue(A[FR][i%4][1])
                time.sleep(t)
                A3.setValue(A[FR][i%4][2])
                time.sleep(t)
                A4.setValue(A[FR][i%4][3])
                time.sleep(t)


finally:
    GP.cleanup()



