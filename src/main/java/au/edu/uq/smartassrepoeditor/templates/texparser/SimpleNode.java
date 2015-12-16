/* Generated By:JJTree: Do not edit this line. SimpleNode.java */

package au.edu.uq.smartassrepoeditor.templates.texparser;

import java.io.Serializable;
import java.util.Set;

import au.edu.uq.smartassrepoeditor.script.Script;

public class SimpleNode implements Node, Cloneable, Serializable {
	protected Node parent;
	protected Node[] children;
	protected int id;
	protected TexParser parser;

	protected String text = "";
	protected boolean is_executed = false;
	
	public void setText(String new_text) {
		text = new_text;
	}

	protected String getText() {
		return text;
	}
	
	public String getCode() {
		String s = getText();
		if(children!=null)
			for(int i=0; i<children.length;i++) {
				SimpleNode n = (SimpleNode)children[i];
				if(n!=null)
					s = s + n.getCode();
			}
		return s;
	}
	
	public void replaceSelf(SimpleNode new_node) {
		for(int i=0;i<parent.jjtGetNumChildren();i++) 
			if(parent.jjtGetChild(i)==this) {
				((SimpleNode)parent).children[i] = new_node;
				parent = null;
				//new_node.jjtSetParent(parent);
				break;
			}
	}
	
	public void insertNode(SimpleNode node, int pos) {
		if(children==null || pos>=children.length)
			jjtAddChild(node, pos);
		else {
			Node c[] = new Node[children.length + 1];
			if(pos>0)
				System.arraycopy(children, 0, c, 0, pos);
			c[pos] = node;
			System.arraycopy(children, pos, c, pos+1, children.length-pos);
			children = c;
			//node.jjtSetParent(this);
		}
	}
	
	public void removeNode(int pos)  {
		if(pos>=0 && pos<children.length) {
			//if(children[pos]!=null)
			//	children[pos].jjtSetParent(null);
			if(children.length==1)
				children = null;
			else {
				Node c[] = new Node[children.length - 1];
				if(pos>0)
					System.arraycopy(children, 0, c, 0, pos);
				System.arraycopy(children, pos+1, c, pos, children.length-pos-1);
				children = c;
			}
		}
	}
	
	public void removeNode(SimpleNode node) {
		if(children==null)
			return;
		for(int i=children.length-1;i>=0;i--)
			if(children[i]==node) 
				removeNode(i);
	}
	
	public void removeSelf() {
		if(parent!=null)
			((SimpleNode)parent).removeNode(this);
	}
	
	public ResultNode execute(Set<String> sections, Script script) {
		is_executed = true;
		return null;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		//clone() is allowed only for node that have not been executed 
		if(is_executed)
			throw new CloneNotSupportedException("Can not clone node after execute()!");
		SimpleNode n = (SimpleNode) super.clone();
		n.setText(text);
		if(children!=null) {
			n.children = new SimpleNode[children.length];
			for(int i=0;i<children.length;i++)
				if(children[i]!=null)
					n.jjtAddChild((SimpleNode)((SimpleNode)children[i]).clone(), i);
		}
		return n;
	}
	
	public SimpleNode(int i) {
		id = i;
	}

	public SimpleNode(TexParser p, int i) {
		this(i);
		parser = p;
	}

	public void jjtOpen() {
	}

	public void jjtClose() {
	}

	public void jjtSetParent(Node n) { parent = n; }
	public Node jjtGetParent() { return parent; }

	public void jjtAddChild(Node n, int i) {
		if(n instanceof ASTAnyText && ((ASTAnyText)n).getText().length()==0) //empty text node
			return;

		if (children == null) {
			children = new Node[i + 1];
		} else if (i >= children.length) {
			Node c[] = new Node[i + 1];
			System.arraycopy(children, 0, c, 0, children.length);
			children = c;
		}
		children[i] = n;
		//if(n instanceof SimpleNode)
		//	n.jjtSetParent(this);
	}

	public Node jjtGetChild(int i) {
		return children[i];
	}

	public int jjtGetNumChildren() {
		return (children == null) ? 0 : children.length;
	}

	/* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

	public String toString() { return TexParserTreeConstants.jjtNodeName[id]; }
	public String toString(String prefix) { return prefix + toString() + " >>> " + text; }

	/* Override this method if you want to customize how the node dumps
     out its children. */

	public void dump(String prefix) {
		System.out.println(toString(prefix));
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode)children[i];
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}
}

