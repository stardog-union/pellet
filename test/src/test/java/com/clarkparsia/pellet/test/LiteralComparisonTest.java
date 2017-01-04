package com.clarkparsia.pellet.test;

import org.junit.Assert;
import org.junit.Test;
import org.mindswap.pellet.ABox;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Literal;

import com.clarkparsia.pellet.utils.TermFactory;

public class LiteralComparisonTest {

    @Test
    public void numericLiteralComparison() {
        final KnowledgeBase kb = new KnowledgeBase();
        ABox abox = new ABox( kb );
        Literal byteLiteral = abox.addLiteral( TermFactory.literal( (byte) 0 ) );
        Literal shortLiteral = abox.addLiteral( TermFactory.literal( (short ) 200) );
        Assert.assertTrue( "numeric literals should be different", byteLiteral.isDifferent( shortLiteral ) );
    }

}
