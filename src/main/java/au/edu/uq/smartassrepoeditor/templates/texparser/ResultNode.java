package au.edu.uq.smartassrepoeditor.templates.texparser;

public class ResultNode {
	protected String node_name;
	protected SimpleNode master_copy;
	protected RComplexNode parent;

	public ResultNode(SimpleNode master_copy) {
		this.master_copy = master_copy;
		if(master_copy!=null)
			node_name = master_copy.toString();
	}
	
	public String getSection(String section) {
		return "";
	}
	
	public SimpleNode getMasterCopy() {
		return master_copy;
	}
	
	public String toString() {
		return node_name;
	}

	public void replaceSelf(ResultNode new_node) {
		int pos = parent.children.indexOf(this);
		if(pos>=0) {
			parent.setChild(pos, new_node);
			new_node.parent = parent;
		}
	}

}
