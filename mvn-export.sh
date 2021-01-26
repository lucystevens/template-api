# Get application properties
MVN_ARTIFACT=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId|grep -Ev '(^\[|Download\w+:)')
MVN_GROUP=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.groupId|grep -Ev '(^\[|Download\w+:)')
MVN_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')