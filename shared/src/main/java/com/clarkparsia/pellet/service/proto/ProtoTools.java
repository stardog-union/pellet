package com.clarkparsia.pellet.service.proto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import com.clarkparsia.pellet.service.io.SerializableNode;
import com.clarkparsia.pellet.service.io.SerializableNodeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import org.semanticweb.binaryowl.BinaryOWLVersion;
import org.semanticweb.binaryowl.owlobject.OWLObjectBinaryType;
import org.semanticweb.binaryowl.stream.BinaryOWLInputStream;
import org.semanticweb.binaryowl.stream.BinaryOWLOutputStream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtoTools {

	public static final BinaryOWLVersion VERSION = BinaryOWLVersion.getVersion(1);

	public static Messages.OwlObject toOwlObject(final OWLObject theObj) throws IOException {
		final ByteArrayOutputStream dataOutput = new ByteArrayOutputStream();
		final BinaryOWLOutputStream out = new BinaryOWLOutputStream(dataOutput, VERSION);

		OWLObjectBinaryType.write(theObj, out);
		final byte[] bytes = dataOutput.toByteArray();

		return Messages.OwlObject.newBuilder()
		                         .setBytes(ByteString.copyFrom(bytes))
		                         .build();
	}

	public static <T extends OWLObject> T fromOwlObject(final Messages.OwlObject theRawObject) throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(theRawObject.getBytes().toByteArray());
		BinaryOWLInputStream owlInStream = new BinaryOWLInputStream(inputStream, OWLManager.getOWLDataFactory(), VERSION);

		return OWLObjectBinaryType.read(owlInStream);
	}

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
			axioms.add(ProtoTools.<OWLAxiom>fromOwlObject(aRawObject));
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

		return new SerializableNode<OWLObject>(theObjects);
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

		return SerializableNodeSet.create(aNodeSet);
	}
}
