# Get application properties
artifact=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId|grep -Ev '(^\[|Download\w+:)')
group=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.groupId|grep -Ev '(^\[|Download\w+:)')
version=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')

echo "::set-env name=MVN_ARTIFACT::$artifact"
echo "::set-env name=MVN_GROUP::$group"
echo "::set-env name=MVN_VERSION::$version"