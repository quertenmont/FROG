package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class BaseColl_Name extends BaseColl{	
	public int ChunkId() {	return 3000;}
	public boolean isCompactible() {return false;}	
	
	public String toString() {
		return name + " (detId=" + id + ")";
	}
	
	public String toTreeLabel() {	
		return name;
	}		

	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		
		int [] BytesRead = {0};		
		id = Factory.getUInt(din, BytesRead); 

		name="";			
		int Length = Factory.getUShort(din, BytesRead);
		for(;Length>0;Length--){name += (char)Factory.getByte(din, BytesRead);}					


		if(level<4)System.out.printf("Object Base with DetId and Name (%s) was read\n",name);
		BytesRead[0] += jfrog.object.Factory.read(url,din,this, bytesToRead-BytesRead[0],fileOffset+BytesRead[0], level);
				
		return BytesRead[0];
	}	
	
	public ByteBuffer writeData(){
		ByteBuffer toReturn = ByteBuffer.allocate(6 + name.length());		
		Factory.putUInt(toReturn, id);
		Factory.putUShort(toReturn, (short)name.length());
		for(int c=0;c<name.length();c++)Factory.putByte(toReturn, (byte)name.charAt(c));		
		return toReturn;			
	}		
	
}
