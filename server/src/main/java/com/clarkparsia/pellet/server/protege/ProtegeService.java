package com.clarkparsia.pellet.server.protege;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.google.common.collect.Lists;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.RemoteOntologyDocument;
import org.protege.owl.server.api.client.RemoteServerDirectory;
import org.protege.owl.server.api.client.RemoteServerDocument;
import org.protege.owl.server.api.exception.OWLServerException;

/**
 * @author Edgar Rodriguez-Diaz
 */
public final class ProtegeService {

	private ProtegeService() {
		throw new IllegalStateException("Can't be instantiated!");
	}

	public static Collection<RemoteOntologyDocument> list(final Client client,
	                                                      final RemoteServerDirectory theDir) throws
	                                                                                          OWLServerException {
		List<RemoteOntologyDocument> docs = Lists.newLinkedList();
		Stack<RemoteServerDirectory> dirsToProc = new Stack<RemoteServerDirectory>();
		dirsToProc.push(theDir);

		while (!dirsToProc.empty()) {
			for (final RemoteServerDocument doc : client.list(dirsToProc.pop())) {
				if (doc != null) {
					if (doc instanceof RemoteOntologyDocument) {
						docs.add((RemoteOntologyDocument) doc);
					}
					else {
						dirsToProc.push((RemoteServerDirectory) doc);
					}
				}
			}
		}

		return docs;
	}
}
