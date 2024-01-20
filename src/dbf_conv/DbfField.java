package dbf_conv;

import java.io.IOException;

public class DbfField {
	int nID;
	String strName;			// 0~10 : Field Name in ASCII(zero-filled)
	char chType;			// 11 : Field Type in ASCII(C, D, F, L, M, or N)
	// 12 ~ 15 : Reserved
	byte byteLength;		// 16 : Field length in binary
	byte byteDecimal;		// 17 : Field decimal count in binary
	// 18 ~ 19 :Reserved
	byte byteWorkAreaID;	// 20 : Work Area ID
	// 21 ~ 30 :Reserved
	byte byteMDXflag;		// 31 : Production MDX file flag; 01H if there is an MDX, 00H if not.
	byte[] bytesDbfFieldData;	 // 0~31 Data

	public DbfField(int nID, String strName, char chType, byte byteLength, 
			byte byteDecimal, byte byteWorkAreaID, byte byteMDXflag, 
			byte[] bytesDbfFieldData) {
		super();
		this.nID = nID;
		this.strName = strName;
		this.chType = chType;
		this.byteLength = byteLength;
		this.byteDecimal = byteDecimal;
		this.byteWorkAreaID = byteWorkAreaID;
		this.byteMDXflag = byteMDXflag;
		this.bytesDbfFieldData = bytesDbfFieldData;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DbfField) {
			DbfField aDbfField = (DbfField)obj;
			return aDbfField.strName.equals(this.strName);
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return strName.hashCode();
	}
	
	public static DbfField makeDbfField(int nID, byte[] bytesDbfFieldData, String characterEncoding) throws IOException {
		String strName = new String(bytesDbfFieldData, 0, 11, characterEncoding).trim();	// 0~10 : Field Name in ASCII(zero-filled)
		char chType = (char)bytesDbfFieldData[11];		// 11 : Field Type in ASCII(C, D, F, L, M, or N)
		byte byteLength = bytesDbfFieldData[16];		// 16 : Field length in binary
		byte byteDecimal = bytesDbfFieldData[17];		// 17 : Field decimal count in binary
		byte byteWorkAreaID = bytesDbfFieldData[20];	// 20 : Work Area ID
		byte byteMDXflag = bytesDbfFieldData[31];		// 31 : Production MDX file flag; 01H if there is an MDX, 00H if not.
		
		// Create DbfField
		DbfField aRet = new DbfField(nID, strName, chType, byteLength, 
				byteDecimal, byteWorkAreaID, byteMDXflag, bytesDbfFieldData);
		return aRet;

	}
	
	public DbfField copyDbfField() {
		int nID = this.nID;
		String strName = new String(this.strName);
		char chType = this.chType;
		byte byteLength = this.byteLength;
		byte byteDecimal = this.byteDecimal;
		byte byteWorkAreaID = this.byteWorkAreaID;
		byte byteMDXflag = this.byteMDXflag;
		byte[] bytesDbfFieldData = new byte [this.bytesDbfFieldData.length];
		for (int i = 0; i < bytesDbfFieldData.length; i++) {
			bytesDbfFieldData[i] = this.bytesDbfFieldData[i];
		}

		// Create DbfField
		DbfField aRet = new DbfField(nID, strName, chType, byteLength, 
				byteDecimal, byteWorkAreaID, byteMDXflag, bytesDbfFieldData);
		return aRet;
	}
	
	
}
