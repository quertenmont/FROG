package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_SiStripCluster extends Base{
    int firstStrip;	float pitch;
	ArrayList<Byte> ampl = new ArrayList<Byte>();
	CMS_Geom_Tracking det = null;
    float clusterCharge = 0;
 

	public int ChunkId() {	return 12113; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format("Event_SiStripCluster [Id=%s charge=%s amplitutes=%s]",  id, clusterCharge, ampl);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		firstStrip  = Factory.getUShort(din, BytesRead);
		pitch    	= Factory.getFloat (din, BytesRead);
		
		int nstrips = 0;
		nstrips    	= Factory.getUShort(din, BytesRead);
		for(int i=0;i<nstrips;i++){
		byte strip  = Factory.getByte  (din, BytesRead);
		ampl.add(strip);
		clusterCharge+=strip;
		}
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(12+ampl.size());		
		Factory.putUInt  (toReturn, id);
		Factory.putUShort(toReturn, firstStrip);		
		Factory.putFloat (toReturn, pitch);
		Factory.putUShort(toReturn, ampl.size());
		for(int i=0;i<ampl.size();i++)
		Factory.putByte  (toReturn, ampl.get(i));				
		return toReturn;		
	}
	


	public void draw(GL2 gl) {
		if(VBO_Vertices[0]==0){
			det = (CMS_Geom_Tracking)jfrog.Common.getObjectWidthId(id, jfrog.Common.geomIdMap);
			if(det==null)VBO_Vertices[0] = -1;
						
			float [] positionData = new float[(1+2*ampl.size())*3];											
			int  [] CubeIndex  	= new int[(1+2*ampl.size())  ];			
								
			float denominator = (float)(2*Math.sqrt(det.WidthX*det.WidthX + det.WidthY*det.WidthY + det.WidthZ*det.WidthZ));
			float x,y,z;			
			x = det.WidthX * (firstStrip-1+(ampl.size()/2))*pitch / denominator;
			y = det.WidthY * (firstStrip-1+(ampl.size()/2))*pitch / denominator;
			z = det.WidthZ * (firstStrip-1+(ampl.size()/2))*pitch / denominator;
			
			positionData[0] = det.PosX+x*det.Trapezo;
			positionData[1] = det.PosY+y*det.Trapezo;
			positionData[2] = det.PosZ+z*det.Trapezo;			
			CubeIndex   [0] = 0;

			for(int i=0;i<ampl.size();i++){
				x = det.WidthX * (firstStrip-1+i)*pitch / denominator;
				y = det.WidthY * (firstStrip-1+i)*pitch / denominator;
				z = det.WidthZ * (firstStrip-1+i)*pitch / denominator;
				
				positionData[3+i*6+0] = det.PosX+x*det.Trapezo+det.LengthX;
				positionData[3+i*6+1] = det.PosY+y*det.Trapezo+det.LengthY;
				positionData[3+i*6+2] = det.PosZ+z*det.Trapezo+det.LengthZ;
				positionData[3+i*6+3] = det.PosX+x*det.Trapezo-det.LengthX;
				positionData[3+i*6+4] = det.PosY+y*det.Trapezo-det.LengthY;
				positionData[3+i*6+5] = det.PosZ+z*det.Trapezo-det.LengthZ;	
				CubeIndex   [1+i*2+0] = (short)(1+i*2+0);						
				CubeIndex   [1+i*2+1] = (short)(1+i*2+1);				
			}
			

			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);					
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(positionData.length);

			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 4, VBO_Vertices, 0);								// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );							
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);

				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );
				if(this.getStyle().texmarker!=null)this.getStyle().texmarker.bind(gl);
				gl.glDrawElements   (    GL2.GL_POINTS, 1, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position(1);
				if(this.getStyle().texmarker!=null)gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
				gl.glDrawElements   (    GL2.GL_LINES , 2*ampl.size(), GL2.GL_UNSIGNED_INT, CubeIndexBuf);				
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );	
				if(this.getStyle().texmarker!=null)this.getStyle().texmarker.bind(gl);				
				gl.glDrawElements( GL2.GL_POINTS, 1, GL2.GL_UNSIGNED_INT, 0);			
				if(this.getStyle().texmarker!=null)gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
				gl.glDrawElements( GL2.GL_LINES , 2*ampl.size(), GL2.GL_UNSIGNED_INT, 1*2);							
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
			det.drawWF(gl);
		}
	}		
	
		
	
}
