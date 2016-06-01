package jfrog.object;

import java.net.URL;
import java.util.ArrayList;


public class Events extends Base {
	int currentEvent = -1;
	private ArrayList<Base> daughtersBuffered = new ArrayList<Base>();
	private ArrayList<String>  daughtersFileName = new ArrayList<String>();
	
	public Events(){		
	}
	
	public void LoadFile(URL url){
		if(url==null)return;
		name = url.getPath().substring(url.getPath().lastIndexOf("/")+1);

		Base tmp = new BaseColl(url);
		for(int i=0;i<tmp.getDaughtersSize();i++){
			daughtersBuffered.add(tmp.getDaughter(i));
			daughtersFileName.add(name);
		}
	}	
		
	public void loadEvent(int i, int previousEvent){
		currentEvent = i;
		if(currentEvent>=daughtersBuffered.size())return;
		name = daughtersFileName.get(i);
		Event event = (Event)daughtersBuffered.get(i);
		if(event==null)return;    	
		event.load();
		if(previousEvent>=0)event.copyProperties(daughtersBuffered.get(previousEvent), event);    	
		event.mother = this;
		daughters.add(event);		
	}
	
	public Event getEvent(){
		if(daughters.size()==0)return null;
		return (Event)daughters.get(0);
	}
	
	
	public void loadAllEvents(){
		for(int i=0;i<daughtersBuffered.size();i++)loadEvent(i, -1);
	}
	
	public void unloadEvent(int i){		
		Event event = (Event)daughtersBuffered.get(i);
		daughters.remove(event);
		event.setStyle(null, true, false);		
	}	
	
	public void unloadAllEvents(){
		for(int i=0;i<daughtersBuffered.size();i++)unloadEvent(i);
	}	
	
	public void nextEvent(){
		int previousEvent = currentEvent;
		currentEvent++;
		if(currentEvent>=daughtersBuffered.size()){
			currentEvent = 0;	
		}
				 			
		loadEvent(currentEvent, previousEvent);
		unloadEvent(previousEvent);
		jfrog.Common.treeMenu.refresh();
		//jfrog.Common.overlay.updateText();
	}
	
	public void previousEvent(){
		int previousEvent = currentEvent;		
		currentEvent--;
		if(currentEvent<0){
			currentEvent = daughtersBuffered.size() -1;
		}
		loadEvent(currentEvent, previousEvent);				
		unloadEvent(previousEvent);
		jfrog.Common.treeMenu.refresh();
		//jfrog.Common.overlay.updateText();		
	}	

}