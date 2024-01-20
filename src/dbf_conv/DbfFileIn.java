package dbf_conv;

import java.io.FileInputStream;
import java.io.IOException;

public class DbfFileIn {
//	private int nOffset;
	private FileInputStream inFile;
	
	public DbfFileIn(FileInputStream inFile, int nOffset) {
//		this.nOffset = nOffset;
		this.inFile = inFile;
	}

	public DbfFileIn(FileInputStream inFile) {
//		this(inFile, 0);
		this.inFile = inFile;
	}
	
	public FileInputStream getInFile() {
		return inFile;
	}

	// byte
	public byte readFileByte() throws IOException {
		byte result = 0;
		int readSize = 1;
		byte[] binBuff = new byte[readSize];
		int readCount = this.inFile.read(binBuff, 0, readSize);
		if (readCount == -1 || readSize != readCount) {
			throw new IOException();
		}
		result = binBuff[0];
		return result;
	}

	// byte[]
	public byte[] readFileBytes(int readSize) throws IOException {
		byte[] result = new byte[readSize];
		int readCount = this.inFile.read(result, 0, readSize);
		if (readCount == -1 || readSize != readCount) {
			throw new IOException();
		}
		return result;
	}
	public int readFileBytes(byte[] binBuff, int start, int readSize) throws IOException {
		int readCount = this.inFile.read(binBuff, start, readSize);
		if (readCount == -1 || readSize != readCount) {
			throw new IOException();
		}
		return readCount;
	}

	// singed_int
	public int readFileInt() throws IOException {
		int result = 0;
		int readSize = Integer.BYTES;	// 4
		byte[] binBuff = new byte[readSize];
		int readCount = this.inFile.read(binBuff, 0, readSize);
		if (readCount == -1 || readSize != readCount) {
			throw new IOException();
		}
		result = DataConv.convBytesToInt(binBuff);
		return result;
	}
	// short
	public short readFileShort() throws IOException {
		short result = 0;
		int readSize = Short.BYTES;	// 2 
		byte[] binBuff = new byte[readSize];
		int readCount = this.inFile.read(binBuff, 0, readSize);
		if (readCount == -1 || readSize != readCount) {
			throw new IOException();
		}
		result = DataConv.convBytesToShort(binBuff);
		return result;
	}
	// String(n)
	public String readFileString(int nSize, String characterEncoding) throws IOException {
		String result = "";
		int readSize = nSize;
		byte[] binBuff = new byte[readSize];
		int readCount = this.inFile.read(binBuff, 0, readSize);
		if (readCount == -1 || readSize != readCount) {
			throw new IOException();
		}
		result = new String(binBuff, 0 ,nSize, characterEncoding);
		return result;
	}

	// unsigned_short
	public int readFileUnsignedShort() throws IOException {
		int result = readFileShort();
		if (result < 0) {
			result = DataConv.short2ushort((short)result);
		}
		return result;
	}
	// unsigned_int
	public long readFileUnsignedInt() throws IOException {
		long result = readFileInt();
		if (result < 0) {
			result = DataConv.int2uint((int)result);
		}
		return result;
	}
}
