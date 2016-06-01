package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_SimVtx extends Base{
    float x; float y; float z; long parentTkId;
 

	public int ChunkId() {	return 11020; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format("Event_SimVtx [x=%s, y=%s, z=%s, parentId=%d]", x, y, z, parentTkId);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		x  			= Factory.getFloat (din, BytesRead);
		y    		= Factory.getFloat (din, BytesRead);
		z    		= Factory.getFloat (din, BytesRead);
		parentTkId 	= Factory.getUInt  (din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(16);		
		Factory.putFloat (toReturn, x);
		Factory.putFloat (toReturn, y);
		Factory.putFloat (toReturn, z);
		Factory.putUInt  (toReturn, parentTkId);				
		return toReturn;		
	}
			
	public void draw(GL2 gl) {					
		if(VBO_Vertices[0]==0){
			float [] positionData = new float[]{x, y, z};														
			int [] CubeIndex    = {0};		
			
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
				gl.glDrawElements   (    GL2.GL_POINTS, 1, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				if(this.getStyle().texmarker!=null)gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
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
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
		}		
	}		
	
}
