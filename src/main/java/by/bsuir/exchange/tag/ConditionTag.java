package by.bsuir.exchange.tag;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ConditionTag extends TagSupport {

    private boolean condition;
    private String attribute;

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            if (condition){
                pageContext.getOut().write(attribute);
            }
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
