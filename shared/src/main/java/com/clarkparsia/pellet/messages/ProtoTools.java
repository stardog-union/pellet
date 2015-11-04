package com.clarkparsia.pellet.messages;

import java.io.Serializable;
import java.util.Set;

import com.clarkparsia.pellet.Messages;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.SerializationUtils;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtoTools {

	public static Messages.RawObject toRawObject(final Serializable theObj) {
		final byte[] objBytes = SerializationUtils.serialize(theObj);
		return Messages.RawObject.newBuilder()
		                         .setKlass(theObj.getClass().getName())
		                         .setRawBytes(ByteString.copyFrom(objBytes))
		                         .build();
	}

	public static <T> T fromRawObject(final Messages.RawObject theRawObject) throws
	                                                                         ClassNotFoundException {
		final String klass = theRawObject.getKlass();
		final byte[] objBytes = theRawObject.getRawBytes().toByteArray();
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
}
