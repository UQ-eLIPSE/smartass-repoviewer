package au.edu.uq.smartassrepoeditor.templates.texparser;

public class RSection extends RComplexNode {
	public RSection(SimpleNode master_copy) {
		super(master_copy);
	}

	@Override
	public String getSection(String section) {
		if(section.equals(((ASTSection)master_copy).getName()))
			return super.getSection(section);
		return "";
	}
	
}
