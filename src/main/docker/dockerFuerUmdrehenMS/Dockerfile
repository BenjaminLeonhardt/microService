FROM jboss/wildfly:latest
MAINTAINER benjaminleonhardt@gmx.de

# replace the standalone.xml config file with our custom one
ENV config_dir=/opt/jboss/wildfly/standalone/configuration/
ADD standalone.xml ${config_dir}

ADD microServices.war /opt/jboss/wildfly/standalone/deployments

# define the deployments directory as a volume that can be mounted
VOLUME /opt/jboss/wildfly/standalone/deployments

# Add a management user named 'admin' 
RUN /opt/jboss/wildfly/bin/add-user.sh -u admin -p bla123 --silent

# expose debug port
EXPOSE 38080
EXPOSE 8787
EXPOSE 9990
EXPOSE 30051

# start WildFly in standalone/debug mode and allow management from host 
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0" , "--debug"]


