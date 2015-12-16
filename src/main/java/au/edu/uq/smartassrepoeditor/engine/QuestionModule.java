package au.edu.uq.smartassrepoeditor.engine;

/**
 * QuestionModule interface.
 */
public interface QuestionModule {

    /**
     * Lookup section details based on section name.
     * Each QuestionModule will only support specific sections.
     *
     *
     */
    public String getSection(final String section);


    /**
     * Initialise a question module with a list of parameters.
     * Each QuesitonModule will have it's own requirements as to what parameter strings are required.
     *
     * @param params Initalisation parameters.
     * @return This QuestionModule after initialization.
     */
    default public QuestionModule initialise(final String[] params) { return this; };

}
