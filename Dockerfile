# Use an official OpenJDK image as the base image
FROM openjdk:11

# Set the Android SDK home directory
ENV ANDROID_HOME /opt/android-sdk
ENV ANDROID_SDK_ROOT ${ANDROID_HOME}
ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/tools/bin:${ANDROID_HOME}/platform-tools

# Install essential packages
RUN apt-get update && apt-get install -y wget unzip

# Download and unzip the Android SDK command-line tools
RUN wget -q "https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip" -O commandlinetools.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    unzip -q commandlinetools.zip -d ${ANDROID_HOME}/cmdline-tools && \
    mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/tools && \
    rm commandlinetools.zip

# Install SDK packages
RUN yes | sdkmanager --sdk_root=${ANDROID_HOME} --licenses
RUN sdkmanager --sdk_root=${ANDROID_HOME} "platform-tools" "platforms;android-30" "build-tools;30.0.3"

# Copy your Android project into the Docker image
# Change 'your-android-project' to the path of your project directory
COPY GymGameProject/root/project

# Set the working directory
WORKDIR /root/project


# Gradle wrapper usage; you might want to replace this with your specific build command if different.
RUN ./gradlew build

# Note:
# "platforms;android-30" and "build-tools;30.0.3" should be adapted based on your project's requirements.