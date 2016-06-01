package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Segment extends Base{
    float x; float y; float z;
    float dx; float dy; float dz;
    Base det = null;
 

	public int ChunkId() {	return 12112; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format("Event_Segment [x=%s, y=%s, z=%s, dx=%s, dy=%s, dz=%s]", x, y, z, dx, dy, dz);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		x  		= Factory.getFloat (din, BytesRead);
		y    	= Factory.getFloat (din, BytesRead);
		z    	= Factory.getFloat (din, BytesRead);
		dx 		= Factory.getFloat (din, BytesRead);
		dy    	= Factory.getFloat (din, BytesRead);
		dz    	= Factory.getFloat (din, BytesRead);					
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(28);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, x);
		Factory.putFloat (toReturn, y);
		Factory.putFloat (toReturn, z);
		Factory.putFloat (toReturn, dx);
		Factory.putFloat (toReturn, dy);
		Factory.putFloat (toReturn, dz);						
		return toReturn;		
	}
	


	public void draw(GL2 gl) {	
		if(VBO_Vertices[0]==0){
//			det = jfrog.Common.geomIdMap.get(id);
//			if(det==null){VBO_Vertices[0]=-1; return;}
			
			float [] positionData = new float[]{x, y, z, x+50*dx, y+50*dy, z+50*dz};											
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
