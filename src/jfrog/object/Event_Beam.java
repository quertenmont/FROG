package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Beam extends Base{
    public float x; public float y; public float z;
    public float px; public float py; public float pz;
    public float sx; public float sy; public float sz;    
    public long N;
    

	public int ChunkId() {	return 19001; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String
				.format("Event_Beam [N=%s, x=%s, y=%s, z=%s, px=%s, py=%s, pz=%s, sx=%s, sy=%s, sz=%s]",
						N, x, y, z, px, py, pz, sx, sy, sz);
	}
	
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 			= Factory.getUInt  (din, BytesRead);		
		x  			= Factory.getFloat (din, BytesRead);
		y    		= Factory.getFloat (din, BytesRead);
		z    		= Factory.getFloat (din, BytesRead);
		px  		= Factory.getFloat (din, BytesRead);
		py    		= Factory.getFloat (din, BytesRead);
		pz    		= Factory.getFloat (din, BytesRead);
		sx  		= Factory.getFloat (din, BytesRead);
		sy    		= Factory.getFloat (din, BytesRead);
		sz    		= Factory.getFloat (din, BytesRead);		
		N			= Factory.getUInt  (din, BytesRead);
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(44);		
		Factory.putUInt  (toReturn, id);		
		Factory.putFloat (toReturn, x);
		Factory.putFloat (toReturn, y);
		Factory.putFloat (toReturn, z);
		Factory.putFloat (toReturn, px);
		Factory.putFloat (toReturn, py);
		Factory.putFloat (toReturn, pz);
		Factory.putFloat (toReturn, sx);
		Factory.putFloat (toReturn, sy);
		Factory.putFloat (toReturn, sz);
		Factory.putUInt  (toReturn, N);		
		return toReturn;		
	}
			
	public void draw(GL2 gl) {		
		if(VBO_Vertices[0]==0){
			float [] positionData = new float[(int)(N*3)];											
			float [] normalData   = new float[(int)(N*3)];
			int  [] CubeIndex  	= new int[(int)(N  )];		
						
			for(int i=0;i<N;i++){
				positionData[3*i+0] = (float)(sx * Math.sqrt(-2*Math.log(Math.random())) * Math.cos(2*Math.PI*Math.random()));
				positionData[3*i+1] = (float)(sy * Math.sqrt(-2*Math.log(Math.random())) * Math.cos(2*Math.PI*Math.random()));
				positionData[3*i+2] = (float)(sz * Math.sqrt(-2*Math.log(Math.random())) * Math.cos(2*Math.PI*Math.random()));				
				normalData  [3*i+0] = 0;
				normalData  [3*i+1] = 0;
				normalData  [3*i+2] = 0;
				CubeIndex   [i]     = (short)i;
			}			
			
			IntBuffer CubeIndexBuf    = GLBuffers.newDirectIntBuffer(CubeIndex);
			FloatBuffer positionDataBuf = GLBuffers.newDirectFloatBuffer(positionData);					
			FloatBuffer normalDataBuf   = GLBuffers.newDirectFloatBuffer(normalData);

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
				gl.glDrawElements   (    GL2.GL_POINTS, (int)N, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				if(this.getStyle().texmarker!=null)gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			gl.glPushMatrix();
			gl.glTranslatef(x+jfrog.Common.time*px*30, y+jfrog.Common.time*py*30, z+jfrog.Common.time*pz*30);
						
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );
				if(this.getStyle().texmarker!=null)this.getStyle().texmarker.bind(gl);
				gl.glDrawElements( GL2.GL_POINTS, (int)N, GL2.GL_UNSIGNED_INT, 0);	
				if(this.getStyle().texmarker!=null)gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
			
			gl.glPopMatrix();
		}		
	}		
	
}
