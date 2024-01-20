package dbf_conv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DbfHeader {
	static final byte DBF_END_HEAD = 0x0D;
	static final byte DBF_END_FILE = 0x1A;
	static final byte DBF_END_RECORD = 0x20;

	private int nVersion;			// 0 : bits 0-2 indicate version number
	// 1~3 : Date of last update; YYMMDD
	private byte byteYY;			// 120 -> 2020년
	private byte byteMM;
	private byte byteDD;
	private int nRecords;			// 4~7 : Number of records in the file
	private short shortHeaderSize;		// 8~9 : Number of bytes in the header
	private short shortRecordSize;		// 10~11 : Number of bytes in the record
	// 12~13 : Reserved; fill with 0
	private byte byteTransaction;		// 14 : Flag indicating imcomplete transaction
	private byte byteEncryption;		// 15 : Encryption flag
	private byte[] bytesUserEnv;		// 16~27 : Reserved for dBASE IV in a multi-user environment
	private byte byteMDXflag;			// 28 : Production MDX file flag; 01H if there is an MDX, 00H if not.
	private byte byteLanguageDriverID;	// 29 : Language driver ID
	// 30~31 : Reserved; fill with 0
	private List<DbfField> listDbfFields;	// 32 - N* : each Field descriptor array.
	// N + 1 1byte 0DH as the field terminator

//	public DbfHeader() {
//		nVersion = 0;
//		byteYY = 0;
//		byteMM = 0;
//		byteDD = 0;
//		nRecords = 0;
//		shortHeaderSize = 0;
//		shortRecordSize = 0;
//		byteTransaction = 0;
//		byteEncryption = 0;
//		bytesUserEnv = new byte[12];
//		byteMDXflag = 0;
//		byteLanguageDriverID = 0;
//		listDbfFields = new LinkedList<DbfField>();
//	}
	
	public DbfHeader(int nVersion, byte byteYY, byte byteMM, byte byteDD, 
			int nRecords, short shortHeaderSize,
			short shortRecordSize, byte byteTransaction, byte byteEncryption, byte[] bytesUserEnv, 
			byte byteMDXflag, byte byteLanguageDriverID, 
			List<DbfField> listDbfFields) {
		super();
		this.nVersion = nVersion;
		this.byteYY = byteYY;
		this.byteMM = byteMM;
		this.byteDD = byteDD;
		this.nRecords = nRecords;
		this.shortHeaderSize = shortHeaderSize;
		this.shortRecordSize = shortRecordSize;
		this.byteTransaction = byteTransaction;
		this.byteEncryption = byteEncryption;
		this.bytesUserEnv = bytesUserEnv;
		this.byteMDXflag = byteMDXflag;
		this.byteLanguageDriverID = byteLanguageDriverID;
		this.listDbfFields = listDbfFields;
	}
	
	public static DbfHeader readDbfHeader(DbfFileIn dbfFileIn, String characterEncoding) throws IOException {
		byte byteVersion = 0;
		byteVersion = dbfFileIn.readFileByte();
		int nVersion = (byteVersion & 0x00000007);	// bits 0-2 indicate version number
		byte byteYY = dbfFileIn.readFileByte();
		byte byteMM = dbfFileIn.readFileByte();
		byte byteDD = dbfFileIn.readFileByte();
		int nRecords = dbfFileIn.readFileInt();
		short shortHeaderSize = dbfFileIn.readFileShort();		// 32 + 32*N + 1(0DH)
		short shortRecordSize = dbfFileIn.readFileShort();		// 1(20H) + 실사이즈
		dbfFileIn.readFileBytes(2);		// 12~13 : Reserved
		byte byteTransaction = dbfFileIn.readFileByte();
		byte byteEncryption = dbfFileIn.readFileByte();
		byte[] bytesUserEnv = dbfFileIn.readFileBytes(12);
		byte byteMDXflag = dbfFileIn.readFileByte();
		byte byteLanguageDriverID = dbfFileIn.readFileByte();
		dbfFileIn.readFileBytes(2);		// 30~31 : Reserved
		
		// The Field descriptor array
		List<DbfField> listDbfFields = new LinkedList<DbfField>();
		byte[] bytesJunk = new byte[32];
		bytesJunk[0] = 0;
		int nID = 0;
		bytesJunk[0] = dbfFileIn.readFileByte();
		while (bytesJunk[0] != DBF_END_HEAD) {		// end of Header
			dbfFileIn.readFileBytes(bytesJunk, 1, 31);
			DbfField aDbfField = DbfField.makeDbfField(nID++, bytesJunk, characterEncoding);
			listDbfFields.add(aDbfField);
			bytesJunk[0] = dbfFileIn.readFileByte();
		}
		
		//Create DbfHeader
		DbfHeader aDbfHeader = new DbfHeader(nVersion, byteYY, byteMM, byteDD, nRecords, 
				shortHeaderSize, shortRecordSize, byteTransaction, byteEncryption, 
				bytesUserEnv,byteMDXflag, byteLanguageDriverID, listDbfFields);
		return aDbfHeader;
	}

	public String printDbfHeader() {
		StringBuilder strBuilder = new StringBuilder();
		String token = "\t";
		
		strBuilder.append("Version:");
		strBuilder.append(this.nVersion);
		strBuilder.append(token);
		strBuilder.append("생성일:");
		strBuilder.append(this.byteYY);
		strBuilder.append("-");
		strBuilder.append(this.byteMM);
		strBuilder.append("-");
		strBuilder.append(this.byteDD);
		strBuilder.append(token);
		
		strBuilder.append('\n');
		strBuilder.append("총 Record 수: ");
		strBuilder.append(this.nRecords);
		strBuilder.append(token);
		strBuilder.append("Header크기(byte): ");
		strBuilder.append(this.shortHeaderSize);
		strBuilder.append(token);
		strBuilder.append("Record당 크기(byte): ");
		strBuilder.append(this.shortRecordSize);
		strBuilder.append('\n');
	
		strBuilder.append("---필드내역----\n");
		strBuilder.append("\t순번\t필드명\t타입\t길이\t정확도\n");
		for (DbfField aDbfField : this.listDbfFields) {
			strBuilder.append(token);
			strBuilder.append(aDbfField.nID);
			strBuilder.append(token);
			strBuilder.append(aDbfField.strName);
			strBuilder.append(token);
			strBuilder.append(aDbfField.chType);
			strBuilder.append(token);
			strBuilder.append(DataConv.byte2ubyte(aDbfField.byteLength));
			strBuilder.append(token);
			strBuilder.append(aDbfField.byteDecimal);
			strBuilder.append('\n');
		}
		strBuilder.append("------------");
		return strBuilder.toString();
	}

	public void gotoDataArea(DbfFileIn dbfFileIn) throws IOException {
		int shortHeaderSize = this.shortHeaderSize;
		FileInputStream dbfFile = dbfFileIn.getInFile();
//		dbfFile.rewind();
//		dbfFile.skip(shortHeaderSize);
	}
	
	
	public String printRecord(DbfFileIn dbfFileIn, String characterEncoding) throws IOException {
		int nRecordSize = this.shortRecordSize;
		byte[] bytesJunk = new byte[nRecordSize];
		bytesJunk[0] = dbfFileIn.readFileByte();
		if (bytesJunk[0] == DBF_END_FILE) {		// End of File
			return null;
		}
		dbfFileIn.readFileBytes(bytesJunk, 1, nRecordSize-1);
		int nOffset = 1;
		
		StringBuilder strBuilder = new StringBuilder();
		String token = "\t";
		for (DbfField aDbfField : this.listDbfFields) {
			char chType = aDbfField.chType;
			int nSize = DataConv.byte2ubyte(aDbfField.byteLength);
			String strData = new String(bytesJunk, nOffset, nSize, characterEncoding);
			strData = strData.trim();
 			nOffset += nSize;
			
			if ((chType == 'N') && (strData.indexOf(".") != -1)) {
				chType = 'F';
			}
			
			strBuilder.append(token);
			switch (chType) {
			case 'C':
				strBuilder.append(strData);
				break;
			case 'N':
				int nResult = Integer.parseInt(strData);
				strBuilder.append(nResult);
				break;
			case 'F':
				double dResult = Double.parseDouble(strData);
				strBuilder.append(dResult);
				break;
			case 'D':
			case 'L':
			default:
				break;
			}
			
		}
		return strBuilder.toString();
	}

	public DbfHeader copyDbfHeader() throws IOException {
		int nVersion = this.nVersion;
		byte byteYY = this.byteYY;
		byte byteMM = this.byteMM;
		byte byteDD = this.byteDD;
		int nRecords = this.nRecords;
		short shortHeaderSize = this.shortHeaderSize;
		short shortRecordSize = this.shortRecordSize;
		byte byteTransaction = this.byteTransaction;
		byte byteEncryption = this.byteEncryption;
		byte[] bytesUserEnv = new byte[12];
		for (int i = 0; i < this.bytesUserEnv.length; i++) {
			bytesUserEnv[i] = this.bytesUserEnv[i];
		}
		byte byteMDXflag = this.byteMDXflag;
		byte byteLanguageDriverID = this.byteLanguageDriverID;

		List<DbfField> listDbfFields = new LinkedList<DbfField>();
		for (DbfField srcDbfField : this.listDbfFields) {
			listDbfFields.add(srcDbfField.copyDbfField());
		}
		
		DbfHeader aRet = new DbfHeader(nVersion, byteYY, byteMM, byteDD, nRecords, 
						shortHeaderSize, shortRecordSize, byteTransaction, byteEncryption, 
						bytesUserEnv, byteMDXflag, byteLanguageDriverID, listDbfFields);
			return aRet;
	}
	
	public void writeDbfHeader(DbfFileOut dbfFileOut, String characterEncoding) throws IOException {
		byte[] bytesEmpty = new byte[20];
		
		byte[] bytesTemp1 = DataConv.convIntToByte(this.nVersion);
		byte byteVersion = (byte)(bytesTemp1[0] & 0x07);	// bits 0-2 indicate version number\
		dbfFileOut.writeFileByte(byteVersion);
		dbfFileOut.writeFileByte(this.byteYY);
		dbfFileOut.writeFileByte(this.byteMM);
		dbfFileOut.writeFileByte(this.byteDD);
		dbfFileOut.writeFileInt(this.nRecords);
		dbfFileOut.writeFileShort(this.shortHeaderSize);
		dbfFileOut.writeFileShort(this.shortRecordSize);
		dbfFileOut.writeFileBytes(bytesEmpty, 0, 2);		// 12~13 : Reserved
		dbfFileOut.writeFileByte(this.byteTransaction);
		dbfFileOut.writeFileByte(this.byteEncryption);
		dbfFileOut.writeFileBytes(this.bytesUserEnv);
		dbfFileOut.writeFileByte(this.byteMDXflag);
		dbfFileOut.writeFileByte(this.byteLanguageDriverID);
		dbfFileOut.writeFileBytes(bytesEmpty, 0, 2);		// 30~31 : Reserved

		for (DbfField aDbfField : this.listDbfFields) {
			dbfFileOut.writeFileString(aDbfField.strName, 11, characterEncoding);
			dbfFileOut.writeFileByte((byte)aDbfField.chType);
			dbfFileOut.writeFileBytes(bytesEmpty, 0, 4);		// Reserved
			dbfFileOut.writeFileByte(aDbfField.byteLength);
			dbfFileOut.writeFileByte(aDbfField.byteDecimal);
			dbfFileOut.writeFileBytes(bytesEmpty, 0, 2);		// Reserved
			dbfFileOut.writeFileByte(aDbfField.byteWorkAreaID);
			dbfFileOut.writeFileBytes(bytesEmpty, 0, 10);		// Reserved
			dbfFileOut.writeFileByte(aDbfField.byteMDXflag);
		}
		dbfFileOut.writeFileByte(DBF_END_HEAD);
	}
	
	public void changeFieldsStringUTF8() {
		short shortSize = 0;
		for (DbfField aDbfField : this.listDbfFields) {
			if (aDbfField.chType == 'C') {
				short stmp = DataConv.byte2ubyte(aDbfField.byteLength);
				stmp = (short)(stmp * 1.5 + 1);
				aDbfField.byteLength = (byte)stmp;
				shortSize += stmp;
			} else {
				shortSize += DataConv.byte2ubyte(aDbfField.byteLength);
			}
		}
		this.shortRecordSize = (short)(shortSize + 1);
	}

	public DbfField getDbfField(String strName) {
		for (DbfField aDbfField : this.listDbfFields) {
			if (aDbfField.strName.endsWith(strName)) {
				return aDbfField;
			}
		}
		return null;
	}

	public int writeRecordStringUTF8(DbfFileIn dbfFileIn,
			DbfFileOut dbfFileOut, DbfHeader targDbfHeader) throws IOException {
		String srcCharSet = "CP949";
		String targCharSet = "UTF-8";
		
		int srcRecordSize = this.shortRecordSize;
		int targRecordSize = targDbfHeader.shortRecordSize;
		byte[] bytesJunk = new byte[srcRecordSize];
		bytesJunk[0] = dbfFileIn.readFileByte();
		if (bytesJunk[0] == DBF_END_FILE) {		// End of File
			dbfFileOut.writeFileByte(bytesJunk[0]);
			return -1;
		}
		dbfFileIn.readFileBytes(bytesJunk, 1, srcRecordSize-1);
		int nOffset = 1;
		
		byte[] bytesTarg = new byte[targRecordSize];
		bytesTarg[0] = bytesJunk[0];
		int targOffset = 1;
		for (DbfField aDbfField : this.listDbfFields) {
			String strName = aDbfField.strName;
			char chType = aDbfField.chType;
			int nSize = DataConv.byte2ubyte(aDbfField.byteLength);
			
			int targSize = nSize;
			if (chType == 'C') {
				DbfField targDbfField = targDbfHeader.getDbfField(strName);
				targSize = DataConv.byte2ubyte(targDbfField.byteLength);
				String strData = new String(bytesJunk, nOffset, nSize, srcCharSet);
				strData = strData.trim();
				byte[] bytesStr = DataConv.convStringToByte(strData, targSize, targCharSet);
				for (int i = 0; i < bytesStr.length; i++) {
					bytesTarg[targOffset + i] = bytesStr[i];
				}
			} else {
				for (int i = 0; i < targSize; i++) {
					bytesTarg[targOffset + i] = bytesJunk[nOffset + i];
				}
			}
			
			nOffset += nSize;
			targOffset += targSize;
		}
		for (int i = 0; i < bytesTarg.length; i++) {
			if (bytesTarg[i] == 0)		bytesTarg[i] = (byte)' ';
		}
		dbfFileOut.writeFileBytes(bytesTarg, 0, targRecordSize);
		return targOffset;	
	}
	
}
