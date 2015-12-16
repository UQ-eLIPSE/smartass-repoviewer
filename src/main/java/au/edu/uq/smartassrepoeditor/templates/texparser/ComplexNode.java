package au.edu.uq.smartassrepoeditor.templates.texparser;

import java.util.Set;
import java.util.Vector;

import au.edu.uq.smartassrepoeditor.script.Script;

public class ComplexNode extends SimpleNode {

	public ComplexNode(int i) {
		super(i);
	}

	public ComplexNode(TexParser p, int i) {
		super(p, i);
	}
	
	@Override
	public RComplexNode execute(Set<String> sections, Script script) {
		super.execute(sections, script);
		return executeChildren(new RComplexNode(this), sections, script);
	}

	protected RComplexNode executeChildren(RComplexNode result, Set<String> sections, Script script) {
		if(children!=null)
			for(int i=0;i<children.length;i++) 
				if(children[i]!=null) {
					SimpleNode n = (SimpleNode)children[i];
					result.addChild(n.execute(sections, script));
				}
		return result;
	}
	
}
