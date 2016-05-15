package jjtraveler;

public interface StateVisitor<T extends Visitable> extends Visitor<T>
{
	public Object getState();

	public void setState(Object state);
}
