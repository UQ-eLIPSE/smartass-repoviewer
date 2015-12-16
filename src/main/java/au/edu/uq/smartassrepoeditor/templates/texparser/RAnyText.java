package au.edu.uq.smartassrepoeditor.templates.texparser;

public class RAnyText extends ResultNode {
	String text;

	public RAnyText(SimpleNode master_copy) {
		super(master_copy);
	}
	
	@Override
	public String getSection(String section) {
		return text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

}
