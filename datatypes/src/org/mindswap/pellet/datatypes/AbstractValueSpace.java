
package org.mindswap.pellet.datatypes;


public abstract class AbstractValueSpace implements ValueSpace {
    private static final long serialVersionUID = -8584918754835331819L;
	public static final Integer EQ = Integer.valueOf( 0 );
    public static final Integer GT = Integer.valueOf( 1 );
    public static final Integer LT = Integer.valueOf( -1 );
    
    public static final Integer SIZE_ZERO = Integer.valueOf( 1 );
    public static final Integer SIZE_ONE  = Integer.valueOf( 1 );
    public static final Integer SIZE_INF  = Integer.valueOf( INFINITE );

    private Object minVal;
    private Object maxVal;
    private Object midVal;

    private boolean isInfinite;

    /* For serialization */
    protected AbstractValueSpace() {}
    
    public AbstractValueSpace( Object minInf, Object midVal, Object maxInf, boolean isInfinite ) {
        this.minVal = minInf;
        this.midVal = midVal;
        this.maxVal = maxInf;
        
        this.isInfinite = isInfinite;
    }
    
    public Object getMidValue() {
        return midVal;
    }

    public Object getMinValue() {
        return minVal;
    }

    public Object getMaxValue() {
        return maxVal;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public boolean isInfinite( Object value ) {
        return isInfinite && ( minVal.equals( value ) || maxVal.equals( value ) );
    }

    protected Integer compareInternal( Object o1, Object o2 ) {
        if( o1.equals( o2 ) )
            return EQ;
        if( minVal.equals( o1 ) || maxVal.equals( o2 ) )
            return LT;
        if( maxVal.equals( o1 ) || minVal.equals( o2 ) )
            return GT;
        
        return null;
    }

    protected Integer countInternal( Object o1, Object o2 ) {
        if( o1.equals( o2 ) )
            return SIZE_ONE;
        if( minVal.equals( o1 ) || maxVal.equals( o2 ) )
            return SIZE_INF;
        if( maxVal.equals( o1 ) || minVal.equals( o2 ) )
            return SIZE_ZERO;
        
        return null;
    }
}
