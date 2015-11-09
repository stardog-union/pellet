package pellet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.clarkparsia.pellet.owlapiv3.LimitedMapIRIMapper;
import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.Environment;
import com.clarkparsia.pellet.server.PelletServerModule;
import com.clarkparsia.pellet.server.protege.ProtegeServerConfiguration;
import com.clarkparsia.pellet.service.json.GenericJsonMessage;
import com.complexible.pellet.client.ClientModule;
import com.complexible.pellet.client.api.PelletService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.mindswap.pellet.utils.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import static pellet.PelletCmdOptionArg.NONE;
import static pellet.PelletCmdOptionArg.REQUIRED;

public class PelletServer extends PelletCmdApp {
	private enum Command { START, STOP }

	@Override
	public String getAppCmd() {
		return "pellet server " + getMandatoryOptions() + "[options] <command>";
	}

	@Override
	public String getAppId() {
		return "PelletServer: Execute commands for Pellet server";
	}

	@Override
	public PelletCmdOptions getOptions() {
		PelletCmdOptions options = new PelletCmdOptions();

		//Don't call getGlobalOptions(), since we override the behaviour of verbose
		PelletCmdOption helpOption = new PelletCmdOption("help");
		helpOption.setShortOption("h");
		helpOption.setDescription("Print this message");
		helpOption.setDefaultValue(false);
		helpOption.setIsMandatory(false);
		helpOption.setArg(NONE);
		options.add(helpOption);

		PelletCmdOption verboseOption = new PelletCmdOption("verbose");
		verboseOption.setShortOption("v");
		verboseOption.setDescription("More verbose output");
		verboseOption.setDefaultValue(false);
		verboseOption.setIsMandatory(false);
		verboseOption.setArg(NONE);
		options.add(verboseOption);

		PelletCmdOption configOption = new PelletCmdOption( "config" );
		configOption.setShortOption( "C" );
		configOption.setDescription( "Use the selected configuration file" );
		configOption.setIsMandatory(false);
		configOption.setType("configuration file");
		configOption.setArg( REQUIRED );
		options.add( configOption );

		return options;
	}

	@Override
	public void run() {
		String[] commands = getInputFiles();

		if (commands.length != 1) {
			throw new PelletCmdException("A single command is required");
		}

		try {
			System.out.println(commands[0]);
			Command command = Command.valueOf(commands[0].toUpperCase());

			switch (command) {
				case START: startServer(); break;
				case STOP: stopServer(); break;
				default: throw new IllegalArgumentException();
			}
		}
		catch (IllegalArgumentException e) {
			throw new PelletCmdException("Unrecognized command: " + commands[0]);
		}
		catch (Exception e) {
			throw new PelletCmdException(e);
		}
	}

	private void startServer() throws Exception {
		Environment.assertHome();

		File aConfigFile = new File(Environment.getHome() + File.separator + Configuration.FILENAME);
		Configuration aConfig = new ProtegeServerConfiguration(aConfigFile);
		com.clarkparsia.pellet.server.PelletServer aPelletServer =
			new com.clarkparsia.pellet.server.PelletServer(Guice.createInjector(new PelletServerModule(aConfig)));
		aPelletServer.start();
	}

	private PelletService service() {
		Injector aInjector = Guice.createInjector(new ClientModule());

		return aInjector.getInstance(PelletService.class);
	}

	private void stopServer() throws IOException {
		GenericJsonMessage aMessage = service().shutdown()
		                                       .execute()
		                                       .body();
		System.out.println(aMessage.message);
	}
}
