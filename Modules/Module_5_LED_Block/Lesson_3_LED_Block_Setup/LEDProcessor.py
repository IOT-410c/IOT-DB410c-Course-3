from GPIOLibrary import GPIOProcessor
from CharacterLibrary import Alphabet as AL
import time

class LEDBlock:

    global OP_OP
    NO_OP          = 0x0000
    global OP_0
    OP_0           = 0x0100
    global OP_1
    OP_1           = 0x0200
    global OP_2
    OP_2           = 0x0300
    global OP_3
    OP_3           = 0x0400
    global OP_4
    OP_4           = 0x0500
    global OP_5
    OP_5           = 0x0600
    global OP_6
    OP_6           = 0x0700
    global OP_7
    OP_7           = 0x0800
    global OP_DECODEMODE
    OP_DECODEMODE  = 0x0900
    global OP_INTENSITY
    OP_INTENSITY   = 0x0A00
    global OP_SCANLIMIT
    OP_SCANLIMIT   = 0x0B00
    global OP_SHUTDOWN
    OP_SHUTDOWN    = 0x0C00
    global OP_DISPLAYTEST
    OP_DISPLAYTEST = 0x0F00

    global numOfDevices
    global status
    status = [[0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0],
              [0, 0, 0, 0, 0, 0, 0, 0]]
    global intensityStatus
    intensityStatus = [0, 0, 0, 0, 0, 0, 0, 0]
    global shutdownStatus
    shutdownStatus = [0, 0, 0, 0, 0, 0, 0, 0]
    global scanStatus
    scanStatus = [0, 0, 0, 0, 0, 0, 0, 0]
    global testStatus
    testStatus = [0, 0, 0, 0, 0, 0, 0, 0]
    global Data
    Data   = [0, 0, 0, 0, 0, 0, 0, 0]
    global Code
    global TOTAL
    TOTAL = 0xFF

    def __init__(self, devices=1, intensity=5, scanlimit=7):
        '''Constructor
           ScanLimit set to 7
           Intensity set to 5 out of 0:15
           Uses GPIOs 23, 24 25 as DIN, CS, and CLK respectively'''
        GP = GPIOProcessor()
        global DIN
        global CS
        global CLK
        DIN = GP.getPin34()  # Gpio 
        CS  = GP.getPin33()  # Gpio 
        CLK = GP.getPin32()  # Gpio 
        DIN.out()
        CS.out()
        CLK.out()
        global numOfDevices
        numOfDevices = devices
        for i in range(0,numOfDevices):
            self.shutDown(False,i+1)
        for i in range(0,numOfDevices):
            self.setScanLimit(scanlimit,i+1)
        for i in range(0,numOfDevices):
            self.setIntensity(intensity,i+1)
        self.clearDisplays()
        global Characters
        Characters = AL()

    def getStatus(self, choice=None):
        '''Returns current status'''
        if choice=="intensity":
            return intensityStatus
        elif choice=="scan":
            return scanStatus
        elif choice=="test":
            return testStatus
        elif choice=="shutdown":
            return shutdownStatus
        else:
            return status

    def createInstruction(self, device, opCode, data):
        '''Create an instruction'''
        global Data
        for i in range(0,device):
            Data[numOfDevices-i-1] = opCode | data[i]

    def transferInstruction(self, device, instruction):
        '''Shifts in the instructions'''
        DIN.low()
        CLK.low()
        CS.low()
        for j in range(0, numOfDevices):
            bitmask = 0x8000
            for i in range(0, 16):
                CLK.low()
                if instruction[j] & bitmask == bitmask:
                    DIN.high()
                else:
                    DIN.low()
                CLK.high()
                bitmask = bitmask >> 1
        CS.high()

    def setRow(self, row, data, device=1):
        '''Choose row 1-8 and send data for LEDs in that row'''
        global status
        status[row-1][device-1] = data
        rowOp = row*256            
        self.createInstruction(device, rowOp, status[row-1][0:device])
        self.transferInstruction(device, Data)
#
    def setColumn(self, col, data):
        '''Choose column 1-8 and send data for LEDs in that column'''
        cdata = 2**(8-col)
        bitmask = 0x01
        for i in range(1,9):
            if data & bitmask == bitmask:
                self.setRow(i, cdata)
            bitmask = bitmask << 1

#
    def setLED(self, row, col, value,device=1):
        '''Choose row and column of LED to turn on or off, with TRUE or FALSE'''
        cdata = 2**(8-col)
        if value == 1:
            cdata |= status[row-1][device-1]
        elif value == 0:
            cdata = TOTAL - cdata
            cdata &= status[row-1][device-1]
        else:
            print "On or Off?"
        
        self.setRow(row, cdata)

    def printLetter(self, letter, rotate, device=1):
        '''Displays the given letter'''
        letter = letter.upper()
        global data
        if rotate == True:
            let = Characters.setLetterR(letter)
        else:
            let = Characters.setLetter(letter)
        for i in range(0,8):
            data = let[i]
            self.setRow(i+1,data,device)

    def printNumber(self, number, device=1):
        '''Displays the given number'''
        global data
        num = Characters.setNumber(number)
        for i in range(0,8):
            data = num[i]
            self.setRow(i+1,data,device)

    def printDice(self, number, device=1):
        '''Displays the given number'''
        global data
        num = Characters.setDiceNumber(number)
        for i in range(0,8):
            data = num[i]
            self.setRow(i+1,data,device)

    def printWord(self, word):
        '''Prints the word given one letter at a time'''
        Time = 0.5
        for i in range(0,len(word)):
            if word[i].isdigit() == False:
                self.printLetter(word[i],True)
            else:
                num = word[i]
                self.printNumber(num)
            time.sleep(Time)

    def clearDisplay(self, device=1):
        '''Clears the Display choosen'''
        for i in range(1, 9):
            self.setRow(i,0x00,device)

    def clearDisplays(self):
        '''Clears all of the Displays'''
        for k in range(0,numOfDevices):
            self.clearDisplay(k+1)

    def setIntensity(self, intensity, device):
        '''Sets intensity of LED. min: 0  --- max : 15'''
        global intensityStatus
        intensityStatus[device-1] = intensity
        self.createInstruction(device, OP_INTENSITY, intensityStatus[0:device])
        self.transferInstruction(device, Data)

    def setScanLimit(self, limit, device):
        '''The limit shoud be an integer: 0-7.'''
        global scanStatus
        scanStatus[device-1] = limit
        self.createInstruction(device, OP_SCANLIMIT, scanStatus[0:device])
        self.transferInstruction(device, Data)

    def shutDown(self, boolean, device):
        '''Brings the block into power saving mode.'''
        global shutdownStatus
        if boolean:
            shutdownStatus[device-1] = 0
            self.createInstruction(device, OP_SHUTDOWN, shutdownStatus[0:device])
            self.transferInstruction(device, Data)
        else:
            shutdownStatus[device-1] = 1
            self.createInstruction(device, OP_SHUTDOWN, shutdownStatus[0:device])
            self.transferInstruction(device, Data)

    def displayTest(self, duration, device):
        '''Turn on all LEDs to test the display for the duration specified.'''
        global testStatus
        testStatus[device-1] = 1
        self.createInstruction(device, OP_DISPLAYTEST, testStatus[0:device])
        self.transferInstruction(device, Data)
        time.sleep(duration)
        self.exitTest(device)    

    def exitTest(self, device):
        '''If the LED block is in Test Mode this will exit.'''
        global testStatus
        testStatus[device-1] = 0
        self.createInstruction(device, OP_DISPLAYTEST, testStatus[0:device])
        self.transferInstruction(device, Data)
    
    def cleanup(self):
        DIN.closePin()
        CS.closePin()
        CLK.closePin()

 ## DEMOS ############
    def sixtyFour(self):
        '''Lights up each of the 64 LEDs individually'''
        for i in range(1, 9):
            for j in range(0, 8):
                data = 2**(7-j)
                self.setRow(i, data)
                time.sleep(0.05)
                self.setRow(i, 0)

    def diagonal(self, device=1):
        '''Lights up a wave around the diagonal'''
        for j in range(0, 1):
            a = 0B1
            for i in range(1, 9):
                self.setRow(i, a, device)
                time.sleep(0.025)
                self.setRow(i, 0, device)
                a = a << 1
                a += 1
            a = 0B10000000
            for i in range(1, 9):
                self.setRow(9-i, a, device)
                time.sleep(0.025)
                self.setRow(9-i, 0, device)
                a = a >> 1
                a += 0B10000000

    def swirl(self):
        '''Lights up the Leds in a clockwise swirl pattern'''
        Time = 0.025
        for i in range(1,5):
            for j in range(i,10-i):
                self.setLED(i, j, True)
                time.sleep(Time)
            if i < 4:
                for j in range(i+1,9-i):
                    self.setLED(j, 9-i, True)
                    time.sleep(Time)
            for j in range(i,10-i):
                self.setLED(9-i, 9-j, True)
                time.sleep(Time)
            if i < 4:
                for j in range(i+1,9-i):
                    self.setLED(9-j, i, True)
                    time.sleep(Time)
        time.sleep(0.5)
        self.clearDisplay()

    def allOnOff(self):
        '''Turns all LEDs on one by one and then off'''
        ListValue = [True, False]
        for k in ListValue:
            for i in range(1,9):
                for j in range(1,9):
                    self.setLED(i,j,k)
                    time.sleep(0.025)

    def showAlphabet(self):
        '''Displays all characters of the alphabet'''
        self.printWord("abcdefghijklmnopqrstuvwxyz")
        self.clearDisplay()

    def showDigits(self):
        '''Displays all digits'''
        self.printWord("1234567890")
        self.clearDisplay()

    def helloWorld(self):
        '''Classic Hello World'''
        self.printWord("Hello World!")
        self.clearDisplay(1)
        
