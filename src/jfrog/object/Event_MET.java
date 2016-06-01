
package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class Event_MET extends Event_Candidate{
	float sumEt;

	
	public int ChunkId() {	return 14100; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format(
				"Event_MET [pdgId=%s, p=%s, eta=%s, phi=%s sumEt=%s]", pdgId, p, eta, phi, sumEt);
	}
	
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};		
		pdgId   = Factory.getUInt  (din, BytesRead);
		p  		= Factory.getFloat (din, BytesRead);
		eta    	= Factory.getFloat (din, BytesRead);
		phi  	= Factory.getFloat (din, BytesRead);
		sumEt  	= Factory.getFloat (din, BytesRead);		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(20);		
		Factory.putUInt  (toReturn, pdgId);
		Factory.putFloat (toReturn, p);
		Factory.putFloat (toReturn, eta);
		Factory.putFloat (toReturn, phi);				
		Factory.putFloat (toReturn, sumEt);		
		return toReturn;		
	}			

}
