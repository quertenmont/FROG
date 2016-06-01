package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Prim_Cube extends Base {
	float PosX;    float PosY;    float PosZ;					
	float WidthX;  float WidthY;  float WidthZ;
	float LengthX; float LengthY; float LengthZ;
	float ThickX;  float ThickY;  float ThickZ;
		
	public int ChunkId() {	return 41020; }
	public boolean isCompactible() {return true;}	

	
	@Override
	public String toString() {
		return "Prim_Cube [PosX=" + PosX + ", PosY=" + PosY + ", PosZ=" + PosZ
				+ ", WidthX=" + WidthX + ", WidthY=" + WidthY + ", WidthZ="
				+ WidthZ + ", LengthX=" + LengthX + ", LengthY=" + LengthY
				+ ", LengthZ=" + LengthZ + ", ThickX=" + ThickX + ", ThickY="
				+ ThickY + ", ThickZ=" + ThickZ + "]";
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);
		PosX    	= Factory.getFloat (din, BytesRead);
		PosY    	= Factory.getFloat (din, BytesRead);
		PosZ    	= Factory.getFloat (din, BytesRead);
		WidthX    	= Factory.getFloat (din, BytesRead);
		WidthY    	= Factory.getFloat (din, BytesRead);
		WidthZ    	= Factory.getFloat (din, BytesRead);		
		LengthX    	= Factory.getFloat (din, BytesRead);
		LengthY    	= Factory.getFloat (din, BytesRead);
		LengthZ    	= Factory.getFloat (din, BytesRead);					
		ThickX    	= Factory.getFloat (din, BytesRead);
		ThickY    	= Factory.getFloat (din, BytesRead);
		ThickZ    	= Factory.getFloat (din, BytesRead);		
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(52);		
		Factory.putUInt  (toReturn, id);	
		Factory.putFloat (toReturn, PosX);
		Factory.putFloat (toReturn, PosY);
		Factory.putFloat (toReturn, PosZ);
		Factory.putFloat (toReturn, WidthX);		
		Factory.putFloat (toReturn, WidthY);
		Factory.putFloat (toReturn, WidthZ);		
		Factory.putFloat (toReturn, LengthX);		
		Factory.putFloat (toReturn, LengthY);
		Factory.putFloat (toReturn, LengthZ);	
		Factory.putFloat (toReturn, ThickX);		
		Factory.putFloat (toReturn, ThickY);
		Factory.putFloat (toReturn, ThickZ);		
		return toReturn;		
	}			
	
	public void draw(GL2 gl) {				
		if(VBO_Vertices[0]==0){
			float C1X = PosX+WidthX+LengthX+ThickX;	float C1Y = PosY+WidthY+LengthY+ThickY;	float C1Z = PosZ+WidthZ+LengthZ+ThickZ;
			float C2X = PosX+WidthX-LengthX+ThickX;	float C2Y = PosY+WidthY-LengthY+ThickY;	float C2Z = PosZ+WidthZ-LengthZ+ThickZ;				 
			float C3X = PosX-WidthX-LengthX+ThickX;	float C3Y = PosY-WidthY-LengthY+ThickY;	float C3Z = PosZ-WidthZ-LengthZ+ThickZ;
			float C4X = PosX-WidthX+LengthX+ThickX;	float C4Y = PosY-WidthY+LengthY+ThickY;	float C4Z = PosZ-WidthZ+LengthZ+ThickZ;
			float C5X = PosX+WidthX+LengthX-ThickX;	float C5Y = PosY+WidthY+LengthY-ThickY;	float C5Z = PosZ+WidthZ+LengthZ-ThickZ;
			float C6X = PosX+WidthX-LengthX-ThickX;	float C6Y = PosY+WidthY-LengthY-ThickY;	float C6Z = PosZ+WidthZ-LengthZ-ThickZ;
			float C7X = PosX-WidthX-LengthX-ThickX;	float C7Y = PosY-WidthY-LengthY-ThickY;	float C7Z = PosZ-WidthZ-LengthZ-ThickZ;
			float C8X = PosX-WidthX+LengthX-ThickX;	float C8Y = PosY-WidthY+LengthY-ThickY;	float C8Z = PosZ-WidthZ+LengthZ-ThickZ;
			
			float [] positionData   = new float[]{
			C1X, C1Y,	C1Z,	C4X, C4Y,	C4Z,	C3X, C3Y,	C3Z,	C2X, C2Y,	C2Z,
			C5X, C5Y,	C5Z,	C6X, C6Y,	C6Z,	C7X, C7Y,	C7Z,	C8X, C8Y,	C8Z,
			C1X, C1Y,	C1Z,	C2X, C2Y,	C2Z,	C6X, C6Y,	C6Z,	C5X, C5Y,	C5Z,
			C4X, C4Y,	C4Z,	C8X, C8Y,	C8Z,	C7X, C7Y,	C7Z,	C3X, C3Y,	C3Z,								
			C1X, C1Y,	C1Z,	C5X, C5Y,	C5Z,	C8X, C8Y,	C8Z,	C4X, C4Y,	C4Z,		
			C2X, C2Y,	C2Z,	C3X, C3Y,	C3Z,	C7X, C7Y,	C7Z,	C6X, C6Y,	C6Z	};							
			int [] CubeIndex  = {0,1,2,3, 4,5,6,7, 8,9,10,11, 12,13,14,15, 16,17,18,19, 20,21,22,23};
			int [] CubeIndexWF= {0,1,2,3};			
			float [] normalData = SetNormalArray(positionData, CubeIndex, 0, CubeIndex.length, 4);	
									
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			IntBuffer CubeIndexWFBuf    = GLBuffers.newDirectIntBuffer(CubeIndexWF);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);			
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);		
									
			if(jfrog.Common.support_VBO){					
				gl.glGenBuffers( 5, VBO_Vertices, 0);								// Get A Valid Name
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );				
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, positionDataBuf.capacity() * 4, positionDataBuf, GL2.GL_STATIC_DRAW );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );			// Bind The Buffer
				gl.glBufferData( GL2.GL_ARRAY_BUFFER, normalDataBuf.capacity() * 4, normalDataBuf, GL2.GL_STATIC_DRAW );							
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[4] );	// Bind The Buffer
				gl.glBufferData( GL2.GL_ELEMENT_ARRAY_BUFFER, CubeIndexWFBuf.capacity() * 4,CubeIndexBuf, GL2.GL_STATIC_DRAW );								
			}else{				
				VBO_Vertices[0] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[0], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, normalDataBuf );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_QUADS, 24, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();							
				
				VBO_Vertices[4] = gl.glGenLists(1);			
				gl.glNewList(VBO_Vertices[4], GL2.GL_COMPILE_AND_EXECUTE);				
				gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );				
				gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, positionDataBuf );			
				gl.glDrawElements   (    GL2.GL_LINE_LOOP, 4, GL2.GL_UNSIGNED_INT, CubeIndexWFBuf);
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

