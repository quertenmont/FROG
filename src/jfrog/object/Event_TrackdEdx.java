package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class Event_TrackdEdx extends Base{
    float dEdx;
 
	public int ChunkId() {	return 12115; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format("Event_TrackdEdx [dEdx=%s]", dEdx);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		dEdx		= Factory.getFloat (din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(4);		
		Factory.putFloat (toReturn, dEdx);
		return toReturn;		
	}
		
}
