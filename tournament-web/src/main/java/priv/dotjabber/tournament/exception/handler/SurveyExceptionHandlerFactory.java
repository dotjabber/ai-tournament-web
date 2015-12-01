package priv.dotjabber.tournament.exception.handler;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class SurveyExceptionHandlerFactory extends ExceptionHandlerFactory {
    private final ExceptionHandlerFactory parent;

    public SurveyExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new SurveyExceptionHandler(parent.getExceptionHandler());
    }
}
