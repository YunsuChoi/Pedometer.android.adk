package com.hardkernel.android.adk;

public class BMP180 {
	private byte mRegisterAddr = (byte) 0xaa;
	private short AC1;
	private short AC2;
	private short AC3;
	private int AC4;
	private int AC5;
	private int AC6;
	private short B1;
	private short B2;
	private short MB;
	private short MC;
	private short MD;
	
	private long b5;
	
	private boolean mEnable;
	
	public BMP180() {
		mEnable = false;
	}
	
	public byte getRegisterAddress() {
		return mRegisterAddr;
	}
	
	public void setNextRegisterAddress() {
		mRegisterAddr += 2;
	}
	
	public void setCalibrationData(
			short ac1, short ac2, short ac3,
			int ac4, int ac5, int ac6, 
			short b1, short b2, short mb, short mc, short md) {
		AC1 = ac1;
		AC2 = ac2;
		AC3 = ac3;
		AC4 = ac4;
		AC5 = ac5;
		AC6 = ac6;
		B1 = b1;
		B2 = b2;
		MB = mb;
		MC = mc;
		MD = md;
		
		mEnable = true;
		mRegisterAddr = (byte) 0xaa;
	}
	
	public boolean isAvailable() {
		return mEnable;
	}
	
	public long calculateTrueTemperature(int UT) {
    	long x1, x2;
    	x1 = ((UT - AC6) * AC5) >> 15;
    	x2 = (MC << 11) / (x1 + MD);
    	b5 = x1 + x2 - 4000;
    	return (x1 + x2 + 8) >> 4;
    }
    
    public long calculateTruePressure(int UP) {
    	long x1, x2, x3, b3;
    	long b4, b7;
        long p;
        long b6 = b5;
        
        x1 = (b6 * b6) >> 12; 
        x1 *= B2;
        x1 >>= 11; 

        x2 = AC2 * b6;
        x2 >>= 11; 

        x3 = x1 + x2; 
        
        b3 = (((long)AC1) * 4 + x3) + 2; 
        b3 >>= 2;

        x1 = (AC3 * b6) >> 13; 
        x2 = (B1 * ((b6 * b6) >> 12)) >> 16; 
        x3 = (x1 + x2 + 2) >> 2;
        b4 = (AC4 * (long)(x3 + 32768)) >> 15; 

        b7 = ((long)UP - b3) * 50000;
        p = ((b7 < 0x80000000) ? ((b7 << 1) / b4) : ((b7 / b4) * 2));

        x1 = p >> 8;
        x1 *= x1; 
        x1 = (x1 * 3038) >> 16; 
        x2 = (-7357 * p) >> 16; 
        p += (x1 + x2 + 3791) >> 4;

        return p;
    }
    
    public long calculateAltitude(long pressure) {
    	return (long) (44330.0 * (1.0 - Math.pow(pressure / 101325.0, 0.19)));
    }
}
