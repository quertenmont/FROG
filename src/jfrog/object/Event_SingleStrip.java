package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_SingleStrip extends Base{
    float x1; float y1; float z1;
    float x2; float y2; float z2;
    Base det = null;
 
	public int ChunkId() {	return 11100; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format(
				"Event_SingleStrip [x1=%s, y1=%s, z1=%s, x2=%s, y2=%s, z2=%s]",
				x1, y1, z1, x2, y2, z2);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		x1 		= Factory.getFloat (din, BytesRead);
		y1    	= Factory.getFloat (din, BytesRead);
		z1    	= Factory.getFloat (din, BytesRead);
		x2 		= Factory.getFloat (din, BytesRead);
		y2    	= Factory.getFloat (din, BytesRead);
		z2    	= Factory.getFloat (din, BytesRead);					
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(28);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, x1);
		Factory.putFloat (toReturn, y1);
		Factory.putFloat (toReturn, z1);
		Factory.putFloat (toReturn, x2);
		Factory.putFloat (toReturn, y2);
		Factory.putFloat (toReturn, z2);						
		return toReturn;		
	}
	


	public void draw(GL2 gl) {	
		if(VBO_Vertices[0]==0){
			//det = jfrog.Common.geomIdMap.get(id);
			//if(det==null){VBO_Vertices[0]=-1; return;}
			
			float [] positionData = new float[]{x1, y1, z1, x2, y2, z2};											
			int [] CubeIndex    = {0, 1};		
			
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
				gl.glDrawElements   (    GL2.GL_LINES, 2, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );				
				gl.glDrawElements( GL2.GL_LINES, 2, GL2.GL_UNSIGNED_INT, 0);								
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
		}
	}	
	

	
}
