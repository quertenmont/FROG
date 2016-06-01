
package jfrog.object;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Event_SimParticles extends Base{
	long track_id;
	float px; float py; float pz; float q;
	float E;int type;
	float v1x, v1y, v1z;
	float v2x, v2y, v2z;
	short nPoints = 0;
	
	
	int 		 MaxIteration_ = 500000;
	float        MinSegLength_ = 20;
	float []	 BField_       = new float[]{0 , 0 , 3.8f};	
	float		 BField_Radius = 129;
	float		 BField_Length = 300;	
	
	public int ChunkId() {	return 11040; }
	public boolean isCompactible() {return true;}    

	public String toString() {
		return String
				.format("Event_SimTrack [q=%+3.1f px=%+6.2f, py=%+6.2f, pz=%6.2f, track_id=%s, type=%s]",
						q, px, py, pz, track_id, type);
	}
	
	
	public int readData(URL url, DataInputStream din, Base obj, Base parent, int bytesToRead, int fileOffset, int level) throws IOException {
		int [] BytesRead = {0};
		track_id	= Factory.getUInt  (din, BytesRead);
		px 			= Factory.getFloat (din, BytesRead);
		py   	 	= Factory.getFloat (din, BytesRead);
		pz    		= Factory.getFloat (din, BytesRead);		
		E    		= Factory.getFloat (din, BytesRead);
		type   		= Factory.getInt   (din, BytesRead);
		q 			= Factory.getFloat (din, BytesRead);		
		v1x 		= Factory.getFloat (din, BytesRead);
		v1y   	 	= Factory.getFloat (din, BytesRead);
		v1z    		= Factory.getFloat (din, BytesRead);				
		v2x 		= Factory.getFloat (din, BytesRead);
		v2y   	 	= Factory.getFloat (din, BytesRead);
		v2z    		= Factory.getFloat (din, BytesRead);				
		return BytesRead[0];
	}
	
	public ByteBuffer writeData(){		
		ByteBuffer toReturn = ByteBuffer.allocate(52);		
		Factory.putUInt  (toReturn, track_id);		
		Factory.putFloat (toReturn, px);
		Factory.putFloat (toReturn, py);
		Factory.putFloat (toReturn, pz);		
		Factory.putFloat (toReturn, E);				
		Factory.putInt   (toReturn, type);		
		Factory.putFloat (toReturn, q);	
		Factory.putFloat (toReturn, v1x);
		Factory.putFloat (toReturn, v1y);
		Factory.putFloat (toReturn, v1z);
		Factory.putFloat (toReturn, v2x);
		Factory.putFloat (toReturn, v2y);
		Factory.putFloat (toReturn, v2z);		
		return toReturn;		
	}
	
	public void draw(GL2 gl) {					
		if(VBO_Vertices[0]==0){
			float [] positionData = null; 											 
			int [] CubeIndex    = null;
			
			boolean vtx2 = (v1x!=v2x || v1y!=v2y || v1z!=v2z);
									
			if(Math.sqrt(v1x*v1x+v1y*v1y)>BField_Radius || Math.abs(v1z)>BField_Length ){
				//IF PREMIARY VERTEX IS OUT OF THE VOLUME, JUST DO NOTHING --> PRINT A POINT AT VTX1
				positionData = new float[]{v1x, v1y, v1z, v1x, v1y, v1z};
				CubeIndex    = new int[]{0,1};
			}else{
				double  px_ = px / 0.003f;		double  py_ = py / 0.003f;		double  pz_ = pz / 0.003f;
				double  pt_ = Math.sqrt(px_*px_+py_*py_);	double  p_ = Math.sqrt(px_*px_+py_*py_+pz_*pz_);
				if(q!=0){
					//IF PARTICLE IS CHARGED DO THE TRACKING IN MAGNETIC FIELD
					
					double e  = E  / 0.003f;
					double M  = Math.sqrt(e*e - (px_*px_ + py_*py_ + pz_*pz_) );               
					double vx = px_/M;				double vy = py_/M;				double vz = pz_/M;
					double Bx = BField_[0];			double By = BField_[1];			double Bz = BField_[2];
					double ax = (q/M)*(Bz*vy-By*vz);double ay = (q/M)*(Bx*vz-Bz*vx);double az = (q/M)*(By*vx-Bx*vy);
					double xold=v1x;				double yold=v1y;				double zold=v1z;
					double x=xold;					double y=yold;					double z=zold;

					double dt = 1/p_;
					if(pt_<266 && vz < 0.0012)	dt = Math.abs(0.001/vz);

					double VTold = Math.sqrt(vx*vx+vy*vy);
					double DVertex = 0;
					if(vtx2)DVertex = ((x-v2x)*(x-v2x)) + ((y-v2y)*(y-v2y)) + ((z-v2z)*(z-v2z)) ;

					positionData = new float[3*150];
					CubeIndex    = new int[150];				
					positionData[nPoints*3+0] = (float)x;
					positionData[nPoints*3+1] = (float)y;
					positionData[nPoints*3+2] = (float)z;
					CubeIndex	[nPoints]     = nPoints;nPoints++;

					int k = 0;
					boolean ShouldPropagate = true;
					while(ShouldPropagate && k<MaxIteration_){
						k++;

						vx += ax*dt;				vy += ay*dt;					vz += az*dt;
						double VTratio = VTold/Math.sqrt(vx*vx+vy*vy);
						vx *= VTratio;				vy *= VTratio;                  
						ax  = (q/M)*(Bz*vy - By*vz);ay  = (q/M)*(Bx*vz - Bz*vx);	az  = (q/M)*(By*vx - Bx*vy);
						x  += vx*dt;				y  += vy*dt;					z  += vz*dt;

						if(vtx2){
							double newDVertex = ((x-v2x)*(x-v2x)) + ((y-v2y)*(y-v2y)) + ((z-v2z)*(z-v2z));
							if(newDVertex<DVertex){		
								DVertex = newDVertex;
							}else{
								// Just make sure that the SimTrack really go from Vertex1 To Vertex2 if Vertex2 Exist!
								x = v2x;		y = v2y;					z = v2z;
								ShouldPropagate = false;
							}
						}

						if( (x*x+y*y) > BField_Radius*BField_Radius ){ x /= (x*x+y*y)/(BField_Radius*BField_Radius); y /= (x*x+y*y)/(BField_Radius*BField_Radius); break;}
						if( Math.abs(z)>BField_Length)break;
						double SegLength = Math.sqrt( (x-xold)*(x-xold) + (y-yold)*(y-yold) + (z-zold)*(z-zold) );
						if( SegLength<MinSegLength_ && ShouldPropagate)continue;			
						xold = x;					yold = y;						zold = z;

						positionData[nPoints*3+0] = (float)x;
						positionData[nPoints*3+1] = (float)y;
						positionData[nPoints*3+2] = (float)z;
						CubeIndex	[nPoints]     = nPoints;	nPoints++;
						if(nPoints*3>=positionData.length)break;
					}
				}else{    
					nPoints = 2;
					if(vtx2 && Math.sqrt(v2x*v2x+v2y*v2y)<BField_Radius && Math.abs(v2z)<BField_Length ){
						//IF PARTICLE IS NEUTRAL AND VTX2 IS IN THE VOLUME DRAW A STRAIGHT LINE BETWEEN VTX1 AND VTX2
						
						positionData = new float[]{v1x, v1y, v1z, v2x, v2y, v2z};
						CubeIndex    = new int[]{0,1};
					}else{                        
						//IF PARTICLE IS NEUTRAL AND VTX2 IS OUT OF THE VOLUME DRAW A STRAIGHT LINE BETWEEN VTX1 AND THE VOLUMENT EDGE TOWARD VTX2						
						
						double t1 = (BField_Radius/Math.sqrt(px_*px_+py_*py_));
						double t2 = (BField_Length/Math.abs(pz_));
						if(t2<t1)t1=t2;
						positionData = new float[]{v1x, v1y, v1z, (float)(px_*t1), (float)(py_*t1), (float)(pz_*t1)};
						CubeIndex    = new int[]{0,1};
					}
				}			

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
				gl.glDrawElements   (    GL2.GL_LINE_STRIP, nPoints, GL2.GL_UNSIGNED_INT, CubeIndexBuf);
				gl.glEndList();			
			}
		}else if(VBO_Vertices[0]>0){
			if(jfrog.Common.support_VBO){
				gl.glBindBuffer( GL2.GL_ELEMENT_ARRAY_BUFFER, VBO_Vertices[0] );
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[2] );	gl.glNormalPointer  (    GL2.GL_FLOAT, 0, 0 );							
				gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, VBO_Vertices[1] );	gl.glVertexPointer  ( 3, GL2.GL_FLOAT, 0, 0 );				
				gl.glDrawElements( GL2.GL_LINE_STRIP,  nPoints, GL2.GL_UNSIGNED_INT, 0);								
			}else{			
				gl.glCallList(VBO_Vertices[0]);						
			}
		}
	}	
	
		
	
}
