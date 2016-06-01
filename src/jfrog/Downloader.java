package jfrog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class Downloader  implements Runnable {
	public URL url    = null;
	public URL outurl = null;
	public boolean downloading = false;
			
		
	public Downloader(String stringURL){
		this.url = jfrog.Common.GetURL(stringURL);
		
		if(this.url==null){
			System.out.printf("File %s can not be openned\n", stringURL);
			//System.exit(0);
		}
						
		//Check for local files:
		File file = new File(this.url.toString());		
		if(file!=null && file.canRead()){					
			this.outurl = this.url;
			downloading = false;
			return;			
		}
				
		downloading = true;
		new Thread(this).start();
	}	
	
	public Downloader(URL url){
		this.url = url;
		downloading = true;
		new Thread(this).start();		
	}
	
	public void run() {
		if(url==null){downloading=false; return;}		

		String FileName = url.toString().substring(url.toString().lastIndexOf("/"));
		if(FileName.endsWith(".gz")){
			FileName = FileName.substring(0, FileName.indexOf(".gz"));
		}else if(FileName.endsWith(".zip")){
			FileName = FileName.substring(0, FileName.indexOf(".zip"));			
		}
		
		File outDir = new File(System.getProperty("java.io.tmpdir"), "jFROG");
		outDir.mkdir();		

		File fileInfo = new File(outDir, FileName + ".info");		
	    File outFile = new File(outDir, FileName);	    
	    
		try {	    	    
			//Check if file as already been downloaded
			if(outFile.exists() && fileInfo!=null && fileInfo.canRead()){
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileInfo)));
				String inputURL = reader.readLine();											
				String size     = reader.readLine();
				reader.close();				
								
				if(inputURL.equals(url.toString()) && Integer.parseInt(size)==url.openConnection().getContentLength()){
					outurl = outFile.toURI().toURL();
					System.out.printf("Previously Downloaded (%s)\n",url.toString());
					downloading=false;
					return;
				}
			}												
			
		    URLConnection urlcon = url.openConnection();
		    urlcon.setReadTimeout(25000);		
//		    urlcon.setUseCaches(false);
//		    String usepass = "guest" + ":" + "guest";
//		    String basicAuth = "Basic "+  javax.xml.bind.DatatypeConverter.printBase64Binary(usepass.getBytes());
//		    urlcon.setRequestProperty("Authorization", basicAuth);
//		    System.out.println("Expires: " + urlcon.getExpiration()); 
			
			InputStream in = null;
			if(url.toString().endsWith(".gz")){
				in = new BufferedInputStream(new GZIPInputStream(urlcon.getInputStream()));								
			}else if (url.toString().endsWith(".zip")){
				in = new BufferedInputStream(new ZipInputStream(urlcon.getInputStream()));				
			}else{
				in = new BufferedInputStream(urlcon.getInputStream());
			}							
			DataInputStream din = new DataInputStream(in);
			
			FileOutputStream dou = new FileOutputStream(outFile);
			int byteRead = 0;
			byte [] buffer = new byte[4096];
			while((byteRead=din.read(buffer))>0){dou.write(buffer,0, byteRead);}			
			
			if(dou != null){dou.close();}												
			if(din != null){din.close();}		    

			outurl = outFile.toURI().toURL();
			System.out.printf("Download finished (%s)\n",url.toString());
			downloading=false;
						
			FileWriter writter = new FileWriter(fileInfo);
			writter.write(url.toString() + "\n" + url.openConnection().getContentLength());
			writter.close();
			
			return;		   		    			
		} catch (IOException ioe) {
			System.out.println("IOException : " + ioe);			
			ioe.printStackTrace();
		}  
		
	    System.out.printf("Download failed (%s)\n",url.toString());
	    downloading=false;		
		return;
	}		
	
	public void waitUntilDownloaded(){
		while(downloading){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
    	}  
	}
	
}
