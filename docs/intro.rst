.. _camus_intro:

Camus
=====

Camus is a simple MapReduce job developed by LinkedIn to load data from Kafka into HDFS.
It is capable of incrementally copying data from Kafka into HDFS such that
every run of the MapReduce job picks up where the previous run left off.
At LinkedIn, Camus is used to load billions of messages per day from Kafka into HDFS.
Confluent's version of Camus integrates with Confluent's Schema Registry which
ensures data compatibility when loading to HDFS as schemas are evolved. You can find the design
and architecture of Camus in the :ref:`design section<camus_design>` .

Key Features
------------
    #. Automatic topic discovery: When a Camus job starts, it automatically fetches available topics
       from Zookeeper and offsets from Kafka and filters topics.

    #. Avro schema management: Camus integrates with Confluent's Schema Registry to ensure
       compatibility as Avro schema evolves.

    #. Output partitioning: Camus automatically partitions the output based on the timestamp of each
       record.

    #. Fault tolerance: Camus saves previous Kafka ETL requests and topic partition offsets to HDFS
       to provide fault tolerance on Zookeeper and Kafka failures. It also uses temp work directory
       to ensure consistency between Kafka and HDFS.

    #. Customizability: Many components of Camus is customizable. Camus provides interfaces for
       customized implementations of message decoder, data writer, data partitioner and
       work allocator.

    #. Load balance: Camus evenly assigns data to MapReduce tasks based on the size of
       each topic partitions. Moreover, as Camus jobs use temp working directories, speculation execution
       can be effective for straggler migration.

    #. Low operation overhead: Camus offers configurations to balance contention between topics and to
       control the Camus job behavior in case of incompatible data. By default, Camus will not
       fail the MapReduce job in case of incompatible data.

Design
------

Data loading from Kafka to HDFS is done within a single MapReduce job divided into three stages:

#. Setup stage: Fetches topics and partitions from Zookeeper and latest offsets from Kafka.
#. Hadoop stage: Allocates a set of MapReduce tasks to pull data from Kafka.
#. Cleanup stage: Collects statistics from MapReduce tasks and submits them to Kafka for consumption
   by other systems.

Quickstart
----------

Assuming you have already installed Confluent Platform. You'll need to specify the location
of Camus's jar file when running the MapReduce job. If you install Camus via zip/tgz archive,
you can find Camus's jar under ``share/java/camus/``. If you install Camus via rpm or deb,
the Camus's jar is under ``/usr/share/java/camus/``.

.. ifconfig:: platform_docs

   Also, you need to have access to a Hadoop Cluster. For installation and deployment of a single node
   Hadoop in pseudo-distributed mode, see this
   `guide <http://www.cloudera.com/content/cloudera/en/documentation/core/latest/topics/cdh_qs_cdh5_pseudo.html>`_.
   Finally, you should have :ref:`Kafka and the Schema Registry running<quickstart>` .

.. ifconfig:: not platform_docs

   Also, you need to have access to a Hadoop Cluster. For installation and deployment of a single node
   Hadoop in pseudo-distributed mode, see this
   `guide <http://www.cloudera.com/content/cloudera/en/documentation/core/latest/topics/cdh_qs_cdh5_pseudo.html>`_.
   Finally, you should have Kafka and the Schema Registry running.

.. sourcecode:: bash

   # Assuming that you have hadoop on your PATH, and make sure that
   # schema.registry.url points to the correct address.
   $ cd camus
   $ hadoop jar confluent-camus-1.0-shaded.jar com.linkedin.camus.etl.kafka.CamusJob \
     -D schema.registry.url=http://localhost:8081 -P etc/camus.properties

Installation
------------

.. ifconfig:: platform_docs

   See the :ref:`installation instructions<installation>` for the Confluent
   Platform. Before starting a Camus job you must have Haddop, Kafka, and the
   Schema Registry running. The :ref:`Confluent Platform quickstart<quickstart>`
   explains how to start Kafka and the Schema Registry locally for testing.
   See this
   `guide <http://www.cloudera.com/content/cloudera/en/documentation/core/latest/topics/cdh_qs_cdh5_pseudo.html>`_
   to setup a single Hadoop node in pseudo-distributed mode.

.. ifconfig:: not platform_docs

   You can download prebuilt versions of Camus as part of the
   `Confluent Platform <http://confluent.io/downloads/>`_. To install from
   source, follow the instructions in the `Development`_ section.
   Before starting a Camus job you must have Haddop, Kafka, and the
   Schema Registry running. You can find instructions for Kafka and the Schema
   Registry in the `Schema Registry repository <http://github.com/confluentinc/schema-registry>`_.
   This `guide <http://www.cloudera.com/content/cloudera/en/documentation/core/latest/topics/cdh_qs_cdh5_pseudo.html>`_.
   to setup a single Hadoop node in pseudo-distributed mode.


Deployment
----------

Camus can be run from the command line. You will need to set some configurations either by specifying a
properties file on the classpath using ``-p`` (filename), or an external properties file using ``-P``
(path to local file system, or to hdfs),
or from the command line using ``-D property=value``.
If the same property is set with multiple methods,
the order of precedence is command-line properties, external properties file and
classpath properties file. You can find a list of settings in :ref:`configuration section<camus_config>` .

.. sourcecode:: bash

   $ hadoop jar confluent-camus-1.0-shaded.jar com.linkedin.camus.etl.kafka.CamusJob
      -D <property=value>
      -P <path to external properties file>
      -p <path to properties file from classpath>

Development
-----------

To build a development version of Camus, you need to get development version of the
`Schema Registry <https://github.com/confluentinc/schema-registry>`_ and its dependencies
and install it into local Maven repository. Once the dependencies are installed, you can build
Confluent version of Camus as follows:

.. sourcecode:: bash

    $ git clone https://github.com/confluentinc/camus.git
    $ cd camus
    $ mvn clean package

Requirements
------------

- Hadoop: Camus works with both MRv1 and YARN. We recommend CDH 5.3.0
- Kafka: 0.8.2.0
- Schema Registry: Confluent Schema Registry 1.0

Contribute
----------

- Upstream Repository: https://github.com/linkedin/camus
- Source Code: https://github.com/confluentinc/camus
- Issue Tracker: https://github.com/confluentinc/camus/issues

License
-------

The project is licensed under the Apache 2 license.
