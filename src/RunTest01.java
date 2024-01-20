import java.io.FileInputStream;
import java.io.FileOutputStream;

import dbf_conv.DbfFileIn;
import dbf_conv.DbfFileOut;
import dbf_conv.DbfHeader;

public class RunTest01 {

	public static void main(String[] args) {
		String srcFName = "D:/Temp/test01.dbf";
		String srcCharSet = "CP949";
		
		String targFName = srcFName.replace(".dbf", "_UTF8.bdf");
		String targCharSet = "UTF-8";
		
		System.out.println("Start pringDBF() : " + srcFName + "  CharSet: " + srcCharSet);
		System.out.println("====================");
		printDBF(srcFName, srcCharSet);
		System.out.println("Complete! printDBF() : " + srcFName + "  CharSet: " + srcCharSet);
		
		System.out.println("\n\n");
		System.out.println("Start makeUTF8DBF() : " + targFName);
		System.out.println("====================");
		makeUTF8DBF(srcFName, targFName);
		System.out.println("Complete! makeUTF8DBF() : " + targFName);
		
		System.out.println("\n\n");
		System.out.println("Start pringDBF() : " + targFName + "  CharSet: " + targCharSet);
		System.out.println("====================");
		printDBF(targFName, targCharSet);
		System.out.println("Complete! printDBF() : " + targFName + "  CharSet: " + targCharSet);

	}

	public static void makeUTF8DBF(String srcFName, String targFName) {
		String srcCharset = "CP949";
		try {
			FileInputStream srcInStm = new FileInputStream(srcFName);
			DbfFileIn srcFileIn = new DbfFileIn(srcInStm);
			DbfHeader srcDbfHeader = DbfHeader.readDbfHeader(srcFileIn, srcCharset);
			FileOutputStream targOutStm = new FileOutputStream(targFName);
			DbfFileOut targFileOut = new DbfFileOut(targOutStm);
			DbfHeader targDbfHeader = srcDbfHeader.copyDbfHeader();
			targDbfHeader.changeFieldsStringUTF8();
			targDbfHeader.writeDbfHeader(targFileOut, srcCharset);
			
			// Write Data
			System.out.println("Run...");
			int nCnt = 0;
			while (srcDbfHeader.writeRecordStringUTF8(srcFileIn,  targFileOut, targDbfHeader) != -1) {
				nCnt += 1;
				if ((nCnt %2) == 0) {
					System.out.println("\tConver: " + nCnt);
				}
			}
			System.out.println("Convert Total Records: " + nCnt);
			
			srcInStm.close();
			targOutStm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printDBF(String fileName, String charSet) {
		String strBuff;
		try {
			FileInputStream srcInStm = new FileInputStream(fileName);
			DbfFileIn srcFileIn = new DbfFileIn(srcInStm);
			
			// Read Header
			DbfHeader srcDbfHeader = DbfHeader.readDbfHeader(srcFileIn, charSet);
			strBuff = srcDbfHeader.printDbfHeader();
			System.out.println(strBuff);
			
			// Read Data
			System.out.println("----Data----");
			int nCnt = 0;
			while((strBuff = srcDbfHeader.printRecord(srcFileIn,  charSet)) != null) {
				nCnt += 1;
				System.out.println("\t" + nCnt + strBuff);
			}
			System.out.println("-----------");
			System.out.println("Done Records: " + nCnt);

			srcInStm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
