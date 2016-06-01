package jfrog.object;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Event extends Base{	
	public long      run;
	public long      event;
	public long      timeStamp;
	int 	  chunkOffset = -1;
	int 	  chunkSize   = -1;
	URL		  fileURL	  = null;
	boolean   isLoaded    = false;
	
	public Map<Long, ArrayList<Base> > idMap = new HashMap<Long, ArrayList<Base> >();
	
	public int ChunkId() {	return 10002; }
	public boolean isCompactible() {return false;}
	public boolean isCollection() {return true;}	
		
	public String toString() {
		return "run=" + run + " event=" + event + " (" + name + ")";
	}
	
	public String toTreeLabel() {
		return toString();
	}		
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		
		if(din!=null){		
			run       = Factory.getUInt(din, BytesRead);
			event 	  = Factory.getUInt(din, BytesRead);
			timeStamp = Factory.getLong(din, BytesRead);
			SetUpDate();
			
			//System.out.printf("Events %s BytesToRead=%d  Position=%d\n",name, bytesToRead-BytesRead[0], fileOffset+BytesRead[0]);
	
			chunkOffset = fileOffset+BytesRead[0];
			chunkSize   = bytesToRead-BytesRead[0];
			fileURL		= url;
			din.skipBytes(bytesToRead-BytesRead[0]);	BytesRead[0] += bytesToRead-BytesRead[0];		
		}else{								
			din = new DataInputStream(new BufferedInputStream(fileURL.openStream()));
			din.skip(chunkOffset);
			jfrog.object.Factory.read(url,din,this, chunkSize, chunkOffset, level);
		    if (din != null){din.close();}
		    
		    this.fillMapOfId(idMap);    	
		}
		return BytesRead[0];
	}
	
	
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(16);		
		Factory.putUInt(toReturn, run);
		Factory.putUInt(toReturn, event);
		Factory.putLong(toReturn, timeStamp);
		return toReturn;		
	}		
		
	void SetUpDate(){				
		if(timeStamp==0){
			name = "No TimeStamp";
			return;
		}

		long Temp = timeStamp>>32;
		if(Temp == 0){
			name = "Simulated Data";
			return;
		}else{
			Date rawtime = new Date((timeStamp>>32)*1000);			
			
			DateFormat formatter = new SimpleDateFormat("E yyyy-dd HH:mm:ss z", Locale.US);
			name = formatter.format(rawtime);
			return;
		}
	}

	
	public void load(){			
		if(isLoaded)return;
		try {			
			readData(null, null, this, null, 0, 0, 99);
		} catch (IOException e) {
			e.printStackTrace();
		}
		isLoaded = true;
	}
	
	public void unload(){
		isLoaded = false;
		setStyle(null, true, false);
		daughters.clear();
	}		
	
	public void copyProperties(Base src, Event dest){
		if(src.isCollection()==false)return;
		
		Base similarColl = null;
		if(src instanceof Event){
			similarColl=dest;
		}else{
			similarColl = jfrog.Common.getObjectWidthId(src.id, dest.idMap);
		}
				
		if(similarColl!=null){
			similarColl.setStyle(src.getStyle(), true, false);
			similarColl.setVisible(src.isVisible());
			similarColl.expandedFlag = src.expandedFlag;
		}

		for(int i=0;i<src.daughters.size();i++){
			copyProperties(src.daughters.get(i), dest);
		}	
	}				
	

}
