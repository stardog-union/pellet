Pellet Server
=============

Pellet server integrates Pellet's schema reasoning and explanation capabilities with Protege server and allows the reasoner to be used
from within Protege desktop client as any other reasoner. Pellet server retrieves ontology contents from the Protege server and updates
these ontologies periodically to keep the reasoning state up-to-date. Classification results are saved onto disk so when the server is
restarted previous classification results are reused. Pellet server automatically detects when new versions are committed to the Protege
server and updates the reasoning state via incremental classification. Pellet server only supports schema reasoning and does not support
instance queries.

Running the server
------------------

Following instructions are given for *nix systems. Adjust them accordingly for Windows systems.

1. First setup a directory where Pellet server will save classification results. 
```bash
$ mkdir /data/pellet
```
2. Create a configuration file. Configuration file contains information about the Protege server,
ontologies that will be loaded by the Pellet server and other server settings. An example configuration file is as follows:
```
# info about how to connect to the protege server
protege.host=localhost
protege.port=5100
protege.username=admin
protege.password=admin

# data directory that will be used by Pellet server
pellet.home=/data/pellet
# port used by Pellet server
pellet.port=18080

# comma separated list of ontologies to load from the protege server. the ontologies
# are identified by their location
protege.ontologies=pizza.history,koala.history

# frequency (in seconds) at which pellet server will check protege server for new commits
pellet.update.interval.sec=60
```
3. Start the Pellet server by using the configuration file: 
```bash
$ bin/pellet server --config server.properties start
```

Make sure Protege server is already running before starting the Pellet server.

Connecting through Protege
--------------------------

In order to connect to Pellet server in Protege first make sure Pellet plugin is installed. You can install the plugin form the Pellet
update site or simply copy the plugin jar file to `plugins` directory in Protege root directory.

Once Pellet plugin is installed select "Reasoner->Configure" option from the Protege menu and go to the "Pellet" tab. Select the
"Remote" option under "Reasoner mode" and enter the URL for the Pellet server. You should use the address of the Pellet server and
the pellet.port value specified in the configuration file. So if the server is running on the same machine with the above configuration
file you should enter "http://localhost:18080".

You should also make sure the "Explanations" option in the configuration tab is set to "Limit explanations to". Retrieving all explanations
can be very slow in client-server mode so this option should not be used with Pellet server.

Once the Pellet remote reasoner is configured in Protege all reasoning functionalities can be used for any ontology loaded from the same
Protege server and specified in the Pellet server configuration file. Trying to do reasoning with any other ontology will raise an error.

Note that Pellet remote reasoner works in buffered mode. This means changes performed in Protege will be buffered locally and send to the
reasoner only when the user selects "Reasoner->Synchronize reasoner" menu option. Reasoner results will be updated after synchronization
is complete.

Multiple Protege clients can connect to the same Pellet server. Local changes performed by a Protege client will only be visible to itself
and not shared by other clients.

Building from source
--------------------
If you are building the server and plugin from source first generate the binaries using the following maven command:
```bash
$ mvn package
```

The server is included in the Pellet distribution bundle which will be located at `distribution/target/pellet-VERSION-dist.zip` with the
current version number. Unzip the distribution and follow the instructions above for running the server. You might need to make the pellet
script executable (`chmod u+x bin/pellet`) after unzipping the distribution.

Protege plugin jar will be located at `protege/target/pellet-protege-VERSION.jar` and copied directly to the Protege plugins directory.
Protege needs to be restarted before the plugin can be used. Follow the instructions above for configuration the Protege reasoner.






