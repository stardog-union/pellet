package com.clarkparsia.pellet.service.proto;

import java.io.Serializable;
import java.util.Set;

import com.clarkparsia.pellet.service.io.SerializableNode;
import com.clarkparsia.pellet.service.io.SerializableNodeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.SerializationUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtoTools {

	public static Messages.RawObject toRawObject(final Serializable theObj) {
		final byte[] objBytes = SerializationUtils.serialize(theObj);
		return Messages.RawObject.newBuilder()
		                         .setKlass(theObj.getClass().getName())
		                         .setBytes(ByteString.copyFrom(objBytes))
		                         .build();
	}

	public static <T> T fromRawObject(final Messages.RawObject theRawObject) throws
	                                                                         ClassNotFoundException {
		final String klass = theRawObject.getKlass();
		final byte[] objBytes = theRawObject.getBytes().toByteArray();
		final Class<T> aAxiomClass = (Class<T>) Class.forName(klass);

		return aAxiomClass.cast(SerializationUtils.deserialize(objBytes));
	}

	public static Messages.AxiomSet toAxiomSet(final Set<OWLAxiom> theAxiomSet) {
		final Messages.AxiomSet.Builder aAxiomSet = Messages.AxiomSet.newBuilder();

		int i = 0;
		for (final OWLAxiom aTheAxiomSet : theAxiomSet) {
			aAxiomSet.addAxioms(i++, toRawObject(aTheAxiomSet));
		}

		return aAxiomSet.build();
	}

	public static Set<OWLAxiom> fromAxiomSet(final Messages.AxiomSet theAxiomSet) throws ClassNotFoundException {
		final ImmutableSet.Builder<OWLAxiom> axioms = ImmutableSet.builder();

		for (Messages.RawObject aRawObject : theAxiomSet.getAxiomsList()) {
			axioms.add(ProtoTools.<OWLAxiom>fromRawObject(aRawObject));
		}

		return axioms.build();
	}

	public static Messages.Node toNode(final Node<? extends OWLObject> theNode) {
		final Messages.Node.Builder aNode = Messages.Node.newBuilder();

		int i = 0;
		for (OWLObject owlObject : theNode) {
			aNode.addOwlObject(i++, toRawObject(owlObject));
		}

		return aNode.build();
	}

	public static Node<OWLObject> fromNode(final Messages.Node theNode) throws ClassNotFoundException {
		Set<OWLObject> theObjects = Sets.newLinkedHashSet();

		for (Messages.RawObject aObject : theNode.getOwlObjectList()) {
			theObjects.add(ProtoTools.<OWLObject>fromRawObject(aObject));
		}

		return new SerializableNode<OWLObject>(theObjects);
	}

	public static Messages.NodeSet toNodeSet(final NodeSet<? extends OWLObject> theNodeSet) {
		final Messages.NodeSet.Builder aNodeSet = Messages.NodeSet.newBuilder();

		int i = 0;
		for (Node<?extends OWLObject> node : theNodeSet) {
			aNodeSet.addNodes(i++, toNode(node));
		}

		return aNodeSet.build();
	}

	public static NodeSet<OWLObject> fromNodeSet(final Messages.NodeSet theNodeSet) throws ClassNotFoundException {
		Set<Node<OWLObject>> aNodeSet = Sets.newLinkedHashSet();

		for (Messages.Node aNode : theNodeSet.getNodesList()) {
			aNodeSet.add(ProtoTools.<Node<OWLObject>>fromNode(aNode));
		}

		return SerializableNodeSet.create(aNodeSet);
	}
}
