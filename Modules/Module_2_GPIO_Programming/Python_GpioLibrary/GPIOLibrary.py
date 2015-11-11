class GPIOProcessor:
    '''This is the GPIO Processor class.  IT is used to create GPIO objects and
    keep track of them. Pins 23-34 have the their corresponding GPIO number
    stored so that they may easily called with the appropriate getPin method.
    '''

    def __init__(self):
        self.GPIOList = []
        
    def getPin(self, pin_number):
        '''The getPin method is used to create a GPIO object. After a GPIO is
        created it is added to a list to keep track of all GPIO's being used.
        '''
        pin = GPIO(pin_number)
        pin.openPin()
        self.GPIOList.append(pin)
        return pin

    def getPin23(self):
        return self.getPin(36)

    def getPin24(self):
        return self.getPin(12)

    def getPin25(self):
        return self.getPin(13)

    def getPin26(self):
        return self.getPin(69)

    def getPin27(self):
        return self.getPin(115)

    def getPin28(self):
        return self.getPin(901)

    def getPin29(self):
        return self.getPin(24)

    def getPin30(self):
        return self.getPin(25)

    def getPin31(self):
        return self.getPin(35)

    def getPin32(self):
        return self.getPin(34)

    def getPin33(self):
        return self.getPin(28)

    def getPin34(self):
        return self.getPin(33)

    def cleanup(self):
        '''The cleanup method should be called at the end of every program that
        uses GPIO's.  It changes the GPIO's back to inputs(which is safer when
        not in use) and then unexports all GPIO's that were used.
        '''
        for pin in self.GPIOList:
            pin.input()
            pin.closePin()
        self.GPIOList = []
	
class GPIO:
    '''This is the GPIO class.  This class contains methods that can be used to
    control the GPIO's.  It is used by the GPIOProcessor class. These methods
    open the appropriate file to export GPIOs or change direction and value. 
    '''
    global PATH
    PATH = "/sys/class/gpio/"
    
    def __init__(self,pin_number):
        self.pin_number = pin_number

    def openPin(self):
        file = open(PATH + "export",'w')
        file.write(str(self.pin_number))
        file.close()
        
    def closePin(self):
        file = open(PATH + "unexport",'w')
        file.write(str(self.pin_number))
        file.close()

    def setDirection(self,direction):
        file = open(PATH + "gpio" + str(self.pin_number) + "/direction",'w')
        file.write(str(direction))
        file.close()

    def setValue(self,value):
        file = open(PATH + "gpio" + str(self.pin_number) + "/value",'w')
        file.write(str(value))
        file.close()
        
    def getDirection(self):
        file = open(PATH + "gpio" + str(self.pin_number) + "/direction",'r')
        direction = file.read()
        file.close()
        return direction
    
    def getValue(self):
        file = open(PATH + "gpio" + str(self.pin_number) + "/value",'r')
        value = file.read()
        file.close()
        return int(value)

    def high(self):
        self.setValue(1)

    def low(self):
        self.setValue(0)

    def input(self):
        self.setDirection("in")

    def out(self):
        self.setDirection("out")    
        







        
        
