package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_Hit extends Base{
    float x; float y; float z; float charge;
    Base det = null;
 

	public int ChunkId() {	return 12111; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String.format("Event_Hit [Id=%s, x=%s, y=%s, z=%s, charge=%s]", id, x, y, z, charge);
	}
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		id 		= Factory.getUInt  (din, BytesRead);
		x  		= Factory.getFloat (din, BytesRead);
		y    	= Factory.getFloat (din, BytesRead);
		z    	= Factory.getFloat (din, BytesRead);
		charge  = Factory.getFloat (din, BytesRead);			
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(20);		
		Factory.putUInt  (toReturn, id);
		Factory.putFloat (toReturn, x);
		Factory.putFloat (toReturn, y);
		Factory.putFloat (toReturn, z);
		Factory.putFloat (toReturn, charge);				
		return toReturn;		
	}
	
	public float getPosX() {return x;}
	public float getPosY() {return y;}
	public float getPosZ() {return z;}

	public void drawSimple(GL2 gl) {					
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
	
	

	public void drawTrackHit(GL2 gl) {
		if(VBO_Vertices[0]==0){		
			//if(style!=null)System.out.printf("TrackingHit Display --> Style = %s\n",style.toString());
			//else System.out.printf("TrackingHit Display --> Style = %s\n","NoStyle");
			CMS_Geom_Tracking tkDet = (CMS_Geom_Tracking)det;
			if(tkDet==null)VBO_Vertices[0] = -1;
			
			float xlocal = x - tkDet.PosX;
			float ylocal = y - tkDet.PosY;
			float zlocal = z - tkDet.PosZ;
			float A = xlocal*tkDet.WidthX  + ylocal*tkDet.WidthY  + zlocal*tkDet.WidthZ;
			A/= (tkDet.WidthX*tkDet.WidthX + tkDet.WidthY*tkDet.WidthY + tkDet.WidthZ*tkDet.WidthZ);
			xlocal-=A*tkDet.WidthX;ylocal-=A*tkDet.WidthY;zlocal-=A*tkDet.WidthZ;
			float B = xlocal*tkDet.LengthX + ylocal*tkDet.LengthY + zlocal*tkDet.LengthZ;
			B/= (tkDet.LengthX*tkDet.LengthX + tkDet.LengthY*tkDet.LengthY + tkDet.LengthZ*tkDet.LengthZ);
			xlocal-=B*tkDet.LengthX;ylocal-=B*tkDet.LengthY;zlocal-=B*tkDet.LengthZ;
			float C = xlocal*tkDet.ThickX  + ylocal*tkDet.ThickY  + zlocal*tkDet.ThickZ;
			C/= (tkDet.ThickX*tkDet.ThickX + tkDet.ThickY*tkDet.ThickY + tkDet.ThickZ*tkDet.ThickZ);
			xlocal-=C*tkDet.ThickX;ylocal-=C*tkDet.ThickY;zlocal-=C*tkDet.ThickZ;
							
			float [] positionData = new float[]{ 
					tkDet.PosX + A*tkDet.WidthX + tkDet.LengthX + C*tkDet.ThickX,
					tkDet.PosY + A*tkDet.WidthY + tkDet.LengthY + C*tkDet.ThickY,
					tkDet.PosZ + A*tkDet.WidthZ + tkDet.LengthZ + C*tkDet.ThickZ,
					tkDet.PosX + A*tkDet.WidthX - tkDet.LengthX + C*tkDet.ThickX,
					tkDet.PosY + A*tkDet.WidthY - tkDet.LengthY + C*tkDet.ThickY,
					tkDet.PosZ + A*tkDet.WidthZ - tkDet.LengthZ + C*tkDet.ThickZ,
					x, y, z};											
			float [] normalData   = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
			int [] CubeIndex    = {0, 1, 2};		
			
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
				gl.glDrawElements   (    GL2.GL_LINES , 2, GL2.GL_UNSIGNED_INT, CubeIndexBuf); CubeIndexBuf.position(2);
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
				gl.glDrawElements( GL2.GL_LINES , 2, GL2.GL_UNSIGNED_INT, 0);				
				if(this.getStyle().texmarker!=null)this.getStyle().texmarker.bind(gl);
				gl.glDrawElements( GL2.GL_POINTS, 1, GL2.GL_UNSIGNED_INT, 2*2);			
				if(this.getStyle().texmarker!=null)gl.glBindTexture(GL2.GL_TEXTURE_2D,0);				
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
		}
	}		
	
	
	public void draw(GL2 gl) {					
		if(VBO_Vertices[0]==0)det = jfrog.Common.getObjectWidthId(id, jfrog.Common.geomIdMap);

		//if(det!=null)det.draw(gl);

		try{			
			CMS_Geom_Tracking tmpdet = (CMS_Geom_Tracking)det;
			if(tmpdet!=null){
				if(tmpdet.isDaughterOf(12000000))drawTrackHit(gl);
				else drawSimple(gl);
			}
		}catch(ClassCastException e){
			drawSimple(gl);
		}
			
		
	}		
	
}
