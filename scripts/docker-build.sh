# Build the docker image

# Get application properties
artifact=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
group=$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout)
version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# Build jar and image
mvn clean package install && \
	docker build \
		-t "lukecmstevens/$artifact:$version" \
		-t "lukecmstevens/$artifact:latest" \
		--build-arg NAME="$artifact" \
		--build-arg GROUP="$group" \
		--build-arg VERSION="$version" .
		
