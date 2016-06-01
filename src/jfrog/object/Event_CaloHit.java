package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_CaloHit extends Base{
    float E; float t;
    CMS_Geom_Calo det = null;
 

	public int ChunkId() {	return 12210; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format("Event_CaloHit [E=%s, t=%s]", E, t);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		E  		= Factory.getFloat (din, BytesRead);
		t    	= Factory.getFloat (din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(12);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, E);
		Factory.putFloat (toReturn, t);				
		return toReturn;		
	}
	
	
	public void draw(GL2 gl) {	
		if(VBO_Vertices[0]==0){			
			det = (CMS_Geom_Calo)jfrog.Common.getObjectWidthId(id, jfrog.Common.geomIdMap);
			if(det==null){VBO_Vertices[0]=-1; System.out.printf("Det%d not found\n", id); return;}			
			if(E<det.getStyle().minE){VBO_Vertices[0]=-1;return;}

			
			float  ERatioA = E/det.getStyle().minE;
//			if(style_->DisplayMode_==0){	ERatioA = 0.75f*log10(ERatioA);	if(ERatioA>1)ERatioA=1.0f; }else
//			if(style_->DisplayMode_==1){	ERatioA = 0.75f*log10(ERatioA);						 	   }else
//			if(style_->DisplayMode_==2){	ERatioA = 0.1f*ERatioA;			if(ERatioA>1)ERatioA=1.0f; }else
//			{								ERatioA = 0.1f*ERatioA;									   }
			ERatioA = (float)Math.log10(ERatioA);
			float  ProjA = 1+(det.ProjFactor-1)*ERatioA;
			
			float C1X = det.PosX-det.WX-det.HX;		float C1Y = det.PosY-det.WY-det.HY;		float C1Z = det.PosZ-det.WZ-det.HZ;
			float C2X = det.PosX+det.WX-det.HX;		float C2Y = det.PosY+det.WY-det.HY;		float C2Z = det.PosZ+det.WZ-det.HZ;	
			float C3X = det.PosX+det.WX+det.HX;		float C3Y = det.PosY+det.WY+det.HY;		float C3Z = det.PosZ+det.WZ+det.HZ;
			float C4X = det.PosX-det.WX+det.HX;		float C4Y = det.PosY-det.WY+det.HY;		float C4Z = det.PosZ-det.WZ+det.HZ;	
			float C5X = C1X*ProjA;	float C5Y = C1Y*ProjA;	float C5Z = C1Z*ProjA;
			float C6X = C2X*ProjA;	float C6Y = C2Y*ProjA;	float C6Z = C2Z*ProjA;	
			float C7X = C3X*ProjA;	float C7Y = C3Y*ProjA;	float C7Z = C3Z*ProjA;	
			float C8X = C4X*ProjA;	float C8Y = C4Y*ProjA;	float C8Z = C4Z*ProjA;			
			
			float [] positionData   = new float[]{
			C1X, C1Y,	C1Z,	C4X, C4Y,	C4Z,	C3X, C3Y,	C3Z,	C2X, C2Y,	C2Z,
			C5X, C5Y,	C5Z,	C6X, C6Y,	C6Z,	C7X, C7Y,	C7Z,	C8X, C8Y,	C8Z,
			C1X, C1Y,	C1Z,	C2X, C2Y,	C2Z,	C6X, C6Y,	C6Z,	C5X, C5Y,	C5Z,
			C4X, C4Y,	C4Z,	C8X, C8Y,	C8Z,	C7X, C7Y,	C7Z,	C3X, C3Y,	C3Z,								
			C1X, C1Y,	C1Z,	C5X, C5Y,	C5Z,	C8X, C8Y,	C8Z,	C4X, C4Y,	C4Z,		
			C2X, C2Y,	C2Z,	C3X, C3Y,	C3Z,	C7X, C7Y,	C7Z,	C6X, C6Y,	C6Z	};					    	
			int [] CubeIndex  = {0,1,2,3, 4,5,6,7, 8,9,10,11, 12,13,14,15, 16,17,18,19, 20,21,22,23};		
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0, CubeIndex.length, 4);
						
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);
															
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 4, VBO_Vertices, 0);										// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );					// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );					// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );				
			}else{
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer( GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer( 3, GL2.GL_FLOAT, 0, positionDataBuf );						
				gl.glDrawElements( GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){		
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );								
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );				
				gl.glDrawElements( GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, 0);				
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}			
		}		
	}	
	
}
