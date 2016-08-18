// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC.
// <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms
// of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under
// the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import aterm.ATermAppl;

import com.clarkparsia.pellet.datatypes.DatatypeReasoner;
import com.clarkparsia.pellet.datatypes.OWLRealUtils;
import com.clarkparsia.pellet.datatypes.exceptions.DatatypeReasonerException;
import com.clarkparsia.pellet.datatypes.exceptions.InvalidLiteralException;
import com.clarkparsia.pellet.datatypes.exceptions.UnrecognizedDatatypeException;

/**
 * @author Evren Sirin
 */
public class Literal extends Node {
    private ATermAppl	atermValue;

    private Object		value;

    // private Datatype datatype;

    private boolean		hasValue;

    private NodeMerge	merge;

    private boolean clashed = false;

    public Literal(ATermAppl name, ATermAppl term, ABox abox, DependencySet ds) {

        super( name, abox );

        if( term != null ) {
            hasValue = !term.getArgument( ATermUtils.LIT_URI_INDEX )
                    .equals( ATermUtils.NO_DATATYPE );
            if( hasValue ) {
                try {
                    value = abox.dtReasoner.getValue( term );
                } catch( InvalidLiteralException e ) {
                    final String msg = format(
                            "Attempt to create literal from invalid literal (%s): %s", term, e
                                    .getMessage() );
                    if( PelletOptions.INVALID_LITERAL_AS_INCONSISTENCY ) {
                        log.fine( msg );
                        value = null;
                    }
                    else {
                        log.severe( msg );
                        throw new InternalReasonerException( msg, e );
                    }
                } catch( UnrecognizedDatatypeException e ) {
                    final String msg = format(
                            "Attempt to create literal from with unrecognized datatype (%s): %s",
                            term, e.getMessage() );
                    log.severe( msg );
                    throw new InternalReasonerException( msg, e );
                }
                if( value == null ) {
                    depends.put( name, ds );
                }
            }

            atermValue = ATermUtils.makeValue( term );
        }
        else {
            hasValue = false;
        }
    }

    public Literal(Literal literal, ABox abox) {
        super( literal, abox );

        atermValue = literal.atermValue;
        value = literal.value;
        hasValue = literal.hasValue;
    }

    @Override
    public DependencySet getNodeDepends() {
        return getDepends( ATermUtils.TOP_LIT );
    }

    @Override
    public Node copyTo(ABox abox) {
        return new Literal( this, abox );
    }

    @Override
    final public boolean isLeaf() {
        return true;
    }

    @Override
    public int getNominalLevel() {
        return isNominal()
            ? NOMINAL
            : BLOCKABLE;
    }

    @Override
    public boolean isNominal() {
        return (value != null);
    }

    @Override
    public boolean isBlockable() {
        return (value == null);
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    public boolean isDifferent(Node node) {
        if( super.isDifferent( node ) ) {
            return true;
        }

        Literal literal = (Literal) node;
        if( hasValue && literal.hasValue ) {
            final Class<? extends Object> valueClass = value.getClass();
            final Class<? extends Object> otherValueClass = literal.value.getClass();
            // XXX due to simplification of numeric types in pellet a special case is needed to compare values of numeric literals
            if (isAcceptableNumber( valueClass ) && isAcceptableNumber( otherValueClass )) {
                return OWLRealUtils.compare( (Number) value, (Number) literal.value ) != 0;
            } else {
                return valueClass.equals( otherValueClass )
                        && !value.equals( literal.value );
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private static boolean isAcceptableNumber(Class<? extends Object> cls) {
        return OWLRealUtils.acceptable( (Class<? extends Number>) cls );
    }

    @Override
    public boolean hasType(ATerm type) {
        if( type instanceof ATermAppl ) {
            final ATermAppl a = (ATermAppl) type;
            if( ATermUtils.isNominal( a ) ) {
                try {
                    final ATermAppl input = (ATermAppl) a.getArgument( 0 );
                    final ATermAppl canonical = abox.getDatatypeReasoner()
                            .getCanonicalRepresentation( input );
                    if( !canonical.equals( input ) ) {
                        type = ATermUtils.makeValue( canonical );
                    }
                } catch( InvalidLiteralException e ) {
                    log
                            .warning( format(
                                    "hasType called with nominal using invalid literal ('%s'), returning false",
                                    e.getMessage() ) );
                    return false;
                } catch( UnrecognizedDatatypeException e ) {
                    log
                            .warning( format(
                                    "hasType called with nominal using literal with unrecognized datatype ('%s'), returning false",
                                    e.getMessage() ) );
                    return false;
                }
            }
        }

        if( super.hasType( type ) ) {
            return true;
        }
        else if( hasValue ) {
            if( atermValue.equals( type ) ) {
                return true;
            }

            // Datatype datatype = abox.getDatatypeReasoner().getDatatype(
            // (ATermAppl) type );
            // if( datatype.contains( value ) )
            // return true;
        }

        return false;
    }

    @Override
    public DependencySet getDifferenceDependency(Node node) {
        DependencySet ds = null;
        if( isDifferent( node ) ) {
            ds = differents.get( node );
            if( ds == null ) {
                ds = DependencySet.INDEPENDENT;
            }
        }

        return ds;
    }

    @Override
    public void addType(ATermAppl c, DependencySet d) {
        if( hasType( c ) ) {
            return;
        }

        /*
         * A negated nominal is turned into a different
         */
        if( ATermUtils.isNot( c ) ) {
            final ATermAppl arg = (ATermAppl) c.getArgument( 0 );
            if( ATermUtils.isNominal( arg ) ) {
                final ATermAppl v = (ATermAppl) arg.getArgument( 0 );
                Literal other = abox.getLiteral( v );
                if( other == null ) {
                    other = abox.addLiteral( v, d );
                }
                super.setDifferent( other, d );
                return;
            }
        }
        super.addType( c, d );

        // TODO when two literals are being merged this is not efficient
        // if(abox.isInitialized())
        checkClash();
    }

    public void addAllTypes(Map<ATermAppl, DependencySet> types, DependencySet ds) {
        for( Entry<ATermAppl, DependencySet> entry : types.entrySet() ) {
            ATermAppl c = entry.getKey();

            if( hasType( c ) ) {
                continue;
            }

            DependencySet depends = entry.getValue();

            super.addType( c, depends.union( ds, abox.doExplanation() ) );
        }

        checkClash();
    }

    @Override
    public boolean hasSuccessor(Node x) {
        return false;
    }

    @Override
    public final Literal getSame() {
        return (Literal) super.getSame();
    }

    @Override
    public ATermAppl getTerm() {
        return hasValue
            ? (ATermAppl) atermValue.getArgument( 0 )
            : null;
    }

    public String getLang() {
        return hasValue
            ? ((ATermAppl) ((ATermAppl) atermValue.getArgument( 0 ))
                    .getArgument( ATermUtils.LIT_LANG_INDEX )).getName()
            : "";
    }

    public String getLexicalValue() {
        if( hasValue ) {
            return value.toString();
        }

        return null;
    }

    void reportClash(Clash clash) {
        clashed = true;
        abox.setClash( clash );
    }

    private void checkClash() {
        clashed = false;

        if( hasValue && value == null ) {
            reportClash( Clash.invalidLiteral( this, getDepends( name ), getTerm() ) );
            return;
        }

        if( hasType( ATermUtils.BOTTOM_LIT ) ) {
            reportClash( Clash.emptyDatatype( this, getDepends( ATermUtils.BOTTOM_LIT ) ) );
            if( abox.doExplanation() ) {
                System.out.println( "1) Literal clash dependency = " + abox.getClash() );
            }
            return;
        }

        final Set<ATermAppl> types = getTypes();
        final DatatypeReasoner dtReasoner = abox.getDatatypeReasoner();

        try {
            if( hasValue ) {

                if( !dtReasoner.isSatisfiable( types, value ) ) {
                    ArrayList<ATermAppl> primitives = new ArrayList<ATermAppl>();
                    for( ATermAppl t : types ) {
                        if( ATermUtils.TOP_LIT.equals( t ) ) {
                            continue;
                        }
                        else {
                            primitives.add( t );
                        }
                    }

                    final ATermAppl dt[] = primitives
                            .toArray( new ATermAppl[primitives.size() - 1] );

                    DependencySet ds = DependencySet.EMPTY;
                    for( int i = 0; i < dt.length; i++ ) {
                        ds = ds.union( getDepends( dt[i] ), abox.doExplanation() );
                        if (abox.doExplanation()) {
                            ATermAppl dtName = ATermUtils.isNot(dt[i]) ? (ATermAppl) dt[i].getArgument(0) : dt[i];
                            ATermAppl definition = dtReasoner.getDefinition(dtName);
                            if (definition != null) {
                                ds = ds.union(Collections.singleton(ATermUtils.makeDatatypeDefinition(dtName, definition)), true);
                            }
                        }
                    }

                    reportClash( Clash.valueDatatype( this, ds, (ATermAppl) atermValue.getArgument(0), dt[0] ) );
                }
            }
            else {
                if( dtReasoner.isSatisfiable( types ) ) {
                    if ( !dtReasoner.containsAtLeast( 2, types ) ) {
                        /*
                         * This literal is a variable, but given current ranges can only
                         * take on a single value.  Merge with that value.
                         */
                        final Object value = dtReasoner.valueIterator( types ).next();
                        final ATermAppl valueTerm = dtReasoner.getLiteral( value );
                        Literal valueLiteral = abox.getLiteral( valueTerm );
                        if (valueLiteral == null) {
                            /*
                             * No dependency set is used here because omitting it prevents the
                             * constant literal from being removed during backtrack
                             */
                            valueLiteral = abox.addLiteral( valueTerm );
                        }
                        DependencySet mergeDs = DependencySet.INDEPENDENT;
                        for ( DependencySet ds : depends.values() ) {
                            mergeDs = mergeDs.union( ds, abox.doExplanation() );
                        }
                        merge = new NodeMerge( this, valueLiteral, mergeDs );
                    }
                } else {
                    ArrayList<ATermAppl> primitives = new ArrayList<ATermAppl>();
                    for( ATermAppl t : types ) {
                        if( ATermUtils.TOP_LIT.equals( t ) ) {
                            continue;
                        }
                        else {
                            primitives.add( t );
                        }
                    }

                    final ATermAppl dt[] = primitives
                            .toArray( new ATermAppl[primitives.size() - 1] );

                    DependencySet ds = DependencySet.EMPTY;
                    for( int i = 0; i < dt.length; i++ ) {
                        ds = ds.union( getDepends( dt[i] ), abox.doExplanation() );
                        if (abox.doExplanation()) {
                            ATermAppl definition = dtReasoner.getDefinition(dt[i]);
                            if (definition != null) {
                                ds = ds.union(Collections.singleton(ATermUtils.makeDatatypeDefinition(dt[i], definition)), true);
                            }
                        }
                    }

                    reportClash( Clash.emptyDatatype( this, ds, dt ) );
                }
            }
        } catch( DatatypeReasonerException e ) {
            final String msg = "Unexcepted datatype reasoner exception: " + e.getMessage();
            log.severe( msg );
            throw new InternalReasonerException( msg, e );
        }
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean restore(int branch) {
        Boolean restorePruned = restorePruned( branch );
        if( Boolean.FALSE.equals( restorePruned ) ) {
            return restorePruned;
        }

        boolean restored = Boolean.TRUE.equals( restorePruned );

        restored |= super.restore( branch );

        if (clashed) {
            checkClash();
        }

        return restored;
    }

    @Override
    final public void prune(DependencySet ds) {
        pruned = ds;
    }

    @Override
    public void unprune(int branch) {
        super.unprune( branch );

        checkClash();
    }

    public String debugString() {
        return name + " = " + getTypes().toString();
    }

    public NodeMerge getMergeToConstant() {
        return merge;
    }

    public void clearMergeToConstant() {
        merge = null;
    }
}
