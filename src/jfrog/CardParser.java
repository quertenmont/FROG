package jfrog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jfrog.object.Base;

public class CardParser {
	static Map<String, String > parameters = new TreeMap<String, String>();
	
	public CardParser(String stringurl){ 
		String config = "";
		try {
			config = fileToString(stringurl, true);			
			config += cleanString(jfrog.Common.Text_Config, "");
		} catch (IOException e) {
			e.printStackTrace();
		}			
	
		String [] configLines = config.split("\n");		
		for(int l=0;l<configLines.length;l++){		
			int index;
			
			index = configLines[l].indexOf("+=");
			if(index>0){				
				String Key = configLines[l].substring(0, index);
				String Val = configLines[l].substring(index+2);
				
				if(Val.startsWith("{")) Val = Val.substring(1);
				if(Val.endsWith("}")) Val = Val.substring(0, Val.length()-1);
				
				String PreviousVal = parameters.get(Key);
				if(PreviousVal==null)PreviousVal = "";
				if(PreviousVal.startsWith("{") && PreviousVal.endsWith("}")){
					PreviousVal = (PreviousVal.substring(0, PreviousVal.length()-1)) + "," + Val + "}";
				}else{					
					PreviousVal = "{" + PreviousVal + ","  + Val + "}";
				}								
				parameters.put(Key, PreviousVal);				
			}			
			
			
			index = configLines[l].indexOf("=");
			if(index>0){
				String Key = configLines[l].substring(0, index);
				String Val = configLines[l].substring(index+1);
				parameters.put(Key, Val);				
			}
		}			
	}
	
		
	
	public static String fileToString(String stringurl, boolean doCleaning) throws IOException {
		URL url = jfrog.Common.GetURL(stringurl);					
				
		InputStream in = url.openStream();			
		Reader din = new InputStreamReader(new BufferedInputStream(in));		
		BufferedReader reader = new BufferedReader(din);			
		String toReturn = "";
		
		while (true){
			String Line = reader.readLine();
			if(Line==null)break;
			toReturn += Line + "\n";
		}
		reader.close();				
						
		if(doCleaning)toReturn = cleanString(toReturn, stringurl);				
		return toReturn;
	}
	
	public static String cleanString(String string, String stringurl) throws IOException {				
		String parsedFile = "";
		
		boolean Quote1 = false;
		boolean Quote2 = false;
		
		String [] lines = string.split("\n"); 
		
		for(int l=0;l<lines.length;l++){
			String line = lines[l];
			
			//remove white space and split lines regarding to ';'
			String cleanedLine = "";
			for(int i=0;i<line.length();i++){
				char c = line.charAt(i);
				if(c=='\"'){Quote1 = !Quote1; continue;}				
				if(c=='\''){Quote2 = !Quote2; continue;}
				if(c==' '  && !Quote1 && !Quote2)continue;
				if(c=='\t' && !Quote1 && !Quote2)continue;				
				if(c==';'  && !Quote1 && !Quote2)c='\n';
//				if(c=='/' && i+1<line.length() && line.charAt(i+1)=='/' && !Quote1 && !Quote2)break;				
				cleanedLine += c;
			}
			
			//remove commented parts
			
			int commentIndex = cleanedLine.indexOf("//");
			if(commentIndex>=0){				
				//make sure we are not removing http/ftp links
				if(commentIndex>0 && cleanedLine.charAt(commentIndex-1)==':'){
					commentIndex = cleanedLine.indexOf("//", commentIndex+1);
					if(commentIndex<0)commentIndex=cleanedLine.length();
				}
				cleanedLine = cleanedLine.substring(0, commentIndex);
			}
		
			//skip white lines
			if(cleanedLine.length()==0)continue;
			
			//System.out.printf("CleanedLine = %s",cleanedLine);
			
			//add the cleaned line to the text buffer
			parsedFile += cleanedLine;
		}
						
		
		
		int Include = parsedFile.indexOf("#Include=");
		while(Include>=0){			
			int IncludeEnd = parsedFile.indexOf("\n",Include);
			String stringurlInclude = stringurl.substring(0,stringurl.lastIndexOf("/")+1) + parsedFile.substring(Include+9, IncludeEnd);			
			parsedFile = parsedFile.substring(0,Include) + fileToString(stringurlInclude, true) + parsedFile.substring(IncludeEnd+1);		
			Include = parsedFile.indexOf("#Include=",IncludeEnd);
		}		
		
		return parsedFile;
	}	 	
		
	public boolean	 tagExist   (String Key){	return parameters.containsKey(Key);					}
	public String    toString   (String Key){	return                      parameters.get(Key) ;	}
	public int       toInt      (String Key){	return Integer.parseInt    (parameters.get(Key));	}
	public long      toLong     (String Key){	return Long   .parseLong   (parameters.get(Key));	}
	public float     toFloat    (String Key){	return Float  .parseFloat  (parameters.get(Key));	}
	public double    toDouble   (String Key){	return Double .parseDouble (parameters.get(Key));	}
	public boolean   toBoolean  (String Key){	return Boolean.parseBoolean(parameters.get(Key));	}	
	public String [] toStringArr(String Key){	
		String Val = parameters.get(Key);
		if(Val!=null && Val.startsWith("{") && Val.endsWith("}")){
			Val = Val.substring(Val.indexOf("{")+1, Val.lastIndexOf("}"));
			return Val.split(",");			
		}
		//return null;
		return new String []{};
	}
	
	public int    [] toIntArr   (String Key){
		String [] Vals = toStringArr(Key);
		int [] toReturn = new int [Vals.length];
		for(int i=0;i<Vals.length;i++){toReturn[i] = Integer.parseInt(Vals[i]);}
		return toReturn;
	}
	public long    [] toLongArr   (String Key){
		String [] Vals = toStringArr(Key);
		long [] toReturn = new long [Vals.length];
		for(int i=0;i<Vals.length;i++){toReturn[i] = Long.parseLong(Vals[i]);}
		return toReturn;
	}	
	public float [] toFloatArr (String Key){
		String [] Vals = toStringArr(Key);
		float [] toReturn = new float [Vals.length];
		for(int i=0;i<Vals.length;i++){toReturn[i] = Float.parseFloat(Vals[i]);}
		return toReturn;
	}	
	public double[] toDoubleArr (String Key){
		String [] Vals = toStringArr(Key);
		double [] toReturn = new double [Vals.length];
		for(int i=0;i<Vals.length;i++){toReturn[i] = Double.parseDouble(Vals[i]);}
		return toReturn;
	}	
		
		
	public void applyStyle(Base base, boolean clearStyleCollection){
		ArrayList<Long> alreadyParsedIds = new ArrayList<Long>();
		
	    Set<String> e = parameters.keySet();		
	    Iterator<String> it = e.iterator();	    
	    while(it.hasNext())
	    {	    	
	    	String Key = (String)it.next();
	    	if(Key.startsWith("Id")){
	    		String [] subKeys = Key.split("_");	    		
	    		long Id = Long.parseLong(subKeys[1]);

	    		boolean isGeom = true;
	    		ArrayList<Base> objs = jfrog.Common.getObjectsWidthId(Id, jfrog.Common.geomIdMap);
	    		if(objs==null){
	    			Base obj = base.findId(Id);	    			
	    			if(obj==null)continue;
	    			objs = new ArrayList<Base>();
	    			objs.add(obj);
	    			isGeom = false;
	    		}
	    			    			    		
	    		for(int j=0;j<objs.size();j++){
	    			Base obj = objs.get(j);
	    			
	    			if(obj==null)continue;
		    		Style style = obj.getStyle(); 
		    		if(!alreadyParsedIds.contains(Id)){		    			
		    			style = obj.getStyle();
		    			if(style==null){
		    				style = new Style();
		    			}else{
		    				style = (Style)style.clone();
		    			}
		    			style.isGeom = isGeom;
		    		}
		    		if(      subKeys[2].equals("Color"        )){	style.color       = toFloatArr(Key);	 
		    		}else if(subKeys[2].equals("Texture"      )){	style.texture     = toString(Key);	    		
		    		}else if(subKeys[2].equals("Marker"       )){	style.marker      = (short)toInt(Key);    				    				    			    				    		
		    		}else if(subKeys[2].equals("MarkerSize"   )){	style.markerSize  = (short)toInt(Key);    				    				    				    			
		    		}else if(subKeys[2].equals("Thickness"    )){	style.thickness   = (short)toFloat(Key);		    		
		    		}else if(subKeys[2].equals("ShowDet"      )){	style.showDet     = toBoolean(Key);
		    		}else if(subKeys[2].equals("EMin"         )){	style.minE        = toFloat(Key);	    		
		    		}else if(subKeys[2].equals("PtMin"        )){	style.minPt       = toFloat(Key);	    		
		    		}else if(subKeys[2].equals("Interpolation")){	style.interpolate = toBoolean(Key);
		    		}//else{System.out.printf("Unknow Id Tag = %s\n",Key.toString()); }	    		
		    		if(style != obj.getStyle()){obj.setStyle(style, true, true);}		    		
	    		}
	    	}   	
	    }
	    Style.sortStyles(jfrog.Common.styles);
//	    for(int i=0;i<jfrog.Common.styles.size();i++)System.out.printf("%d - %s - %d\n", i, jfrog.Common.styles.get(i).toString(), jfrog.Common.styles.get(i).objList.size());
	    	    
	    
//	    base.updateStyleColl(clearStyleCollection);
	}
	
	public void applyConfig(Base base){
		applyStyle(base, true);
		
		int [] GeomToDisplay = toIntArr("GeomToDisplay");
		if(jfrog.Common.geom!=null){
			for(int i=0;i<GeomToDisplay.length;i++){
		    	jfrog.object.Base tmp = jfrog.Common.geom.findId(GeomToDisplay[i]);
		    	if(tmp!=null)tmp.setVisible((byte)1);			
			}
		}
		
		int [] EventToDisplay = toIntArr("EventToDisplay");
		if(jfrog.Common.events!=null && jfrog.Common.events.getEvent()!=null){
			for(int i=0;i<EventToDisplay.length;i++){
		    	jfrog.object.Base tmp = jfrog.Common.events.getEvent().findId(EventToDisplay[i]);
		    	if(tmp!=null)tmp.setVisible((byte)1);			
			}
		}		

		if(tagExist("ScreenShot_Width" )){jfrog.Common.Screenshot_width  =  toInt("ScreenShot_Width" );}
		if(tagExist("ScreenShot_Height")){jfrog.Common.Screenshot_height =  toInt("ScreenShot_Height");}		
		
		//DET MAP
		if(tagExist("DetValueMapFile")){
			String filePath = toString("DetValueMapFile");
//		String filePath = "/rsc/Gain.txt";
			
			try {			
			URL url = jfrog.Common.GetURL(filePath);								
			InputStream in;

				in = url.openStream();			
				Reader din = new InputStreamReader(new BufferedInputStream(in));		
				BufferedReader reader = new BufferedReader(din);			
				
				double minValue = 1E100;
				double maxValue = -1E100;
				double minValueTmp =  1E100;
				double maxValueTmp = -1E100;								 
				long detId;
				double value;
				float defaultColor[] = new float[]{0,0,0,-1};				 
				while (true){										
					String Line = reader.readLine();
					if(Line==null)break;					
					if      (Line.startsWith("#minValue")){minValue = Double.parseDouble(Line.split("=")[1]);					
					}else if(Line.startsWith("#maxValue")){maxValue = Double.parseDouble(Line.split("=")[1]);
					}else if(Line.startsWith("#defaultColor")){
						String [] split = (Line.split("=")[1]).split(",");
						defaultColor[0] = Float.parseFloat(split[0]);
						defaultColor[1] = Float.parseFloat(split[1]);
						defaultColor[2] = Float.parseFloat(split[2]);
						defaultColor[3] = Float.parseFloat(split[3]);						
					}else if(Line.startsWith("#")){continue;					
					}else{						
						String [] split = Line.split(" ");
						detId = Long.parseLong(split[0]);
						value = Double.parseDouble(split[1]);
						
						if(value<minValueTmp)minValueTmp = value;
						if(value>maxValueTmp)maxValueTmp = value;					
						
				    	jfrog.object.Base tmp = jfrog.Common.geom.findId(detId);
				    	if(tmp!=null){
				    		if(defaultColor[3]>=0)tmp.getStyle().color = defaultColor;
				    		tmp.setValue(value, jfrog.Common.stylePalette);
				    	}
					}
				}			
				if(minValue!=1E100){jfrog.Common.stylePalette.minValue = minValue;
				}else{				jfrog.Common.stylePalette.minValue = minValueTmp; }
				
				if(maxValue!=-1E100){jfrog.Common.stylePalette.maxValue = maxValue;
				}else{			 	 jfrog.Common.stylePalette.maxValue = maxValueTmp;	}				
									
				reader.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		
	}
	

	
	String GenerateStyleString(Base obj, Base mother){
		String toReturn = "";
		
		if(obj!=null && obj.getStyle()!=null && (mother==null || mother.getStyle()==null || mother.getStyle()!=obj.getStyle())){
			Style defaultStyle = new Style();
			Style objStyle     = obj.getStyle();
			
			if(!Arrays.equals(objStyle.color, defaultStyle.color))toReturn+=String.format("Id_%d_Color     = {%f,%f,%f,%f};\n", obj.id, objStyle.color[0], objStyle.color[1], objStyle.color[2], objStyle.color[3]);
			if(objStyle.texture    != defaultStyle.texture)       toReturn+=String.format("Id_%d_Texture   = %s;\n", obj.id, objStyle.texture);
			if(objStyle.marker     != defaultStyle.marker)        toReturn+=String.format("Id_%d_Marker    = %d;\n", obj.id, objStyle.marker);			
			if(objStyle.markerSize != defaultStyle.markerSize)    toReturn+=String.format("Id_%d_MarkerSize= %d;\n", obj.id, objStyle.markerSize);			
			if(objStyle.thickness  != defaultStyle.thickness)     toReturn+=String.format("Id_%d_Thickness = %f;\n", obj.id, objStyle.thickness);			
			if(objStyle.showDet    != defaultStyle.showDet)       toReturn+=String.format("Id_%d_ShowDet   = %b;\n", obj.id, objStyle.showDet);			
			if(objStyle.minE       != defaultStyle.minE)          toReturn+=String.format("Id_%d_EMin      = %f;\n", obj.id, objStyle.minE);			
			if(objStyle.minPt      != defaultStyle.minPt)         toReturn+=String.format("Id_%d_PtMin     = %f;\n", obj.id, objStyle.minPt);			
						
			if(toReturn.length()>0){				
				toReturn = String.format("//%s\n",obj.toString()) + toReturn + "\n";
			}
		}
		
	
		for(int i=0;i<obj.getDaughtersSize();i++){		
			toReturn +=  GenerateStyleString(obj.getDaughter(i), obj);
		}		
		return toReturn;				
	}
	
	
	void GenerateConfig(String path){		
		String toWrite = GenerateStyleString(jfrog.Common.root, null);
		
		try {
			FileWriter output = new FileWriter(path);
			output.write(toWrite);
			output.close();				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
	
}





