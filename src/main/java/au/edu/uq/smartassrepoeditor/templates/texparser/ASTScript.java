/* Generated By:JJTree: Do not edit this line. ASTScript.java */

package au.edu.uq.smartassrepoeditor.templates.texparser;

import java.util.Set;

import au.edu.uq.smartassrepoeditor.script.Script;
import au.edu.uq.smartassrepoeditor.script.SimpleScript;

public class ASTScript extends SimpleNode {
	
	public ASTScript(int id) {
		super(id);
	}

	public ASTScript(TexParser p, int id) {
		super(p, id);
	}
	
	@Override
	public ResultNode execute(Set<String> sections, Script script) {
		//TODO: a possibility of different script modules selection 
		RAnyText r = new RAnyText(this);
		r.setText(script.executeBlock(text));
		return r;
	};

	public String getCode() {
		return "#<" + text + "#>";
	}
}