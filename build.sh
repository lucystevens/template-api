# Build the docker image

# Get application properties
artifact=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId|grep -Ev '(^\[|Download\w+:)')
group=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.groupId|grep -Ev '(^\[|Download\w+:)')
version=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')

# Build jar and image
mvn clean package install && \
	docker build \
		-t "lukestevens/$artifact:$version" \
		-t "lukestevens/$artifact:latest" \
		--build-arg NAME="$artifact" \
		--build-arg GROUP="$group" \
		--build-arg VERSION="$version" .