package pellet;

import java.io.File;
import java.io.IOException;

import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.pellet.server.Configuration;
import com.clarkparsia.pellet.server.ConfigurationReader;
import com.clarkparsia.pellet.server.PelletServerModule;
import com.clarkparsia.pellet.server.protege.ProtegeServerConfiguration;
import com.complexible.pellet.client.ClientModule;
import com.complexible.pellet.client.ClientTools;
import com.complexible.pellet.client.PelletService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import retrofit2.Call;

import static pellet.PelletCmdOptionArg.NONE;
import static pellet.PelletCmdOptionArg.REQUIRED;

public class PelletServer extends PelletCmdApp {
	private enum Command { START, STOP }

	private Configuration serverConfig;

	@Override
	public String getAppCmd() {
		return "pellet server " + getMandatoryOptions() + "[options] (start|stop)";
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
		PelletExplanation.setup();

		String[] commands = getInputFiles();

		if (commands.length < 1) {
			throw new PelletCmdException("A command (start or stop) is required");
		}

		try {
			Command command = Command.valueOf(commands[0].toUpperCase());

			switch (command) {
				case START: startServer(); break;
				case STOP: stopServer(commands); break;
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
		Configuration aConfig = getServerConfig();
		com.clarkparsia.pellet.server.PelletServer aPelletServer =
			new com.clarkparsia.pellet.server.PelletServer(Guice.createInjector(new PelletServerModule(aConfig)));
		aPelletServer.start();
	}

	private PelletService service(String endpoint) {
		Injector aInjector = Guice.createInjector(new ClientModule(endpoint));

		return aInjector.getInstance(PelletService.class);
	}

	private void stopServer(final String[] args) throws IOException {
		String endpoint;
		if (args.length > 1) {
			endpoint = args[1];
		}
		else {
			ConfigurationReader.PelletSettings settings = ConfigurationReader.of(getServerConfig()).pelletSettings();
			endpoint = "http://"+ settings.host() + ":" + settings.port();
		}

		Call<Void> shutdownCall = service(endpoint).shutdown();
		ClientTools.executeCall(shutdownCall);

		System.out.println("Pellet server is shutting down");
	}

	private Configuration getServerConfig() throws IOException {
		if (serverConfig == null) {
			String configFile = options.getOption("config").getValueAsString();
			if( configFile == null ) {
				configFile = "server.properties";
			}
			serverConfig = new ProtegeServerConfiguration(new File(configFile));
		}

		return serverConfig;
	}
}
