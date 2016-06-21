Openllet: An Open Source OWL DL reasoner for Java
-----------------------------------------------

[![Build Status](https://api.travis-ci.org/Galigator/openllet.svg?branch=2.5.1-galigator)](https://travis-ci.org/Galigator/openllet)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Galigator/pelletEvolution?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


Openllet is the OWL 2 DL reasoner: 
--------------------------------


Openllet can be used with Jena or OWL-API libraries. Openllet provides functionality to check consistency of ontologies, compute the classification hierarchy, 
explain inferences, and answer SPARQL queries.

Feel free to fork this repository and submit pull requests if you want to see changes, new features, etc. in Pellet.
We need a lot more tests, send your samples if you can.

There are some  code samples in the examples/ directory.
Issues are on [Github](http://github.com/galigator/openllet/issues).

Openllet 2.5.X:
-----------

* full java 8 support, java 8 is a requierement.
* speed and stability improvement

Changes :
* Update versions of libs : owlapi 5, jena3 and lots more. Some old libs have been integrated and cleaned, strongly typed into openllet.
* Corrections : all tests works, no more warnings with high level of reports in Eclipse.

Migration :
* pellet/owlapi/src/main/java/com/clarkparsia/owlapiv3/ is now  pellet/owlapi/src/main/java/com/clarkparsia/owlapiv/
* groupId   com.clarkparsia.pellet   is now   com.github.galigator.openllet

Pellet 1..2.3] Licences and supports: 
-------------------------------------
 
* [open source](https://github.com/complexible/pellet/blob/master/LICENSE.txt) (AGPL) or commercial license
* pure Java
* developed and [commercially supported](http://complexible.com/) by Complexible Inc. 

Commercial support for Pellet is [available](http://complexible.com/). 
The [Pellet FAQ](http://clarkparsia.com/pellet/faq) answers some frequently asked questions.

There is a [pellet-users mailing list](https://groups.google.com/forum/?fromgroups#!forum/pellet-users) for questions and feedback.
You can search [pellet-users archives](http://news.gmane.org/gmane.comp.web.pellet.user).
Bug reports and enhancement requests should be sent to the mailing list. 

Thanks for using Pellet.
