package jjtraveler;

public interface StateVisitor extends Visitor
{

	public Object getState();

	public void setState(Object state);

}
