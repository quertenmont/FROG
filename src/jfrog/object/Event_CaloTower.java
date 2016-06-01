package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;

public class Event_CaloTower extends Base{
    float Eem; float Ehad;
    long componentsColl;
    ArrayList<Long> componentsId = new ArrayList<Long>();
    ArrayList<Base> components = new ArrayList<Base>();
 

	public int ChunkId() {	return 12310; }
	public boolean isCompactible() {return false;}    

	public String toString() {
		return String.format("Event_CaloTower [E=%s Eem=%s, Ehad=%s]", Eem+Ehad, Eem, Ehad);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		Eem  	= Factory.getFloat (din, BytesRead);
		Ehad    = Factory.getFloat (din, BytesRead);
		if(BytesRead[0]>=bytesToRead)return BytesRead[0];		
		
		componentsColl = Factory.getUInt (din, BytesRead);
		int NComp = (bytesToRead-BytesRead[0])/4;
		for(int i=0;i<NComp;i++)componentsId.add(Factory.getUInt (din, BytesRead));
						
		if(Eem<0)Eem*=-1;
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(16+componentsId.size()*4);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, Eem);
		Factory.putFloat (toReturn, Ehad);
		
		if(componentsId.size()>0){
			Factory.putUInt (toReturn, componentsColl);
			for(int i=0;i<componentsId.size();i++){
			Factory.putUInt (toReturn, componentsId.get(i));				
			}
		}		
		return toReturn;		
	}
	
	
	public void draw(GL2 gl) {	
		if(VBO_Vertices[0]==0){
			Base coll = jfrog.Common.events.findId(componentsColl);
			if(coll==null)VBO_Vertices[0] = -1;
			for(int i=0;i<componentsId.size();i++){
				Base obj = coll.findId(componentsId.get(i));
				if(obj!=null)components.add(obj);
			}
			VBO_Vertices[0] = 1;
		}else if(VBO_Vertices[0]>0){
			for(int i=0;i<components.size();i++){
				components.get(i).draw(gl);
			}
		}
	}	
	
}
