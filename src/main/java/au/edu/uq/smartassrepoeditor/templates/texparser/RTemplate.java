package au.edu.uq.smartassrepoeditor.templates.texparser;

public class RTemplate extends RComplexNode {
	RAnyText header;
	ResultNode doc;

	public RTemplate(SimpleNode master_copy, RAnyText header, RComplexNode doc) {
		super(master_copy);
		addChild(this.header = header);
		addChild(this.doc = doc);
	}

	@Override
	public String getSection(String section) {
		return header.getSection(section) + "\\begin{document}" +
			doc.getSection(section) +
			"\\end{document}";
	}
	
	public ResultNode getDocument() {
		return doc;
	}

	public String wrapWithHeaders(String tex) {
		return header.getText() + "\\begin{document}\n" + tex + "\n\\end{document}";
	}
	
	@Override
	/**
	 * Root node - nothing to replace
	 */
	public void replaceSelf(ResultNode new_node) {
	}
	
	@Override
	public void setChild(int pos, ResultNode node) {
		super.setChild(pos, node);
		if(pos==1)
			doc = (ResultNode) node;
	}
	
}
