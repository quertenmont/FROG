package jfrog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jfrog.object.Base;

public class Tree extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4468943184432884207L;
	JTree tree = null;
	JTextField text = null;
	JScrollPane treeView = null;
	public FrogTreeModel treeModel = null;

	public Tree(String title){
		super(title);
		
		treeModel = new FrogTreeModel(jfrog.Common.root);
		treeModel.addTreeModelListener(new FrogTreeModelListener());
		tree = new JTree(treeModel);
		
		//expand the tree up to level-3 --> nicer
    	this.expandTreeToLevelN(new TreePath(jfrog.Common.root), 2, 0);
		
		
		
//		tree = new JTree(makeTree(jfrog.Common.root));			
		tree.addTreeSelectionListener(new FrogTreeSelectionListener());
		tree.addTreeExpansionListener(new FrogTreeExpansionListener()); 
		tree.addMouseListener(FrogTreeMouseAdapter());
		tree.setCellRenderer(new FrogTreeCellRenderer());		
		treeView = new JScrollPane(tree);                                                 
		this.add(treeView);
		text = new JTextField("");
		this.add(text,BorderLayout.SOUTH);		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();		
		this.setSize(300,(int)screen.getHeight()-50);
		this.setLocation((int)(screen.getWidth()-300),0);	        
        this.setVisible(true);			
	}
	
	
	
	public void refresh(){
		jfrog.Common.treeMenu.treeModel.reload(jfrog.Common.root);
		expandTree(new TreePath(jfrog.Common.root));
	}
	
	public void expandTree(TreePath nodePath){
		Base node = (Base)nodePath.getLastPathComponent();
		if(node.expandedFlag)tree.expandPath(nodePath);				
		for(int i=0;i<node.getDaughtersSize();i++)expandTree(nodePath.pathByAddingChild(node.getDaughter(i)));		
	}	
	
	
	public void expandTreeToLevelN(TreePath nodePath, int maxLevel, int currentLevel){		
		Base node = (Base)nodePath.getLastPathComponent();
		
		if(currentLevel<=maxLevel){			
			tree.expandPath(nodePath);
			node.expandedFlag = true;
			for(int i=0;i<node.getDaughtersSize();i++)expandTreeToLevelN(nodePath.pathByAddingChild(node.getDaughter(i)),maxLevel, currentLevel+1);
		}
	}
	
	
					
	class FrogTreeSelectionListener implements TreeSelectionListener{

		public void valueChanged(TreeSelectionEvent arg0) {			
			if(arg0.isAddedPath()){			
				Base node = (Base) arg0.getPaths()[0].getLastPathComponent();
				if(node==null)return;				
				text.setText( node.toString() );
			}else{
				if(tree.getSelectionCount() == 0)text.setText( "" );
			}
		}		
		
	}

	class FrogTreeCellRenderer extends DefaultTreeCellRenderer{
		private static final long serialVersionUID = 5654720005517881350L;

		class ColoredIcon implements Icon{
			Color color;
			public ColoredIcon(Color color){this.color = color;}			
			public int getIconHeight() { return 10;}
			public int getIconWidth() { return 10; }
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.setColor(color);
				g.fillOval(x, y, getIconWidth(), getIconHeight());				 
			}			
		}		
		
		ColoredIcon greenIcon = new ColoredIcon(Color.green);
		ColoredIcon redIcon = new ColoredIcon(Color.red);
		ColoredIcon orangeIcon = new ColoredIcon(Color.orange);

		public FrogTreeCellRenderer() {
			super();			
		}

		public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {			
			Base obj = (Base)val;
			if(obj==null)return this;
			
			//if (leaf)			setIcon(getLeafIcon());
			//else if (expanded)	setIcon(getOpenIcon());
			//else				setIcon(getClosedIcon());

			if(obj.visibleFlag==0){			setIcon(redIcon);
			}else if(obj.visibleFlag==1){	setIcon(greenIcon);				
			}else{							setIcon(orangeIcon);				
			}			
			
			setText(obj.toTreeLabel());
			this.selected = selected;
			this.hasFocus = hasFocus;
			setHorizontalAlignment(LEFT);
			setOpaque(false);
			setVerticalAlignment(CENTER);
			setEnabled(true);
			super.setFont(UIManager.getFont("Tree.font"));

			if (selected){
				super.setBackground(getBackgroundSelectionColor());
				setForeground(getTextSelectionColor());

				if (hasFocus)	setBorderSelectionColor(UIManager.getLookAndFeelDefaults().getColor("Tree.selectionBorderColor"));
				else			setBorderSelectionColor(null);
			}else{
				super.setBackground(getBackgroundNonSelectionColor());
				setForeground(getTextNonSelectionColor());
				setBorderSelectionColor(null);
			}					
			
			return this;
		}		
	} 
	

	private MouseAdapter FrogTreeMouseAdapter(){
		MouseAdapter ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1 && e.getClickCount() >= 1) {
					if(e.getButton() == MouseEvent.BUTTON1){			     					            	 
						int IconMin = 0+(selPath.getPathCount()-1)*20; 
						int IconMax =10+(selPath.getPathCount()-1)*20;
						if(e.getX()>IconMin && e.getX()<IconMax){		     				
							jfrog.object.Base obj = (jfrog.object.Base)selPath.getLastPathComponent();
							if(obj.isVisible()==0){
								obj.setVisible((byte)1);
							}else{
								obj.setVisible((byte)0);							
							}
							//if(obj.getStyle()!=null && obj.getStyle().color[3]<1)Style.transparentObjects = null; //reset the list of transparent object
							Style.transparentObjects = null;
							
							tree.setVisible(false);
							tree.setVisible(true);
							
						}
					}else{
						tree.addSelectionPath(selPath);
						FrogTreePopUpMenu popup = new FrogTreePopUpMenu();
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		};
		return ml;
	}
	
	
	class FrogTreePopUpMenu extends JPopupMenu{ 
		private static final long serialVersionUID = -5156034083090112171L;
		JMenuItem menuItem; 
	    public FrogTreePopUpMenu(){ 
	    	menuItem = new JMenuItem("Set Style"); 
	        add(menuItem); 
			menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				//JColorChooser c= new JColorChooser();
				//c.setPreviewPanel(null);
				//c.showDialog(null, "test", Color.red);
				TreePath [] selection = tree.getSelectionPaths();
				for(int i=0;i<selection.length;i++){
					jfrog.object.Base obj = (jfrog.object.Base)selection[i].getLastPathComponent();
					System.out.printf("Style = %s\n",obj.getStyle());
					
				}
			}});
			
	    	menuItem = new JMenuItem("Change to Red"); 
	        add(menuItem); 
			menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				TreePath [] selection = tree.getSelectionPaths();
				for(int i=0;i<selection.length;i++){
					jfrog.object.Base obj = (jfrog.object.Base)selection[i].getLastPathComponent();
					Style tmpStyle = obj.getStyle();
					if(tmpStyle==null){
						tmpStyle = new Style();
					}else{
						tmpStyle = (Style)tmpStyle.clone();
					}
					tmpStyle.color = new float[]{1,0,0,1};
					obj.setStyle(tmpStyle, true, false);					
				}
			}});			

			
	    	menuItem = new JMenuItem("Print PickingId"); 
	        add(menuItem); 
			menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				TreePath [] selection = tree.getSelectionPaths();
				for(int i=0;i<selection.length;i++){
					jfrog.object.Base obj = (jfrog.object.Base)selection[i].getLastPathComponent();
					System.out.printf("Id %d = %d %d %d\n", obj.getPickingId(), ((obj.getPickingId()&0xFF0000)>>16), ((obj.getPickingId()&0x00FF00)>>8), (obj.getPickingId()&0x0000FF));					
				}
			}});			
			
	    	menuItem = new JMenuItem("Print ExpandedFlag"); 
	        add(menuItem); 
			menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				TreePath [] selection = tree.getSelectionPaths();
				for(int i=0;i<selection.length;i++){
					jfrog.object.Base obj = (jfrog.object.Base)selection[i].getLastPathComponent();
					System.out.printf("ExpandFlag %b\n", obj.expandedFlag);
					for(int d=0;d<obj.getDaughtersSize();d++){
						System.out.printf("   ExpandFlag %b\n", obj.getDaughter(d).expandedFlag);
					}
				}
			}});				
						

			menuItem = new JMenuItem("Export this Item"); 
			add(menuItem); 
			menuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				JFileChooser c = new JFileChooser();		
				c.setFileFilter(jfrog.Common.menuBar.new FrogFileFilter());
				int rVal = c.showSaveDialog(null); 			  			  
				if (rVal == JFileChooser.APPROVE_OPTION) {

					TreePath [] selection = tree.getSelectionPaths();
					if(selection.length==0)return;

					jfrog.object.Base root = null;
					if(selection.length>1){
						root = new jfrog.object.Base();			      
						for(int i=0;i<selection.length;i++){
							jfrog.object.Base obj = (jfrog.object.Base)selection[i].getLastPathComponent();
							root.addDaughter(obj);
						}
					}else{
						root = (jfrog.object.Base)selection[0].getLastPathComponent();
					}			    	  

					try {
						root.write(c.getSelectedFile().toURI().toURL());
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}});			
	        
	    }
	} 
	
	
	public class FrogTreeModel implements TreeModel{
	    private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
	    private Base rootBase; 
		
	    public FrogTreeModel(Base root){
	    	rootBase = root;
	    }
	    
		@Override
		public void addTreeModelListener(TreeModelListener arg0) {
			treeModelListeners.add(arg0); 			
		}

		@Override
		public Object getChild(Object parent, int arg1) {
			return ((Base)parent).getDaughter(arg1);
		}

		@Override
		public int getChildCount(Object arg0) {
			return ((Base)arg0).getDaughtersSize();
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			Base p = (Base)parent;
			Base c = (Base)child;
			for(int i=0;i<p.getDaughtersSize();i++){
				if(p.getDaughter(i).equals(c))return i;
			}			
			return -1;
		}

		@Override
		public Object getRoot() {
			return rootBase;
		}

		@Override
		public boolean isLeaf(Object arg0) {
			return ((Base)arg0).getDaughtersSize()==0;
		}

		@Override
		public void removeTreeModelListener(TreeModelListener arg0) {
			treeModelListeners.remove(arg0);			
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
	        System.out.println("*** valueForPathChanged : " + path + " --> " + ((Base)newValue).toString() ); 						
		}
		

		public Base[] getPathToRoot(Base node){
			return getPathToRoot(node, 0);
		}

		protected Base[] getPathToRoot(Base node, int depth)
		{
			if (node == null){
				if (depth == 0)
					return null;
				return new Base[depth];
			}

			Base[] path = getPathToRoot(node.mother, depth + 1);
			path[path.length - depth - 1] = node;
			return path;
		}


		    
		
		public void reload(Base node){
			if(node==null)node=rootBase;

			// Need to duplicate the code because the root can formally be
			// no an instance of the TreeNode.
			int n = getChildCount(node);
			int[] childIdx = new int[n];
			Object[] children = new Object[n];

			for (int i = 0; i < n; i++)
			{
				childIdx[i] = i;
				children[i] = getChild(node, i);
			}

			fireTreeStructureChanged(this, new Object[] { node }, childIdx, children);
		}

		 public void nodeChanged(Base node) {
			 Base parent = node.mother;
			 int[] childIndices = new int[1];
			 childIndices[0] = getIndexOfChild(parent, node);
			 Object[] children = new Object[1];
			 children[0] = node;
			 fireTreeNodesChanged(this, getPathToRoot(node), childIndices, children);
		 }
		
		

		protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children){
			TreeModelEvent event = new TreeModelEvent(source, path, childIndices,children);

			for (int i = treeModelListeners.size() - 1; i >= 0; --i)
				treeModelListeners.get(i).treeStructureChanged(event);
		}
		
		protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children){
			TreeModelEvent event = new TreeModelEvent(source, path, childIndices,children);

			for (int i = treeModelListeners.size() - 1; i >= 0; --i)
				treeModelListeners.get(i).treeNodesChanged(event);
		}
		
		
		
	}
	
	public class FrogTreeExpansionListener implements TreeExpansionListener{
		public void treeCollapsed(TreeExpansionEvent arg0) {
			((Base)arg0.getPath().getLastPathComponent()).expandedFlag = false;
		}

		public void treeExpanded(TreeExpansionEvent arg0) {
			((Base)arg0.getPath().getLastPathComponent()).expandedFlag = true;
			//System.out.printf("%s --> expanded\n", ((Base)arg0.getPath().getLastPathComponent()).toString());
		}		
	}
	
	
	public class FrogTreeModelListener implements TreeModelListener{

		@Override
		public void treeNodesChanged(TreeModelEvent arg0) {
			//System.out.println("New treeNodesChanged");
		}

		@Override
		public void treeNodesInserted(TreeModelEvent arg0) {
			//System.out.println("New treeNodesInserted");
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent arg0) {
			//System.out.println("New treeNodesRemoved");
		}

		@Override
		public void treeStructureChanged(TreeModelEvent arg0) {
			//System.out.println("New treeStructureChanged"); 			
		}
		
	}
	
	
		
}
