/*
 * Copyright (c) 2002-2007, CWI and INRIA
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package aterm.test;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermFactory;

public class VisitorBenchmark {
    static int id = 0;

    static ATermFactory factory = new aterm.pure.PureFactory();

    static AFun fun;

    public final static void main(String[] args) {
        try {
            int depth = 5;
            int fanout = 5;

            long beforeBuild = System.currentTimeMillis();
            fun = factory.makeAFun("f", fanout, false);
            ATerm term = buildTerm(depth, fanout);
            long beforeVisit = System.currentTimeMillis();
            NodeCounter nodeCounter = new NodeCounter();
            jjtraveler.Visitor topDownNodeCounter = new jjtraveler.TopDown(
                    nodeCounter);
            try {
                topDownNodeCounter.visit(term);
                long end = System.currentTimeMillis();
                System.out.println("Term of depth " + depth + " with fanout "
                        + fanout + " (" + nodeCounter.getCount() + " nodes)"
                        + ": build=" + (beforeVisit - beforeBuild) + ", visit="
                        + (end - beforeVisit));
                // System.out.println("term = " + term);
            } catch (jjtraveler.VisitFailure e) {
                System.err.println("WARING: VisitFailure: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.err.println("usage: java VisitorBenchmark <depth> <fanout>");
        }
    }

    private static ATerm buildTerm(int depth, int fanout) {
        if (depth == 1) {
            return factory.makeInt(id++);
        }
        ATerm[] args = new ATerm[fanout];
        ATerm arg = buildTerm(depth - 1, fanout);
        for (int i = 0; i < fanout; i++) {
            args[i] = arg;
        }
        return factory.makeAppl(fun, args);
    }
}

class NodeCounter implements jjtraveler.Visitor {
    private int count;

    public jjtraveler.Visitable visit(jjtraveler.Visitable visitable) {
        count++;
        return visitable;
    }

    public int getCount() {
        return count;
    }
}
