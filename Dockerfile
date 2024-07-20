# Use the official OpenJDK 11 image from the Docker Hub
FROM openjdk:11

# Set the Android SDK home directory
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/bin:${ANDROID_HOME}/platform-tools

# Install essential packages
RUN apt-get update && apt-get install -y wget unzip

# Download and install the Android SDK command-line tools
RUN wget -q "https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip" -O android-sdk.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    unzip -q android-sdk.zip -d ${ANDROID_HOME}/cmdline-tools && \
    rm android-sdk.zip

# Verify the contents of the cmdline-tools directory
RUN ls -la ${ANDROID_HOME}/cmdline-tools
RUN ls -la ${ANDROID_HOME}/cmdline-tools/bin

# Verify that the downloaded files have the correct permissions to execute sdkmanager
RUN chmod +x ${ANDROID_HOME}/cmdline-tools/bin/sdkmanager

# Accept Android SDK licenses
RUN mkdir -p ~/.android/ && echo '### User Agreements' > ~/.android/repositories.cfg && yes | ${ANDROID_HOME}/cmdline-tools/bin/sdkmanager --licenses

# Note:
# - The tools are placed directly under `cmdline-tools`.
# - The PATH is updated to reflect the correct location for `sdkmanager`.
# - Intermediate steps to list directory contents and verify installation.