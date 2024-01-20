package dbf_conv;

import java.io.FileOutputStream;
import java.io.IOException;

public class DbfFileOut {
	private FileOutputStream outFile;
	
	public DbfFileOut(FileOutputStream outFile) {
		super();
		this.outFile = outFile;
	}

	// byte
	public void writeFileByte(byte byteData) throws IOException {
		byte[] binBuff = new byte[1];
		binBuff[0] = byteData;
		this.outFile.write(binBuff, 0, 1);
	}
	// byte[]
	public void writeFileBytes(byte[] bytesData) throws IOException {
		this.outFile.write(bytesData, 0, bytesData.length);
	}
	public void writeFileBytes(byte[] bytesData, int start, int writeSize) throws IOException {
		this.outFile.write(bytesData, start, writeSize);
	}
	
	// int
	public void writeFileInt(int intData) throws IOException {
		byte[] binBuff = DataConv.convIntToByte(intData);
		writeFileBytes(binBuff);
	}
	// short
	public void writeFileShort(short shortData) throws IOException {
		byte[] binBuff = DataConv.convShortToByte(shortData);
		writeFileBytes(binBuff);
	}
	// string(n)
	public void writeFileString(String strData, int nWriteSize, String characterEncoding) throws IOException {
		byte[] binBuff = DataConv.convStringToByte(strData, nWriteSize, characterEncoding);
		writeFileBytes(binBuff, 0, nWriteSize);
	}

}
