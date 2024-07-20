# Use the official OpenJDK 11 image from the Docker Hub
FROM openjdk:11

# Set the Android SDK home directory
ENV ANDROID_HOME /opt/android-sdk-linux

# Add Android SDK tools and platform-tools to the PATH
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

# Install essential packages
RUN apt-get update && apt-get install -y wget unzip

# Download and install the Android command-line tools
RUN wget -q "https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip" -O android-sdk.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools/latest && \
    unzip -q android-sdk.zip -d ${ANDROID_HOME}/cmdline-tools/latest && \
    rm android-sdk.zip

# The previous commands created the cmdline-tools directory but placed the tools in a 'cmdline-tools/latest' subdirectory.
# We need to ensure that sdkmanager and other tools are directly accessible.

# Accept Android SDK licenses
RUN mkdir -p ~/.android/ && echo '### User Agreements' > ~/.android/repositories.cfg && yes | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager --licenses

# Note: By specifying 'cmdline-tools/latest/bin/sdkmanager', we ensure that the correct path is used.