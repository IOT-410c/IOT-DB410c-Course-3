class Alphabet:
    
    global data
    data = [0,0,0,0,0,0,0,0]
    
    def setLetter(self, let):
        '''Database for 5x5 letters'''
        if let == "A":
            data = [0,0B011100,0B100010,0B111110,0B100010,0B100010,0,0]
            return data
        elif let == "B":
            data = [0,0B111100,0B100010,0B111100,0B100010,0B111100,0,0]
            return data
        elif let == "C":
            data = [0,0B011110,0B100000,0B100000,0B100000,0B011110,0,0]
            return data
        elif let == "D":
            data = [0,0B111100,0B100010,0B100010,0B100010,0B111100,0,0]
            return data
        elif let == "E":
            data = [0,0B111110,0B100000,0B111000,0B100000,0B111110,0,0]
            return data
        elif let == "F":
            data = [0,0B111110,0B100000,0B111000,0B100000,0B100000,0,0]
            return data
        elif let == "G":
            data = [0,0B011110,0B100000,0B101110,0B100010,0B011100,0,0]
            return data
        elif let == "H":
            data = [0,0B100010,0B100010,0B111110,0B100010,0B100010,0,0]
            return data
        elif let == "I":
            data = [0,0B111110,0B001000,0B001000,0B001000,0B111110,0,0]
            return data
        elif let == "J":
            data = [0,0B000010,0B000010,0B000010,0B100010,0B011100,0,0]
            return data
        elif let == "K":
            data = [0,0B100010,0B100100,0B111000,0B100100,0B100010,0,0]
            return data
        elif let == "L":
            data = [0,0B100000,0B100000,0B100000,0B100000,0B111110,0,0]
            return data
        elif let == "M":
            data = [0,0B100010,0B110110,0B101010,0B100010,0B100010,0,0]
            return data
        elif let == "N":
            data = [0,0B100010,0B110010,0B101010,0B100110,0B100010,0,0]
            return data
        elif let == "O":
            data = [0,0B011100,0B100010,0B100010,0B100010,0B011100,0,0]
            return data
        elif let == "P":
            data = [0,0B111100,0B100010,0B111100,0B100000,0B100000,0,0]
            return data
        elif let == "Q":
            data = [0,0B011100,0B100010,0B101010,0B100100,0B011010,0,0]
            return data
        elif let == "R":
            data = [0,0B111100,0B100010,0B111100,0B100010,0B100010,0,0]
            return data
        elif let == "S":
            data = [0,0B011110,0B100000,0B011100,0B000010,0B111100,0,0]
            return data
        elif let == "T":
            data = [0,0B111110,0B001000,0B001000,0B001000,0B001000,0,0]
            return data
        elif let == "U":
            data = [0,0B100010,0B100010,0B100010,0B100010,0B011100,0,0]
            return data
        elif let == "V":
            data = [0,0B100010,0B100010,0B100010,0B010100,0B001000,0,0]
            return data
        elif let == "W":
            data = [0,0B100010,0B100010,0B101010,0B110110,0B100010,0,0]
            return data
        elif let == "X":
            data = [0,0B100010,0B010100,0B001000,0B010100,0B100010,0,0]
            return data
        elif let == "Y":
            data = [0,0B100010,0B010100,0B001000,0B001000,0B001000,0,0]
            return data
        elif let == "Z":
            data = [0,0B111110,0B000100,0B001000,0B010000,0B111110,0,0]
            return data
        elif let == "!":
            data = [0,0B001000,0B001000,0B001000,0B000000,0B001000,0,0]
            return data
        elif let == "?":
            data = [0,0B011100,0B100010,0B001100,0B000000,0B001000,0,0]
            return data
        elif let == " ":
            data = [0,0,0,0,0,0,0,0]
            return data

    def setLetterR(self, let):
        '''Database for 5x5 letters'''
        if let == "A":
            data = [0,0,0B111100,0B001010,0B001010,0B001010,0B111100,0]
            return data
        elif let == "B":
            data = [0,0,0B111110,0B101010,0B101010,0B101010,0B010100,0]
            return data
        elif let == "C":
            data = [0,0,0B011100,0B100010,0B100010,0B100010,0B100010,0]
            return data
        elif let == "D":
            data = [0,0,0B111110,0B100010,0B100010,0B100010,0B011100,0]
            return data
        elif let == "E":
            data = [0,0,0B111110,0B101010,0B101010,0B100010,0B100010,0]
            return data
        elif let == "F":
            data = [0,0,0B111110,0B001010,0B001010,0B000010,0B000010,0]
            return data
        elif let == "G":
            data = [0,0,0B011100,0B100010,0B101010,0B101010,0B011010,0]
            return data
        elif let == "H":
            data = [0,0,0B111110,0B001000,0B001000,0B001000,0B111110,0]
            return data
        elif let == "I":
            data = [0,0,0B100010,0B100010,0B111110,0B100010,0B100010,0]
            return data
        elif let == "J":
            data = [0,0,0B010000,0B100000,0B100000,0B100000,0B011110,0]
            return data
        elif let == "K":
            data = [0,0,0B111110,0B001000,0B001000,0B010100,0B100010,0]
            return data
        elif let == "L":
            data = [0,0,0B111110,0B100000,0B100000,0B100000,0B100000,0]
            return data
        elif let == "M":
            data = [0,0,0B111110,0B000100,0B001000,0B000100,0B111110,0]
            return data
        elif let == "N":
            data = [0,0,0B111110,0B000100,0B001000,0B010000,0B111110,0]
            return data
        elif let == "O":
            data = [0,0,0B011100,0B100010,0B100010,0B100010,0B011100,0]
            return data
        elif let == "P":
            data = [0,0,0B111110,0B001010,0B001010,0B001010,0B000100,0]
            return data
        elif let == "Q":
            data = [0,0,0B011100,0B100010,0B101010,0B010010,0B101100,0]
            return data
        elif let == "R":
            data = [0,0,0B111110,0B001010,0B001010,0B001010,0B110100,0]
            return data
        elif let == "S":
            data = [0,0,0B100100,0B101010,0B101010,0B101010,0B010010,0]
            return data
        elif let == "T":
            data = [0,0,0B000010,0B000010,0B111110,0B000010,0B000010,0]
            return data
        elif let == "U":
            data = [0,0,0B011110,0B100000,0B100000,0B100000,0B011110,0]
            return data
        elif let == "V":
            data = [0,0,0B001110,0B010000,0B100000,0B010000,0B001110,0]
            return data
        elif let == "W":
            data = [0,0,0B111110,0B010000,0B001000,0B010000,0B111110,0]
            return data
        elif let == "X":
            data = [0,0,0B100010,0B010100,0B001000,0B010100,0B100010,0]
            return data
        elif let == "Y":
            data = [0,0,0B000010,0B000100,0B111000,0B000100,0B000010,0]
            return data
        elif let == "Z":
            data = [0,0,0B100010,0B110010,0B101010,0B100110,0B100010,0]
            return data
        elif let == "!":
            data = [0,0,0B000000,0B000000,0B101110,0B000000,0B000000,0]
            return data
        elif let == "?":
            data = [0,0,0B000100,0B000010,0B101010,0B001010,0B000100,0]
            return data
        elif let == " ":
            data = [0,0,0,0,0,0,0,0]
            return data

    def setNumber(self, num):
        '''Database for 5x5 numbers'''
        if num == 0 or num == "0":
            data = [0,0B011100,0B100110,0B101010,0B110010,0B011100,0,0]
            return data
        elif num == 1 or num == "1":
            data = [0,0B111000,0B001000,0B001000,0B001000,0B111110,0,0]
            return data
        elif num == 2 or num == "2":
            data = [0,0B111100,0B000010,0B011100,0B100000,0B111110,0,0]
            return data
        elif num == 3 or num == "3":
            data = [0,0B111100,0B000010,0B001100,0B000010,0B111100,0,0]
            return data
        elif num == 4 or num == "4":
            data = [0,0B100100,0B100100,0B100100,0B111110,0B000100,0,0]
            return data
        elif num == 5 or num == "5":
            data = [0,0B111110,0B100000,0B111100,0B000010,0B111100,0,0]
            return data
        elif num == 6 or num == "6":
            data = [0,0B011110,0B100000,0B111100,0B100010,0B011100,0,0]
            return data
        elif num == 7 or num == "7":
            data = [0,0B111110,0B000010,0B000100,0B001000,0B001000,0,0]
            return data
        elif num == 8 or num == "8":
            data = [0,0B011100,0B100010,0B011100,0B100010,0B011100,0,0]
            return data
        elif num == 9 or num == "9":
            data = [0,0B011100,0B100010,0B011110,0B000010,0B111100,0,0]
            return data

    def setDiceNumber(self, num):
        '''Database for 5x5 dice numbers'''
        if num == 1:
            data = [0,0B00000000,0B00000000,0B00011000,0B00011000,0B00000000,0B00000000,0]
            return data
        elif num == 2:
            data = [0,0B00000110,0B00000110,0B00000000,0B00000000,0B01100000,0B01100000,0]
            return data
        elif num == 3:
            data = [0,0B00000011,0B00000011,0B00011000,0B00011000,0B11000000,0B11000000,0]
            return data
        elif num == 4:
            data = [0,0B01100110,0B01100110,0B00000000,0B00000000,0B01100110,0B01100110,0]
            return data
        elif num == 5:
            data = [0,0B11000011,0B11000011,0B00011000,0B00011000,0B11000011,0B11000011,0]
            return data
        elif num == 6:
            data = [0,0B11011011,0B11011011,0B00000000,0B00000000,0B11011011,0B11011011,0]
            return data
