package dbf_conv;

import java.io.IOException;

public class DataConv {
	public static long int2uint(int num) {
//		return (long)(num & 0xFFFFFFFFL);
		return ((long) num) & 0xffffffffL;
	}
	public static int short2ushort(short num) {
		return ((int) num) & 0xffff;
	}
	public static short byte2ubyte(byte num) {
		return (short) (((short) num) & 0xff);
	}
	public static byte[] convIntToByte(int num) {
		byte byte1 = (byte)((num & 0x000000FF));
		byte byte2 = (byte)((num & 0x0000FF00) >>> 8);
		byte byte3 = (byte)((num & 0x00FF0000) >>> 16);
		byte byte4 = (byte)((num & 0xFF000000) >>> 24);
		byte[] result = new byte[] {byte1, byte2, byte3, byte4};
		return result;
	}
	public static byte[] convShortToByte(short num) {
		byte byte1 = (byte)((num & 0x000000FF));
		byte byte2 = (byte)((num & 0x0000FF00) >>> 8);
		byte[] result = new byte[] {byte1, byte2};
		return result;
	}
	public static byte[] convStringToByte(String strData, int nMax, String characterEncoding) throws IOException {
		byte[] bytesTemp = strData.getBytes(characterEncoding);
		byte[] result = new byte[nMax];
		for (int i = 0; i < bytesTemp.length; i++) {
			if (i >= result.length)		break;
			result[i] = bytesTemp[i];			
		}
		return result;
	}
	
	public static int convBytesToInt(byte[] bytes) {
		return convBytesToInt(bytes, 0);
	}
	public static int convBytesToInt(byte[] bytes, int offset) {
		int num = 0x00000000;
		num = (num << 8) | (bytes[offset+3] & 0x000000FF);
		num = (num << 8) | (bytes[offset+2] & 0x000000FF);
		num = (num << 8) | (bytes[offset+1] & 0x000000FF);
		num = (num << 8) | (bytes[offset+0] & 0x000000FF);
		return num;
	}
	public static short convBytesToShort(byte[] bytes) {
		int num = 0x00000000;
		num = (num << 8) | (bytes[1] & 0x000000FF);
		num = (num << 8) | (bytes[0] & 0x000000FF);
		return (short) num;
	}
	public static String printBytes(byte[] bytes) {
		StringBuilder strBuilder = new StringBuilder();
		for (byte byteData : bytes) {
			strBuilder.append(Integer.toString((byteData & 0xff)+0x100, 16).substring(1)+" ");
		}
		strBuilder.delete(strBuilder.length()-1, strBuilder.length());
		return strBuilder.toString();
	}
}
