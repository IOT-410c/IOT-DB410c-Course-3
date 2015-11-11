package net.calit2.mooc.iot_db410c.ledblock;
import android.util.Log;

//import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor;
import net.calit2.mooc.iot_db410c.db410c_gpiolib.GpioProcessor.Gpio;
/**
 * Created by Robert and Eric on 1/18/15.
 */
public class LEDProcessor {

    public final static char NO_OP=0x0000;
    public final static char OP_DIGIT0 = 0x0100;
    public final static char OP_DIGIT1 = 0x0200;
    public final static char OP_DIGIT2 = 0x0300;
    public final static char OP_DIGIT3 = 0x0400;
    public final static char OP_DIGIT4 = 0x0500;
    public final static char OP_DIGIT5 = 0x0600;
    public final static char OP_DIGIT6 = 0x0700;
    public final static char OP_DIGIT7 = 0x0800;
    public final static char OP_DECODEMODE = 0x0900;
    public final static char OP_INTENSITY = 0x0A00;
    public final static char OP_SCANLIMIT = 0x0B00;
    public final static char OP_SHUTDOWN = 0x0C00;
    public final static char OP_DISPLAYTEST = 0x0F00;
    private static final String TAG = "LEDProcessor";
    // private final static char BITMASK=0x8000;

    // private GpioProcessor  gpioProcessor;
    private Gpio DIN;
    private Gpio CS;
    private Gpio CLK;
    private char[] data;
    private int numOfDevices;


    // YOU MUST CREATE CODE THAT TAKES INTO ACCOUNT # OF LEDBLOCKS
    // Also, set Scanlimit to 7, and set shutdown to true.
    public LEDProcessor(int devices, Gpio din, Gpio clk, Gpio cs){
      //  gpioProcessor = new GpioProcessor();
        DIN = din;//gpioProcessor.getPin23();
        CS = cs;//gpioProcessor.getPin24();
        CLK = clk;//gpioProcessor.getPin25();
        DIN.out();
        CS.out();
        CLK.out();

        numOfDevices = devices;
        data = new char[numOfDevices];

        initialInstructions();

        // CLK.high();
    }

    //Instructions are taken in 1 row at a time.
    //This means you need to specify the op_code for the specific row (i+1) and
    //the data for the row (00001111 = 0x0F).
    public void setRow(int device, int row, char dataCode){

        char opCode;
        switch (row){
            case 0: opCode = OP_DIGIT0;
                break;
            case 1: opCode = OP_DIGIT1;
                break;
            case 2: opCode = OP_DIGIT2;
                break;
            case 3: opCode = OP_DIGIT3;
                break;
            case 4: opCode = OP_DIGIT4;
                break;
            case 5: opCode = OP_DIGIT5;
                break;
            case 6: opCode = OP_DIGIT6;
                break;
            case 7: opCode = OP_DIGIT7;
                break;
            default: opCode = NO_OP;
                break;
        }
        createInstruction(device, opCode, dataCode);
        transferInstruction(device, data);
    }

    public void clearDisplay(int device){
        for(int j = 0; j < 8; j++){
            setRow(device, j, (char)0);
        }
    }



    /*
        THIS SHOULD ONLY BE FOR ONE INSTRUCTION.
        Transfer pseudo_code:
        Load low.
        Loop until 16*devices iterations has occurred:
            clock low
            extract bit, check to see if high or low(Data goes in order from MSB to LSB(D-15 to D-0))
            write to data pin
            clock high
        Load high.
    */
    public void transferInstruction(int device, char[] instruction){
        Log.i(TAG, "Transfering Instruction");
        CS.low();
        String str="";

        for(int i = device; i >= 0; i--){
           char bitMask = 0x8000;
            for(int j = 16; j > 0; j--) {

                CLK.low();
                if ((char) (instruction[i] & bitMask) == bitMask) {
                    DIN.high();

                } else {
                    DIN.low();
                }

                CLK.high();
                bitMask = (char) (bitMask >> 1);

            }
        }

        CS.high();
    }

    /*
    Creates a 16-bit instruction to the led display. It is most probably best to utilize the
    char array, because in java, they are unsigned two-byte values(the only unsigned value).
    We cant use short because it is a signed data-type; we can use byte if we want, but it is
    also signed, and you need 2 for every instruction, so its easier just to use char.
    Instruction is in this format: D15-D12, D11-D8, D7-D0; Where D0-D7 is your data, D8-D11 is
    your op_code, and D15-D12 is nothing, so can be any 4 values you want it to be(lets make it
     0000)
    */
    public void createInstruction(int device, char opCode, char dataCode){
        data[device] = (char) (opCode | dataCode);
    }

    public void setIntensity(int device, char intensityLevel){

        createInstruction(device, OP_INTENSITY, intensityLevel);

        transferInstruction(device, data);
    }

    private void initialInstructions(){
        for(int i = 0; i <numOfDevices;i++){
            shutDown(i);
            turnOn(i);
            testOff(i);
            scanLimit(i, (char) 7);
            setIntensity(i, (char) 15);
            clearDisplay(i);

        }

        /*Operate = new char[numOfDevices];
        ShutDown = new char[numOfDevices];
        ScanLimit = new char[numOfDevices];
        Intensity = new char[numOfDevices];
        Test = new char[numOfDevices];*/

       /* createInstruction(0, OP_SHUTDOWN, (char)1);
        for(int i = 0; i < numOfDevices; i++) {
            Operate[i] = data[0];
        }

        createInstruction(0, OP_SHUTDOWN, (char)0);
        for(int i = 0; i < numOfDevices; i++) {
            ShutDown[i] = data[0];
        }

        createInstruction(0, OP_SCANLIMIT, (char)7);
        for(int i = 0; i < numOfDevices; i++) {
            ScanLimit[i] = data[0];
        }*/
    }

    public void testOn(int device){
        createInstruction(device, OP_DISPLAYTEST, (char)1);
        transferInstruction(device, data);
    }

    public void testOff(int device){
        createInstruction(device, OP_DISPLAYTEST, (char)0);
        transferInstruction(device, data);
    }

    public void turnOn(int device){
        createInstruction(device, OP_SHUTDOWN, (char)1);
        transferInstruction(device, data);
    }

    public void shutDown(int device){
        createInstruction(device, OP_SHUTDOWN, (char)0);
        transferInstruction(device, data);
    }

    public void scanLimit(int device, char dataCode){
        createInstruction(device , OP_SCANLIMIT, dataCode);
        transferInstruction(device, data);
    }


    //Here is your main transfer function. Essentially loop through an array setRows or
    // maybe setCols or what not.
    //First turn off shutdown, then set the intensity of the LED's, clearDisplay,
    //and then begin your transfer while loop.
    public void transfer(char[] data){
       for(int i = 0;i<data.length;i++){
          setRow(0,i,data[i]);
       }


    }

    public int getDevicesNum(){
        return numOfDevices;
    }
}