package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;



public class BaseColl_Id extends Base {	
	public int ChunkId() {	return 1000; }
	public boolean isCompactible() {return false;}	
		
	public String toString() {
		return String.valueOf(id);
	}
	
	public String toTreeLabel() {
		if(name=="unknown")return toString();
		return name + " ("+ id + ")";
	}		

	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		System.out.printf("Object Base with DetId was read\n");
		int [] BytesRead = {0};

		id = Factory.getUInt(din, BytesRead);

		BytesRead[0] += jfrog.object.Factory.read(url,din,this, bytesToRead-BytesRead[0], fileOffset+BytesRead[0], level);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(4);		
		Factory.putUInt(toReturn, id);		
		return toReturn;		
	}				
	
}
