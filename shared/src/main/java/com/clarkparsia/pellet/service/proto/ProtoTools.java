package com.clarkparsia.pellet.service.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import com.clarkparsia.owlapiv3.ImmutableNode;
import com.clarkparsia.owlapiv3.ImmutableNodeSet;
import com.clarkparsia.owlapiv3.OWL;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParser;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtoTools {
	private static final OWLOntologyManager MANAGER = OWL.manager;
	private static final OWLDataFactory FACTORY = OWL.factory;
	private static final FunctionalSyntaxDocumentFormat FORMAT = new FunctionalSyntaxDocumentFormat();

	static {
		FORMAT.setAddMissingTypes(false);
		FORMAT.clear();
	}

	public static Messages.OwlObject toOwlObject(final OWLObject theObj) throws IOException {
		final ByteArrayOutputStream dataOutput = new ByteArrayOutputStream();

		return toOwlObject(FACTORY.getOWLDeclarationAxiom((OWLEntity) theObj));
	}

	public static Messages.OwlObject toOwlObject(final OWLAxiom theObj) throws IOException {
		OWLOntology aOnt = null;
		try {
			final ByteArrayOutputStream dataOutput = new ByteArrayOutputStream();
			aOnt = OWL.Ontology((OWLAxiom) theObj);

			MANAGER.setOntologyFormat(aOnt, FORMAT);

			aOnt.saveOntology(FORMAT, dataOutput);

			return Messages.OwlObject.newBuilder()
			                         .setBytes(ByteString.copyFrom(dataOutput.toByteArray()))
			                         .build();
		}
		catch (OWLException e) {
			throw new IOException(e);
		}
		finally {
			if (aOnt != null) {
				MANAGER.removeOntology(aOnt);
			}
		}
	}

	public static <T extends OWLEntity> T fromOwlObject(final Messages.OwlObject theRawObject) throws IOException {
		return (T) ((OWLDeclarationAxiom) fromOwlAxiom(theRawObject)).getEntity();
	}

	public static OWLAxiom fromOwlAxiom(final Messages.OwlObject theRawObject) throws IOException {
		OWLParser aParser = new OWLFunctionalSyntaxOWLParser();
		OWLOntology aOnt = OWL.Ontology();
		aParser.parse(new StringDocumentSource(theRawObject.getBytes().toStringUtf8()), aOnt, MANAGER.getOntologyLoaderConfiguration());

		return Iterables.getOnlyElement(aOnt.getAxioms());
	}

	// BinaryOWL support disabled for now since it is not owlapi 4.x compatible
	//
	//	public static final BinaryOWLVersion VERSION = BinaryOWLVersion.getVersion(1);
	//
	//	public static Messages.OwlObject toOwlObject(final OWLObject theObj) throws IOException {
	//		final ByteArrayOutputStream dataOutput = new ByteArrayOutputStream();
	//		final BinaryOWLOutputStream out = new BinaryOWLOutputStream(dataOutput, VERSION);
	//
	//		OWLObjectBinaryType.write(theObj, out);
	//		final byte[] bytes = dataOutput.toByteArray();
	//
	//		return Messages.OwlObject.newBuilder()
	//		                         .setBytes(ByteString.copyFrom(bytes))
	//		                         .build();
	//	}
	//
	//	public static <T extends OWLObject> T fromOwlObject(final Messages.OwlObject theRawObject) throws IOException {
	//		ByteArrayInputStream inputStream = new ByteArrayInputStream(theRawObject.getBytes().toByteArray());
	//		BinaryOWLInputStream owlInStream = new BinaryOWLInputStream(inputStream, OWLManager.getOWLDataFactory(), VERSION);
	//
	//		return OWLObjectBinaryType.read(owlInStream);
	//	}

	public static Messages.AxiomSet toAxiomSet(final Set<OWLAxiom> theAxiomSet) throws IOException {
		final Messages.AxiomSet.Builder aAxiomSet = Messages.AxiomSet.newBuilder();

		int i = 0;
		for (final OWLAxiom aTheAxiomSet : theAxiomSet) {
			aAxiomSet.addAxioms(i++, toOwlObject(aTheAxiomSet));
		}

		return aAxiomSet.build();
	}

	public static Set<OWLAxiom> fromAxiomSet(final Messages.AxiomSet theAxiomSet) throws IOException {
		final ImmutableSet.Builder<OWLAxiom> axioms = ImmutableSet.builder();

		for (Messages.OwlObject aRawObject : theAxiomSet.getAxiomsList()) {
			axioms.add(ProtoTools.fromOwlAxiom(aRawObject));
		}

		return axioms.build();
	}

	public static Messages.Node toNode(final Node<? extends OWLObject> theNode) throws IOException {
		final Messages.Node.Builder aNode = Messages.Node.newBuilder();

		int i = 0;
		for (OWLObject owlObject : theNode) {
			aNode.addOwlObject(i++, toOwlObject(owlObject));
		}

		return aNode.build();
	}

	public static Node<OWLObject> fromNode(final Messages.Node theNode) throws IOException {
		Set<OWLObject> theObjects = Sets.newLinkedHashSet();

		for (Messages.OwlObject aObject : theNode.getOwlObjectList()) {
			theObjects.add(ProtoTools.fromOwlObject(aObject));
		}

		return ImmutableNode.of(theObjects);
	}

	public static Messages.NodeSet toNodeSet(final NodeSet<? extends OWLObject> theNodeSet) throws IOException {
		final Messages.NodeSet.Builder aNodeSet = Messages.NodeSet.newBuilder();

		int i = 0;
		for (Node<?extends OWLObject> node : theNodeSet) {
			aNodeSet.addNodes(i++, toNode(node));
		}

		return aNodeSet.build();
	}

	public static NodeSet<OWLObject> fromNodeSet(final Messages.NodeSet theNodeSet) throws IOException {
		Set<Node<OWLObject>> aNodeSet = Sets.newLinkedHashSet();

		for (Messages.Node aNode : theNodeSet.getNodesList()) {
			aNodeSet.add(ProtoTools.<Node<OWLObject>>fromNode(aNode));
		}

		return ImmutableNodeSet.of(aNodeSet);
	}
}
