package au.edu.uq.smartassrepoeditor.templates.texparser;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import au.edu.uq.smartassrepoeditor.script.Script;

public class RComplexNode extends ResultNode {
	protected Vector<ResultNode> children;
	
	public RComplexNode(SimpleNode master_copy) {
		super(master_copy);
		children = new Vector<ResultNode>();
	}
	
	public RComplexNode(SimpleNode master_copy, String node_name) {
		this(master_copy);
		this.node_name = node_name; 
	}

	public String getSection(String section) {
		String s = "";
		for(Iterator<ResultNode> it=children.iterator();it.hasNext();)
			s = s + it.next().getSection(section);
		return s;
	}
	
	public Iterator<ResultNode> iterateResultNodes() {
		return children.iterator();
	}
	
	public void addChild(ResultNode node) {
		children.add(node);
		node.parent = this;
	}
	
	public ResultNode getChild(int pos) {
		return children.get(pos);
	}
	
	public void setChild(int pos, ResultNode node) {
		children.set(pos, node);
	}

	public int getChildrenCount() {
		return children.size();
	}
}
